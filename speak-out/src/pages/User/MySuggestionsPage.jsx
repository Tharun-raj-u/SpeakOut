import React, { useEffect, useState } from "react";
import "./SuggestionsPage.css";

const MySuggestionsPage = () => {
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(true); // For handling loading state
  const [error, setError] = useState(null); // To catch errors
  const token = localStorage.getItem("token");

  // 🔹 Load only logged-in user's suggestions
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
        setSuggestions([]); // Optionally, clear the suggestions if error occurs
      } finally {
        setLoading(false); // Stop loading
      }
    };

    fetchSuggestions();
  }, [token]);

  // 🔹 Delete suggestion
  const handleDelete = async (id) => {
    try {
      const response = await fetch(`http://localhost:8080/api/suggestions/${id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!response.ok) {
        throw new Error("Failed to delete the suggestion.");
      }

      setSuggestions((prev) => prev.filter((s) => s.id !== id));
    } catch (error) {
      alert(error.message); // You can show an alert on failure
    }
  };

  // 🔹 Edit suggestion (changing title and description)
  const handleEdit = async (id, newTitle, newDesc) => {
    try {
      const response = await fetch(`http://localhost:8080/api/suggestions/${id}/status`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ title: newTitle, description: newDesc }),
      });

      if (!response.ok) {
        throw new Error("Failed to update the suggestion.");
      }

      const updated = await response.json();
      setSuggestions((prev) =>
        prev.map((s) => (s.id === id ? updated : s))
      );
    } catch (error) {
      alert(error.message); // You can show an alert on failure
    }
  };

  return (
    <div className="suggestions-container">
      <h2>📝 My Suggestions</h2>

      {loading && <p>Loading suggestions...</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}
      {suggestions.length === 0 && !loading && <p>You haven’t added any suggestions yet.</p>}

      {suggestions.map((s) => (
        <div key={s.id} className="suggestion-bubble mine">
          <p className="suggestion-title">{s.title}</p>
          <p>{s.description}</p>
          <small>{s.status}</small>

          <div className="suggestion-actions">
            {s.status === "OPEN" && (
              <>
                <button
                  onClick={() => {
                    const newTitle = prompt("New Title", s.title);
                    const newDesc = prompt("New Description", s.description);
                    if (newTitle && newDesc) {
                      handleEdit(s.id, newTitle, newDesc);
                    }
                  }}
                >
                  ✏️ Edit
                </button>
                <button onClick={() => handleDelete(s.id)}>🗑 Delete</button>
              </>
            )}
          </div>
        </div>
      ))}
    </div>
  );
};

export default MySuggestionsPage;
