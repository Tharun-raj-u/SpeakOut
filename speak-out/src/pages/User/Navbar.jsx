import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./Navbar.css";

const Navbar = () => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");
    window.location.href = "/"; // Redirect after logout
  };

  const toggleMobileMenu = () => {
    setMobileMenuOpen(!mobileMenuOpen);
  };

  return (
    <nav className="navbar">
      <div className="navbar-brand">💡 Suggestion Box</div>
      <button className="mobile-menu-toggle" onClick={toggleMobileMenu}>
        &#9776;
      </button>
      <ul className={`navbar-links ${mobileMenuOpen ? "mobile-open" : ""}`}>
        <li>
          <Link to="/user">Dash Board</Link>
        </li>
        <li>
          <Link to="/suggestions">Create A Suggestion</Link>
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
