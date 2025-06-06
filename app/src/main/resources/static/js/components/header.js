function renderHeader() {
  const headerDiv = document.getElementById("header");
  if (!headerDiv) return;

  // Clear role/token on homepage
  if (window.location.pathname.endsWith("/")) {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
  }

  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  // Handle invalid session
  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  let headerContent = "<nav class='header-nav'>";

  if (role === "admin") {
    headerContent += `
      <button id="addDocBtn" class="adminBtn">Add Doctor</button>
      <a href="#" id="logoutBtn">Logout</a>`;
  } else if (role === "doctor") {
    headerContent += `
      <a href="/doctorDashboard">Home</a>
      <a href="#" id="logoutBtn">Logout</a>`;
  } else if (role === "patient") {
    headerContent += `
      <a href="/login">Login</a>
      <a href="/signup">Sign Up</a>`;
  } else if (role === "loggedPatient") {
    headerContent += `
      <a href="/patientDashboard">Home</a>
      <a href="/appointments">Appointments</a>
      <a href="#" id="logoutBtn">Logout</a>`;
  }

  headerContent += "</nav>";
  headerDiv.innerHTML = headerContent;
  attachHeaderButtonListeners();
}

function attachHeaderButtonListeners() {
  const addDocBtn = document.getElementById("addDocBtn");
  const logoutBtn = document.getElementById("logoutBtn");

  if (addDocBtn) {
    addDocBtn.addEventListener("click", () => openModal("addDoctor"));
  }

  if (logoutBtn) {
    logoutBtn.addEventListener("click", () => logout());
  }
}

function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userRole");
  window.location.href = "/";
}

function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.setItem("userRole", "patient");
  window.location.href = "/patientDashboard";
}

renderHeader();
