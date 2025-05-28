// Helper: get auth token for doctor
function getAuthToken() {
  return localStorage.getItem("doctorToken");
}

// GET /api/doctor/patients/today
export async function getPatientsForToday() {
  const token = getAuthToken();
  if (!token) throw new Error("No doctor token found.");

  const response = await fetch("/api/doctor/patients/today", {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!response.ok) throw new Error("Failed to fetch today's patients");
  return response.json();
}

// GET /api/doctor/patients?date=YYYY-MM-DD
export async function getPatientsByDate(date) {
  const token = getAuthToken();
  if (!token) throw new Error("No doctor token found.");

  const response = await fetch(`/api/doctor/patients?date=${date}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!response.ok) throw new Error(`Failed to fetch patients for ${date}`);
  return response.json();
}
