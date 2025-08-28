import React from "react";
import { Link, useNavigate } from "react-router-dom";
import "./Navbar.css";

const Navbar = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login"); // redirect after logout
  };

  return (
    <nav className="navbar">
      <div className="navbar-brand">💡 Suggestion Box</div>
      <ul className="navbar-links">
        <li>
          <Link to="/suggestions">Create An Suggestions</Link>
        </li>
        <li>
          <Link to="/my-suggestions">My Suggestions</Link>
        </li>
        <li>
          <button onClick={handleLogout} className="logout-btn">
            Logout
          </button>
        </li>
      </ul>
    </nav>
  );
};

export default Navbar;
