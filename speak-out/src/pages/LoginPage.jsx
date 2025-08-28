import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./LoginPage.css";

function LoginPage() {
  const [credentials, setCredentials] = useState({ email: "", password: "" });
  const navigate = useNavigate();

  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(credentials),
      });

      if (response.ok) {
        const data = await response.json();

        // Save token + role
        localStorage.setItem("token", data.token);
        localStorage.setItem("role", data.role);

        // Redirect based on role
        if (data.role === "ROLE_ADMIN") {
          navigate("/admin");
        } else {
          navigate("/user");
        }
      } else {
        alert("Invalid credentials!");
      }
    } catch (error) {
      console.error("Error:", error);
      alert("Server not reachable");
    }
  };

  return (
    <div className="form-container">
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <label>Email:</label>
        <input
          type="email"
          name="email"
          value={credentials.email}
          onChange={handleChange}
          required
        />

        <label>Password:</label>
        <input
          type="password"
          name="password"
          value={credentials.password}
          onChange={handleChange}
          required
        />

        <button type="submit">Login</button>
      </form>
    </div>
  );
}

export default LoginPage;
