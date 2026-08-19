import { useEffect, useState } from 'react';
import { adminAPI } from '../api/api';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line } from 'recharts';
import LoadingSpinner from '../components/LoadingSpinner';

const MONTHS = ['', 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

export default function AdminAnalytics() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminAPI.getStats()
      .then(res => setStats(res.data.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner fullScreen />;

  const diseaseData = (stats?.predictionsByDisease || []).map(d => ({
    name: d.disease?.length > 15 ? d.disease.substring(0, 15) + '...' : d.disease,
    count: d.count,
  }));

  const monthlyPredictions = (stats?.monthlyPredictions || []).map(m => ({
    month: MONTHS[m.month] || m.month,
    count: m.count,
  }));

  const monthlyRegistrations = (stats?.monthlyRegistrations || []).map(m => ({
    month: MONTHS[m.month] || m.month,
    count: m.count,
  }));

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold">Admin Analytics</h1>
        <p className="text-gray-600">Platform statistics and trends</p>
      </div>

      <div className="grid md:grid-cols-3 gap-4">
        {[
          { label: 'Total Users', value: stats?.totalUsers },
          { label: 'Total Doctors', value: stats?.totalDoctors },
          { label: 'Pending Doctors', value: stats?.pendingDoctors },
          { label: 'Total Predictions', value: stats?.totalPredictions },
          { label: 'Total Appointments', value: stats?.totalAppointments },
          { label: 'Pending Appointments', value: stats?.pendingAppointments },
        ].map(({ label, value }) => (
          <div key={label} className="card text-center">
            <p className="text-sm text-gray-500">{label}</p>
            <p className="text-3xl font-bold mt-1">{value ?? 0}</p>
          </div>
        ))}
      </div>

      <div className="grid md:grid-cols-2 gap-6">
        <div className="card">
          <h2 className="text-lg font-semibold mb-4">Disease Predictions</h2>
          {diseaseData.length > 0 ? (
            <ResponsiveContainer width="100%" height={280}>
              <BarChart data={diseaseData} layout="vertical">
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis type="number" />
                <YAxis dataKey="name" type="category" width={100} tick={{ fontSize: 11 }} />
                <Tooltip />
                <Bar dataKey="count" fill="#3b82f6" radius={[0, 4, 4, 0]} />
              </BarChart>
            </ResponsiveContainer>
          ) : (
            <p className="text-gray-500 text-center py-12">No prediction data yet</p>
          )}
        </div>

        <div className="card">
          <h2 className="text-lg font-semibold mb-4">Monthly Predictions</h2>
          {monthlyPredictions.length > 0 ? (
            <ResponsiveContainer width="100%" height={280}>
              <LineChart data={monthlyPredictions}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip />
                <Line type="monotone" dataKey="count" stroke="#8b5cf6" strokeWidth={2} dot={{ r: 4 }} />
              </LineChart>
            </ResponsiveContainer>
          ) : (
            <p className="text-gray-500 text-center py-12">No monthly data yet</p>
          )}
        </div>

        {monthlyRegistrations.length > 0 && (
          <div className="card md:col-span-2">
            <h2 className="text-lg font-semibold mb-4">Patient Registrations by Month</h2>
            <ResponsiveContainer width="100%" height={200}>
              <BarChart data={monthlyRegistrations}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="count" fill="#22c55e" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        )}
      </div>
    </div>
  );
}
