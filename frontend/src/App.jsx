import { Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/Layout';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Predict from './pages/Predict';
import PredictionResult from './pages/PredictionResult';
import PredictionHistory from './pages/PredictionHistory';
import MedicalHistory from './pages/MedicalHistory';
import Doctors from './pages/Doctors';
import DoctorProfile from './pages/DoctorProfile';
import Appointments from './pages/Appointments';
import Profile from './pages/Profile';
import Admin from './pages/Admin';
import AdminUsers from './pages/AdminUsers';
import AdminDoctors from './pages/AdminDoctors';
import AdminPredictions from './pages/AdminPredictions';
import AdminAnalytics from './pages/AdminAnalytics';
import AdminAuditLogs from './pages/AdminAuditLogs';
import RegisterDoctor from './pages/RegisterDoctor';
import DoctorLogin from './pages/DoctorLogin';
import AdminLogin from './pages/AdminLogin';
import AdminRegister from './pages/AdminRegister';
import Prescriptions from './pages/Prescriptions';
import DoctorDashboard from './pages/DoctorDashboard';
import Notifications from './pages/Notifications';
import Documents from './pages/Documents';
import Emergency from './pages/Emergency';
import NearbyHealthcare from './pages/NearbyHealthcare';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/doctor-login" element={<DoctorLogin />} />
      <Route path="/admin-login" element={<AdminLogin />} />
      <Route path="/admin-register" element={<AdminRegister />} />
      <Route path="/register" element={<Register />} />
      <Route path="/register-doctor" element={<RegisterDoctor />} />

      <Route path="/dashboard" element={
        <ProtectedRoute><Layout><Dashboard /></Layout></ProtectedRoute>
      } />
      <Route path="/predict" element={
        <ProtectedRoute roles={['USER']}><Layout><Predict /></Layout></ProtectedRoute>
      } />
      <Route path="/prediction-result/:id" element={
        <ProtectedRoute><Layout><PredictionResult /></Layout></ProtectedRoute>
      } />
      <Route path="/prediction-history" element={
        <ProtectedRoute><Layout><PredictionHistory /></Layout></ProtectedRoute>
      } />
      <Route path="/medical-history" element={
        <ProtectedRoute roles={['USER']}><Layout><MedicalHistory /></Layout></ProtectedRoute>
      } />
      <Route path="/doctors" element={
        <ProtectedRoute><Layout><Doctors /></Layout></ProtectedRoute>
      } />
      <Route path="/doctor/:id" element={
        <ProtectedRoute><Layout><DoctorProfile /></Layout></ProtectedRoute>
      } />
      <Route path="/appointments" element={
        <ProtectedRoute><Layout><Appointments /></Layout></ProtectedRoute>
      } />
      <Route path="/profile" element={
        <ProtectedRoute><Layout><Profile /></Layout></ProtectedRoute>
      } />
      <Route path="/prescriptions" element={
        <ProtectedRoute roles={['USER']}><Layout><Prescriptions /></Layout></ProtectedRoute>
      } />
      <Route path="/documents" element={
        <ProtectedRoute roles={['USER']}><Layout><Documents /></Layout></ProtectedRoute>
      } />
      <Route path="/notifications" element={
        <ProtectedRoute><Layout><Notifications /></Layout></ProtectedRoute>
      } />
      <Route path="/emergency" element={
        <ProtectedRoute><Layout><Emergency /></Layout></ProtectedRoute>
      } />
      <Route path="/nearby" element={
        <ProtectedRoute><Layout><NearbyHealthcare /></Layout></ProtectedRoute>
      } />

      <Route path="/admin" element={
        <ProtectedRoute roles={['ADMIN']}><Layout><Admin /></Layout></ProtectedRoute>
      } />
      <Route path="/admin/users" element={
        <ProtectedRoute roles={['ADMIN']}><Layout><AdminUsers /></Layout></ProtectedRoute>
      } />
      <Route path="/admin/doctors" element={
        <ProtectedRoute roles={['ADMIN']}><Layout><AdminDoctors /></Layout></ProtectedRoute>
      } />
      <Route path="/admin/predictions" element={
        <ProtectedRoute roles={['ADMIN']}><Layout><AdminPredictions /></Layout></ProtectedRoute>
      } />
      <Route path="/admin/analytics" element={
        <ProtectedRoute roles={['ADMIN']}><Layout><AdminAnalytics /></Layout></ProtectedRoute>
      } />
      <Route path="/admin/audit-logs" element={
        <ProtectedRoute roles={['ADMIN']}><Layout><AdminAuditLogs /></Layout></ProtectedRoute>
      } />

      <Route path="/doctor-dashboard" element={
        <ProtectedRoute roles={['DOCTOR']}><Layout><DoctorDashboard /></Layout></ProtectedRoute>
      } />

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
