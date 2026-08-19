import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useEffect, useState } from 'react';
import { notificationAPI } from '../api/api';
import {
  LayoutDashboard, Stethoscope, History, Users, Calendar,
  User, LogOut, Menu, X, Shield, BarChart3, Activity, Bell,
  FileText, MapPin, AlertTriangle, ClipboardList
} from 'lucide-react';
import HealixLogo from './HealixLogo';

export default function Layout({ children }) {
  const { user, logout, isAdmin, isDoctor } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    if (user && !isAdmin() && !isDoctor()) {
      notificationAPI.getUnreadCount()
        .then(res => setUnreadCount(res.data.data.count))
        .catch(() => {});
    }
  }, [user, location.pathname]);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const userLinks = [
    { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
    { to: '/predict', icon: Stethoscope, label: 'Predict Disease' },
    { to: '/prediction-history', icon: History, label: 'Prediction History' },
    { to: '/medical-history', icon: ClipboardList, label: 'Medical History' },
    { to: '/doctors', icon: Users, label: 'Find Doctors' },
    { to: '/appointments', icon: Calendar, label: 'Appointments' },
    { to: '/prescriptions', icon: Activity, label: 'Prescriptions' },
    { to: '/documents', icon: FileText, label: 'My Documents' },
    { to: '/nearby', icon: MapPin, label: 'Nearby Healthcare' },
    { to: '/emergency', icon: AlertTriangle, label: 'Emergency' },
    { to: '/notifications', icon: Bell, label: 'Notifications', badge: unreadCount },
    { to: '/profile', icon: User, label: 'Profile' },
  ];

  const adminLinks = [
    { to: '/admin', icon: Shield, label: 'Admin Dashboard' },
    { to: '/admin/users', icon: Users, label: 'Patients' },
    { to: '/admin/doctors', icon: Stethoscope, label: 'Doctors' },
    { to: '/admin/predictions', icon: Activity, label: 'Predictions' },
    { to: '/admin/analytics', icon: BarChart3, label: 'Analytics' },
    { to: '/admin/audit-logs', icon: ClipboardList, label: 'Audit Logs' },
  ];

  const doctorLinks = [
    { to: '/doctor-dashboard', icon: LayoutDashboard, label: 'My Dashboard' },
    { to: '/appointments', icon: Calendar, label: 'Appointments' },
    { to: '/notifications', icon: Bell, label: 'Notifications', badge: unreadCount },
  ];

  let links = userLinks;
  if (isAdmin()) links = adminLinks;
  else if (isDoctor()) links = doctorLinks;

  const isActive = (path) => location.pathname === path;

  return (
    <div className="min-h-screen bg-slate-50">
      <div className="lg:hidden bg-white border-b border-primary-100 px-4 py-3 flex items-center justify-between">
        <HealixLogo size="sm" />
        <button onClick={() => setSidebarOpen(!sidebarOpen)}>
          {sidebarOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
        </button>
      </div>

      <div className="flex">
        <aside className={`fixed lg:static inset-y-0 left-0 z-50 w-64 bg-white border-r border-primary-100 transform transition-transform lg:translate-x-0 ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'}`}>
          <div className="p-5 hidden lg:block border-b border-primary-50 bg-gradient-to-r from-primary-50/80 to-white">
            <HealixLogo size="md" />
          </div>

          <nav className="px-3 py-4 space-y-1 overflow-y-auto max-h-[calc(100vh-120px)]">
            {links.map(({ to, icon: Icon, label, badge }) => (
              <Link
                key={to}
                to={to}
                onClick={() => setSidebarOpen(false)}
                className={`flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                  isActive(to)
                    ? 'bg-primary-50 text-primary-700'
                    : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                }`}
              >
                <Icon className="w-5 h-5" />
                <span className="flex-1">{label}</span>
                {badge > 0 && (
                  <span className="w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">
                    {badge}
                  </span>
                )}
              </Link>
            ))}
          </nav>

          {user && (
            <div className="absolute bottom-0 left-0 right-0 p-4 border-t bg-white">
              <div className="flex items-center gap-3 mb-3">
                <div className="w-9 h-9 bg-primary-100 rounded-full flex items-center justify-center">
                  <User className="w-5 h-5 text-primary-600" />
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium truncate">{user.firstName} {user.lastName}</p>
                  <p className="text-xs text-gray-500">{user.role}</p>
                </div>
              </div>
              <button onClick={handleLogout} className="flex items-center gap-2 text-sm text-red-600 hover:text-red-700 w-full px-2 py-1">
                <LogOut className="w-4 h-4" />
                Logout
              </button>
            </div>
          )}
        </aside>

        {sidebarOpen && (
          <div className="fixed inset-0 bg-black/20 z-40 lg:hidden" onClick={() => setSidebarOpen(false)} />
        )}

        <main className="flex-1 min-h-screen lg:ml-0">
          <div className="p-4 lg:p-8 max-w-7xl mx-auto">
            {children}
          </div>
        </main>
      </div>
    </div>
  );
}
