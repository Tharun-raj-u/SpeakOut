import React, { useEffect, useState } from "react";
import "./AdminSuggestionsPage.css";
import AdminNavbar from "./AdminNavbar";
import Modal from "./Modal";

const AdminSuggestionsPage = () => {
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // status editing modal
  const [editingSuggestion, setEditingSuggestion] = useState(null);
  const [newStatus, setNewStatus] = useState("");
  const [reason, setReason] = useState("");

  // pagination
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const size = 6;
  const [filterStatus, setFilterStatus] = useState("ALL");

  const token = localStorage.getItem("token");

  useEffect(() => {
    if (!token) return;
    fetchSuggestions(page, filterStatus);
  }, [token, page, filterStatus]);

  const fetchSuggestions = (pageNumber = 0, status = "ALL") => {
    setLoading(true);
    let url = `${import.meta.env.VITE_API_BASE_URL}/suggestions?paginated=true&page=${pageNumber}&size=${size}`;
    if (status !== "ALL") url += `&status=${status}`;

    fetch(url, { headers: { Authorization: `Bearer ${token}` } })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to fetch suggestions");
        return res.json();
      })
      .then((data) => {
        setSuggestions(data.content || []);
        setTotalPages(data.totalPages || 0);
        setError(null);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  };

  const handleDelete = (id) => {
    if (!window.confirm("Are you sure you want to delete this suggestion?"))
      return;

    fetch(`${import.meta.env.VITE_API_BASE_URL}/admin/suggestions/${id}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to delete suggestion");
        setSuggestions((prev) => prev.filter((s) => s.id !== id));
      })
      .catch((err) => alert(err.message));
  };

  const submitStatusChange = () => {
    if (!newStatus) return alert("Please select a status");

    fetch(`${import.meta.env.VITE_API_BASE_URL}/admin/suggestions/${editingSuggestion.id}/status`, {
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
      .then(() => {
        fetchSuggestions(page, filterStatus);
        setEditingSuggestion(null);
        setNewStatus("");
        setReason("");
      })
      .catch((err) => alert(err.message));
  };

  return (
    <>
      <AdminNavbar />
      <div className="admin-suggestions-wrapper">
        <h2 className="admin-title">🛠 Admin: Manage Suggestions</h2>

        {/* Filter */}
        <div className="filter-bar">
          <label>Filter by Status:</label>
          <select
            value={filterStatus}
            onChange={(e) => {
              setPage(0);
              setFilterStatus(e.target.value);
            }}
          >
            <option value="ALL">All</option>
            <option value="OPEN">Open</option>
            <option value="UNDER_REVIEW">Under Review</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="IMPLEMENTED">Implemented</option>
            <option value="ON_HOLD">On Hold</option>
            <option value="REJECTED">Rejected</option>
          </select>
        </div>

        {loading && <p className="admin-loading">Loading suggestions...</p>}
        {error && <p className="admin-error">{error}</p>}
        {!loading && suggestions.length === 0 && (
          <p className="admin-empty">No suggestions found.</p>
        )}

        <div className="admin-grid">
          {suggestions.map((s) => (
            <div key={s.id} className="admin-suggestion-card">
              <div className="admin-card-header">
                <p className="admin-suggestion-title">{s.title}</p>
                <span className={`admin-status-badge status-${s.status}`}>
                  {s.status}
                </span>
              </div>
              <p className="admin-description">{s.description}</p>
              <small className="admin-meta">
                {s.isAnonymous ? "Anonymous" : s.submitterName || "Unknown"}
              </small>

              <div className="admin-actions">
                <button
                  className="admin-btn admin-btn-status"
                  onClick={() => setEditingSuggestion(s)}
                >
                  🔄 Change Status
                </button>
                <button
                  className="admin-btn admin-btn-delete"
                  onClick={() => handleDelete(s.id)}
                >
                  🗑 Delete
                </button>
              </div>
            </div>
          ))}
        </div>

        {totalPages > 1 && (
          <div className="pagination">
            <button
              className="page-btn"
              onClick={() => setPage((p) => Math.max(p - 1, 0))}
              disabled={page === 0}
            >
              ⬅ Prev
            </button>
            <span className="page-info">
              Page {page + 1} of {totalPages}
            </span>
            <button
              className="page-btn"
              onClick={() => setPage((p) => Math.min(p + 1, totalPages - 1))}
              disabled={page === totalPages - 1}
            >
              Next ➡
            </button>
          </div>
        )}
      </div>

      {/* Modal for Status Change */}
      <Modal
        show={!!editingSuggestion}
        onClose={() => setEditingSuggestion(null)}
      >
        <h3>Change Status</h3>
        <select
          value={newStatus}
          onChange={(e) => setNewStatus(e.target.value)}
        >
          <option value="">Select status</option>
          <option value="OPEN">Open</option>
          <option value="UNDER_REVIEW">Under Review</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="IMPLEMENTED">Implemented</option>
          <option value="ON_HOLD">On Hold</option>
          <option value="REJECTED">Rejected</option>
        </select>
        <input
          type="text"
          placeholder="Reason (optional)"
          value={reason}
          onChange={(e) => setReason(e.target.value)}
        />
        <div className="modal-actions">
          <button className="admin-btn admin-btn-save" onClick={submitStatusChange}>
            ✅ Save
          </button>
          <button
            className="admin-btn admin-btn-cancel"
            onClick={() => setEditingSuggestion(null)}
          >
            ❌ Cancel
          </button>
        </div>
      </Modal>
    </>
  );
};

export default AdminSuggestionsPage;
