import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  registerDoctor: (data) => api.post('/auth/register-doctor', data),
  registerAdmin: (data) => api.post('/auth/register-admin', data),
  login: (data) => api.post('/auth/login', data),
};

export const userAPI = {
  getProfile: () => api.get('/users/profile'),
  updateProfile: (data) => api.put('/users/profile', data),
  getDashboard: () => api.get('/users/dashboard'),
  getMedicalHistory: () => api.get('/users/medical-history'),
};

export const predictionAPI = {
  predict: (data) => api.post('/predictions', data),
  getHistory: () => api.get('/predictions'),
  getById: (id) => api.get(`/predictions/${id}`),
  downloadReport: (id) => api.get(`/predictions/${id}/report`, { responseType: 'blob' }),
};

export const doctorAPI = {
  getAll: () => api.get('/doctors'),
  getById: (id) => api.get(`/doctors/${id}`),
  search: (params) => api.get('/doctors/search', { params }),
};

export const appointmentAPI = {
  create: (data) => api.post('/appointments', data),
  getAll: () => api.get('/appointments'),
  updateStatus: (id, status) => api.patch(`/appointments/${id}/status`, { status }),
  reschedule: (id, data) => api.patch(`/appointments/${id}/reschedule`, data),
  downloadLetter: (id) => api.get(`/appointments/${id}/letter`, { responseType: 'blob' }),
  getSlots: (doctorId, date, excludeAppointmentId = null) => {
    let url = `/appointments/slots?doctorId=${doctorId}&date=${date}`;
    if (excludeAppointmentId) url += `&excludeAppointmentId=${excludeAppointmentId}`;
    return api.get(url);
  },
};

export const prescriptionAPI = {
  create: (data) => api.post('/prescriptions', data),
  getAll: () => api.get('/prescriptions'),
  download: (id) => api.get(`/prescriptions/${id}/download`, { responseType: 'blob' }),
};

export const placesAPI = {
  getNearby: (city, type) => api.get(`/places/nearby?city=${city}${type ? `&type=${type}` : ''}`),
  getAll: () => api.get('/places'),
};

export const notificationAPI = {
  getAll: () => api.get('/notifications'),
  getUnreadCount: () => api.get('/notifications/unread-count'),
  markAsRead: (id) => api.patch(`/notifications/${id}/read`),
  markAllAsRead: () => api.patch('/notifications/read-all'),
};

export const reviewAPI = {
  submit: (data) => api.post('/reviews', data),
};

export const documentAPI = {
  getAll: () => api.get('/documents'),
};

export const favoriteAPI = {
  getAll: () => api.get('/favorites'),
  add: (doctorId) => api.post(`/favorites/${doctorId}`),
  remove: (doctorId) => api.delete(`/favorites/${doctorId}`),
};

export const adminAPI = {
  getStats: () => api.get('/admin/stats'),
  getUsers: () => api.get('/admin/users'),
  getDoctors: () => api.get('/admin/doctors'),
  getPredictions: () => api.get('/admin/predictions'),
  getAppointments: () => api.get('/admin/appointments'),
  getAuditLogs: () => api.get('/admin/audit-logs'),
  setUserStatus: (id, enabled) => api.patch(`/admin/users/${id}/status`, { enabled }),
  approveDoctor: (id) => api.patch(`/admin/doctors/${id}/approve`),
  rejectDoctor: (id) => api.patch(`/admin/doctors/${id}/reject`),
};

export const doctorDashboardAPI = {
  getProfile: () => api.get('/doctor/profile'),
  updateProfile: (data) => api.put('/doctor/profile', data),
  updateSchedule: (data) => api.put('/doctor/schedule', data),
  getAppointments: () => api.get('/doctor/appointments'),
  accept: (id) => api.patch(`/doctor/appointments/${id}/accept`),
  reject: (id) => api.patch(`/doctor/appointments/${id}/reject`),
  complete: (id) => api.patch(`/doctor/appointments/${id}/complete`),
  uncomplete: (id) => api.patch(`/doctor/appointments/${id}/uncomplete`),
};

export default api;
