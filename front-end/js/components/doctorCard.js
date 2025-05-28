export function createDoctorCard(doctor) {
  const card = document.createElement("div");
  card.className = "card-doctor";

  card.innerHTML = `
    <h5>${doctor.name}</h5>
    <p><strong>Specialty:</strong> ${doctor.specialty}</p>
    <p><strong>Availability:</strong> ${doctor.availability}</p>
    <button class="btn btn-danger btn-sm delete-btn">Delete</button>
  `;

  card.querySelector('.delete-btn').addEventListener('click', () => {
    alert(`Deleting ${doctor.name}...`);
  });

  return card;
}
