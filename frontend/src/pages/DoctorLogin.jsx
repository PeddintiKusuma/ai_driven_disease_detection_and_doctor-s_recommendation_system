import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Mail, Lock, Eye, EyeOff, Stethoscope } from 'lucide-react';
import HealixLogo from '../components/HealixLogo';
import toast from 'react-hot-toast';

export default function DoctorLogin() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const { login, logout } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const user = await login(email, password);
      if (user.role !== 'DOCTOR') {
        logout();
        toast.error('This login is for doctors only. Use the patient login page.');
        return;
      }
      toast.success('Welcome, Doctor!');
      navigate('/doctor-dashboard');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen healix-gradient flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <div className="flex justify-center mb-4">
            <HealixLogo size="xl" />
          </div>
          <div className="inline-flex items-center gap-2 bg-white/80 border border-primary-100 text-primary-700 px-4 py-1.5 rounded-full text-sm font-semibold mb-4">
            <Stethoscope className="w-4 h-4" /> Doctor Portal
          </div>
          <h1 className="text-2xl font-bold text-slate-900">Doctor Sign In</h1>
          <p className="text-gray-600 mt-1">Access your dashboard, schedule & patients</p>
        </div>

        <form onSubmit={handleSubmit} className="card space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Doctor Email</label>
            <div className="relative">
              <Mail className="absolute left-3 top-3 w-5 h-5 text-gray-400" />
              <input type="email" value={email} onChange={(e) => setEmail(e.target.value)}
                className="input-field pl-10" placeholder="doctor@healix.in" required />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Password</label>
            <div className="relative">
              <Lock className="absolute left-3 top-3 w-5 h-5 text-gray-400" />
              <input type={showPassword ? 'text' : 'password'} value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="input-field pl-10 pr-10" placeholder="••••••••" required />
              <button type="button" onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-3 text-gray-400">
                {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
              </button>
            </div>
          </div>
          <button type="submit" disabled={loading} className="btn-primary w-full py-3">
            {loading ? 'Signing in...' : 'Sign In as Doctor'}
          </button>
        </form>

        <p className="text-center mt-6 text-sm text-gray-600">
          New doctor? <Link to="/register-doctor" className="text-primary-600 font-medium hover:underline">Register as Doctor</Link>
          {' · '}
          <Link to="/login" className="text-primary-600 hover:underline">Patient Login</Link>
        </p>
      </div>
    </div>
  );
}
