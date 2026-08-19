import { useEffect, useState } from 'react';
import { doctorDashboardAPI, prescriptionAPI } from '../api/api';
import {
  Calendar, Check, X, CheckCircle, FileText, Users, Clock,
  UserCog, RotateCcw, Save, Stethoscope
} from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';
import toast from 'react-hot-toast';

const DAYS = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

export default function DoctorDashboard() {
  const [appointments, setAppointments] = useState([]);
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('appointments');
  const [prescriptionForm, setPrescriptionForm] = useState(null);
  const [rxForm, setRxForm] = useState({ diagnosis: '', medicines: '', instructions: '', followUpDate: '' });
  const [profileForm, setProfileForm] = useState({});
  const [scheduleForm, setScheduleForm] = useState({ availableDays: [], startTime: '09:00', endTime: '17:00' });
  const [savingProfile, setSavingProfile] = useState(false);
  const [savingSchedule, setSavingSchedule] = useState(false);

  const load = () => {
    Promise.all([
      doctorDashboardAPI.getAppointments(),
      doctorDashboardAPI.getProfile(),
    ])
      .then(([apptRes, profileRes]) => {
        setAppointments(apptRes.data.data);
        const p = profileRes.data.data;
        setProfile(p);
        setProfileForm({
          phone: p.phone || '',
          qualification: p.qualification || '',
          hospital: p.hospital || '',
          city: p.city || '',
          bio: p.bio || '',
          consultationFee: p.consultationFee || '',
        });
        const timeParts = (p.availableTime || '09:00-17:00').split('-');
        setScheduleForm({
          availableDays: (p.availableDays || '').split(',').filter(Boolean),
          startTime: timeParts[0]?.trim() || '09:00',
          endTime: timeParts[1]?.trim() || '17:00',
        });
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleAction = async (id, action) => {
    try {
      await action(id);
      toast.success('Updated');
      load();
    } catch {
      toast.error('Action failed');
    }
  };

  const handlePrescription = async (e) => {
    e.preventDefault();
    try {
      await prescriptionAPI.create({ appointmentId: prescriptionForm, ...rxForm });
      toast.success('Prescription sent to patient');
      setPrescriptionForm(null);
      setRxForm({ diagnosis: '', medicines: '', instructions: '', followUpDate: '' });
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to create prescription');
    }
  };

  const handleSaveProfile = async (e) => {
    e.preventDefault();
    setSavingProfile(true);
    try {
      await doctorDashboardAPI.updateProfile({
        ...profileForm,
        consultationFee: profileForm.consultationFee ? Number(profileForm.consultationFee) : null,
      });
      toast.success('Profile updated');
      load();
    } catch {
      toast.error('Failed to update profile');
    } finally {
      setSavingProfile(false);
    }
  };

  const handleSaveSchedule = async (e) => {
    e.preventDefault();
    setSavingSchedule(true);
    try {
      await doctorDashboardAPI.updateSchedule({
        availableDays: scheduleForm.availableDays.join(','),
        availableTime: `${scheduleForm.startTime}-${scheduleForm.endTime}`,
      });
      toast.success('Schedule updated — patients will see these days and times when booking');
      load();
    } catch {
      toast.error('Failed to update schedule');
    } finally {
      setSavingSchedule(false);
    }
  };

  const toggleDay = (day) => {
    setScheduleForm(prev => ({
      ...prev,
      availableDays: prev.availableDays.includes(day)
        ? prev.availableDays.filter(d => d !== day)
        : [...prev.availableDays, day],
    }));
  };

  const statusColor = (s) => ({
    PENDING: 'bg-yellow-100 text-yellow-800',
    CONFIRMED: 'bg-green-100 text-green-800',
    CANCELLED: 'bg-red-100 text-red-800',
    COMPLETED: 'bg-blue-100 text-blue-800',
  }[s] || '');

  if (loading) return <LoadingSpinner fullScreen />;

  const today = new Date().toISOString().split('T')[0];
  const todayAppts = appointments.filter(a => a.appointmentDate === today && a.status !== 'CANCELLED');
  const pending = appointments.filter(a => a.status === 'PENDING');
  const confirmed = appointments.filter(a => a.status === 'CONFIRMED');
  const completed = appointments.filter(a => a.status === 'COMPLETED');
  const uniquePatients = new Set(appointments.map(a => a.userId)).size;

  const tabs = [
    { id: 'appointments', label: 'Appointments' },
    { id: 'schedule', label: 'My Schedule' },
    { id: 'profile', label: 'Edit Profile' },
    { id: 'completed', label: 'Completed' },
  ];

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold">Doctor Dashboard</h1>
        <p className="text-gray-600">{profile?.name} · {profile?.specialization}</p>
      </div>

      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div className="card text-center">
          <Calendar className="w-8 h-8 text-primary-600 mx-auto mb-2" />
          <p className="text-2xl font-bold">{todayAppts.length}</p>
          <p className="text-sm text-gray-500">Today's Appointments</p>
        </div>
        <div className="card text-center">
          <Clock className="w-8 h-8 text-yellow-600 mx-auto mb-2" />
          <p className="text-2xl font-bold">{pending.length}</p>
          <p className="text-sm text-gray-500">Pending Requests</p>
        </div>
        <div className="card text-center">
          <CheckCircle className="w-8 h-8 text-green-600 mx-auto mb-2" />
          <p className="text-2xl font-bold">{completed.length}</p>
          <p className="text-sm text-gray-500">Completed</p>
        </div>
        <div className="card text-center">
          <Users className="w-8 h-8 text-purple-600 mx-auto mb-2" />
          <p className="text-2xl font-bold">{uniquePatients}</p>
          <p className="text-sm text-gray-500">Total Patients</p>
        </div>
      </div>

      <div className="flex gap-2 flex-wrap border-b border-gray-200 pb-1">
        {tabs.map(tab => (
          <button key={tab.id} onClick={() => setActiveTab(tab.id)}
            className={`px-4 py-2 text-sm font-medium rounded-t-lg transition-colors ${
              activeTab === tab.id ? 'bg-primary-600 text-white' : 'text-gray-600 hover:bg-gray-100'
            }`}>
            {tab.label}
          </button>
        ))}
      </div>

      {activeTab === 'appointments' && (
        <div className="space-y-6">
          {todayAppts.length > 0 && (
            <div className="card">
              <h2 className="text-lg font-semibold mb-4">Today's Patients</h2>
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b text-left text-gray-500">
                      <th className="pb-2 pr-4">Patient</th>
                      <th className="pb-2 pr-4">Time</th>
                      <th className="pb-2 pr-4">Reason</th>
                      <th className="pb-2">Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {todayAppts.map(a => (
                      <tr key={a.id} className="border-b last:border-0">
                        <td className="py-2 pr-4 font-medium">{a.userName}</td>
                        <td className="py-2 pr-4">{a.appointmentTime?.substring(0, 5)}</td>
                        <td className="py-2 pr-4">{a.reason}</td>
                        <td className="py-2"><span className={`badge ${statusColor(a.status)}`}>{a.status}</span></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {pending.length > 0 && (
            <div>
              <h2 className="text-lg font-semibold mb-3 text-yellow-700">New Patient Requests</h2>
              <div className="space-y-3">
                {pending.map(a => (
                  <div key={a.id} className="card border-yellow-200">
                    <div className="flex items-start justify-between flex-wrap gap-3">
                      <div>
                        <p className="font-semibold text-lg">{a.userName}</p>
                        <p className="text-sm text-gray-600 mt-1"><strong>Reason:</strong> {a.reason}</p>
                        <p className="text-sm text-gray-500 mt-2 flex items-center gap-1">
                          <Calendar className="w-4 h-4" /> {a.appointmentDate} at {a.appointmentTime?.substring(0, 5)}
                        </p>
                      </div>
                      <div className="flex gap-2">
                        <button onClick={() => handleAction(a.id, doctorDashboardAPI.accept)} className="btn-primary text-sm py-2 flex items-center gap-1">
                          <Check className="w-4 h-4" /> Accept
                        </button>
                        <button onClick={() => handleAction(a.id, doctorDashboardAPI.reject)}
                          className="bg-red-600 text-white px-3 py-2 rounded-lg text-sm flex items-center gap-1 hover:bg-red-700">
                          <X className="w-4 h-4" /> Reject
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {confirmed.length > 0 && (
            <div>
              <h2 className="text-lg font-semibold mb-3">Confirmed — Write Prescription</h2>
              <div className="space-y-3">
                {confirmed.map(a => (
                  <div key={a.id} className="card border-green-200">
                    <div className="flex items-start justify-between flex-wrap gap-3">
                      <div>
                        <p className="font-semibold">{a.userName}</p>
                        <p className="text-sm text-gray-500">{a.appointmentDate} at {a.appointmentTime?.substring(0, 5)} · {a.reason}</p>
                      </div>
                      <div className="flex gap-2">
                        <button onClick={() => setPrescriptionForm(a.id)}
                          className="btn-secondary text-sm py-2 flex items-center gap-1">
                          <FileText className="w-4 h-4" /> Write Prescription
                        </button>
                        <button onClick={() => handleAction(a.id, doctorDashboardAPI.complete)} className="btn-primary text-sm py-2 flex items-center gap-1">
                          <CheckCircle className="w-4 h-4" /> Complete
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {prescriptionForm && (
            <form onSubmit={handlePrescription} className="card border-2 border-primary-200 space-y-4">
              <h3 className="font-semibold flex items-center gap-2"><FileText className="w-5 h-5" /> Create Prescription</h3>
              <div>
                <label className="block text-sm font-medium mb-1">Diagnosis</label>
                <input value={rxForm.diagnosis} onChange={(e) => setRxForm({ ...rxForm, diagnosis: e.target.value })}
                  className="input-field" required />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Medicines *</label>
                <textarea value={rxForm.medicines} onChange={(e) => setRxForm({ ...rxForm, medicines: e.target.value })}
                  className="input-field font-mono text-sm" rows={5} required />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Instructions</label>
                <textarea value={rxForm.instructions} onChange={(e) => setRxForm({ ...rxForm, instructions: e.target.value })}
                  className="input-field" rows={2} />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Follow-up Date</label>
                <input type="date" value={rxForm.followUpDate} onChange={(e) => setRxForm({ ...rxForm, followUpDate: e.target.value })}
                  className="input-field" min={new Date().toISOString().split('T')[0]} />
              </div>
              <div className="flex gap-2">
                <button type="submit" className="btn-primary flex-1 py-3">Generate Prescription</button>
                <button type="button" onClick={() => setPrescriptionForm(null)} className="btn-secondary py-3">Cancel</button>
              </div>
            </form>
          )}

          {pending.length === 0 && confirmed.length === 0 && todayAppts.length === 0 && (
            <div className="card text-center py-12 text-gray-500">No active appointments right now</div>
          )}
        </div>
      )}

      {activeTab === 'schedule' && (
        <form onSubmit={handleSaveSchedule} className="card space-y-5">
          <h2 className="text-lg font-semibold flex items-center gap-2">
            <Calendar className="w-5 h-5 text-primary-600" /> My Schedule
          </h2>
          <p className="text-sm text-gray-600 bg-primary-50 border border-primary-100 rounded-lg p-3">
            Set the days and hours when you accept appointments. Patients will only see these options when booking with you.
          </p>
          <div>
            <label className="block text-sm font-medium mb-2">Available Days *</label>
            <div className="flex flex-wrap gap-2">
              {DAYS.map(day => (
                <button key={day} type="button" onClick={() => toggleDay(day)}
                  className={`px-3 py-2 rounded-lg text-sm font-medium border transition-colors ${
                    scheduleForm.availableDays.includes(day)
                      ? 'bg-primary-600 text-white border-primary-600'
                      : 'bg-white text-gray-600 border-gray-200 hover:border-primary-300'
                  }`}>
                  {day}
                </button>
              ))}
            </div>
            {scheduleForm.availableDays.length === 0 && (
              <p className="text-xs text-amber-600 mt-2">Select at least one day so patients can book you.</p>
            )}
          </div>
          <div className="grid md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium mb-1">Start Time *</label>
              <input type="time" value={scheduleForm.startTime}
                onChange={(e) => setScheduleForm({ ...scheduleForm, startTime: e.target.value })}
                className="input-field" required />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">End Time *</label>
              <input type="time" value={scheduleForm.endTime}
                onChange={(e) => setScheduleForm({ ...scheduleForm, endTime: e.target.value })}
                className="input-field" required />
            </div>
          </div>
          <div className="bg-gray-50 border border-gray-200 rounded-lg p-4 text-sm">
            <p><strong>Patients will see:</strong> {scheduleForm.availableDays.join(', ') || 'No days selected'} · {scheduleForm.startTime} – {scheduleForm.endTime}</p>
          </div>
          <button type="submit" disabled={savingSchedule || scheduleForm.availableDays.length === 0} className="btn-primary flex items-center gap-2">
            <Save className="w-4 h-4" /> {savingSchedule ? 'Saving...' : 'Save Schedule'}
          </button>
        </form>
      )}

      {activeTab === 'profile' && (
        <form onSubmit={handleSaveProfile} className="card space-y-4">
          <h2 className="text-lg font-semibold flex items-center gap-2">
            <UserCog className="w-5 h-5 text-primary-600" /> Edit Profile
          </h2>
          <div className="bg-gray-50 rounded-lg p-4 text-sm space-y-1">
            <p><strong>Name:</strong> {profile?.name}</p>
            <p><strong>Email:</strong> {profile?.email}</p>
            <p><strong>Specialization:</strong> {profile?.specialization}</p>
            <p><strong>License:</strong> {profile?.licenseNumber}</p>
          </div>
          <div className="grid md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium mb-1">Phone</label>
              <input value={profileForm.phone} onChange={(e) => setProfileForm({ ...profileForm, phone: e.target.value })} className="input-field" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Consultation Fee (₹)</label>
              <input type="number" value={profileForm.consultationFee} onChange={(e) => setProfileForm({ ...profileForm, consultationFee: e.target.value })} className="input-field" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Qualification</label>
              <input value={profileForm.qualification} onChange={(e) => setProfileForm({ ...profileForm, qualification: e.target.value })} className="input-field" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Hospital / Clinic</label>
              <input value={profileForm.hospital} onChange={(e) => setProfileForm({ ...profileForm, hospital: e.target.value })} className="input-field" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">City</label>
              <input value={profileForm.city} onChange={(e) => setProfileForm({ ...profileForm, city: e.target.value })} className="input-field" />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Bio</label>
            <textarea value={profileForm.bio} onChange={(e) => setProfileForm({ ...profileForm, bio: e.target.value })}
              className="input-field" rows={4} />
          </div>
          <button type="submit" disabled={savingProfile} className="btn-primary flex items-center gap-2">
            <Stethoscope className="w-4 h-4" /> {savingProfile ? 'Saving...' : 'Save Profile'}
          </button>
        </form>
      )}

      {activeTab === 'completed' && (
        <div className="space-y-3">
          <h2 className="text-lg font-semibold">Completed Appointments</h2>
          {completed.length === 0 ? (
            <div className="card text-center py-12 text-gray-500">No completed appointments yet</div>
          ) : (
            completed.map(a => (
              <div key={a.id} className="card border-blue-100">
                <div className="flex items-start justify-between flex-wrap gap-3">
                  <div>
                    <p className="font-semibold">{a.userName}</p>
                    <p className="text-sm text-gray-500">{a.appointmentDate} at {a.appointmentTime?.substring(0, 5)}</p>
                    <p className="text-sm text-gray-600 mt-1">{a.reason}</p>
                    <span className={`badge ${statusColor(a.status)} mt-2 inline-block`}>{a.status}</span>
                  </div>
                  <button onClick={() => handleAction(a.id, doctorDashboardAPI.uncomplete)}
                    className="btn-secondary text-sm py-2 flex items-center gap-1">
                    <RotateCcw className="w-4 h-4" /> Mark Not Completed
                  </button>
                </div>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
}
