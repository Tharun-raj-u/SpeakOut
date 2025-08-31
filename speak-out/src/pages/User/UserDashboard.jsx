import React, { useState, useEffect } from "react";
import Navbar from "./Navbar";
import "./UserDashboard.css";

function UserDashboard() {
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [votingStates, setVotingStates] = useState({});
  const [page, setPage] = useState(0); // current page
  const [totalPages, setTotalPages] = useState(0);
  const token = localStorage.getItem("token");

  // Load suggestions when page or token changes
  useEffect(() => {
    if (token) {
      getData(page);
    }
  }, [page, token]);

  const getData = async (pageNumber = 0) => {
  
    try {
      const response = await fetch(
        `${import.meta.env.VITE_API_BASE_URL}/suggestions?paginated=true&page=${pageNumber}&size=10`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      const data = await response.json();
      setSuggestions(data.content || []);
      setTotalPages(data.totalPages || 0);
    } catch {
      setSuggestions([]);
    } finally {
      setLoading(false);
    }
  };

  const handleVote = async (id) => {
    if (votingStates[id]) return;
    setVotingStates((prev) => ({ ...prev, [id]: true }));

    let deviceId = "device123";
    try {
      const FingerprintJS = await import("@fingerprintjs/fingerprintjs");
      const fp = await FingerprintJS.load();
      const result = await fp.get();
      deviceId = result.visitorId;
    } catch {
      console.warn("FingerprintJS failed, using fallback");
    }

    try {
      const response = await fetch(
        `${import.meta.env.VITE_API_BASE_URL}/votes/suggestion/${id}/toggle`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ deviceId }),
        }
      );

      const data = await response.json();
      if (data.success) {
        getData(page); // refresh current page
      } else {
        alert(data.message || "Vote failed");
      }
    } catch (error) {
      console.error("Vote error:", error);
      alert("Failed to vote. Please try again.");
    } finally {
      setVotingStates((prev) => ({ ...prev, [id]: false }));
    }
  };

  const getStatusBadgeClass = (status) => {
    switch (status?.toLowerCase()) {
      case "open":
        return "status-open";
      case "pending":
        return "status-pending";
      case "approved":
        return "status-approved";
      case "rejected":
        return "status-rejected";
      case "implemented":
        return "status-implemented";
      default:
        return "status-default";
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat("en-US", {
      weekday: "short",
      year: "numeric",
      month: "short",
      day: "numeric",
      hour: "numeric",
      minute: "numeric",
    }).format(date);
  };

  return (
    <div className="suggestions-container">
      <Navbar />
      <div className="dashboard-header">
        <h2>💡 Employee Suggestions</h2>
        <p className="dashboard-subtitle">
          Discover and vote on amazing ideas from your colleagues
        </p>
      </div>

      {loading ? (
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Loading suggestions...</p>
        </div>
      ) : suggestions.length === 0 ? (
        <div className="empty-state">
          <div className="empty-icon">💭</div>
          <h3>No suggestions yet</h3>
          <p>Be the first to share your brilliant idea!</p>
        </div>
      ) : (
        <>
          <div className="suggestions-grid">
            {suggestions.map((s) => (
              <div key={s.id} className="suggestion-card">
                <div className="card-header">
                  <h3 className="suggestion-title">{s.title}</h3>
                  <span
                    className={`status-badge ${getStatusBadgeClass(s.status)}`}
                  >
                    {s.status || "Pending"}
                  </span>
                </div>

                <p className="suggestion-description">{s.description}</p>

                <div className="card-footer">
                  <span className="submitter">
                    {s.isAnonymous
                      ? "🎭 Anonymous"
                      : `👤 ${s.submitterName || "Unknown"}`}
                  </span>

                  {/* Display formatted creation date */}
                  <span className="creation-date">
                    📅 {formatDate(s.createdAt)}
                  </span>

                  <button
                    className={`vote-button ${
                      votingStates[s.id] ? "voting" : ""
                    }`}
                    onClick={() => handleVote(s.id)}
                    disabled={votingStates[s.id]}
                  >
                    {votingStates[s.id] ? (
                      <>
                        <span className="vote-spinner"></span>
                        {s.voteCount}
                      </>
                    ) : (
                      <>👍 {s.voteCount}</>
                    )}
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* 🔹 Pagination controls */}
          <div className="pagination">
            <button
              disabled={page === 0}
              onClick={() => setPage((prev) => prev - 1)}
            >
              ⬅ Prev
            </button>

            {Array.from({ length: totalPages }, (_, i) => (
              <button
                key={i}
                className={i === page ? "active" : ""}
                onClick={() => setPage(i)}
              >
                {i + 1}
              </button>
            ))}

            <button
              disabled={page >= totalPages - 1}
              onClick={() => setPage((prev) => prev + 1)}
            >
              Next ➡
            </button>
          </div>
        </>
      )}
    </div>
  );
}

export default UserDashboard;
