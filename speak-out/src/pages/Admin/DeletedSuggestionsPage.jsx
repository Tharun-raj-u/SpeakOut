import React, { useEffect, useState } from "react";
import "./DeletedSuggestionsPage.css";
import AdminNavbar from "./AdminNavbar";

const DeletedSuggestionsPage = () => {
  const [deletedSuggestions, setDeletedSuggestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const token = localStorage.getItem("token");

  // 🔹 Load deleted suggestions
  useEffect(() => {
   getAllDeletedSuggestions();
  }, [token]);
  const getAllDeletedSuggestions = () => {
    if (!token) return;
    fetch("http://localhost:8080/api/admin/suggestions/deleted", {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to fetch deleted suggestions");
        return res.json();
      })
      .then((data) => setDeletedSuggestions(data))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  };

const handleClearAll = () => {
  if (!window.confirm("Are you sure you want to permanently delete all suggestions?"))
    return;

  fetch(`${import.meta.env.VITE_API_BASE_URL}/admin/suggestions/hardDelete`, {
    method: "DELETE",
    headers: { Authorization: `Bearer ${token}` },
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to clear deleted suggestions");

      // if 204, no body to parse
      if (res.status === 204) return null; 
      return res.json();
    })
    .then(() => {
      setDeletedSuggestions([]); // clear local state
      getAllDeletedSuggestions(); // refresh list after success
    })
    .catch((err) => setError(err.message));
};



  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getStatusBadgeClass = (status) => {
    switch (status?.toLowerCase()) {
      case 'rejected': return 'status-rejected';
      case 'approved': return 'status-approved';
      case 'open': return 'status-open';
      case 'pending': return 'status-pending';
      default: return 'status-default';
    }
  };

  return (
    
    <>
      <AdminNavbar />
   
           <div className="deleted-suggestions-container">
      <div className="header-section">
        <div className="header-content">
          <h1 className="page-title">
            <span className="trash-icon">🗑️</span>
            Deleted Suggestions
          </h1>
          <p className="page-subtitle">Manage and review deleted suggestions</p>
        </div>
        
        {deletedSuggestions.length > 0 && (
          <button className="clear-all-btn" onClick={handleClearAll}>
            <span className="btn-icon">🧹</span>
            Clear All
          </button>
        )}
      </div>

      {loading && (
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Loading deleted suggestions...</p>
        </div>
      )}

      {error && (
        <div className="error-container">
          <div className="error-icon">⚠️</div>
          <p className="error-message">{error}</p>
        </div>
      )}

      {!loading && deletedSuggestions.length === 0 && (
        <div className="empty-state">
          <div className="empty-icon">📭</div>
          <h3>No deleted suggestions found</h3>
          <p>All clear! There are no deleted suggestions to display.</p>
        </div>
      )}

      <div className="suggestions-grid">
        {deletedSuggestions.map((suggestion) => (
          <div key={suggestion.id} className="suggestion-card">
            <div className="card-header">
              <div className="suggestion-id">#{suggestion.id}</div>
              <div className={`status-badge ${getStatusBadgeClass(suggestion.status)}`}>
                {suggestion.status}
              </div>
            </div>

            <div className="card-content">
              <h3 className="suggestion-title">{suggestion.title}</h3>
              <p className="suggestion-description">{suggestion.description}</p>

              <div className="suggestion-meta">
                <div className="meta-item">
                  <span className="meta-icon">👤</span>
                  <span className="meta-label">Submitted by:</span>
                  <span className="meta-value">
                    {suggestion.isAnonymous ? "Anonymous" : suggestion.submitterName}
                  </span>
                </div>

                <div className="meta-item">
                  <span className="meta-icon">👍</span>
                  <span className="meta-label">Votes:</span>
                  <span className="meta-value">{suggestion.voteCount || 0}</span>
                </div>

                <div className="meta-item">
                  <span className="meta-icon">📅</span>
                  <span className="meta-label">Created:</span>
                  <span className="meta-value">{formatDate(suggestion.createdAt)}</span>
                </div>

                <div className="meta-item">
                  <span className="meta-icon">🗑️</span>
                  <span className="meta-label">Deleted:</span>
                  <span className="meta-value">{formatDate(suggestion.deletedAt)}</span>
                </div>
              </div>
            </div>

            <div className="card-sections">
              {suggestion.statusHistory && suggestion.statusHistory.length > 0 && (
                <div className="section">
                  <h4 className="section-title">
                    <span className="section-icon">📋</span>
                    Status History
                  </h4>
                  <div className="status-timeline">
                    {suggestion.statusHistory.map((history) => (
                      <div key={history.id} className="timeline-item">
                        <div className="timeline-dot"></div>
                        <div className="timeline-content">
                          <div className="status-change">
                            <span className="status-from">
                              {history.previousStatus || "Initial"}
                            </span>
                            <span className="arrow">→</span>
                            <span className="status-to">{history.newStatus}</span>
                          </div>
                          <div className="change-details">
                            <span className="changed-by">by {history.changedBy}</span>
                            {history.changeReason && (
                              <span className="change-reason">• {history.changeReason}</span>
                            )}
                          </div>
                          <div className="change-date">
                            {formatDate(history.createdAt)}
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {suggestion.votes && suggestion.votes.length > 0 && (
                <div className="section">
                  <h4 className="section-title">
                    <span className="section-icon">🗳️</span>
                    Vote Details ({suggestion.votes.length})
                  </h4>
                  <div className="votes-list">
                    {suggestion.votes.map((vote) => (
                      <div key={vote.id} className="vote-item">
                        <div className="vote-device">
                          <span className="device-icon">📱</span>
                          {vote.deviceIdentifier}
                        </div>
                        <div className="vote-date">
                          {formatDate(vote.createdAt)}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
          </>
  );
};

export default DeletedSuggestionsPage;