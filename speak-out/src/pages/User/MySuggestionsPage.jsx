import React, { useEffect, useState } from "react";
import "./MySuggestionsPage.css";
import Navbar from "./Navbar";

const MySuggestionsPage = () => {
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const token = localStorage.getItem("token");

  useEffect(() => {
    if (!token) return;

    const fetchSuggestions = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/suggestions/employee", {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (!response.ok) {
          throw new Error("Failed to fetch suggestions.");
        }
        const data = await response.json();
        setSuggestions(data || []);
      } catch (error) {
        setError(error.message);
        setSuggestions([]);
      } finally {
        setLoading(false);
      }
    };

    fetchSuggestions();
  }, [token]);

  const handleDelete = async (id) => {
    try {
      const response = await fetch(`http://localhost:8080/api/admin/suggestions/${id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!response.ok) throw new Error("Failed to delete suggestion.");

      setSuggestions((prev) => prev.filter((s) => s.id !== id));
    } catch (error) {
      alert(error.message);
    }
  };

  const handleEdit = async (id, newTitle, newDesc) => {
    try {
      const response = await fetch(`http://localhost:8080/api/suggestions/${id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ title: newTitle, description: newDesc }),
      });

      if (!response.ok) throw new Error("Failed to update suggestion.");

      const updated = await response.json();
      setSuggestions((prev) => prev.map((s) => (s.id === id ? updated : s)));
    } catch (error) {
      alert(error.message);
    }
  };

  return (
    <>  <Navbar />
    <div className="suggestions-container">
    
      <h2>📝 My Suggestions</h2>

      {loading && <p>Loading suggestions...</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}
      {suggestions.length === 0 && !loading && <p>You haven’t added any suggestions yet.</p>}

      <div className="suggestions-grid">
        {suggestions.map((s) => (
          <div key={s.id} className="suggestion-card">
            <div className="card-header">
              <h3>{s.title}</h3>
              <span className={`status-badge ${s.status.toLowerCase()}`}>{s.status}</span>
            </div>
            <p className="desc">{s.description}</p>

            <div className="card-meta">
              <small>By: {s.submitterName}</small>
              <small>Votes: {s.voteCount}</small>
              <small>Created: {new Date(s.createdAt).toLocaleString()}</small>
            </div>

            <details className="history">
              <summary>Status History</summary>
              <ul>
                {s.statusHistory.map((h) => (
                  <li key={h.id}>
                    <strong>{h.newStatus}</strong> by {h.changedBy} (
                    {new Date(h.createdAt).toLocaleString()})
                  </li>
                ))}
              </ul>
            </details>

            <div className="suggestion-actions">
              {s.status === "OPEN" && (
                <>
                  <button
                    className="edit-btn"
                    onClick={() => {
                      const newTitle = prompt("New Title", s.title);
                      const newDesc = prompt("New Description", s.description);
                      if (newTitle && newDesc) handleEdit(s.id, newTitle, newDesc);
                    }}
                  >
                    ✏️ Edit
                  </button>
                  <button className="delete-btn" onClick={() => handleDelete(s.id)}>
                    🗑 Delete
                  </button>
                </>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
      </>
  );
};

export default MySuggestionsPage;
