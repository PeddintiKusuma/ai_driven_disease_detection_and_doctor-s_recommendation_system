# API Documentation

Base URL: `http://localhost:8080/api`

## Authentication

All protected endpoints require: `Authorization: Bearer <jwt_token>`

### POST /auth/register
Register a new user account.

**Request:**
```json
{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "age": 25,
  "gender": "MALE"
}
```

### POST /auth/login
**Request:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbG...",
    "user": { "id": 1, "email": "...", "role": "USER" }
  }
}
```

## Predictions

### POST /predictions
Create a new disease prediction.

**Request:**
```json
{
  "age": 25,
  "gender": "MALE",
  "symptoms": ["fever", "cough", "fatigue"]
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "predictedDisease": "Influenza",
    "confidence": 0.87,
    "riskLevel": "MODERATE",
    "recommendedSpecialization": "General Physician",
    "possibleDiseases": [
      { "name": "Influenza", "confidence": 0.87 },
      { "name": "Common Cold", "confidence": 0.65 }
    ],
    "recommendedDoctors": [...],
    "warnings": []
  }
}
```

### GET /predictions
Get user's prediction history.

### GET /predictions/{id}
Get specific prediction details.

### GET /predictions/{id}/report
Download PDF report.

## Doctors

### GET /doctors
List all doctors (public).

### GET /doctors/{id}
Get doctor profile with reviews.

### GET /doctors/search?specialization=Cardiologist
Search doctors by specialization.

## Appointments

### POST /appointments
**Request:**
```json
{
  "doctorId": 1,
  "predictionId": 1,
  "appointmentDate": "2026-08-15",
  "appointmentTime": "10:00",
  "reason": "Follow-up consultation",
  "notes": "Optional notes"
}
```

### GET /appointments
Get user's appointments.

### PATCH /appointments/{id}/status
Update appointment status.

## Admin

### GET /admin/stats
System statistics (ADMIN only).

### GET /admin/users
List all users (ADMIN only).

## Doctor Dashboard

### GET /doctor/appointments
Get doctor's appointments (DOCTOR only).

### PATCH /doctor/appointments/{id}/accept
Accept appointment.

### PATCH /doctor/appointments/{id}/reject
Reject appointment.

### PATCH /doctor/appointments/{id}/complete
Mark appointment complete.

## ML Service (Flask)

Base URL: `http://localhost:5000`

### GET /health
Health check.

### POST /predict
Direct ML prediction (used internally by backend).

### GET /model-info
Model metadata and accuracy metrics.

## Error Responses

```json
{
  "success": false,
  "message": "Invalid symptoms",
  "status": 400
}
```
