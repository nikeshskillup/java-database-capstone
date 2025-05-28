export function renderHeader() {
  const userRole = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  const header = document.createElement("header");
  header.classList.add("navbar", "navbar-expand-lg", "navbar-dark", "bg-dark", "px-4");
  header.innerHTML = `
    <a class="navbar-brand" href="#">Smart Clinic</a>
    <div class="ms-auto">
      ${token ? `<span class="text-white me-3">Role: ${userRole}</span>
      <button class="btn btn-outline-light btn-sm" id="logoutBtn">Logout</button>` : ""}
    </div>
  `;

  document.body.prepend(header);

  if (token) {
    document.getElementById("logoutBtn").addEventListener("click", () => {
      localStorage.clear();
      location.href = "defineRole.html";
    });
  }
}
