# Project Report

## AI-Driven Disease Prediction and Doctor Recommendation System

### Abstract

This project implements a full-stack healthcare application that uses locally trained machine learning models for preliminary disease prediction and recommends appropriate doctors based on the predicted condition. The system runs entirely on localhost using free and open-source technologies.

### Objectives

1. Develop a local ML-based disease prediction system without external AI APIs
2. Implement intelligent doctor recommendation based on disease-specialization mapping
3. Build a complete appointment management system
4. Ensure medical safety with appropriate disclaimers
5. Create a professional healthcare dashboard UI

### Methodology

**Machine Learning:**
- Algorithm: RandomForestClassifier (scikit-learn)
- Features: 20 binary symptom indicators
- Classes: 20 disease categories
- Evaluation: Accuracy, Precision, Recall, F1 Score
- Dataset: Synthetic sample data (60 samples, 3 per disease)

**Backend:**
- Spring Boot 3.2 with Java 21
- JWT authentication with BCrypt password hashing
- RESTful API with DTO pattern
- MySQL persistence with JPA/Hibernate

**Frontend:**
- React 18 with Vite build tool
- Tailwind CSS for responsive design
- Recharts for data visualization
- Role-based routing and protected routes

### Results

- Successfully trained ML model with high accuracy on sample dataset
- Complete user registration, login, and role-based access
- End-to-end prediction flow from symptom input to doctor recommendation
- Appointment booking with double-booking prevention
- PDF report generation for predictions
- Admin analytics dashboard

### Limitations

- Uses synthetic/sample dataset — NOT medically validated
- Predictions are preliminary and for educational purposes only
- Doctor data is fictional demonstration data
- No real-time notifications or email integration

### Conclusion

The system demonstrates a complete full-stack AI healthcare application suitable for academic projects, running entirely on local infrastructure without any paid services or external AI APIs.

### Future Work

- Integration with validated clinical datasets
- Natural language symptom input processing
- Mobile application development
- Real-time notification system
- Multi-hospital network support
