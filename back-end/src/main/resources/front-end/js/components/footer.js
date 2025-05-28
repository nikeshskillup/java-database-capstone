export function renderFooter() {
  const footer = document.getElementById("footer");
  footer.innerHTML = `
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-column">
          <h4>Clinic Management</h4>
          <p>&copy; 2025 Clinic Management System</p>
        </div>
        <div class="footer-column">
          <h4>Navigation</h4>
          <a href="defineRole.html">Login</a>
          <a href="adminDashboard.html">Admin Dashboard</a>
          <a href="doctorDashboard.html">Doctor Dashboard</a>
        </div>
        <div class="footer-column">
          <h4>Contact</h4>
          <p>Email: support@clinic.com</p>
          <p>Phone: +1234567890</p>
        </div>
      </div>
    </footer>
  `;
}
document.addEventListener("DOMContentLoaded", renderFooter);
