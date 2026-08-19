import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { predictionAPI } from '../api/api';
import { Stethoscope, Check, Loader2 } from 'lucide-react';
import toast from 'react-hot-toast';
const SYMPTOMS = [
  { id: 'fever', label: 'Fever' },
  { id: 'cough', label: 'Cough' },
  { id: 'fatigue', label: 'Fatigue' },
  { id: 'headache', label: 'Headache' },
  { id: 'nausea', label: 'Nausea' },
  { id: 'vomiting', label: 'Vomiting' },
  { id: 'diarrhea', label: 'Diarrhea' },
  { id: 'chest_pain', label: 'Chest Pain' },
  { id: 'shortness_of_breath', label: 'Shortness of Breath' },
  { id: 'sore_throat', label: 'Sore Throat' },
  { id: 'runny_nose', label: 'Runny Nose' },
  { id: 'joint_pain', label: 'Joint Pain' },
  { id: 'muscle_pain', label: 'Muscle Pain' },
  { id: 'abdominal_pain', label: 'Abdominal Pain' },
  { id: 'dizziness', label: 'Dizziness' },
  { id: 'skin_rash', label: 'Skin Rash' },
  { id: 'itching', label: 'Itching' },
  { id: 'chills', label: 'Chills' },
  { id: 'sweating', label: 'Sweating' },
  { id: 'weakness', label: 'Weakness' },
];

const LOADING_STEPS = [
  'Processing symptoms...',
  'Comparing patterns...',
  'Generating prediction...',
  'Finding suitable specialists...',
];

export default function Predict() {
  const [selected, setSelected] = useState([]);
  const [loading, setLoading] = useState(false);
  const [loadingStep, setLoadingStep] = useState(0);
  const navigate = useNavigate();

  const toggleSymptom = (id) => {
    setSelected(prev => prev.includes(id) ? prev.filter(s => s !== id) : [...prev, id]);
  };

  const handlePredict = async () => {
    if (selected.length === 0) {
      toast.error('Please select at least one symptom');
      return;
    }

    setLoading(true);
    let step = 0;
    const interval = setInterval(() => {
      step = (step + 1) % LOADING_STEPS.length;
      setLoadingStep(step);
    }, 1500);

    try {
      const res = await predictionAPI.predict({ symptoms: selected });
      clearInterval(interval);
      toast.success('Prediction complete!');
      navigate(`/prediction-result/${res.data.data.id}`);
    } catch (err) {
      clearInterval(interval);
      toast.error(err.response?.data?.message || 'Prediction failed');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] animate-fade-in">
        <div className="relative">
          <Loader2 className="w-16 h-16 text-primary-600 animate-spin" />
          <Stethoscope className="w-6 h-6 text-primary-400 absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2" />
        </div>
        <h2 className="text-xl font-semibold mt-6">Analyzing your symptoms...</h2>
        <p className="text-gray-500 mt-2 animate-pulse">{LOADING_STEPS[loadingStep]}</p>
        <div className="flex gap-2 mt-6">
          {LOADING_STEPS.map((_, i) => (
            <div key={i} className={`w-2 h-2 rounded-full transition-colors ${i <= loadingStep ? 'bg-primary-600' : 'bg-gray-300'}`} />
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto space-y-6 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold">AI Preliminary Disease Prediction</h1>
        <p className="text-gray-600">Select the symptoms you are experiencing</p>
      </div>

      <div className="card">
        <h2 className="text-lg font-semibold mb-4 flex items-center gap-2">
          <Stethoscope className="w-5 h-5 text-primary-600" />
          Select Symptoms ({selected.length} selected)
        </h2>
        <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
          {SYMPTOMS.map(({ id, label }) => (
            <button
              key={id}
              onClick={() => toggleSymptom(id)}
              className={`flex items-center gap-2 p-3 rounded-lg border-2 transition-all text-sm font-medium ${
                selected.includes(id)
                  ? 'border-primary-500 bg-primary-50 text-primary-700'
                  : 'border-gray-200 hover:border-gray-300 text-gray-700'
              }`}
            >
              {selected.includes(id) && <Check className="w-4 h-4" />}
              {label}
            </button>
          ))}
        </div>

        <button onClick={handlePredict} disabled={selected.length === 0}
          className="btn-primary w-full mt-6 py-3 text-lg">
          Predict Disease
        </button>
      </div>
    </div>
  );
}
