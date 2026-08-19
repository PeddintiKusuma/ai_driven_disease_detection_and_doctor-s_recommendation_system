import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { predictionAPI } from '../api/api';
import { History, Eye } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';

export default function PredictionHistory() {
  const [predictions, setPredictions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    predictionAPI.getHistory()
      .then(res => setPredictions(res.data.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const riskBadge = (level) => {
    const map = { LOW: 'badge-low', MODERATE: 'badge-moderate', HIGH: 'badge-high', CRITICAL: 'badge-critical' };
    return map[level] || 'badge-moderate';
  };

  if (loading) return <LoadingSpinner fullScreen />;

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold">Prediction History</h1>
        <p className="text-gray-600">View your past AI predictions</p>
      </div>

      {predictions.length === 0 ? (
        <div className="card text-center py-12">
          <History className="w-12 h-12 text-gray-300 mx-auto mb-3" />
          <p className="text-gray-500">No predictions yet</p>
          <Link to="/predict" className="btn-primary inline-block mt-4">Start Prediction</Link>
        </div>
      ) : (
        <div className="card overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b text-left text-gray-500">
                <th className="pb-3 pr-4">Date</th>
                <th className="pb-3 pr-4">Symptoms</th>
                <th className="pb-3 pr-4">Prediction</th>
                <th className="pb-3 pr-4">Confidence</th>
                <th className="pb-3 pr-4">Risk</th>
                <th className="pb-3 pr-4">Specialist</th>
                <th className="pb-3">Action</th>
              </tr>
            </thead>
            <tbody>
              {predictions.map(p => (
                <tr key={p.id} className="border-b last:border-0 hover:bg-gray-50">
                  <td className="py-3 pr-4">{new Date(p.createdAt).toLocaleDateString()}</td>
                  <td className="py-3 pr-4 max-w-[150px] truncate">{p.symptoms?.join(', ')}</td>
                  <td className="py-3 pr-4 font-medium">{p.predictedDisease}</td>
                  <td className="py-3 pr-4">{(p.confidence * 100).toFixed(0)}%</td>
                  <td className="py-3 pr-4"><span className={`badge ${riskBadge(p.riskLevel)}`}>{p.riskLevel}</span></td>
                  <td className="py-3 pr-4">{p.recommendedSpecialization}</td>
                  <td className="py-3">
                    <Link to={`/prediction-result/${p.id}`} className="text-primary-600 hover:underline flex items-center gap-1">
                      <Eye className="w-4 h-4" /> View
                    </Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
