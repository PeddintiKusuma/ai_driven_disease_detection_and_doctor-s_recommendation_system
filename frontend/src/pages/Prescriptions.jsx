import { useEffect, useState } from 'react';
import { prescriptionAPI } from '../api/api';
import { FileText, Eye, Printer } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';
import { viewPdf, printPdf } from '../utils/pdfUtils';
import toast from 'react-hot-toast';

export default function Prescriptions() {
  const [prescriptions, setPrescriptions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    prescriptionAPI.getAll()
      .then(res => setPrescriptions(res.data.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const handleView = async (id) => {
    try {
      await viewPdf(() => prescriptionAPI.download(id));
    } catch {
      toast.error('Failed to open prescription');
    }
  };

  const handlePrint = async (id) => {
    try {
      await printPdf(() => prescriptionAPI.download(id));
    } catch {
      toast.error('Failed to print prescription');
    }
  };

  if (loading) return <LoadingSpinner fullScreen />;

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold flex items-center gap-2">
          <FileText className="w-7 h-7 text-primary-600" /> My Prescriptions
        </h1>
        <p className="text-gray-600">View and print prescriptions given by your doctors</p>
      </div>

      {prescriptions.length === 0 ? (
        <div className="card text-center py-12">
          <FileText className="w-12 h-12 text-gray-300 mx-auto mb-3" />
          <p className="text-gray-500">No prescriptions yet</p>
          <p className="text-sm text-gray-400 mt-1">Prescriptions appear after your doctor completes a consultation</p>
        </div>
      ) : (
        <div className="space-y-4">
          {prescriptions.map(p => (
            <div key={p.id} className="card">
              <div className="flex items-start justify-between flex-wrap gap-3">
                <div>
                  <p className="font-semibold text-lg">Dr. {p.doctorName?.replace('Dr. ', '')}</p>
                  <p className="text-sm text-gray-500">{new Date(p.createdAt).toLocaleDateString()}</p>
                  {p.diagnosis && <p className="mt-2"><span className="font-medium">Diagnosis:</span> {p.diagnosis}</p>}
                  <p className="mt-2 text-sm whitespace-pre-line"><span className="font-medium">Medicines:</span> {p.medicines}</p>
                  {p.instructions && <p className="mt-1 text-sm text-gray-600">{p.instructions}</p>}
                </div>
                <div className="flex gap-2">
                  <button onClick={() => handleView(p.id)} className="btn-secondary flex items-center gap-2 text-sm">
                    <Eye className="w-4 h-4" /> View
                  </button>
                  <button onClick={() => handlePrint(p.id)} className="btn-primary flex items-center gap-2 text-sm">
                    <Printer className="w-4 h-4" /> Print
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
