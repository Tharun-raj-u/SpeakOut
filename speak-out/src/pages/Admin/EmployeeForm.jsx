import React, { useState } from "react";
import "./EmployeeForm.css";   // updated CSS file
import AdminNavbar from "./AdminNavbar";

function EmployeeForm() {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    department: "",
    position: "",
    role: "USER",
  });

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      let response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/auth/register`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        alert("Employee created successfully!");
        setFormData({
          name: "",
          email: "",
          password: "",
          department: "",
          position: "",
          role: "USER",
        });
      } else {
        alert("Error creating employee!");
      }
    } catch (error) {
      console.error("Error:", error);
      alert("Server not reachable");
    }
  };

  return (
    <>
      <AdminNavbar />
      <div className="employee-registration-wrapper">
        <div className="employee-registration-container">
          <h2 className="employee-registration-title">Employee Registration</h2>
          <form className="employee-registration-form" onSubmit={handleSubmit}>
            <label className="employee-label">Name:</label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
              className="employee-input"
            />

            <label className="employee-label">Email:</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              className="employee-input"
            />

            <label className="employee-label">Password:</label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              className="employee-input"
            />

            <label className="employee-label">Department:</label>
            <input
              type="text"
              name="department"
              value={formData.department}
              onChange={handleChange}
              className="employee-input"
            />

            <label className="employee-label">Position:</label>
            <input
              type="text"
              name="position"
              value={formData.position}
              onChange={handleChange}
              className="employee-input"
            />

            <label className="employee-label">Role:</label>
            <select
              name="role"
              value={formData.role}
              onChange={handleChange}
              className="employee-input"
            >
              <option value="USER">USER</option>
              <option value="ADMIN">ADMIN</option>
            </select>

            <button type="submit" className="employee-submit-btn">
              Create Employee
            </button>
          </form>
        </div>
      </div>
    </>
  );
}

export default EmployeeForm;
