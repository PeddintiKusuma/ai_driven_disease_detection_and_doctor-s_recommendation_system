"""Tests for ML service."""

import os
import sys
import json
import pytest

sys.path.insert(0, os.path.dirname(__file__))

from train_model import load_and_clean_data, train_model, SYMPTOM_COLUMNS


class TestModelTraining:
    def test_dataset_loads(self):
        df = load_and_clean_data()
        assert len(df) > 0
        assert 'disease' in df.columns
        for col in SYMPTOM_COLUMNS:
            assert col in df.columns

    def test_model_trains(self):
        df = load_and_clean_data()
        model, metadata = train_model(df)
        assert model is not None
        assert metadata['accuracy'] > 0
        assert 'f1_score' in metadata


class TestFlaskApp:
    @pytest.fixture(autouse=True)
    def setup_model(self):
        df = load_and_clean_data()
        model, metadata = train_model(df)
        os.makedirs('models', exist_ok=True)
        import joblib
        joblib.dump(model, 'models/disease_model.pkl')
        with open('models/model_metadata.json', 'w') as f:
            json.dump(metadata, f)

    def test_health_endpoint(self):
        from app import app
        client = app.test_client()
        response = client.get('/health')
        assert response.status_code == 200
        data = response.get_json()
        assert data['status'] == 'healthy'

    def test_predict_valid_symptoms(self):
        from app import app, load_model
        load_model()
        client = app.test_client()
        response = client.post('/predict', json={
            'age': 25,
            'gender': 'MALE',
            'symptoms': ['fever', 'cough', 'fatigue']
        })
        assert response.status_code == 200
        data = response.get_json()
        assert data['success'] is True
        assert 'disease' in data
        assert 'confidence' in data
        assert 'riskLevel' in data

    def test_predict_invalid_symptoms(self):
        from app import app, load_model
        load_model()
        client = app.test_client()
        response = client.post('/predict', json={
            'symptoms': ['invalid_symptom_xyz']
        })
        assert response.status_code == 400
        data = response.get_json()
        assert data['success'] is False

    def test_predict_missing_symptoms(self):
        from app import app, load_model
        load_model()
        client = app.test_client()
        response = client.post('/predict', json={'age': 25})
        assert response.status_code == 400

    def test_model_info(self):
        from app import app, load_model
        load_model()
        client = app.test_client()
        response = client.get('/model-info')
        assert response.status_code == 200
        data = response.get_json()
        assert data['success'] is True
        assert 'accuracy' in data
