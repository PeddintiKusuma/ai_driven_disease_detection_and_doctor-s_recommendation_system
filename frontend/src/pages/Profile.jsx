import { useEffect, useState } from 'react';
import { userAPI } from '../api/api';
import { useAuth } from '../context/AuthContext';
import { User } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';
import toast from 'react-hot-toast';

export default function Profile() {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [form, setForm] = useState({});
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    userAPI.getProfile()
      .then(res => { setProfile(res.data.data); setForm(res.data.data); })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      const res = await userAPI.updateProfile(form);
      setProfile(res.data.data);
      toast.success('Profile updated');
    } catch {
      toast.error('Update failed');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <LoadingSpinner fullScreen />;

  return (
    <div className="max-w-lg mx-auto space-y-6 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold">Profile</h1>
        <p className="text-gray-600">Manage your account information</p>
      </div>

      <div className="card">
        <div className="flex items-center gap-4 mb-6">
          <div className="w-16 h-16 bg-primary-100 rounded-full flex items-center justify-center">
            <User className="w-8 h-8 text-primary-600" />
          </div>
          <div>
            <h2 className="text-lg font-semibold">{profile.firstName} {profile.lastName}</h2>
            <p className="text-sm text-gray-500">{profile.email} • {profile.role}</p>
          </div>
        </div>

        <form onSubmit={handleSave} className="space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium mb-1">First Name</label>
              <input value={form.firstName || ''} onChange={(e) => setForm({ ...form, firstName: e.target.value })}
                className="input-field" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Last Name</label>
              <input value={form.lastName || ''} onChange={(e) => setForm({ ...form, lastName: e.target.value })}
                className="input-field" />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Phone</label>
            <input value={form.phone || ''} onChange={(e) => setForm({ ...form, phone: e.target.value })}
              className="input-field" />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium mb-1">Age</label>
              <input type="number" value={form.age || ''} onChange={(e) => setForm({ ...form, age: parseInt(e.target.value) })}
                className="input-field" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Gender</label>
              <select value={form.gender || ''} onChange={(e) => setForm({ ...form, gender: e.target.value })}
                className="input-field">
                <option value="MALE">Male</option>
                <option value="FEMALE">Female</option>
                <option value="OTHER">Other</option>
              </select>
            </div>
          </div>
          <button type="submit" disabled={saving} className="btn-primary w-full py-3">
            {saving ? 'Saving...' : 'Save Changes'}
          </button>
        </form>
      </div>
    </div>
  );
}
