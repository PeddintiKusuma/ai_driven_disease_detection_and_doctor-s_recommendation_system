import { useEffect, useState } from 'react';
import { userAPI } from '../api/api';
import { ClipboardList } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';

const typeBadge = (type) => ({
  PREDICTION: 'bg-blue-100 text-blue-800',
  APPOINTMENT: 'bg-purple-100 text-purple-800',
  PRESCRIPTION: 'bg-green-100 text-green-800',
}[type] || 'bg-gray-100 text-gray-800');

export default function MedicalHistory() {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    userAPI.getMedicalHistory()
      .then(res => setHistory(res.data.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner fullScreen />;

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold flex items-center gap-2">
          <ClipboardList className="w-7 h-7 text-primary-600" /> Medical History
        </h1>
        <p className="text-gray-600">Your complete health timeline — predictions, appointments & prescriptions</p>
      </div>

      {history.length === 0 ? (
        <div className="card text-center py-12">
          <ClipboardList className="w-12 h-12 text-gray-300 mx-auto mb-3" />
          <p className="text-gray-500">No medical history yet</p>
        </div>
      ) : (
        <div className="card overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b text-left text-gray-500">
                <th className="pb-3 pr-4">Date</th>
                <th className="pb-3 pr-4">Type</th>
                <th className="pb-3 pr-4">Description</th>
                <th className="pb-3">Doctor</th>
              </tr>
            </thead>
            <tbody>
              {history.map((entry, i) => (
                <tr key={i} className="border-b last:border-0 hover:bg-gray-50">
                  <td className="py-3 pr-4">{entry.date}</td>
                  <td className="py-3 pr-4">
                    <span className={`badge ${typeBadge(entry.type)}`}>{entry.type}</span>
                  </td>
                  <td className="py-3 pr-4 font-medium">{entry.description}</td>
                  <td className="py-3 text-gray-500">{entry.doctorName || '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
