import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authAPI } from '../api/api';
import { Stethoscope } from 'lucide-react';
import HealixLogo from '../components/HealixLogo';
import toast from 'react-hot-toast';

export default function RegisterDoctor() {
  const [form, setForm] = useState({
    email: '', password: '', firstName: '', lastName: '', phone: '',
    specialization: 'General Physician', qualification: '', experience: '',
    consultationFee: '', hospital: '', city: '', availableDays: 'Mon,Tue,Wed,Thu,Fri',
    availableTime: '09:00-17:00', bio: '', licenseNumber: '',
  });
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await authAPI.registerDoctor({
        ...form,
        experience: parseInt(form.experience),
        consultationFee: parseFloat(form.consultationFee),
      });
      const { token, user } = res.data.data;
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(user));
      toast.success(res.data.message || 'Registration submitted — pending admin approval.');
      navigate('/doctor-dashboard');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  const specializations = [
    'General Physician', 'Cardiologist', 'Dermatologist', 'Pulmonologist',
    'Gastroenterologist', 'Neurologist', 'Nephrologist', 'Ophthalmologist',
    'ENT Specialist', 'Psychiatrist', 'Endocrinologist', 'Orthopedic',
  ];

  return (
    <div className="min-h-screen healix-gradient py-8 px-4">
      <div className="max-w-2xl mx-auto">
        <div className="text-center mb-8">
          <div className="flex justify-center mb-4">
            <HealixLogo size="lg" />
          </div>
          <div className="inline-flex items-center gap-2 bg-primary-100 text-primary-700 px-3 py-1 rounded-full text-sm font-semibold mb-3">
            <Stethoscope className="w-4 h-4" />
            Doctor Registration
          </div>
          <h1 className="text-2xl font-bold text-slate-900">Join Healix as a Doctor</h1>
          <p className="text-gray-600">Register your profile for admin verification</p>
        </div>

        <form onSubmit={handleSubmit} className="card space-y-4">
          <div className="grid md:grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium mb-1">First Name</label>
              <input name="firstName" value={form.firstName} onChange={handleChange} className="input-field" required />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Last Name</label>
              <input name="lastName" value={form.lastName} onChange={handleChange} className="input-field" required />
            </div>
          </div>
          <div className="grid md:grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium mb-1">Email</label>
              <input type="email" name="email" value={form.email} onChange={handleChange} className="input-field" required />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Password</label>
              <input type="password" name="password" value={form.password} onChange={handleChange} className="input-field" minLength={6} required />
            </div>
          </div>
          <div className="grid md:grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium mb-1">Specialization</label>
              <select name="specialization" value={form.specialization} onChange={handleChange} className="input-field">
                {specializations.map(s => <option key={s}>{s}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Qualification</label>
              <input name="qualification" value={form.qualification} onChange={handleChange} className="input-field" placeholder="MBBS, MD" required />
            </div>
          </div>
          <div className="grid md:grid-cols-3 gap-3">
            <div>
              <label className="block text-sm font-medium mb-1">Experience (years)</label>
              <input type="number" name="experience" value={form.experience} onChange={handleChange} className="input-field" min="0" required />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Consultation Fee (₹)</label>
              <input type="number" name="consultationFee" value={form.consultationFee} onChange={handleChange} className="input-field" min="0" required />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Phone</label>
              <input name="phone" value={form.phone} onChange={handleChange} className="input-field" />
            </div>
          </div>
          <div className="grid md:grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium mb-1">Hospital</label>
              <input name="hospital" value={form.hospital} onChange={handleChange} className="input-field" required />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">City / Location</label>
              <input name="city" value={form.city} onChange={handleChange} className="input-field" required />
            </div>
          </div>
          <div className="grid md:grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium mb-1">Available Days</label>
              <input name="availableDays" value={form.availableDays} onChange={handleChange} className="input-field" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Available Time</label>
              <input name="availableTime" value={form.availableTime} onChange={handleChange} className="input-field" />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Medical License Number</label>
            <input name="licenseNumber" value={form.licenseNumber} onChange={handleChange}
              className="input-field" placeholder="MCI/DCI Registration No." required />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Bio</label>
            <textarea name="bio" value={form.bio} onChange={handleChange} className="input-field" rows={3} />
          </div>
          <button type="submit" disabled={loading} className="btn-primary w-full py-3">
            {loading ? 'Creating Profile...' : 'Register as Doctor'}
          </button>
        </form>

        <p className="text-center mt-6 text-sm text-gray-600">
          Are you a patient? <Link to="/register" className="text-primary-600 font-medium hover:underline">Patient Registration</Link>
          {' · '}
          <Link to="/login" className="text-primary-600 font-medium hover:underline">Login</Link>
        </p>
      </div>
    </div>
  );
}
