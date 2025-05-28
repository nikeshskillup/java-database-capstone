// Create and export a function to create a <tr> for a patient object
export function createPatientRow(patient) {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${patient.id}</td>
      <td>${patient.name}</td>
      <td>${patient.phone}</td>
      <td>${patient.email}</td>
      <td><button class="prescription-btn">View</button></td>
    `;
    // Optionally attach event listener for prescription button here
    tr.querySelector(".prescription-btn").addEventListener("click", () => {
      // TODO: Open prescription modal/view (implement modal logic elsewhere)
      alert(`Viewing prescription for ${patient.name}`);
    });
    return tr;
  }
  