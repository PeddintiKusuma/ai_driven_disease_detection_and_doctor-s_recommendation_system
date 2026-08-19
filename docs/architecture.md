# Architecture Documentation

## System Overview

The AI-Driven Disease Prediction and Doctor Recommendation System is a three-tier architecture running entirely on localhost.

## Component Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    React Frontend                        │
│              http://localhost:5173                       │
│  Pages: Dashboard, Predict, Doctors, Appointments, Admin │
└──────────────────────┬──────────────────────────────────┘
                       │ HTTP REST (Axios)
                       ▼
┌─────────────────────────────────────────────────────────┐
│               Spring Boot Backend                        │
│              http://localhost:8080                       │
│  Controllers → Services → Repositories → MySQL           │
│  Security: JWT + BCrypt + Role-based access              │
└──────────┬──────────────────────────┬───────────────────┘
           │ JDBC                      │ HTTP REST
           ▼                           ▼
┌──────────────────┐      ┌──────────────────────────────┐
│  MySQL Database   │      │    Flask ML Service           │
│  localhost:3306   │      │    http://localhost:5000      │
│  ai_healthcare    │      │    RandomForestClassifier     │
└──────────────────┘      └──────────────────────────────┘
```

## Prediction Flow

1. User selects symptoms in React frontend
2. Frontend sends POST /api/predictions to Spring Boot
3. Spring Boot validates request and forwards to Flask ML service
4. Flask loads local model (disease_model.pkl) and predicts
5. Spring Boot stores prediction in MySQL
6. DoctorRecommendationService finds matching doctors
7. Combined response returned to frontend
8. User views results and can book appointments

## Security Architecture

- **Authentication**: JWT tokens with 24-hour expiration
- **Password Storage**: BCrypt hashing
- **Authorization**: Role-based (USER, DOCTOR, ADMIN)
- **CORS**: Configured for localhost:5173
- **Input Validation**: Jakarta Bean Validation on DTOs

## ML Pipeline

1. **Dataset**: disease_symptoms.csv (synthetic, 20 diseases, 20 symptoms)
2. **Training**: RandomForestClassifier with 100 estimators
3. **Evaluation**: Accuracy, Precision, Recall, F1 Score
4. **Deployment**: joblib serialized model served via Flask

## Doctor Recommendation Algorithm

```
recommendationScore = (rating × 0.5) + (experienceScore × 0.3) + (availabilityScore × 0.2)
```

Doctors are filtered by disease-to-specialization mapping, then sorted by score.
