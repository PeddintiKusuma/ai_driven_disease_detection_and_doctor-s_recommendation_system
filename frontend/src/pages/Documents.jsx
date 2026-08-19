import { useEffect, useState } from 'react';
import { documentAPI, predictionAPI, appointmentAPI, prescriptionAPI } from '../api/api';
import { FileText, Eye, Printer } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';
import { viewPdf, printPdf } from '../utils/pdfUtils';
import toast from 'react-hot-toast';

const typeConfig = {
  PREDICTION: { icon: '🔬', color: 'bg-blue-50 text-blue-700', label: 'Prediction Report' },
  APPOINTMENT: { icon: '📅', color: 'bg-purple-50 text-purple-700', label: 'Appointment Letter' },
  PRESCRIPTION: { icon: '💊', color: 'bg-primary-50 text-primary-700', label: 'Prescription' },
};

export default function Documents() {
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    documentAPI.getAll()
      .then(res => setDocuments(res.data.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const getFetchFn = (doc) => {
    if (doc.type === 'PREDICTION') return () => predictionAPI.downloadReport(doc.id);
    if (doc.type === 'APPOINTMENT') return () => appointmentAPI.downloadLetter(doc.id);
    if (doc.type === 'PRESCRIPTION') return () => prescriptionAPI.download(doc.id);
    return null;
  };

  const handleView = async (doc) => {
    try {
      const fetchFn = getFetchFn(doc);
      if (!fetchFn) return;
      await viewPdf(fetchFn);
    } catch {
      toast.error('Failed to open document');
    }
  };

  const handlePrint = async (doc) => {
    try {
      const fetchFn = getFetchFn(doc);
      if (!fetchFn) return;
      await printPdf(fetchFn);
    } catch {
      toast.error('Failed to print document');
    }
  };

  if (loading) return <LoadingSpinner fullScreen />;

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold flex items-center gap-2">
          <FileText className="w-7 h-7 text-primary-600" /> My Documents
        </h1>
        <p className="text-gray-600">View and print your prediction reports, appointment letters & prescriptions</p>
      </div>

      {documents.length === 0 ? (
        <div className="card text-center py-12">
          <FileText className="w-12 h-12 text-gray-300 mx-auto mb-3" />
          <p className="text-gray-500">No documents yet. Start by making a prediction or booking an appointment.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {documents.map(doc => {
            const cfg = typeConfig[doc.type] || typeConfig.PREDICTION;
            return (
              <div key={`${doc.type}-${doc.id}`} className="card flex items-center justify-between flex-wrap gap-3">
                <div className="flex items-center gap-3">
                  <div className={`w-10 h-10 rounded-lg flex items-center justify-center text-xl ${cfg.color}`}>
                    {cfg.icon}
                  </div>
                  <div>
                    <p className="font-medium">{doc.title}</p>
                    <p className="text-sm text-gray-500">{doc.description}</p>
                    <p className="text-xs text-gray-400">{new Date(doc.createdAt).toLocaleDateString()}</p>
                  </div>
                </div>
                <div className="flex gap-2">
                  <button onClick={() => handleView(doc)} className="btn-secondary text-sm py-2 flex items-center gap-1">
                    <Eye className="w-4 h-4" /> View
                  </button>
                  <button onClick={() => handlePrint(doc)} className="btn-primary text-sm py-2 flex items-center gap-1">
                    <Printer className="w-4 h-4" /> Print
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
