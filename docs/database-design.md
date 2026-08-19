# Database Design

## Database: ai_healthcare

### Entity Relationship

```
users ──┬── predictions ── prediction_symptoms
        ├── appointments ── doctors ── specializations
        └── reviews

diseases ── disease_symptoms ── symptoms
```

### Tables

| Table | Description | Key Fields |
|-------|-------------|------------|
| users | System users | email, password, role |
| doctors | Doctor profiles | specialization_id, rating, experience |
| specializations | Medical specializations | name |
| symptoms | Symptom catalog | name |
| diseases | Disease catalog | name, severity_level |
| predictions | ML prediction results | predicted_disease, confidence, risk_level |
| prediction_symptoms | Symptoms per prediction | symptom_name |
| appointments | Doctor appointments | doctor_id, date, time, status |
| reviews | Doctor reviews | rating, comment |
| notifications | User notifications | title, message, read_flag |
| audit_logs | System audit trail | action, entity_type |

### Indexes

- users: email, role
- doctors: specialization_id, rating, city
- predictions: user_id, predicted_disease, created_at
- appointments: user_id, doctor_id, status
- Unique constraint: (doctor_id, appointment_date, appointment_time) for double-booking prevention

### Sample Data

- 13 specializations
- 20 symptoms
- 20 diseases
- 12 demo doctors (fictional)
- 1 admin user

See `database/schema.sql` and `database/seed.sql`.
