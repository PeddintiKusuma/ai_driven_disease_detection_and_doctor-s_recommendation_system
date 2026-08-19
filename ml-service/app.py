"""
Flask ML Service for Disease Prediction

Provides local ML-based disease prediction using scikit-learn.
NO external AI APIs are used.
"""

import os
import json
import joblib
import numpy as np
from flask import Flask, request, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

MODEL_PATH = os.environ.get('MODEL_PATH', 'models/disease_model.pkl')
METADATA_PATH = 'models/model_metadata.json'

SYMPTOM_COLUMNS = [
    'fever', 'cough', 'fatigue', 'headache', 'nausea', 'vomiting',
    'diarrhea', 'chest_pain', 'shortness_of_breath', 'sore_throat',
    'runny_nose', 'joint_pain', 'muscle_pain', 'abdominal_pain',
    'dizziness', 'skin_rash', 'itching', 'chills', 'sweating', 'weakness'
]

SEVERE_SYMPTOMS = ['chest_pain', 'shortness_of_breath']

SEVERE_WARNING = (
    "If you are experiencing severe or emergency symptoms, seek immediate medical attention."
)

RISK_MAPPING = {
    'Influenza': 'MODERATE',
    'Common Cold': 'LOW',
    'Pneumonia': 'HIGH',
    'Asthma': 'MODERATE',
    'Bronchitis': 'MODERATE',
    'Gastroenteritis': 'MODERATE',
    'Migraine': 'MODERATE',
    'Hypertension': 'MODERATE',
    'Heart Disease': 'HIGH',
    'Diabetes': 'MODERATE',
    'Eczema': 'LOW',
    'Allergic Reaction': 'MODERATE',
    'Urinary Tract Infection': 'MODERATE',
    'Anxiety Disorder': 'MODERATE',
    'Depression': 'MODERATE',
    'Conjunctivitis': 'LOW',
    'Sinusitis': 'LOW',
    'Arthritis': 'MODERATE',
    'COVID-19': 'HIGH',
    'Food Poisoning': 'MODERATE',
}

model = None
metadata = None


def load_model():
    global model, metadata
    if not os.path.exists(MODEL_PATH):
        raise FileNotFoundError(
            f"Model not found at {MODEL_PATH}. Run 'python train_model.py' first."
        )
    model = joblib.load(MODEL_PATH)
    if os.path.exists(METADATA_PATH):
        with open(METADATA_PATH, 'r') as f:
            metadata = json.load(f)
    print(f"Model loaded from {MODEL_PATH}")


def symptoms_to_features(symptoms):
    """Convert symptom list to feature vector."""
    features = np.zeros(len(SYMPTOM_COLUMNS))
    normalized = [s.lower().replace(' ', '_').replace('-', '_') for s in symptoms]
    for i, col in enumerate(SYMPTOM_COLUMNS):
        if col in normalized:
            features[i] = 1
    return features.reshape(1, -1)


def get_risk_level(disease, symptoms):
    """Determine risk level based on disease and symptoms."""
    base_risk = RISK_MAPPING.get(disease, 'MODERATE')
    has_severe = any(s.lower().replace(' ', '_') in SEVERE_SYMPTOMS for s in symptoms)
    if has_severe:
        return 'CRITICAL' if base_risk == 'HIGH' else 'HIGH'
    return base_risk


@app.route('/health', methods=['GET'])
def health():
    return jsonify({
        'status': 'healthy',
        'service': 'AI Disease Prediction ML Service',
        'model_loaded': model is not None
    })


@app.route('/model-info', methods=['GET'])
def model_info():
    if metadata is None:
        return jsonify({'success': False, 'message': 'Model metadata not available'}), 503
    return jsonify({
        'success': True,
        'model_type': metadata.get('model_type'),
        'accuracy': metadata.get('accuracy'),
        'precision': metadata.get('precision'),
        'recall': metadata.get('recall'),
        'f1_score': metadata.get('f1_score'),
        'disease_classes': metadata.get('disease_classes', []),
        'symptom_columns': metadata.get('symptom_columns', []),
        'dataset_note': metadata.get('dataset_note')
    })


@app.route('/predict', methods=['POST'])
def predict():
    if model is None:
        return jsonify({'success': False, 'message': 'Model not loaded'}), 503

    data = request.get_json()
    if not data:
        return jsonify({'success': False, 'message': 'Invalid request body'}), 400

    symptoms = data.get('symptoms', [])
    if not symptoms or not isinstance(symptoms, list):
        return jsonify({'success': False, 'message': 'Invalid symptoms - provide a list of symptom names'}), 400

    valid_symptoms = set(SYMPTOM_COLUMNS)
    normalized = [s.lower().replace(' ', '_').replace('-', '_') for s in symptoms]
    invalid = [s for s in normalized if s not in valid_symptoms]
    if invalid:
        return jsonify({
            'success': False,
            'message': f'Invalid symptoms: {", ".join(invalid)}',
            'valid_symptoms': SYMPTOM_COLUMNS
        }), 400

    features = symptoms_to_features(symptoms)
    prediction = model.predict(features)[0]
    probabilities = model.predict_proba(features)[0]
    classes = model.classes_

    sorted_probs = sorted(
        zip(classes, probabilities),
        key=lambda x: x[1],
        reverse=True
    )

    confidence = float(sorted_probs[0][1])
    risk_level = get_risk_level(prediction, symptoms)

    possible_diseases = [
        {'name': name, 'confidence': round(float(prob), 4)}
        for name, prob in sorted_probs[:5]
    ]

    has_severe = any(s in SEVERE_SYMPTOMS for s in normalized)
    warnings = []
    if has_severe:
        warnings.append(SEVERE_WARNING)

    return jsonify({
        'success': True,
        'disease': prediction,
        'confidence': round(confidence, 4),
        'riskLevel': risk_level,
        'possibleDiseases': possible_diseases,
        'warnings': warnings,
        'age': data.get('age'),
        'gender': data.get('gender')
    })


if __name__ == '__main__':
    try:
        load_model()
    except FileNotFoundError as e:
        print(f"Warning: {e}")
        print("Starting server without model. Train model first with: python train_model.py")
    app.run(host='0.0.0.0', port=5000, debug=True)
