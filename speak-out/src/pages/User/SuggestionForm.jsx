import React, { useState } from "react";
import "./SuggestionForm.css";

function SuggestionForm() {
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    anonymous: false,
  });

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === "checkbox" ? checked : value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const token = localStorage.getItem("token"); // 🔑 Authenticated request
      const response = await fetch("http://localhost:8080/api/suggestions", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        alert("Suggestion submitted successfully!");
        setFormData({
          title: "",
          description: "",
          anonymous: false,
        });
      } else {
        alert("Error submitting suggestion!");
      }
    } catch (error) {
      console.error("Error:", error);
      alert("Server not reachable");
    }
  };

  return (
    <div className="form-container">
      <h2>Submit a Suggestion</h2>
      <form onSubmit={handleSubmit}>
        <label>Title:</label>
        <input
          type="text"
          name="title"
          value={formData.title}
          onChange={handleChange}
          required
        />

        <label>Description:</label>
        <textarea
          name="description"
          value={formData.description}
          onChange={handleChange}
          rows="4"
          required
        ></textarea>

        <label className="checkbox-label">
          <input
            type="checkbox"
            name="anonymous"
            checked={formData.anonymous}
            onChange={handleChange}
          />
          Submit Anonymously
        </label>

        <button type="submit">Submit Suggestion</button>
      </form>
    </div>
  );
}

export default SuggestionForm;
