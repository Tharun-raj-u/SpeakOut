import React, { useEffect, useState } from "react";
import "./AdminSuggestionsPage.css";
import AdminNavbar from "./AdminNavbar";

const AdminSuggestionsPage = () => {
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // status editing
  const [editingStatusId, setEditingStatusId] = useState(null);
  const [newStatus, setNewStatus] = useState("");
  const [reason, setReason] = useState("");

  // pagination states
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const size = 6; // items per page

  // filter state
  const [filterStatus, setFilterStatus] = useState("ALL");

  const token = localStorage.getItem("token");

  useEffect(() => {
    if (!token) return;
    fetchSuggestions(page, filterStatus);
  }, [token, page, filterStatus]);

  // 🔹 Fetch data
  const fetchSuggestions = (pageNumber = 0, status = "ALL") => {
    setLoading(true);
    let url = `http://localhost:8080/api/suggestions?paginated=true&page=${pageNumber}&size=${size}`;

    if (status !== "ALL") {
      url += `&status=${status}`;
    }

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

  // 🔹 Delete suggestion
  const handleDelete = (id) => {
    if (!window.confirm("Are you sure you want to delete this suggestion?"))
      return;

    fetch(`http://localhost:8080/api/admin/suggestions/${id}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to delete suggestion");
        setSuggestions((prev) => prev.filter((s) => s.id !== id));
      })
      .catch((err) => alert(err.message));
  };

  // 🔹 Change status
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
      .then(() => {
        fetchSuggestions(page, filterStatus); // reload with filter
        setEditingStatusId(null);
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
              setPage(0); // reset page on filter change
              setFilterStatus(e.target.value);
            }}
          >
            <option value="ALL">All</option>
            <option value="OPEN">Open</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="CLOSED">Closed</option>
            <option value="REJECTED">Rejected</option>
          </select>
        </div>

        {/* Loading / Error / Empty states */}
        {loading && <p className="admin-loading">Loading suggestions...</p>}
        {error && <p className="admin-error">{error}</p>}
        {!loading && suggestions.length === 0 && (
          <p className="admin-empty">No suggestions found.</p>
        )}

        {/* Suggestions Grid */}
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

              {/* Actions */}
              <div className="admin-actions">
                {editingStatusId === s.id ? (
                  <div className="admin-status-form">
                    <select
                      value={newStatus}
                      onChange={(e) => setNewStatus(e.target.value)}
                    >
                      <option value="">Select status</option>
                      <option value="OPEN">OPEN</option>
                      <option value="IN_PROGRESS">IN_PROGRESS</option>
                      <option value="CLOSED">CLOSED</option>
                      <option value="REJECTED">REJECTED</option>
                    </select>
                    <input
                      type="text"
                      placeholder="Reason (optional)"
                      value={reason}
                      onChange={(e) => setReason(e.target.value)}
                    />
                    <button
                      className="admin-btn admin-btn-save"
                      onClick={() => submitStatusChange(s.id)}
                    >
                      ✅ Save
                    </button>
                    <button
                      className="admin-btn admin-btn-cancel"
                      onClick={() => setEditingStatusId(null)}
                    >
                      ❌ Cancel
                    </button>
                  </div>
                ) : (
                  <button
                    className="admin-btn admin-btn-status"
                    onClick={() => setEditingStatusId(s.id)}
                  >
                    🔄 Change Status
                  </button>
                )}

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

        {/* Pagination */}
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
    </>
  );
};

export default AdminSuggestionsPage;
