import React from "react";
import { Link } from "react-router-dom";
import AdminNavbar from "./AdminNavbar";
    
function AdminDashboard() {
  return (
    <div>
        <AdminNavbar/>
      <h1>Admin Dashboard</h1>
      <p>Welcome! You are logged in as ADMIN.</p>
      <Link to="/register">
        <button>Register Employee</button>
      </Link>
    </div>
  );
}

export default AdminDashboard;
