import React from "react";
import { useNavigate } from "react-router-dom";
import "./PageNotFound.css";

function PageNotFound() {
  const navigate = useNavigate();

  return (
    <div className="notfound-container">
      <h1 className="notfound-title">404</h1>
      <h2 className="notfound-subtitle">Page Not Found</h2>
      <p className="notfound-text">
        Oops! The page you are looking for doesn’t exist or was moved.
      </p>
      <button className="back-home-btn" onClick={() => navigate("/")}>
        Go Back Home
      </button>
    </div>
  );
}

export default PageNotFound;
