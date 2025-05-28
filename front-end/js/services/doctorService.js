const baseUrl = "http://localhost:8080/api/doctors";

export async function getDoctors() {
  const res = await fetch(baseUrl);
  return res.ok ? res.json() : [];
}

export async function saveDoctor(doctor, token) {
  const res = await fetch(baseUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`
    },
    body: JSON.stringify(doctor)
  });
  return res.json();
}

export async function deleteDoctor(id, token) {
  await fetch(`${baseUrl}/${id}`, {
    method: "DELETE",
    headers: { Authorization: `Bearer ${token}` }
  });
}

export async function filterDoctors(query) {
  const res = await fetch(`${baseUrl}?search=${query}`);
  return res.ok ? res.json() : [];
}
