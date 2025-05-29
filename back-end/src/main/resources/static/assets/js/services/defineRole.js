import { openModal } from '../components/modal.js';
import { API_BASE_URL } from '../config.js';

const ADMIN_LOGIN_URL = `${API_BASE_URL}/admin/login`;
const DOCTOR_LOGIN_URL = `${API_BASE_URL}/doctor/login`;

window.onload = () => {
  document.getElementById("adminBtn").onclick = () => openModal("admin");
  document.getElementById("doctorBtn").onclick = () => openModal("doctor");
};

window.adminLoginHandler = async () => {
  const username = document.getElementById("admin-username").value;
  const password = document.getElementById("admin-password").value;

  try {
    const response = await fetch(ADMIN_LOGIN_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });

    if (response.ok) {
      const { token } = await response.json();
      localStorage.setItem('token', token);
      localStorage.setItem('role', 'admin');
      window.location.href = "adminDashboard.html";
    } else {
      alert("Invalid admin credentials!");
    }
  } catch (err) {
    console.error("Admin login error:", err);
    alert("Something went wrong.");
  }
};

window.doctorLoginHandler = async () => {
  const email = document.getElementById("doctor-email").value;
  const password = document.getElementById("doctor-password").value;

  try {
    const response = await fetch(DOCTOR_LOGIN_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });

    if (response.ok) {
      const { token } = await response.json();
      localStorage.setItem('token', token);
      localStorage.setItem('role', 'doctor');
      window.location.href = "doctorDashboard.html";
    } else {
      alert("Invalid doctor credentials!");
    }
  } catch (err) {
    console.error("Doctor login error:", err);
    alert("Something went wrong.");
  }
};
