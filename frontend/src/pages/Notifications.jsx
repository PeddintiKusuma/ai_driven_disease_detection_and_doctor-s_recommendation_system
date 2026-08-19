import { useEffect, useState } from 'react';
import { notificationAPI } from '../api/api';
import { Bell, CheckCheck } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';
import toast from 'react-hot-toast';

export default function Notifications() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = () => {
    notificationAPI.getAll()
      .then(res => setNotifications(res.data.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleMarkAllRead = async () => {
    await notificationAPI.markAllAsRead();
    toast.success('All notifications marked as read');
    load();
  };

  const handleMarkRead = async (id) => {
    await notificationAPI.markAsRead(id);
    load();
  };

  const typeIcon = (type) => ({
    APPOINTMENT: '📅', PRESCRIPTION: '💊', APPROVAL: '✅', REVIEW: '⭐', INFO: 'ℹ️'
  }[type] || '🔔');

  if (loading) return <LoadingSpinner fullScreen />;

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold flex items-center gap-2">
            <Bell className="w-7 h-7 text-primary-600" /> Notifications
          </h1>
          <p className="text-gray-600">Stay updated on appointments, prescriptions & more</p>
        </div>
        {notifications.some(n => !n.readFlag) && (
          <button onClick={handleMarkAllRead} className="btn-secondary flex items-center gap-2 text-sm">
            <CheckCheck className="w-4 h-4" /> Mark All Read
          </button>
        )}
      </div>

      {notifications.length === 0 ? (
        <div className="card text-center py-12">
          <Bell className="w-12 h-12 text-gray-300 mx-auto mb-3" />
          <p className="text-gray-500">No notifications yet</p>
        </div>
      ) : (
        <div className="space-y-2">
          {notifications.map(n => (
            <div key={n.id}
              onClick={() => !n.readFlag && handleMarkRead(n.id)}
              className={`card cursor-pointer transition-colors ${!n.readFlag ? 'border-l-4 border-l-primary-500 bg-primary-50/30' : ''}`}>
              <div className="flex items-start gap-3">
                <span className="text-2xl">{typeIcon(n.type)}</span>
                <div className="flex-1">
                  <p className={`font-medium ${!n.readFlag ? 'text-gray-900' : 'text-gray-600'}`}>{n.title}</p>
                  <p className="text-sm text-gray-500 mt-1">{n.message}</p>
                  <p className="text-xs text-gray-400 mt-2">
                    {new Date(n.createdAt).toLocaleString()}
                  </p>
                </div>
                {!n.readFlag && <span className="w-2 h-2 bg-primary-500 rounded-full mt-2" />}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
