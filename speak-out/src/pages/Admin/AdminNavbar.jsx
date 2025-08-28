import React from "react";
import { Link } from "react-router-dom";
import "./AdminNavbar.css";

const AdminNavbar = () => {
  // Handle logout
  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    window.location.href = "/login"; // redirect to login
  };

  return (
    <nav className="admin-navbar">
      <div className="navbar-logo">
        <h2>Admin Panel</h2>
      </div>
      <ul className="navbar-links">
        <li>
          <Link to="/admin" className="navbar-item">Dashboard</Link>
        </li>
        <li>
          <Link to="/admin/register" className="navbar-item">Register an Employee</Link>
        </li>
        <li>
          <Link to="/admin/users" className="navbar-item">Manage Users</Link>
        </li>
        <li>
          <Link to="/admin/suggestions" className="navbar-item">Manage Suggestions</Link>
        </li>
        <li>
          <Link to="/admin/deleted-suggestions" className="navbar-item">Deleted Suggestions</Link>
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
