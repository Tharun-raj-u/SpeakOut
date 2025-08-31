import React, { useEffect, useState } from "react";
import "./MySuggestionsPage.css";
import Navbar from "./Navbar";

const MySuggestionsPage = () => {
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editModal, setEditModal] = useState(false);
  const [toast, setToast] = useState({ show: false, message: "" }); // ✅ Toast state
  const [editData, setEditData] = useState({ id: null, title: "", description: "" });

  const token = localStorage.getItem("token");

  useEffect(() => {
    if (!token) return;

    const fetchSuggestions = async () => {
      try {
        const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/suggestions/employee`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (!response.ok) throw new Error("Failed to fetch suggestions.");
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
      const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/admin/suggestions/${id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!response.ok) throw new Error("Failed to delete suggestion.");
      setSuggestions((prev) => prev.filter((s) => s.id !== id));

      // ✅ Show toast message
      setToast({ show: true, message: "Suggestion deleted successfully ✅" });
      setTimeout(() => setToast({ show: false, message: "" }), 3000);
    } catch (error) {
      setToast({ show: true, message: error.message || "Failed to delete ❌" });
      setTimeout(() => setToast({ show: false, message: "" }), 3000);
    }
  };

  const handleEditSubmit = async () => {
    try {
      const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/suggestions/${editData.id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ title: editData.title, description: editData.description }),
      });

      if (!response.ok) throw new Error("Failed to update suggestion.");

      const updated = await response.json();
      setSuggestions((prev) => prev.map((s) => (s.id === updated.id ? updated : s)));
      setEditModal(false);

      // ✅ Show toast
      setToast({ show: true, message: "Suggestion updated successfully ✏️" });
      setTimeout(() => setToast({ show: false, message: "" }), 3000);
    } catch (error) {
      setToast({ show: true, message: error.message || "Update failed ❌" });
      setTimeout(() => setToast({ show: false, message: "" }), 3000);
    }
  };

  return (
    <>
      <Navbar />
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
                        setEditData({ id: s.id, title: s.title, description: s.description });
                        setEditModal(true);
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

      {/* 🔹 Edit Modal */}
      {editModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>Edit Suggestion</h3>
            <input
              type="text"
              value={editData.title}
              onChange={(e) => setEditData({ ...editData, title: e.target.value })}
              placeholder="Enter new title"
            />
            <textarea
              value={editData.description}
              onChange={(e) => setEditData({ ...editData, description: e.target.value })}
              placeholder="Enter new description"
            />
            <div className="modal-actions">
              <button onClick={handleEditSubmit} className="save-btn">💾 Save</button>
              <button onClick={() => setEditModal(false)} className="cancel-btn">❌ Cancel</button>
            </div>
          </div>
        </div>
      )}

      {/* 🔹 Toast Notification */}
      {toast.show && (
        <div className="toast">
          {toast.message}
        </div>
      )}
    </>
  );
};

export default MySuggestionsPage;
