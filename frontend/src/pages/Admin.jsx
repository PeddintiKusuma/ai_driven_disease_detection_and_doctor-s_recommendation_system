import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { adminAPI } from '../api/api';
import { Users, Stethoscope, Activity, Calendar, ArrowRight, AlertCircle } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';

export default function Admin() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminAPI.getStats()
      .then(res => setStats(res.data.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner fullScreen />;

  const cards = [
    { icon: Users, label: 'Users', value: stats?.totalUsers, to: '/admin/users', color: 'bg-blue-50 text-blue-600' },
    { icon: Stethoscope, label: 'Doctors', value: stats?.totalDoctors, to: '/admin/doctors', color: 'bg-primary-50 text-primary-600' },
    { icon: Activity, label: 'Predictions', value: stats?.totalPredictions, to: '/admin/predictions', color: 'bg-purple-50 text-purple-600' },
    { icon: Calendar, label: 'Appointments', value: stats?.totalAppointments, to: '/admin/analytics', color: 'bg-orange-50 text-orange-600' },
  ];

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold">Admin Dashboard</h1>
        <p className="text-gray-600">System overview and management</p>
      </div>

      <div className="grid md:grid-cols-2 lg:grid-cols-5 gap-4">
        {cards.map(({ icon: Icon, label, value, to, color }) => (
          <Link key={label} to={to} className="card hover:shadow-md transition-shadow group">
            <div className={`w-12 h-12 rounded-xl flex items-center justify-center ${color} mb-3`}>
              <Icon className="w-6 h-6" />
            </div>
            <p className="text-sm text-gray-500">{label}</p>
            <div className="flex items-center justify-between mt-1">
              <p className="text-2xl font-bold">{value ?? 0}</p>
              <ArrowRight className="w-4 h-4 text-gray-400 group-hover:text-primary-600 transition-colors" />
            </div>
          </Link>
        ))}
        <Link to="/admin/doctors" className="card hover:shadow-md transition-shadow group border-yellow-200">
          <div className="w-12 h-12 rounded-xl flex items-center justify-center bg-yellow-50 text-yellow-600 mb-3">
            <AlertCircle className="w-6 h-6" />
          </div>
          <p className="text-sm text-gray-500">Pending Doctors</p>
          <div className="flex items-center justify-between mt-1">
            <p className="text-2xl font-bold text-yellow-700">{stats?.pendingDoctors ?? 0}</p>
            <ArrowRight className="w-4 h-4 text-gray-400 group-hover:text-primary-600 transition-colors" />
          </div>
        </Link>
      </div>

      {stats?.pendingAppointments > 0 && (
        <div className="card bg-yellow-50 border-yellow-200">
          <p className="text-yellow-800 font-medium">{stats.pendingAppointments} pending appointments awaiting action</p>
        </div>
      )}

      {stats?.pendingDoctors > 0 && (
        <div className="card bg-orange-50 border-orange-200 flex items-center justify-between">
          <p className="text-orange-800 font-medium">{stats.pendingDoctors} doctor registration(s) need review</p>
          <Link to="/admin/doctors" className="btn-primary text-sm py-2">Review Now</Link>
        </div>
      )}
    </div>
  );
}
