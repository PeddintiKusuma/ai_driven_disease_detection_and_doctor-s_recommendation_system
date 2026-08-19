import { useEffect, useState } from 'react';
import { adminAPI } from '../api/api';
import { Activity } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';

export default function AdminPredictions() {
  const [predictions, setPredictions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminAPI.getPredictions()
      .then(res => setPredictions(res.data.data || []))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner fullScreen />;

  return (
    <div className="space-y-6 animate-fade-in">
      <h1 className="text-2xl font-bold">Predictions Overview</h1>
      {predictions.length === 0 ? (
        <div className="card text-center py-12">
          <Activity className="w-12 h-12 text-gray-300 mx-auto mb-3" />
          <p className="text-gray-500">No predictions recorded yet</p>
        </div>
      ) : (
        <div className="card overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b text-left text-gray-500">
                <th className="pb-3 pr-4">Date</th>
                <th className="pb-3 pr-4">Disease</th>
                <th className="pb-3 pr-4">Confidence</th>
                <th className="pb-3">Risk</th>
              </tr>
            </thead>
            <tbody>
              {predictions.map(p => (
                <tr key={p.id} className="border-b last:border-0">
                  <td className="py-3 pr-4">{new Date(p.createdAt).toLocaleDateString()}</td>
                  <td className="py-3 pr-4 font-medium">{p.predictedDisease}</td>
                  <td className="py-3 pr-4">{(p.confidence * 100).toFixed(0)}%</td>
                  <td className="py-3">{p.riskLevel}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
