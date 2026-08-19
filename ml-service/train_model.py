"""
Disease Prediction Model Training Script

NOTE: This dataset is SYNTHETIC/SAMPLE data for development and testing purposes only.
It does NOT represent medically validated clinical data.

Trains a RandomForestClassifier on symptom-disease mappings.
"""

import os
import json
import joblib
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import (
    accuracy_score, precision_score, recall_score, f1_score,
    classification_report, confusion_matrix
)

DATASET_PATH = os.path.join('dataset', 'disease_symptoms.csv')
MODEL_DIR = 'models'
MODEL_PATH = os.path.join(MODEL_DIR, 'disease_model.pkl')
METADATA_PATH = os.path.join(MODEL_DIR, 'model_metadata.json')

SYMPTOM_COLUMNS = [
    'fever', 'cough', 'fatigue', 'headache', 'nausea', 'vomiting',
    'diarrhea', 'chest_pain', 'shortness_of_breath', 'sore_throat',
    'runny_nose', 'joint_pain', 'muscle_pain', 'abdominal_pain',
    'dizziness', 'skin_rash', 'itching', 'chills', 'sweating', 'weakness'
]

SEVERE_SYMPTOMS = ['chest_pain', 'shortness_of_breath']

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


def load_and_clean_data():
    """Load dataset and perform basic cleaning."""
    print(f"Loading dataset from {DATASET_PATH}...")
    df = pd.read_csv(DATASET_PATH)

    print(f"Dataset shape: {df.shape}")
    print(f"Diseases: {df['disease'].nunique()}")
    print(f"Samples per disease:\n{df['disease'].value_counts()}")

    # Remove duplicates
    df = df.drop_duplicates()

    # Ensure binary values
    for col in SYMPTOM_COLUMNS:
        df[col] = df[col].astype(int).clip(0, 1)

    return df


def train_model(df):
    """Train RandomForestClassifier and evaluate."""
    X = df[SYMPTOM_COLUMNS]
    y = df['disease']

    # Split - avoid stratify on small datasets (< 50 samples per class min 2)
    try:
        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.2, random_state=42, stratify=y
        )
    except ValueError:
        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.2, random_state=42
        )

    print("\nTraining RandomForestClassifier...")
    model = RandomForestClassifier(
        n_estimators=100,
        max_depth=10,
        random_state=42,
        class_weight='balanced'
    )
    model.fit(X_train, y_train)

    y_pred = model.predict(X_test)

    accuracy = accuracy_score(y_test, y_pred)
    precision = precision_score(y_test, y_pred, average='weighted', zero_division=0)
    recall = recall_score(y_test, y_pred, average='weighted', zero_division=0)
    f1 = f1_score(y_test, y_pred, average='weighted', zero_division=0)

    min_class_count = y.value_counts().min()
    cv_folds = min(3, min_class_count, len(y) // 2)
    if cv_folds >= 2:
        cv_scores = cross_val_score(model, X, y, cv=cv_folds, scoring='accuracy')
    else:
        cv_scores = np.array([accuracy])

    print("\n" + "=" * 50)
    print("MODEL EVALUATION RESULTS")
    print("=" * 50)
    print(f"Accuracy:  {accuracy:.4f} ({accuracy*100:.2f}%)")
    print(f"Precision: {precision:.4f} ({precision*100:.2f}%)")
    print(f"Recall:    {recall:.4f} ({recall*100:.2f}%)")
    print(f"F1 Score:  {f1:.4f} ({f1*100:.2f}%)")
    print(f"CV Accuracy: {cv_scores.mean():.4f} (+/- {cv_scores.std()*2:.4f})")
    print("\nClassification Report:")
    print(classification_report(y_test, y_pred, zero_division=0))
    print("\nConfusion Matrix:")
    print(confusion_matrix(y_test, y_pred))

    metadata = {
        'model_type': 'RandomForestClassifier',
        'accuracy': round(accuracy, 4),
        'precision': round(precision, 4),
        'recall': round(recall, 4),
        'f1_score': round(f1, 4),
        'cv_accuracy_mean': round(cv_scores.mean(), 4),
        'cv_accuracy_std': round(cv_scores.std(), 4),
        'n_estimators': 100,
        'max_depth': 10,
        'symptom_columns': SYMPTOM_COLUMNS,
        'disease_classes': sorted(y.unique().tolist()),
        'risk_mapping': RISK_MAPPING,
        'dataset_note': 'SYNTHETIC/SAMPLE data for development only - NOT medically validated'
    }

    return model, metadata


def save_model(model, metadata):
    """Save trained model and metadata."""
    os.makedirs(MODEL_DIR, exist_ok=True)
    joblib.dump(model, MODEL_PATH)
    with open(METADATA_PATH, 'w') as f:
        json.dump(metadata, f, indent=2)
    print(f"\nModel saved to {MODEL_PATH}")
    print(f"Metadata saved to {METADATA_PATH}")


def main():
    print("=" * 50)
    print("AI Disease Prediction - Model Training")
    print("NOTE: Using SYNTHETIC sample dataset")
    print("=" * 50)

    df = load_and_clean_data()
    model, metadata = train_model(df)
    save_model(model, metadata)

    print("\nTraining complete!")


if __name__ == '__main__':
    main()
