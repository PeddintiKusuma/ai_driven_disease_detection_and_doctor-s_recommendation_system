import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Mail, Lock, Eye, EyeOff, Shield, KeyRound } from 'lucide-react';
import HealixLogo from '../components/HealixLogo';
import toast from 'react-hot-toast';

export default function AdminRegister() {
  const [form, setForm] = useState({
    firstName: '', lastName: '', email: '', password: '', phone: '', registrationKey: '',
  });
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const { registerAdmin } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await registerAdmin(form);
      toast.success('Admin account created!');
      navigate('/admin');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen healix-gradient flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <div className="flex justify-center mb-4">
            <HealixLogo size="lg" />
          </div>
          <div className="inline-flex items-center gap-2 bg-white/80 border border-primary-100 text-primary-700 px-4 py-1.5 rounded-full text-sm font-semibold mb-4">
            <Shield className="w-4 h-4" /> Admin Registration
          </div>
          <h1 className="text-2xl font-bold text-slate-900">Create Admin Account</h1>
          <p className="text-gray-600 mt-1">Requires a valid admin registration key</p>
        </div>

        <form onSubmit={handleSubmit} className="card space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium mb-1">First Name</label>
              <input name="firstName" value={form.firstName} onChange={handleChange} className="input-field" required />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Last Name</label>
              <input name="lastName" value={form.lastName} onChange={handleChange} className="input-field" required />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Email</label>
            <div className="relative">
              <Mail className="absolute left-3 top-3 w-5 h-5 text-gray-400" />
              <input type="email" name="email" value={form.email} onChange={handleChange}
                className="input-field pl-10" required />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Phone</label>
            <input name="phone" value={form.phone} onChange={handleChange} className="input-field" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Password</label>
            <div className="relative">
              <Lock className="absolute left-3 top-3 w-5 h-5 text-gray-400" />
              <input type={showPassword ? 'text' : 'password'} name="password" value={form.password}
                onChange={handleChange} className="input-field pl-10 pr-10" minLength={6} required />
              <button type="button" onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-3 text-gray-400">
                {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
              </button>
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Admin Registration Key</label>
            <div className="relative">
              <KeyRound className="absolute left-3 top-3 w-5 h-5 text-gray-400" />
              <input name="registrationKey" value={form.registrationKey} onChange={handleChange}
                className="input-field pl-10" placeholder="Provided by system owner" required />
            </div>
          </div>
          <button type="submit" disabled={loading} className="btn-primary w-full py-3">
            {loading ? 'Creating account...' : 'Create Admin Account'}
          </button>
        </form>

        <p className="text-center mt-6 text-sm text-gray-600">
          Already have an account? <Link to="/admin-login" className="text-primary-600 font-medium hover:underline">Admin Sign In</Link>
        </p>
      </div>
    </div>
  );
}
