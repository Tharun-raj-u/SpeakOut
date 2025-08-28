import React, { useState } from "react";
import "../styles/employee.css";

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
      let response = await fetch("http://localhost:8080/api/auth/register", {
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
    <div className="form-container">
      <h2>Employee Registration</h2>
      <form onSubmit={handleSubmit}>
        <label>Name:</label>
        <input
          type="text"
          name="name"
          value={formData.name}
          onChange={handleChange}
          required
        />

        <label>Email:</label>
        <input
          type="email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          required
        />

        <label>Password:</label>
        <input
          type="password"
          name="password"
          value={formData.password}
          onChange={handleChange}
          required
        />

        <label>Department:</label>
        <input
          type="text"
          name="department"
          value={formData.department}
          onChange={handleChange}
        />

        <label>Position:</label>
        <input
          type="text"
          name="position"
          value={formData.position}
          onChange={handleChange}
        />

        <label>Role:</label>
        <select
          name="role"
          value={formData.role}
          onChange={handleChange}
        >
          <option value="USER">USER</option>
          <option value="ADMIN">ADMIN</option>
        </select>

        <button type="submit">Create Employee</button>
      </form>
    </div>
  );
}

export default EmployeeForm;
