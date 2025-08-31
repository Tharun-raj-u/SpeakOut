import React, { useEffect, useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import "./AdminNavbar.css";

const AdminNavbar = () => {
  const navigate = useNavigate();
  const [theme, setTheme] = useState("dark");
  const [menuOpen, setMenuOpen] = useState(false);

  // Handle logout
  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    window.location.href = "/"; // Redirect after logout
  };

  // Apply theme
  useEffect(() => {
    document.documentElement.setAttribute("data-theme", theme);
  }, [theme]);

  return (
    <nav className="admin-navbar">
      {/* Logo */}
      <div className="navbar-logo">
        <h2>Admin Panel</h2>
      </div>

      {/* Hamburger Icon for mobile */}
      <button
        className="menu-toggle"
        onClick={() => setMenuOpen(!menuOpen)}
        aria-label="Toggle menu"
      >
        ☰
      </button>

      {/* Links */}
      <ul className={`navbar-links ${menuOpen ? "active" : ""}`}>
        <li>
          <NavLink to="/admin" className="navbar-item">
            Dashboard
          </NavLink>
        </li>
        <li>
          <NavLink to="/admin/register" className="navbar-item">
            Register Employee
          </NavLink>
        </li>
        <li>
          <NavLink to="/admin/suggestions" className="navbar-item">
            Manage Suggestions
          </NavLink>
        </li>
        <li>
          <NavLink to="/admin/deleted-suggestions" className="navbar-item">
            Deleted Suggestions
          </NavLink>
        </li>
        <li>
          <button onClick={handleLogout} className="navbar-item logout-btn">
            Logout
          </button>
        </li>
      </ul>

    </nav>
  );
};

export default AdminNavbar;
