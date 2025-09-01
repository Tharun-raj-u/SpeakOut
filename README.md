# SpeakOut – Employee Suggestion Platform

A full-stack web application that allows employees to submit suggestions, and admins to manage and review them.  
Built with **Spring Boot (Java)** for the backend, **React (Vite)** for the frontend, **MySQL** as the database, and **JWT** for authentication.

---

## 🚀 Features

### 🔐 Authentication & Security
- User login with JWT-based authentication
- Role-based access control (Admin / Employee)
- Token expiration & refresh mechanism

### 👨‍💼 Employee
- Register and log in
- Submit suggestions
- View own suggestions
- Delete / edit own suggestions

### 🛠️ Admin
- Manage employees
- View all suggestions
- Approve / reject suggestions
- View deleted suggestions

### 🎨 UI/UX
- Built with **React + Vite**
- Routing with **React Router**
- Responsive design
- Deployed on **Vercel**

---

## 🏗️ Tech Stack

### Backend
- **Spring Boot 3**
- **Spring Security + JWT**
- **MySQL**
- **JPA/Hibernate**
- **Maven**

### Frontend
- **React (Vite)**
- **React Router**
- **Axios** for API calls
- **Tailwind / CSS** for styling

### Deployment
- Frontend: **Vercel**
- Backend: **Dockerized Spring Boot**
- Database: **MySQL (Aiven Cloud / Local)**

---

## 🌍 Deployed URLs

- **Frontend (React):**  
  👉 [SpeakOut on Vercel](https://speakout-j7xhnm8gl-traj-8109s-projects.vercel.app)

- **Backend (Spring Boot API):**  
  👉 `https://your-deployed-backend-url/api`

*(Replace `your-deployed-backend-url` with your actual backend host — e.g., Railway, Render, EC2, or Docker server IP)*  

---

## ⚙️ Installation

### 1️⃣ Backend (Spring Boot)
```bash
# Clone repo
git clone https://github.com/your-username/speakout-backend.git
cd speakout-backend

# Configure DB in application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/speakout
spring.datasource.username=root
spring.datasource.password=yourpassword

# Run app
mvn spring-boot:run
