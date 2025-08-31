import React, { useState } from "react";
import "./SuggestionForm.css";
import Navbar from "./Navbar";
import { useNavigate } from "react-router-dom";

function SuggestionForm() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    anonymous: false,
  });

  const [modalMessage, setModalMessage] = useState(""); // ✅ For success/error messages
  const [showModal, setShowModal] = useState(false);

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
      const token = localStorage.getItem("token");
      const response = await fetch(
        `${import.meta.env.VITE_API_BASE_URL}/suggestions`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(formData),
        }
      );

      if (response.ok) {
        setModalMessage("🎉 Suggestion submitted successfully!");
        setShowModal(true);
        setFormData({ title: "", description: "", anonymous: false });

        // redirect after a short delay
        setTimeout(() => {
          setShowModal(false);
          navigate("/user");
        }, 2000);
      } else {
        setModalMessage("❌ Error submitting suggestion!");
        setShowModal(true);
      }
    } catch (error) {
      console.error("Error:", error);
      setModalMessage("⚠️ Server not reachable");
      setShowModal(true);
    }
  };

  return (
    <>
      <Navbar />
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

      {/* ✅ Confirmation Modal */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal-box">
            <p>{modalMessage}</p>
            <button onClick={() => setShowModal(false)}>OK</button>
          </div>
        </div>
      )}
    </>
  );
}

export default SuggestionForm;
