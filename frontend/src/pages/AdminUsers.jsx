import { useEffect, useState } from 'react';
import { adminAPI } from '../api/api';
import LoadingSpinner from '../components/LoadingSpinner';
import toast from 'react-hot-toast';

export default function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = () => {
    adminAPI.getUsers()
      .then(res => setUsers(res.data.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const toggleStatus = async (user) => {
    try {
      await adminAPI.setUserStatus(user.id, !user.enabled);
      toast.success(`User ${user.enabled ? 'disabled' : 'enabled'}`);
      load();
    } catch {
      toast.error('Failed to update user');
    }
  };

  if (loading) return <LoadingSpinner fullScreen />;

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold">Manage Patients & Users</h1>
        <p className="text-gray-600">Enable or disable patient and doctor accounts</p>
      </div>
      <div className="card overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b text-left text-gray-500">
              <th className="pb-3 pr-4">Name</th>
              <th className="pb-3 pr-4">Email</th>
              <th className="pb-3 pr-4">Role</th>
              <th className="pb-3 pr-4">Phone</th>
              <th className="pb-3 pr-4">Status</th>
              <th className="pb-3">Action</th>
            </tr>
          </thead>
          <tbody>
            {users.map(u => (
              <tr key={u.id} className="border-b last:border-0 hover:bg-gray-50">
                <td className="py-3 pr-4 font-medium">{u.firstName} {u.lastName}</td>
                <td className="py-3 pr-4">{u.email}</td>
                <td className="py-3 pr-4"><span className="badge bg-gray-100 text-gray-800">{u.role}</span></td>
                <td className="py-3 pr-4">{u.phone || '-'}</td>
                <td className="py-3 pr-4">
                  <span className={`badge ${u.enabled !== false ? 'badge-low' : 'badge-high'}`}>
                    {u.enabled !== false ? 'Active' : 'Disabled'}
                  </span>
                </td>
                <td className="py-3">
                  <button onClick={() => toggleStatus(u)}
                    className={`text-sm px-3 py-1 rounded-lg ${u.enabled !== false ? 'bg-red-100 text-red-700 hover:bg-red-200' : 'bg-green-100 text-green-700 hover:bg-green-200'}`}>
                    {u.enabled !== false ? 'Disable' : 'Enable'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
