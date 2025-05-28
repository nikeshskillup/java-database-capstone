import { createPatientRow } from './patientRows.js';
import { getPatientsForToday, getPatientsByDate } from './patientServices.js';

const patientTableBody = document.querySelector("#patientTable tbody");
const searchBar = document.getElementById("searchBar");
const todayBtn = document.getElementById("todayBtn");
const datePicker = document.getElementById("datePicker");

// Render patient rows or show "no records" message
function renderPatients(patients) {
  patientTableBody.innerHTML = '';
  if (!patients.length) {
    const tr = document.createElement("tr");
    tr.classList.add("noPatientRecord");
    tr.innerHTML = `<td colspan="5" style="font-style: italic; color: gray; text-align:center;">No patients found for the selected date.</td>`;
    patientTableBody.appendChild(tr);
    return;
  }
  patients.forEach(patient => {
    patientTableBody.appendChild(createPatientRow(patient));
  });
}

// Load patients for today's date
async function loadPatientsForToday() {
  try {
    const patients = await getPatientsForToday();
    renderPatients(patients);
  } catch (error) {
    console.error("Error loading today's patients:", error);
    renderPatients([]);
  }
}

// Load patients for selected date
async function loadPatientsByDate(date) {
  try {
    const patients = await getPatientsByDate(date);
    renderPatients(patients);
  } catch (error) {
    console.error(`Error loading patients for ${date}:`, error);
    renderPatients([]);
  }
}

// Search/filter table rows by input text
function filterPatientRows(searchText) {
  const rows = patientTableBody.querySelectorAll("tr");
  rows.forEach(row => {
    const rowText = row.textContent.toLowerCase();
    row.style.display = rowText.includes(searchText.toLowerCase()) ? "" : "none";
  });
}

// Event Listeners
todayBtn.addEventListener("click", () => {
  datePicker.value = ''; // Clear date picker
  loadPatientsForToday();
});

datePicker.addEventListener("change", () => {
  if (datePicker.value) {
    loadPatientsByDate(datePicker.value);
  }
});

searchBar.addEventListener("input", e => {
  filterPatientRows(e.target.value);
});

// Initial load on page ready
document.addEventListener("DOMContentLoaded", () => {
  loadPatientsForToday();
});
