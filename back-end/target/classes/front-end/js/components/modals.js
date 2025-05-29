export function openModal(role) {
  const modal = document.getElementById("modal");
  const modalBody = document.getElementById("modal-body");

  if (role === "admin") {
    modalBody.innerHTML = `
      <h4>Admin Login</h4>
      <input id="admin-username" class="form-control my-2" placeholder="Username" />
      <input id="admin-password" type="password" class="form-control my-2" placeholder="Password" />
      <button onclick="adminLoginHandler()" class="btn btn-primary w-100 mt-2">Login</button>
    `;
  } else if (role === "doctor") {
    modalBody.innerHTML = `
      <h4>Doctor Login</h4>
      <input id="doctor-email" class="form-control my-2" placeholder="Email" />
      <input id="doctor-password" type="password" class="form-control my-2" placeholder="Password" />
      <button onclick="doctorLoginHandler()" class="btn btn-success w-100 mt-2">Login</button>
    `;
  }

  modal.style.display = "flex";

  document.getElementById("closeModal").onclick = () => {
    modal.style.display = "none";
  };
}
