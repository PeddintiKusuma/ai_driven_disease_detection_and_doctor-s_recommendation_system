import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { userAPI } from '../api/api';
import { useAuth } from '../context/AuthContext';
import {
  Activity, Stethoscope, Calendar, MapPin, FileText, ArrowRight,
  Building2, Pill, Bell, Heart, AlertTriangle, Eye, Printer
} from 'lucide-react';
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from 'recharts';
import LoadingSpinner from '../components/LoadingSpinner';
import DoctorCard from '../components/DoctorCard';
import FeaturedDoctorCard from '../components/FeaturedDoctorCard';
import { useNavigate } from 'react-router-dom';
import { predictionAPI, appointmentAPI } from '../api/api';
import { viewPdf, printPdf } from '../utils/pdfUtils';
import toast from 'react-hot-toast';

const COLORS = ['#3b82f6', '#22c55e', '#f59e0b', '#ef4444', '#8b5cf6'];

export default function Dashboard() {
  const { user } = useAuth();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    userAPI.getDashboard()
      .then(res => setData(res.data.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const handleViewReport = async (id) => {
    try {
      await viewPdf(() => predictionAPI.downloadReport(id));
    } catch { toast.error('Failed to open report'); }
  };

  const handlePrintLetter = async (id) => {
    try {
      await printPdf(() => appointmentAPI.downloadLetter(id));
    } catch { toast.error('Failed to print letter'); }
  };

  if (loading) return <LoadingSpinner fullScreen message="Loading dashboard..." />;
  if (!data) return <div className="text-center py-12 text-gray-500">Failed to load dashboard</div>;

  const diseaseData = Object.entries(data.diseaseFrequency || {}).map(([name, count]) => ({ name, count }));
  const riskBadge = (level) => ({ LOW: 'badge-low', MODERATE: 'badge-moderate', HIGH: 'badge-high', CRITICAL: 'badge-critical' }[level] || 'badge-moderate');
  const placeIcon = (type) => type === 'MEDICAL_STORE' ? Pill : Building2;
  const upcoming = data.upcomingAppointments?.[0];

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex items-center justify-between flex-wrap gap-3">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Welcome, {data.userName || user?.firstName}</h1>
          <p className="text-gray-600">Your AI-assisted healthcare dashboard</p>
        </div>
        <div className="flex gap-2">
          <Link to="/notifications" className="btn-secondary flex items-center gap-2 relative">
            <Bell className="w-4 h-4" />
            {data.unreadNotifications > 0 && (
              <span className="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">
                {data.unreadNotifications}
              </span>
            )}
          </Link>
          <Link to="/predict" className="btn-primary flex items-center gap-2">
            <Stethoscope className="w-4 h-4" /> Predict Disease
          </Link>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {[
          { icon: Activity, label: 'Total Predictions', value: data.totalPredictions, color: 'bg-blue-50 text-blue-600' },
          { icon: Calendar, label: 'Upcoming Appointments', value: data.upcomingAppointments?.length || 0, color: 'bg-purple-50 text-purple-600' },
          { icon: FileText, label: 'Prescriptions', value: data.totalPrescriptions || 0, color: 'bg-orange-50 text-orange-600' },
          { icon: Stethoscope, label: 'Recommended Doctors', value: data.recommendedDoctorCount || 0, color: 'bg-primary-50 text-primary-600' },
        ].map(({ icon: Icon, label, value, color }) => (
          <div key={label} className="card flex items-center gap-3">
            <div className={`w-11 h-11 rounded-xl flex items-center justify-center ${color}`}>
              <Icon className="w-5 h-5" />
            </div>
            <div>
              <p className="text-xs text-gray-500">{label}</p>
              <p className="text-2xl font-bold">{value}</p>
            </div>
          </div>
        ))}
      </div>

      <div className="grid lg:grid-cols-2 gap-6">
        {/* Latest Prediction */}
        <div className="card">
          <h2 className="text-lg font-semibold mb-4">Latest Prediction</h2>
          {data.latestPrediction ? (
            <div className="space-y-4">
              <div className="flex justify-between items-center">
                <span className="text-xl font-bold text-primary-700">{data.latestPrediction.predictedDisease}</span>
                <span className={`badge ${riskBadge(data.latestPrediction.riskLevel)}`}>{data.latestPrediction.riskLevel}</span>
              </div>
              <p className="text-sm text-gray-600">
                Confidence: <strong>{(data.latestPrediction.confidence * 100).toFixed(0)}%</strong>
                {' · '}Risk: <strong>{data.latestPrediction.riskLevel}</strong>
              </p>
              {data.latestPrediction.dietPlan && (
                <div className="bg-primary-50 border border-primary-100 rounded-xl p-3 text-sm space-y-2">
                  <p className="font-semibold text-primary-800">Recommended Diet (Educational Guidance)</p>
                  <p className="text-xs text-slate-500 italic">General educational guidance — not a personalized medical prescription.</p>
                  <div className="grid grid-cols-2 gap-2">
                    <div>
                      <p className="font-medium text-primary-700">✓ Do</p>
                      {data.latestPrediction.dietPlan.dos?.split('|').slice(0, 3).map((d, i) => (
                        <p key={i} className="text-primary-600 text-xs">{d.trim()}</p>
                      ))}
                    </div>
                    <div>
                      <p className="font-medium text-red-700">✗ Don't</p>
                      {data.latestPrediction.dietPlan.donts?.split('|').slice(0, 3).map((d, i) => (
                        <p key={i} className="text-red-600 text-xs">{d.trim()}</p>
                      ))}
                    </div>
                  </div>
                </div>
              )}
              <div className="flex gap-2">
                <Link to={`/prediction-result/${data.latestPrediction.id}`} className="btn-primary flex-1 text-center">
                  View Result
                </Link>
                <button onClick={() => handleViewReport(data.latestPrediction.id)} className="btn-secondary flex items-center gap-1">
                  <Eye className="w-4 h-4" /> View Report
                </button>
              </div>
            </div>
          ) : (
            <div className="text-center py-8">
              <p className="text-gray-500 mb-4">No predictions yet</p>
              <Link to="/predict" className="btn-primary inline-block">Start Prediction</Link>
            </div>
          )}
        </div>

        {/* Upcoming Appointment */}
        <div className="card">
          <h2 className="text-lg font-semibold mb-4">Upcoming Appointment</h2>
          {upcoming ? (
            <div className="space-y-3">
              <p className="text-xl font-bold">{upcoming.doctor?.name}</p>
              <p className="text-primary-600">{upcoming.doctor?.specialization}</p>
              <p className="text-sm text-gray-600">
                {upcoming.appointmentDate} | {upcoming.appointmentTime?.substring(0, 5)}
              </p>
              <p className="text-sm text-gray-500">{upcoming.doctor?.hospital}</p>
              <span className={`badge ${upcoming.status === 'CONFIRMED' ? 'badge-low' : 'badge-moderate'}`}>{upcoming.status}</span>
              <div className="flex gap-2 pt-2">
                <Link to="/appointments" className="btn-primary flex-1 text-center">View Appointment</Link>
                <button onClick={() => handlePrintLetter(upcoming.id)} className="btn-secondary flex items-center gap-1">
                  <Printer className="w-4 h-4" /> Print Letter
                </button>
              </div>
            </div>
          ) : (
            <div className="text-center py-8">
              <Calendar className="w-10 h-10 text-gray-300 mx-auto mb-3" />
              <p className="text-gray-500 mb-3">No upcoming appointments</p>
              <Link to="/doctors" className="btn-primary inline-block">Find a Doctor</Link>
            </div>
          )}
        </div>
      </div>

      {/* Follow-ups */}
      {data.upcomingFollowUps?.length > 0 && (
        <div className="card">
          <h2 className="text-lg font-semibold mb-4">Upcoming Follow-up</h2>
          {data.upcomingFollowUps.map(f => (
            <div key={f.id} className="flex items-center justify-between p-3 bg-yellow-50 rounded-lg">
              <div>
                <p className="font-medium">{f.doctorName}</p>
                <p className="text-sm text-gray-600">{f.followUpDate} · {f.diagnosis}</p>
              </div>
              <Link to="/prescriptions" className="btn-secondary text-sm">View Details</Link>
            </div>
          ))}
        </div>
      )}

      {/* Emergency Banner */}
      <div className="card bg-red-50 border-red-200 flex items-center justify-between flex-wrap gap-3">
        <div className="flex items-center gap-3">
          <AlertTriangle className="w-8 h-8 text-red-600" />
          <div>
            <p className="font-semibold text-red-800">Emergency?</p>
            <p className="text-sm text-red-600">If you are experiencing severe symptoms, seek immediate medical attention.</p>
          </div>
        </div>
        <Link to="/emergency" className="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 text-sm font-medium">
          Find Emergency Services
        </Link>
      </div>

      {/* Featured Doctors */}
      <div>
        <div className="flex items-center justify-between mb-4">
          <div>
            <h2 className="text-lg font-semibold">Our Doctors — Book Instantly</h2>
            <p className="text-sm text-gray-500">Verified specialists ready to consult</p>
          </div>
          <Link to="/doctors" className="text-primary-600 text-sm flex items-center gap-1 hover:underline">
            View All <ArrowRight className="w-4 h-4" />
          </Link>
        </div>
        <div className="grid lg:grid-cols-2 gap-4">
          {(data.availableDoctors || []).slice(0, 6).map(d => (
            <FeaturedDoctorCard key={d.id} doctor={d} onBook={(doc) => navigate('/appointments', { state: { doctor: doc } })} />
          ))}
        </div>
      </div>

      {/* Nearby Healthcare */}
      <div className="card">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-semibold flex items-center gap-2">
            <MapPin className="w-5 h-5 text-primary-600" /> Nearby Healthcare
          </h2>
          <Link to="/nearby" className="text-primary-600 text-sm hover:underline">View All</Link>
        </div>
        <div className="grid md:grid-cols-2 gap-3">
          {(data.nearbyPlaces || []).slice(0, 6).map(place => {
            const Icon = placeIcon(place.type);
            return (
              <div key={place.id} className="flex items-start gap-3 p-3 bg-gray-50 rounded-lg">
                <div className="w-9 h-9 bg-primary-100 rounded-lg flex items-center justify-center flex-shrink-0">
                  <Icon className="w-4 h-4 text-primary-600" />
                </div>
                <div>
                  <p className="font-medium text-sm">{place.name}</p>
                  <p className="text-xs text-gray-500">{place.type.replace('_', ' ')} · {place.address}</p>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Favorite Doctors */}
      {data.favoriteDoctors?.length > 0 && (
        <div>
          <h2 className="text-lg font-semibold mb-4 flex items-center gap-2">
            <Heart className="w-5 h-5 text-red-500" /> Saved Doctors
          </h2>
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
            {data.favoriteDoctors.map(d => (
              <DoctorCard key={d.id} doctor={d} onBook={(doc) => navigate('/appointments', { state: { doctor: doc } })} />
            ))}
          </div>
        </div>
      )}

      {diseaseData.length > 0 && (
        <div className="card">
          <h2 className="text-lg font-semibold mb-4">Disease History</h2>
          <ResponsiveContainer width="100%" height={200}>
            <PieChart>
              <Pie data={diseaseData} dataKey="count" nameKey="name" cx="50%" cy="50%" outerRadius={70} label>
                {diseaseData.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>
      )}
    </div>
  );
}
