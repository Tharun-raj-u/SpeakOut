import React, { useEffect, useState } from "react";
import "./DeletedSuggestionsPage.css";

const DeletedSuggestionsPage = () => {
  const [deletedSuggestions, setDeletedSuggestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const token = localStorage.getItem("token");

  // 🔹 Load deleted suggestions
  useEffect(() => {
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
  }, [token]);

  // 🔹 Clear all deleted suggestions
  const handleClearAll = () => {
    if (!window.confirm("Are you sure you want to permanently delete all suggestions?"))
      return;

    fetch("http://localhost:8080/api/admin/suggestions/deleted", {
      method: "DELETE",
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to clear deleted suggestions");
        return res.json();
      })
      .then(() => setDeletedSuggestions([]))
      .catch((err) => alert(err.message));
  };

  return (
    <div className="deleted-suggestions-container">
      <h2>🗑 Deleted Suggestions</h2>
      <button className="clear-btn" onClick={handleClearAll}>
        Clear All
      </button>

      {loading && <p>Loading deleted suggestions...</p>}
      {error && <p className="error-message">{error}</p>}
      {!loading && deletedSuggestions.length === 0 && <p>No deleted suggestions.</p>}

      <div className="deleted-suggestions-list">
        {deletedSuggestions.map((s) => (
          <div key={s.id} className="deleted-suggestion-item">
            <p className="suggestion-title">{s.title}</p>
            <p>{s.description}</p>
            <small>
              Submitted by: {s.isAnonymous ? "Anonymous" : s.submitterName} • Status: {s.status} • Deleted At: {new Date(s.deletedAt).toLocaleString()}
            </small>

            <div className="status-history">
              <strong>Status History:</strong>
              <ul>
                {s.statusHistory.map((h) => (
                  <li key={h.id}>
                    {h.previousStatus || "N/A"} → {h.newStatus} by {h.changedBy} {h.changeReason ? `(${h.changeReason})` : ""}
                  </li>
                ))}
              </ul>
            </div>

            <div className="votes">
              <strong>Votes:</strong>
              <ul>
                {s.votes.map((v) => (
                  <li key={v.id}>
                    Device: {v.deviceIdentifier}, Voted At: {new Date(v.createdAt).toLocaleString()}
                  </li>
                ))}
              </ul>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default DeletedSuggestionsPage;
