export function renderHeader() {
  const headerDiv = document.getElementById("header");

  if (window.location.pathname.includes("defineRole.html")) {
    localStorage.removeItem("userRole");
    headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-section">
          <img src="images/logo.png" alt="Clinic Logo" />
          <span class="logo-title">Clinic Management</span>
        </div>
      </header>
    `;
    return;
  }

  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  let headerContent = `
    <header class="header">
      <div class="logo-section">
        <img src="images/logo.png" alt="Clinic Logo" />
        <span class="logo-title">Clinic Management</span>
      </div>
      <nav>
  `;

  if (role === "admin") {
    headerContent += `
      <button id="addDoctorBtn">Add Doctor</button>
      <a href="#" id="logoutBtn">Logout</a>
    `;
  } else if (role === "doctor") {
    headerContent += `
      <a href="doctorDashboard.html">Dashboard</a>
      <a href="#" id="logoutBtn">Logout</a>
    `;
  } else if (role === "patient") {
    headerContent += `
      <a href="patientDashboard.html">My Appointments</a>
      <a href="#" id="logoutBtn">Logout</a>
    `;
  } else {
    headerContent += `<a href="defineRole.html">Login</a>`;
  }

  headerContent += `
      <select id="roleSelector">
        <option value="admin" ${role === "admin" ? "selected" : ""}>Admin</option>
        <option value="doctor" ${role === "doctor" ? "selected" : ""}>Doctor</option>
        <option value="patient" ${role === "patient" ? "selected" : ""}>Patient</option>
      </select>
    </nav>
    </header>
  `;

  headerDiv.innerHTML = headerContent;

  // Event listeners
  document.getElementById("roleSelector").addEventListener("change", e => {
    window.location.href = `${e.target.value}Dashboard.html`;
  });

  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", e => {
      e.preventDefault();
      localStorage.clear();
      window.location.href = "defineRole.html";
    });
  }

  const addDoctorBtn = document.getElementById("addDoctorBtn");
  if (addDoctorBtn) {
    addDoctorBtn.addEventListener("click", () => {
      // Open add doctor modal or redirect to add doctor page
      alert("Add doctor functionality triggered.");
    });
  }
}

// Call on script load
document.addEventListener("DOMContentLoaded", renderHeader);
