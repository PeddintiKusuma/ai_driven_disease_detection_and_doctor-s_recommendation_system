import { useEffect, useState } from 'react';
import { adminAPI } from '../api/api';
import { Check, X } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';
import toast from 'react-hot-toast';

const statusBadge = (status) => ({
  APPROVED: 'bg-green-100 text-green-800',
  PENDING: 'bg-yellow-100 text-yellow-800',
  REJECTED: 'bg-red-100 text-red-800',
}[status] || 'bg-gray-100 text-gray-800');

export default function AdminDoctors() {
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL');

  const load = () => {
    adminAPI.getDoctors()
      .then(res => setDoctors(res.data.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleApprove = async (id) => {
    try {
      await adminAPI.approveDoctor(id);
      toast.success('Doctor approved');
      load();
    } catch { toast.error('Failed to approve'); }
  };

  const handleReject = async (id) => {
    try {
      await adminAPI.rejectDoctor(id);
      toast.success('Doctor rejected');
      load();
    } catch { toast.error('Failed to reject'); }
  };

  const filtered = filter === 'ALL' ? doctors :
    doctors.filter(d => d.approvalStatus === filter);

  if (loading) return <LoadingSpinner fullScreen />;

  const pendingCount = doctors.filter(d => d.approvalStatus === 'PENDING').length;

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold">Doctors Management</h1>
        <p className="text-sm text-gray-500">Approve, reject, and manage doctor profiles</p>
      </div>

      {pendingCount > 0 && (
        <div className="card bg-yellow-50 border-yellow-200">
          <p className="text-yellow-800 font-medium">{pendingCount} doctor(s) pending approval</p>
        </div>
      )}

      <div className="flex gap-2">
        {['ALL', 'PENDING', 'APPROVED', 'REJECTED'].map(f => (
          <button key={f} onClick={() => setFilter(f)}
            className={`px-3 py-1.5 rounded-lg text-sm font-medium ${
              filter === f ? 'bg-primary-600 text-white' : 'bg-gray-100 text-gray-700'
            }`}>
            {f}
          </button>
        ))}
      </div>

      <div className="card overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b text-left text-gray-500">
              <th className="pb-3 pr-4">Name</th>
              <th className="pb-3 pr-4">Specialization</th>
              <th className="pb-3 pr-4">Hospital</th>
              <th className="pb-3 pr-4">Rating</th>
              <th className="pb-3 pr-4">Fee</th>
              <th className="pb-3 pr-4">Status</th>
              <th className="pb-3">Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map(d => (
              <tr key={d.id} className="border-b last:border-0 hover:bg-gray-50">
                <td className="py-3 pr-4 font-medium">{d.name}</td>
                <td className="py-3 pr-4">{d.specialization}</td>
                <td className="py-3 pr-4">{d.hospital}</td>
                <td className="py-3 pr-4">{d.rating}</td>
                <td className="py-3 pr-4">₹{d.consultationFee}</td>
                <td className="py-3 pr-4">
                  <span className={`badge ${statusBadge(d.approvalStatus)}`}>{d.approvalStatus || 'APPROVED'}</span>
                </td>
                <td className="py-3">
                  {d.approvalStatus === 'PENDING' && (
                    <div className="flex gap-1">
                      <button onClick={() => handleApprove(d.id)}
                        className="p-1.5 bg-green-100 text-green-700 rounded hover:bg-green-200">
                        <Check className="w-4 h-4" />
                      </button>
                      <button onClick={() => handleReject(d.id)}
                        className="p-1.5 bg-red-100 text-red-700 rounded hover:bg-red-200">
                        <X className="w-4 h-4" />
                      </button>
                    </div>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
