export function renderFooter() {
  const footer = document.createElement("footer");
  footer.className = "bg-dark text-white text-center p-3 mt-5";
  footer.innerHTML = `<p>&copy; 2025 Smart Clinic System. All rights reserved.</p>`;
  document.body.appendChild(footer);
}
