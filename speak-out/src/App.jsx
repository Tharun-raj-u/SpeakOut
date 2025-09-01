import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage.jsx";
import UserDashboard from "./pages/User/UserDashboard.jsx";
import AdminDashBoard from "./pages/Admin/AdminDashBoard.jsx";
import EmployeeForm from "./pages/Admin/EmployeeForm.jsx";
import PageNotFound from "./pages/PageNotFound.jsx";
import SuggestionForm from "./pages/User/SuggestionForm.jsx";
import AdminSuggestionsPage from "./pages/Admin/AdminSuggestionsPage.jsx";
import MySuggestionsPage from "./pages/User/MySuggestionsPage.jsx";
import DeletedSuggestionsPage from "./pages/Admin/DeletedSuggestionsPage.jsx";
import {jwtDecode} from "jwt-decode";

function App() {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");

  const isAuthenticated = token !== null && role !== null;

  if (token) {
    try {
      const decoded = jwtDecode(token);
      if (decoded && decoded.exp && decoded.exp * 1000 < Date.now()) {
        localStorage.removeItem("token");
        localStorage.removeItem("role");
        window.location.href = "/";
      }
    } catch (error) {
      // Invalid token, remove and redirect to login
      localStorage.removeItem("token");
      localStorage.removeItem("role");
      window.location.href = "/";
    }
  }

  return (
    <Router>
      <Routes>
        {/* Redirect root (/) to dashboard if logged in */}
        {isAuthenticated ? (
          <Route
            path="/"
            element={<Navigate to={role === "ROLE_ADMIN" ? "/admin" : "/user"} />}
          />
        ) : (
          <Route path="/" element={<LoginPage />} />
        )}

        {/* User Routes (only if authenticated & role = USER) */}
        <Route
          path="/user"
          element={
            isAuthenticated && role === "ROLE_USER"  || role === "ROLE_ADMIN" ? (
              <UserDashboard />
            ) : (
              <Navigate to="/" />
            )
          }
        />
        <Route
          path="/suggestions"
          element={
            isAuthenticated && role === "ROLE_USER" ? (
              <SuggestionForm />
            ) : (
              <Navigate to="/" />
            )
          }
        />
        <Route
          path="/my-suggestions"
          element={
            isAuthenticated && role === "ROLE_USER" ? (
              <MySuggestionsPage />
            ) : (
              <Navigate to="/" />
            )
          }
        />

        {/* Admin Routes (only if authenticated & role = ADMIN) */}
        <Route
          path="/admin"
          element={
            isAuthenticated && role === "ROLE_ADMIN" ? (
              <AdminDashBoard />
            ) : (
              <Navigate to="/" />
            )
          }
        />
        <Route
          path="/admin/register"
          element={
            isAuthenticated && role === "ROLE_ADMIN" ? (
              <EmployeeForm />
            ) : (
              <Navigate to="/" />
            )
          }
        />
        <Route
          path="/admin/suggestions"
          element={
            isAuthenticated && role === "ROLE_ADMIN" ? (
              <AdminSuggestionsPage />
            ) : (
              <Navigate to="/" />
            )
          }
        />
         <Route
          path="/admin/deleted-suggestions"
          element={
            isAuthenticated && role === "ROLE_ADMIN" ? (
              <DeletedSuggestionsPage />
            ) : (
              <Navigate to="/" />
            )
          }
        />

        {/* Catch-all (404 Page) */}
        <Route path="*" element={<PageNotFound />} />
      </Routes>
    </Router>
  );
}

export default App;
