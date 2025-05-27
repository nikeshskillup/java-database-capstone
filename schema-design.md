# Schema Design for Smart Clinic Management System

## MySQL Database Design

The relational portion of the Smart Clinic Management System handles structured and interrelated data, such as patients, doctors, appointments, and prescriptions.

---

### Table: patients
- `id`: INT, PRIMARY KEY, AUTO_INCREMENT
- `first_name`: VARCHAR(100), NOT NULL
- `last_name`: VARCHAR(100), NOT NULL
- `email`: VARCHAR(150), UNIQUE, NOT NULL
- `phone_number`: VARCHAR(15), NOT NULL
- `date_of_birth`: DATE
- `gender`: ENUM('Male', 'Female', 'Other')
- `address`: TEXT

_Comments:_
- Email must be unique for identification and communication.
- Phone format and validations will be handled at the application level.

---

### Table: doctors
- `id`: INT, PRIMARY KEY, AUTO_INCREMENT
- `first_name`: VARCHAR(100), NOT NULL
- `last_name`: VARCHAR(100), NOT NULL
- `email`: VARCHAR(150), UNIQUE, NOT NULL
- `specialization`: VARCHAR(100)
- `phone_number`: VARCHAR(15)
- `available_from`: TIME
- `available_to`: TIME

_Comments:_
- Doctor availability could later be expanded into a separate `schedules` table.

---

### Table: appointments
- `id`: INT, PRIMARY KEY, AUTO_INCREMENT
- `patient_id`: INT, FOREIGN KEY REFERENCES patients(id) ON DELETE CASCADE
- `doctor_id`: INT, FOREIGN KEY REFERENCES doctors(id) ON DELETE SET NULL
- `appointment_time`: DATETIME, NOT NULL
- `status`: INT, DEFAULT 0  
  *(0 = Scheduled, 1 = Completed, 2 = Cancelled)*
- `notes`: TEXT

_Comments:_
- If a patient is deleted, their appointments are also removed.
- Doctors can be retained even if deleted by keeping their ID nullable in past appointments.

---

### Table: prescriptions
- `id`: INT, PRIMARY KEY, AUTO_INCREMENT
- `appointment_id`: INT, FOREIGN KEY REFERENCES appointments(id) ON DELETE CASCADE
- `issued_date`: DATE, NOT NULL
- `medication`: VARCHAR(255), NOT NULL
- `dosage`: VARCHAR(100)
- `instructions`: TEXT

_Comments:_
- Prescriptions are linked to specific appointments.
- For flexibility and extended metadata, a MongoDB version of prescription data is used (see below).

---

## MongoDB Collection Design

Unstructured or semi-structured data that may evolve over time or doesn’t fit neatly into relational schemas is stored in MongoDB.

---

### Collection: prescriptions

```json
{
  "_id": "ObjectId('664bc123456abcde7890')",
  "patientId": 101,
  "appointmentId": 51,
  "medication": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "instructions": "Take 1 tablet every 6 hours after meals"
    },
    {
      "name": "Vitamin C",
      "dosage": "1000mg",
      "instructions": "Once daily in the morning"
    }
  ],
  "doctorNotes": "Monitor temperature daily. Revisit if fever persists beyond 3 days.",
  "refillCount": 2,
  "pharmacy": {
    "name": "WellMed Central Pharmacy",
    "location": "Downtown Clinic, 2nd Floor"
  },
  "tags": ["flu", "urgent", "fever"],
  "issuedAt": "2025-05-26T10:30:00Z"
}
