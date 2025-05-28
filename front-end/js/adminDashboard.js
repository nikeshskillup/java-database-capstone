// File: front-end/js/services/adminDashboard.js
import { openModal, closeModal } from '../components/modals.js';
import { getDoctors, saveDoctor, filterDoctors, deleteDoctor } from './doctorService.js';
import { createDoctorCard } from '../render.js';

window.onload = () => {
  // Load doctors on page load
  loadDoctorCards();

  // Attach event listeners
  document.getElementById('searchBar').addEventListener('input', filterDoctorsOnChange);
  document.getElementById('filterTime').addEventListener('change', filterDoctorsOnChange);
  document.getElementById('filterSpecialty').addEventListener('change', filterDoctorsOnChange);
  document.getElementById('addDoctorBtn').addEventListener('click', openAddDoctorModal);
  document.getElementById('closeModal').addEventListener('click', closeModal);

  // Close modal on clicking outside modal body
  window.addEventListener('click', (event) => {
    const modal = document.getElementById('modal');
    if (event.target === modal) {
      closeModal();
    }
  });
};

async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctors(doctors);
  } catch (error) {
    alert('Failed to load doctors: ' + error.message);
  }
}

function renderDoctors(doctors) {
  const contentDiv = document.getElementById('content');
  contentDiv.innerHTML = ''; // Clear previous content

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = '<p>No doctors found.</p>';
    return;
  }

  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);

    // Attach delete event listener inside the card button
    const deleteBtn = card.querySelector('.delete-doctor-btn');
    if (deleteBtn) {
      deleteBtn.addEventListener('click', () => handleDeleteDoctor(doctor.id));
    }
  });
}

async function filterDoctorsOnChange() {
  const searchText = document.getElementById('searchBar').value.trim().toLowerCase();
  const filterTime = document.getElementById('filterTime').value;
  const filterSpecialty = document.getElementById('filterSpecialty').value;

  try {
    const filteredDoctors = await filterDoctors(searchText, filterTime, filterSpecialty);
    renderDoctors(filteredDoctors);
  } catch (error) {
    alert('Failed to filter doctors: ' + error.message);
  }
}

function openAddDoctorModal() {
  const modalBody = document.getElementById('modal-body');

  modalBody.innerHTML = `
    <h3>Add New Doctor</h3>
    <form id="addDoctorForm" novalidate>
      <div class="mb-3">
        <label for="doctorName" class="form-label">Name</label>
        <input type="text" class="form-control" id="doctorName" required />
      </div>
      <div class="mb-3">
        <label for="doctorSpecialty" class="form-label">Specialty</label>
        <select id="doctorSpecialty" class="form-select" required>
          <option value="" disabled selected>Select specialty</option>
          <option value="cardiologist">Cardiologist</option>
          <option value="neurologist">Neurologist</option>
          <option value="dermatologist">Dermatologist</option>
        </select>
      </div>
      <div class="mb-3">
        <label for="doctorEmail" class="form-label">Email</label>
        <input type="email" class="form-control" id="doctorEmail" required />
      </div>
      <div class="mb-3">
        <label for="doctorPassword" class="form-label">Password</label>
        <input type="password" class="form-control" id="doctorPassword" required minlength="6" />
      </div>
      <div class="mb-3">
        <label for="doctorMobile" class="form-label">Mobile No.</label>
        <input type="tel" class="form-control" id="doctorMobile" required pattern="[0-9]{10}" />
      </div>
      <div class="mb-3">
        <label for="doctorAvailability" class="form-label">Availability Time</label>
        <select id="doctorAvailability" class="form-select" required>
          <option value="" disabled selected>Select availability</option>
          <option value="morning">Morning</option>
          <option value="afternoon">Afternoon</option>
          <option value="evening">Evening</option>
        </select>
      </div>
      <button type="submit" class="btn btn-primary">Save Doctor</button>
    </form>
  `;

  openModal();

  document.getElementById('addDoctorForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    await adminAddDoctor();
  });
}

async function adminAddDoctor() {
  const name = document.getElementById('doctorName').value.trim();
  const specialty = document.getElementById('doctorSpecialty').value;
  const email = document.getElementById('doctorEmail').value.trim();
  const password = document.getElementById('doctorPassword').value;
  const mobile = document.getElementById('doctorMobile').value.trim();
  const availability = document.getElementById('doctorAvailability').value;

  // Simple client-side validation
  if (!name || !specialty || !email || !password || !mobile || !availability) {
    alert('Please fill out all required fields.');
    return;
  }

  if (password.length < 6) {
    alert('Password should be at least 6 characters long.');
    return;
  }

  const doctorData = {
    name,
    specialty,
    email,
    password,
    mobile,
    availabilityTime: availability,
  };

  try {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('You must be logged in to add a doctor.');
      closeModal();
      return;
    }

    await saveDoctor(doctorData, token);

    alert('Doctor added successfully.');
    closeModal();
    loadDoctorCards();
  } catch (error) {
    alert('Failed to add doctor: ' + error.message);
  }
}

async function handleDeleteDoctor(doctorId) {
  if (!confirm('Are you sure you want to delete this doctor?')) return;

  try {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('You must be logged in to delete a doctor.');
      return;
    }
    await deleteDoctor(doctorId, token);
    alert('Doctor deleted successfully.');
    loadDoctorCards();
  } catch (error) {
    alert('Failed to delete doctor: ' + error.message);
  }
}
