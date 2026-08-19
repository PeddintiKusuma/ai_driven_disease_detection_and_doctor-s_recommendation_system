import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { predictionAPI } from '../api/api';
import { Activity, Eye, Printer, Calendar, ArrowLeft, AlertTriangle } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';
import DoctorCard from '../components/DoctorCard';
import { viewPdf, printPdf } from '../utils/pdfUtils';
import toast from 'react-hot-toast';

export default function PredictionResult() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [prediction, setPrediction] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    predictionAPI.getById(id)
      .then(res => setPrediction(res.data.data))
      .catch(() => toast.error('Failed to load prediction'))
      .finally(() => setLoading(false));
  }, [id]);

  const handleView = async () => {
    try {
      await viewPdf(() => predictionAPI.downloadReport(id));
    } catch {
      toast.error('Failed to open report');
    }
  };

  const handlePrint = async () => {
    try {
      await printPdf(() => predictionAPI.downloadReport(id));
    } catch {
      toast.error('Failed to print report');
    }
  };

  const handleBook = (doctor) => {
    navigate('/appointments', { state: { doctor, predictionId: id } });
  };

  const riskBadge = (level) => {
    const map = { LOW: 'badge-low', MODERATE: 'badge-moderate', HIGH: 'badge-high', CRITICAL: 'badge-critical' };
    return map[level] || 'badge-moderate';
  };

  if (loading) return <LoadingSpinner fullScreen />;
  if (!prediction) return <div className="text-center py-12">Prediction not found</div>;

  return (
    <div className="max-w-4xl mx-auto space-y-6 animate-fade-in">
      <button onClick={() => navigate(-1)} className="flex items-center gap-2 text-gray-600 hover:text-gray-900">
        <ArrowLeft className="w-4 h-4" /> Back
      </button>

      {prediction.warnings?.length > 0 && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 flex gap-3">
          <AlertTriangle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
          <div>
            {prediction.warnings.map((w, i) => (
              <p key={i} className="text-sm text-red-700 font-medium">{w}</p>
            ))}
          </div>
        </div>
      )}

      {/* Main Result */}
      <div className="card bg-gradient-to-br from-primary-50 to-white border-primary-100">
        <div className="flex items-center gap-3 mb-6">
          <div className="w-12 h-12 bg-primary-100 rounded-xl flex items-center justify-center">
            <Activity className="w-6 h-6 text-primary-600" />
          </div>
          <div>
            <p className="text-sm text-primary-600 font-medium">AI Preliminary Prediction</p>
            <h1 className="text-2xl font-bold">{prediction.predictedDisease}</h1>
          </div>
        </div>

        <div className="grid md:grid-cols-3 gap-4 mb-6">
          <div className="bg-white rounded-lg p-4 text-center">
            <p className="text-sm text-gray-500">Confidence</p>
            <p className="text-3xl font-bold text-primary-600">
              {(prediction.confidence * 100).toFixed(0)}%
            </p>
          </div>
          <div className="bg-white rounded-lg p-4 text-center">
            <p className="text-sm text-gray-500">Risk Level</p>
            <span className={`badge text-base mt-1 ${riskBadge(prediction.riskLevel)}`}>
              {prediction.riskLevel}
            </span>
          </div>
          <div className="bg-white rounded-lg p-4 text-center">
            <p className="text-sm text-gray-500">Recommended Specialist</p>
            <p className="text-lg font-semibold mt-1">{prediction.recommendedSpecialization}</p>
          </div>
        </div>

        {/* Symptoms */}
        <div className="mb-6">
          <p className="text-sm font-medium text-gray-600 mb-2">Reported Symptoms</p>
          <div className="flex flex-wrap gap-2">
            {prediction.symptoms?.map(s => (
              <span key={s} className="bg-white px-3 py-1 rounded-full text-sm border">{s.replace(/_/g, ' ')}</span>
            ))}
          </div>
        </div>

        {/* Possible Conditions */}
        {prediction.possibleDiseases?.length > 0 && (
          <div className="mb-6">
            <p className="text-sm font-medium text-gray-600 mb-3">Possible Conditions</p>
            <div className="space-y-2">
              {prediction.possibleDiseases.map(({ name, confidence }) => (
                <div key={name} className="flex items-center gap-3">
                  <span className="text-sm w-40 truncate">{name}</span>
                  <div className="flex-1 bg-gray-200 rounded-full h-2.5">
                    <div className="bg-primary-600 h-2.5 rounded-full" style={{ width: `${confidence * 100}%` }} />
                  </div>
                  <span className="text-sm font-medium w-12 text-right">{(confidence * 100).toFixed(0)}%</span>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Diet Plan - Dos and Don'ts */}
        {prediction.dietPlan && (
          <div className="mb-6">
            <p className="text-sm font-medium text-gray-600 mb-3">Diet Plan — Do's & Don'ts</p>
            <div className="grid md:grid-cols-2 gap-4">
              <div className="bg-primary-50 border border-primary-200 rounded-xl p-4">
                <p className="font-semibold text-primary-800 mb-2">✅ Do's</p>
                <ul className="space-y-1">
                  {prediction.dietPlan.dos?.split('|').map((item, i) => (
                    <li key={i} className="text-sm text-primary-700 flex items-start gap-2">
                      <span>•</span>{item.trim()}
                    </li>
                  ))}
                </ul>
              </div>
              <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                <p className="font-semibold text-red-800 mb-2">❌ Don'ts</p>
                <ul className="space-y-1">
                  {prediction.dietPlan.donts?.split('|').map((item, i) => (
                    <li key={i} className="text-sm text-red-700 flex items-start gap-2">
                      <span>•</span>{item.trim()}
                    </li>
                  ))}
                </ul>
              </div>
            </div>
            {prediction.dietPlan.dietRecommendations && (
              <div className="mt-3 bg-blue-50 border border-blue-200 rounded-lg p-4">
                <p className="font-semibold text-blue-800 mb-2">🍽 Recommended Foods</p>
                <p className="text-sm text-blue-700">{prediction.dietPlan.dietRecommendations.replace(/\|/g, ' · ')}</p>
              </div>
            )}
            {prediction.dietPlan.generalAdvice && (
              <p className="text-xs text-gray-500 mt-2 italic">{prediction.dietPlan.generalAdvice}</p>
            )}
            <p className="text-xs text-primary-700 bg-primary-50 border border-primary-200 rounded-xl p-2 mt-2">
              ⚠️ This is general educational guidance only — not a personalized medical prescription. Consult a doctor for proper advice.
            </p>
          </div>
        )}

        <div className="flex gap-2 flex-wrap">
          <button onClick={handleView} className="btn-secondary flex items-center gap-2">
            <Eye className="w-4 h-4" /> View Report
          </button>
          <button onClick={handlePrint} className="btn-primary flex items-center gap-2">
            <Printer className="w-4 h-4" /> Print Report
          </button>
        </div>
      </div>

      {/* Recommended Doctors */}
      {prediction.recommendedDoctors?.length > 0 && (
        <div>
          <h2 className="text-lg font-semibold mb-4 flex items-center gap-2">
            <Calendar className="w-5 h-5" /> Recommended Doctors
          </h2>
          <div className="grid md:grid-cols-2 gap-4">
            {prediction.recommendedDoctors.map(d => (
              <DoctorCard key={d.id} doctor={d} onBook={handleBook} />
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
