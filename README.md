# AI-Driven Disease Prediction and Doctor Recommendation System

A complete full-stack healthcare application that uses **local machine learning** for disease prediction and recommends doctors based on predicted conditions. **100% free to develop and run locally** — no paid APIs, no subscriptions, no credit cards required.

## Features

- **AI Preliminary Disease Prediction** — Local RandomForest ML model (scikit-learn)
- **Doctor Recommendation** — Smart matching by disease, specialization, rating, experience
- **Appointment Booking** — With double-booking prevention
- **JWT Authentication** — Role-based access (USER, DOCTOR, ADMIN)
- **PDF Reports** — Download prediction reports (iText)
- **Admin Dashboard** — Analytics and system management
- **Doctor Dashboard** — Accept/reject/complete appointments
- **Responsive UI** — Modern healthcare dashboard with charts

## Architecture

```
React Frontend (5173) → Spring Boot Backend (8080) → MySQL (3306)
                                                    → Flask ML Service (5000)
```

## Technology Stack

| Layer | Technologies |
|-------|-------------|
| Frontend | React, Vite, Tailwind CSS, Recharts, Axios |
| Backend | Java 21, Spring Boot, Spring Security, JWT, JPA |
| Database | MySQL Community Edition |
| ML | Python 3.11+, Flask, scikit-learn, pandas |

## Project Structure

```
├── frontend/          # React + Vite application
├── backend/           # Spring Boot REST API
├── ml-service/        # Flask ML prediction service
├── database/          # SQL schema and seed data
├── docs/              # Documentation
├── docker-compose.yml # MySQL container setup
└── README.md
```

## Prerequisites

- Java JDK 21
- Maven 3.8+
- Node.js 18+
- Python 3.11+
- MySQL Community Edition 8.0
- Git

## Setup Instructions

### Step 1: Database

**Option A — Docker:**
```bash
docker-compose up -d
```

**Option B — Manual MySQL:**
```sql
CREATE DATABASE ai_healthcare;
```
Then run:
```bash
mysql -u root -p ai_healthcare < database/schema.sql
mysql -u root -p ai_healthcare < database/seed.sql
```

### Step 2: ML Service

```bash
cd ml-service
python -m venv venv
venv\Scripts\activate        # Windows
pip install -r requirements.txt
python train_model.py          # Train the model
python app.py                  # Start on http://localhost:5000
```

### Step 3: Backend

```bash
cd backend
# Set environment variables or edit application.yml
mvn clean install
mvn spring-boot:run            # Start on http://localhost:8080
```

### Step 4: Frontend

```bash
cd frontend
npm install
npm run dev                    # Start on http://localhost:5173
```

## Environment Variables

Copy `.env.example` and configure:

```
DB_URL=jdbc:mysql://localhost:3306/ai_healthcare
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_secret_key_at_least_256_bits
ML_API_URL=http://localhost:5000
VITE_API_URL=http://localhost:8080/api
```

## Default Accounts

| Role | Email | Password | Notes |
|------|-------|----------|-------|
| Admin | admin@aihealthcare.com | Admin@123 | Full system access |
| Doctor | rajesh.kumar@demo-hospital.com | Doctor@123 | Doctor dashboard — all demo doctors use same password |
| Patient (User) | — | — | Register via `/register` page |

**Doctor login:** Doctors cannot self-register. Demo doctor accounts are auto-created by the backend on startup and linked to doctor profiles. Any doctor email from the sample data works with password `Doctor@123`.

**Patient login:** Use the Register page to create a USER account, then sign in.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register user |
| POST | /api/auth/login | Login |
| POST | /api/predictions | Create prediction |
| GET | /api/predictions | Prediction history |
| GET | /api/doctors | List doctors |
| POST | /api/appointments | Book appointment |
| GET | /api/admin/stats | Admin statistics |

See `docs/api-documentation.md` for full API reference.

## Testing

```bash
# Backend
cd backend && mvn clean test

# ML Service
cd ml-service && pytest test_ml_service.py -v

# Frontend
cd frontend && npm run build
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| ML service "Model not found" | Run `python train_model.py` first |
| Backend connection refused | Ensure MySQL is running on port 3306 |
| CORS errors | Check frontend runs on port 5173 |
| JWT errors | Set JWT_SECRET in environment |

## Future Enhancements

- Real clinical dataset integration
- Multi-language support
- Email notifications
- Telemedicine video calls
- Mobile app (React Native)

## License

Educational project — suitable for B.Tech Major Project, Capstone, and Portfolio.
