import { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { appointmentAPI, doctorAPI } from '../api/api';
import { Calendar, Clock, Printer, Eye, IndianRupee, RefreshCw, X } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';
import { viewPdf, printPdf } from '../utils/pdfUtils';
import { getNextAvailableDates, formatDisplayDate } from '../utils/scheduleUtils';
import toast from 'react-hot-toast';

export default function Appointments() {
  const location = useLocation();
  const preselected = location.state;
  const [appointments, setAppointments] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(!!preselected?.doctor);
  const [slots, setSlots] = useState([]);
  const [slotInfo, setSlotInfo] = useState(null);
  const [loadingSlots, setLoadingSlots] = useState(false);
  const [rescheduleId, setRescheduleId] = useState(null);
  const [form, setForm] = useState({
    doctorId: preselected?.doctor?.id || '',
    predictionId: preselected?.predictionId || '',
    appointmentDate: '',
    appointmentTime: '',
    reason: preselected?.predictionId ? 'Follow-up for AI prediction' : '',
    notes: '',
  });
  const [rescheduleForm, setRescheduleForm] = useState({ appointmentDate: '', appointmentTime: '' });

  const load = () => {
    Promise.all([appointmentAPI.getAll(), doctorAPI.getAll()])
      .then(([apptRes, docRes]) => {
        setAppointments(apptRes.data.data);
        setDoctors(docRes.data.data);
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const fetchSlots = (doctorId, date, excludeId = null) => {
    if (!doctorId || !date) {
      setSlots([]);
      setSlotInfo(null);
      return;
    }
    setLoadingSlots(true);
    appointmentAPI.getSlots(doctorId, date, excludeId)
      .then(res => {
        const data = res.data.data;
        setSlots(data.slots || []);
        setSlotInfo(data);
      })
      .catch((err) => {
        setSlots([]);
        setSlotInfo({ message: err.response?.data?.message || 'Unable to load slots' });
      })
      .finally(() => setLoadingSlots(false));
  };

  useEffect(() => {
    if (showForm && form.doctorId && form.appointmentDate) {
      fetchSlots(form.doctorId, form.appointmentDate);
    }
  }, [showForm, form.doctorId, form.appointmentDate]);

  useEffect(() => {
    if (rescheduleId) {
      const appt = appointments.find(a => a.id === rescheduleId);
      if (appt && rescheduleForm.appointmentDate) {
        fetchSlots(appt.doctor.id, rescheduleForm.appointmentDate, rescheduleId);
      }
    }
  }, [rescheduleId, rescheduleForm.appointmentDate, appointments]);

  const selectedDoctor = doctors.find(d => d.id === parseInt(form.doctorId)) || preselected?.doctor;
  const reschedulingAppt = appointments.find(a => a.id === rescheduleId);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.appointmentTime) {
      toast.error('Please select a time slot');
      return;
    }
    try {
      await appointmentAPI.create(form);
      toast.success('Appointment booked successfully!');
      setShowForm(false);
      setForm({ ...form, appointmentDate: '', appointmentTime: '' });
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Booking failed');
    }
  };

  const openReschedule = (appt) => {
    setRescheduleId(appt.id);
    setRescheduleForm({ appointmentDate: appt.appointmentDate, appointmentTime: appt.appointmentTime?.substring(0, 5) || '' });
    setSlots([]);
    setSlotInfo(null);
  };

  const handleReschedule = async (e) => {
    e.preventDefault();
    if (!rescheduleForm.appointmentTime) {
      toast.error('Please select a new time slot');
      return;
    }
    try {
      await appointmentAPI.reschedule(rescheduleId, rescheduleForm);
      toast.success('Appointment rescheduled! Waiting for doctor confirmation.');
      setRescheduleId(null);
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Reschedule failed');
    }
  };

  const handleViewLetter = async (id) => {
    try {
      await viewPdf(() => appointmentAPI.downloadLetter(id));
    } catch {
      toast.error('Failed to open letter');
    }
  };

  const handlePrintLetter = async (id) => {
    try {
      await printPdf(() => appointmentAPI.downloadLetter(id));
    } catch {
      toast.error('Failed to print letter');
    }
  };

  const renderSlotPicker = (selectedTime, onSelect, info, doctor) => (
    <div>
      {doctor?.availableDays && (
        <div className="bg-primary-50 border border-primary-100 rounded-lg p-3 mb-3 text-sm">
          <p className="font-medium text-primary-900">Doctor's availability</p>
          <p className="text-primary-700 mt-1">
            <strong>Days:</strong> {doctor.availableDays} · <strong>Hours:</strong> {doctor.availableTime || 'Not set'}
          </p>
        </div>
      )}
      {info?.bookedCount != null && info?.doctorAvailableOnDate && (
        <p className="text-xs text-gray-500 mb-2">
          {info.bookedCount} of {info.dailyLimit} appointments booked for this day
        </p>
      )}
      {loadingSlots ? (
        <p className="text-sm text-gray-500">Loading available slots...</p>
      ) : info?.scheduleConfigured === false ? (
        <div className="bg-amber-50 border border-amber-200 rounded-lg p-3 text-sm text-amber-800">
          {info.message || 'This doctor has not set their schedule yet.'}
        </div>
      ) : info?.doctorAvailableOnDate === false ? (
        <div className="bg-amber-50 border border-amber-200 rounded-lg p-3 text-sm text-amber-800">
          {info.message}
        </div>
      ) : info?.dailyLimitReached ? (
        <div className="bg-amber-50 border border-amber-200 rounded-lg p-3 text-sm text-amber-800">
          {info.message || 'Doctor is fully booked for this day. Please choose another date.'}
        </div>
      ) : slots.length === 0 ? (
        <div className="bg-red-50 border border-red-200 rounded-lg p-3 text-sm text-red-700">
          {info?.message || 'All slots are booked for this date. Try another date from the doctor\'s available days.'}
        </div>
      ) : (
        <div>
          <p className="text-xs text-gray-500 mb-2">Available times on this date ({doctor?.availableTime}):</p>
          <div className="grid grid-cols-3 md:grid-cols-5 gap-2">
            {slots.map(slot => (
              <button key={slot.time} type="button"
                onClick={() => onSelect(slot.time)}
                className={`py-2 px-3 rounded-lg text-sm font-medium border transition-colors ${
                  selectedTime === slot.time
                    ? 'bg-primary-600 text-white border-primary-600'
                    : 'bg-white text-gray-700 border-gray-200 hover:border-primary-400'
                }`}>
                {slot.label}
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  );

  const renderDatePicker = (selectedDate, onSelect, doctor) => {
    const suggestedDates = doctor?.availableDays ? getNextAvailableDates(doctor.availableDays) : [];
    return (
      <div className="space-y-3">
        {doctor?.availableDays ? (
          <div className="bg-blue-50 border border-blue-100 rounded-lg p-3 text-sm text-blue-900">
            <p className="font-medium">Book only on these days: {doctor.availableDays}</p>
            <p className="text-blue-700 mt-1">Working hours: {doctor.availableTime || 'Contact doctor'}</p>
          </div>
        ) : (
          <div className="bg-amber-50 border border-amber-200 rounded-lg p-3 text-sm text-amber-800">
            This doctor has not published their schedule yet.
          </div>
        )}
        {suggestedDates.length > 0 && (
          <div>
            <p className="text-sm font-medium mb-2">Quick pick — next available dates:</p>
            <div className="flex flex-wrap gap-2">
              {suggestedDates.map(d => (
                <button key={d} type="button" onClick={() => onSelect(d)}
                  className={`px-3 py-2 rounded-lg text-sm border transition-colors ${
                    selectedDate === d
                      ? 'bg-primary-600 text-white border-primary-600'
                      : 'bg-white text-gray-700 border-gray-200 hover:border-primary-400'
                  }`}>
                  {formatDisplayDate(d)}
                </button>
              ))}
            </div>
          </div>
        )}
        <div>
          <label className="block text-sm font-medium mb-1">Or choose a date</label>
          <input type="date" value={selectedDate}
            onChange={(e) => onSelect(e.target.value)}
            className="input-field" min={new Date().toISOString().split('T')[0]} />
        </div>
      </div>
    );
  };

  const statusColor = (s) => ({
    PENDING: 'bg-yellow-100 text-yellow-800',
    CONFIRMED: 'bg-green-100 text-green-800',
    CANCELLED: 'bg-red-100 text-red-800',
    COMPLETED: 'bg-blue-100 text-blue-800',
  }[s] || '');

  if (loading) return <LoadingSpinner fullScreen />;

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Appointments</h1>
          <p className="text-gray-600">Book doctors with available time slots (max 30 patients per doctor per day)</p>
        </div>
        <button onClick={() => setShowForm(!showForm)} className="btn-primary">
          {showForm ? 'Cancel' : 'Book Doctor'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="card space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Select Doctor</label>
            <select value={form.doctorId} onChange={(e) => setForm({ ...form, doctorId: e.target.value, appointmentTime: '' })}
              className="input-field" required>
              <option value="">Choose a doctor...</option>
              {doctors.map(d => (
                <option key={d.id} value={d.id}>
                  {d.name} — {d.specialization} — ₹{d.consultationFee} — {d.city}
                </option>
              ))}
            </select>
          </div>

          {selectedDoctor && (
            <div className="bg-primary-50 p-4 rounded-lg grid md:grid-cols-2 gap-3 text-sm border border-primary-100">
              <div><span className="text-gray-500">Hospital:</span> <strong>{selectedDoctor.hospital}</strong></div>
              <div><span className="text-gray-500">Fee:</span> <strong>₹{selectedDoctor.consultationFee}</strong></div>
              <div className="md:col-span-2">
                <span className="text-gray-500">Accepts appointments:</span>{' '}
                <strong>{selectedDoctor.availableDays || 'Schedule not set'}</strong>
                {selectedDoctor.availableTime && <> · <strong>{selectedDoctor.availableTime}</strong></>}
              </div>
            </div>
          )}

          {selectedDoctor && (
            renderDatePicker(form.appointmentDate, (date) => setForm({ ...form, appointmentDate: date, appointmentTime: '' }), selectedDoctor)
          )}

          {form.appointmentDate && selectedDoctor && (
            <div>
              <label className="block text-sm font-medium mb-2">Select Time Slot</label>
              {renderSlotPicker(
                form.appointmentTime,
                (time) => setForm({ ...form, appointmentTime: time }),
                slotInfo,
                selectedDoctor
              )}
            </div>
          )}

          <div>
            <label className="block text-sm font-medium mb-1">Reason</label>
            <input value={form.reason} onChange={(e) => setForm({ ...form, reason: e.target.value })}
              className="input-field" required />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Notes (optional)</label>
            <textarea value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })}
              className="input-field" rows={2} />
          </div>
          <button type="submit" className="btn-primary w-full py-3"
            disabled={!form.appointmentTime || !form.appointmentDate || slotInfo?.dailyLimitReached || slotInfo?.doctorAvailableOnDate === false || slotInfo?.scheduleConfigured === false}>
            Book Appointment
          </button>
        </form>
      )}

      {rescheduleId && reschedulingAppt && (
        <form onSubmit={handleReschedule} className="card border-2 border-primary-200 space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold flex items-center gap-2">
              <RefreshCw className="w-5 h-5 text-primary-600" /> Reschedule Appointment
            </h2>
            <button type="button" onClick={() => setRescheduleId(null)} className="text-gray-400 hover:text-gray-600">
              <X className="w-5 h-5" />
            </button>
          </div>
          <p className="text-sm text-gray-600">
            Rescheduling with <strong>{reschedulingAppt.doctor?.name}</strong>. Your appointment will go back to pending until the doctor confirms.
          </p>
          <div>
            <label className="block text-sm font-medium mb-1">New Date</label>
            {renderDatePicker(
              rescheduleForm.appointmentDate,
              (date) => setRescheduleForm({ ...rescheduleForm, appointmentDate: date, appointmentTime: '' }),
              reschedulingAppt?.doctor
            )}
          </div>
          {rescheduleForm.appointmentDate && (
            <div>
              <label className="block text-sm font-medium mb-2">New Time Slot</label>
              {renderSlotPicker(
                rescheduleForm.appointmentTime,
                (time) => setRescheduleForm({ ...rescheduleForm, appointmentTime: time }),
                slotInfo,
                reschedulingAppt?.doctor
              )}
            </div>
          )}
          <button type="submit" className="btn-primary w-full py-3" disabled={!rescheduleForm.appointmentTime || slotInfo?.dailyLimitReached}>
            Confirm Reschedule
          </button>
        </form>
      )}

      {appointments.length === 0 ? (
        <div className="card text-center py-12">
          <Calendar className="w-12 h-12 text-gray-300 mx-auto mb-3" />
          <p className="text-gray-500">No appointments yet</p>
        </div>
      ) : (
        <div className="space-y-3">
          {appointments.map(a => (
            <div key={a.id} className="card">
              <div className="flex items-center justify-between flex-wrap gap-3">
                <div>
                  <p className="font-semibold">{a.doctor?.name}</p>
                  <p className="text-sm text-primary-600">{a.doctor?.specialization}</p>
                  <div className="flex items-center gap-4 mt-2 text-sm text-gray-500 flex-wrap">
                    <span className="flex items-center gap-1"><Calendar className="w-4 h-4" /> {a.appointmentDate}</span>
                    <span className="flex items-center gap-1"><Clock className="w-4 h-4" /> {a.appointmentTime?.substring(0, 5)}</span>
                    <span className="flex items-center gap-1"><IndianRupee className="w-4 h-4" /> ₹{a.doctor?.consultationFee}</span>
                  </div>
                  <p className="text-xs text-gray-400 mt-1">{a.doctor?.hospital}, {a.doctor?.city}</p>
                </div>
                <div className="flex items-center gap-2 flex-wrap">
                  <span className={`badge ${statusColor(a.status)}`}>{a.status}</span>
                  {(a.status === 'PENDING' || a.status === 'CONFIRMED') && (
                    <button onClick={() => openReschedule(a)}
                      className="btn-secondary text-sm py-2 flex items-center gap-1">
                      <RefreshCw className="w-4 h-4" /> Reschedule
                    </button>
                  )}
                  <button onClick={() => handleViewLetter(a.id)}
                    className="btn-secondary text-sm py-2 flex items-center gap-1">
                    <Eye className="w-4 h-4" /> View
                  </button>
                  <button onClick={() => handlePrintLetter(a.id)}
                    className="btn-primary text-sm py-2 flex items-center gap-1">
                    <Printer className="w-4 h-4" /> Print
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
