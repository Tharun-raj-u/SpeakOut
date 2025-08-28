import React, { useState, useEffect } from "react";
import Navbar from "./Navbar";
import "./UserDashboard.css";

function UserDashboard() {
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [votingStates, setVotingStates] = useState({}); // Track voting states
  const token = localStorage.getItem("token");

  // 🔹 Load all suggestions (paginated)
  useEffect(() => {
    getData();
  }, [token]);


  const getData = async () => {
    if (!token) return;

    try {
      const response = await fetch(
        "http://localhost:8080/api/suggestions?paginated=true&page=0&size=10",
        { headers: { Authorization: `Bearer ${token}` } }
      );
      const data = await response.json();
      setSuggestions(data.content || []);
    } catch {
      setSuggestions([]);
    } finally {
      setLoading(false);
    }
  };

  // 🔹 Toggle vote with proper UI updates
  const handleVote = async (id) => {
    // Prevent multiple clicks
    if (votingStates[id]) return;
    
    setVotingStates(prev => ({ ...prev, [id]: true }));

    let deviceId = "device123"; // fallback

    try {
      const FingerprintJS = await import("@fingerprintjs/fingerprintjs");
      const fp = await FingerprintJS.load();
      const result = await fp.get();
      deviceId = result.visitorId;
    } catch {
      console.warn("FingerprintJS failed, using fallback");
    }

    try {
      const response = await fetch(`http://localhost:8080/api/votes/suggestion/${id}/toggle`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ deviceId }),
      });

      const data = await response.json();
      
      if (data.success) {
         getData();
      } else {
        alert(data.message || "Vote failed");
      }
    } catch (error) {
      console.error("Vote error:", error);
      alert("Failed to vote. Please try again.");
    } finally {
      setVotingStates(prev => ({ ...prev, [id]: false }));
    }
  };

  const getStatusBadgeClass = (status) => {
    switch (status?.toLowerCase()) {
      case 'pending': return 'status-pending';
      case 'approved': return 'status-approved';
      case 'rejected': return 'status-rejected';
      case 'implemented': return 'status-implemented';
      default: return 'status-default';
    }
  };

  if (loading) {
    return (
      <div className="suggestions-container">
        <Navbar />
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Loading suggestions...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="suggestions-container">
      <Navbar />
      <div className="dashboard-header">
        <h2>💡 Employee Suggestions</h2>
        <p className="dashboard-subtitle">Discover and vote on amazing ideas from your colleagues</p>
      </div>
      
      {suggestions.length === 0 ? (
        <div className="empty-state">
          <div className="empty-icon">💭</div>
          <h3>No suggestions yet</h3>
          <p>Be the first to share your brilliant idea!</p>
        </div>
      ) : (
        <div className="suggestions-grid">
          {suggestions.map((s) => (
            <div key={s.id} className="suggestion-card">
              <div className="card-header">
                <h3 className="suggestion-title">{s.title}</h3>
                <span className={`status-badge ${getStatusBadgeClass(s.status)}`}>
                  {s.status || 'Pending'}
                </span>
              </div>
              
              <p className="suggestion-description">{s.description}</p>
              
              <div className="card-footer">
                <div className="suggestion-meta">
                  <span className="submitter">
                    {s.isAnonymous ? "🎭 Anonymous" : `👤 ${s.submitterName || "Unknown"}`}
                  </span>
                </div>
                
                <button 
                  className={`vote-button ${votingStates[s.id] ? 'voting' : ''}`}
                  onClick={() => handleVote(s.id)}
                  disabled={votingStates[s.id]}
                >
                  {votingStates[s.id] ? (
                    <>
                      <span className="vote-spinner"></span>
                      {s.voteCount}
                    </>
                  ) : (
                    <>
                      👍 {s.voteCount}
                    </>
                  )}
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default UserDashboard;