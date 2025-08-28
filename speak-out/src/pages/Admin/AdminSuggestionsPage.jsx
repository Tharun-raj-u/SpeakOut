import React, { useEffect, useState } from "react";
import "./AdminSuggestionsPage.css";

const AdminSuggestionsPage = () => {
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editingStatusId, setEditingStatusId] = useState(null);
  const [newStatus, setNewStatus] = useState("");
  const [reason, setReason] = useState("");
  const token = localStorage.getItem("token");

  // 🔹 Load all suggestions
  useEffect(() => {
    if (!token) return;

    setLoading(true);
    fetch(
      "http://localhost:8080/api/suggestions?paginated=true&page=0&size=10",
      { headers: { Authorization: `Bearer ${token}` } }
    )
      .then((res) => {
        if (!res.ok) throw new Error("Failed to fetch suggestions");
        return res.json();
      })
      .then((data) => {
        setSuggestions(data.content || []);
        setError(null);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [token]);

  // 🔹 Delete suggestion
  const handleDelete = (id) => {
    if (!window.confirm("Are you sure you want to delete this suggestion?"))
      return;

    fetch(`http://localhost:8080/api/suggestions/${id}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to delete suggestion");
        setSuggestions((prev) => prev.filter((s) => s.id !== id));
      })
      .catch((err) => alert(err.message));
  };

  // 🔹 Submit status change
  const submitStatusChange = (id) => {
    if (!newStatus) return alert("Please select a status");

    fetch(`http://localhost:8080/api/admin/suggestions/${id}/status`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ status: newStatus, reason }),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to update status");
        return res.json();
      })
      .then((updated) => {
        setSuggestions((prev) =>
          prev.map((s) => (s.id === id ? updated : s))
        );
        setEditingStatusId(null);
        setNewStatus("");
        setReason("");
      })
      .catch((err) => alert(err.message));
  };

  return (
    <div className="suggestions-container">
      <h2>🛠 Admin: Manage Suggestions</h2>

      {loading && <p>Loading suggestions...</p>}
      {error && <p className="error-message">{error}</p>}
      {!loading && suggestions.length === 0 && <p>No suggestions found.</p>}

      {suggestions.map((s) => (
        <div key={s.id} className="suggestion-bubble">
          <p className="suggestion-title">{s.title}</p>
          <p>{s.description}</p>
          <small>
            {s.isAnonymous ? "Anonymous" : s.submitterName || "Unknown"} •{" "}
            {s.status}
          </small>

          <div className="suggestion-actions">
            {editingStatusId === s.id ? (
              <div className="status-form">
                <select
                  value={newStatus}
                  onChange={(e) => setNewStatus(e.target.value)}
                >
                  <option value="">Select status</option>
                  <option value="OPEN">OPEN</option>
                  <option value="CLOSED">CLOSED</option>
                  <option value="IN_PROGRESS">IN_PROGRESS</option>
                  <option value="REJECTED">REJECTED</option>
                </select>
                <input
                  type="text"
                  placeholder="Reason (optional)"
                  value={reason}
                  onChange={(e) => setReason(e.target.value)}
                />
                <button onClick={() => submitStatusChange(s.id)}>✅ Save</button>
                <button onClick={() => setEditingStatusId(null)}>❌ Cancel</button>
              </div>
            ) : (
              <button onClick={() => setEditingStatusId(s.id)}>
                🔄 Change Status
              </button>
            )}

            <button onClick={() => handleDelete(s.id)}>🗑 Delete</button>
          </div>
        </div>
      ))}
    </div>
  );
};

export default AdminSuggestionsPage;
