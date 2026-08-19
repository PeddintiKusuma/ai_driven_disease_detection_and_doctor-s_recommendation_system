import { useEffect, useState } from 'react';
import { doctorAPI } from '../api/api';
import { Search, Users, Filter } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';
import DoctorCard from '../components/DoctorCard';
import { useNavigate } from 'react-router-dom';

const SPECIALIZATIONS = [
  '', 'General Physician', 'Cardiologist', 'Dermatologist', 'Pulmonologist',
  'Gastroenterologist', 'Neurologist', 'Orthopedic', 'Psychiatrist', 'ENT Specialist',
  'Ophthalmologist', 'Endocrinologist', 'Nephrologist'
];

const CITIES = ['', 'Hyderabad', 'Bangalore', 'Chennai', 'Delhi', 'Mumbai', 'Pune', 'Ahmedabad', 'Kochi'];

export default function Doctors() {
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showFilters, setShowFilters] = useState(false);
  const [filters, setFilters] = useState({
    q: '', specialization: '', city: '', minFee: '', maxFee: '', minRating: ''
  });
  const navigate = useNavigate();

  const search = () => {
    setLoading(true);
    const params = {};
    if (filters.q) params.q = filters.q;
    if (filters.specialization) params.specialization = filters.specialization;
    if (filters.city) params.city = filters.city;
    if (filters.minFee) params.minFee = filters.minFee;
    if (filters.maxFee) params.maxFee = filters.maxFee;
    if (filters.minRating) params.minRating = filters.minRating;

    doctorAPI.search(params)
      .then(res => setDoctors(res.data.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => { search(); }, []);

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold flex items-center gap-2">
          <Users className="w-7 h-7 text-primary-600" /> Find Doctors
        </h1>
        <p className="text-gray-600">Search and filter verified healthcare professionals</p>
      </div>

      <div className="space-y-3">
        <div className="flex gap-3">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-3 w-5 h-5 text-gray-400" />
            <input value={filters.q} onChange={e => setFilters({ ...filters, q: e.target.value })}
              onKeyDown={e => e.key === 'Enter' && search()}
              placeholder="Search doctors..."
              className="input-field pl-10" />
          </div>
          <button onClick={() => setShowFilters(!showFilters)} className="btn-secondary flex items-center gap-2">
            <Filter className="w-4 h-4" /> Filters
          </button>
          <button onClick={search} className="btn-primary">Search</button>
        </div>

        {showFilters && (
          <div className="card grid md:grid-cols-3 lg:grid-cols-5 gap-3">
            <div>
              <label className="text-xs text-gray-500 mb-1 block">Specialization</label>
              <select value={filters.specialization} onChange={e => setFilters({ ...filters, specialization: e.target.value })}
                className="input-field text-sm">
                {SPECIALIZATIONS.map(s => <option key={s} value={s}>{s || 'All'}</option>)}
              </select>
            </div>
            <div>
              <label className="text-xs text-gray-500 mb-1 block">Location</label>
              <select value={filters.city} onChange={e => setFilters({ ...filters, city: e.target.value })}
                className="input-field text-sm">
                {CITIES.map(c => <option key={c} value={c}>{c || 'All Cities'}</option>)}
              </select>
            </div>
            <div>
              <label className="text-xs text-gray-500 mb-1 block">Min Rating</label>
              <select value={filters.minRating} onChange={e => setFilters({ ...filters, minRating: e.target.value })}
                className="input-field text-sm">
                <option value="">Any</option>
                <option value="4.5">4.5+</option>
                <option value="4.0">4.0+</option>
                <option value="3.5">3.5+</option>
              </select>
            </div>
            <div>
              <label className="text-xs text-gray-500 mb-1 block">Min Fee (₹)</label>
              <input type="number" value={filters.minFee} onChange={e => setFilters({ ...filters, minFee: e.target.value })}
                className="input-field text-sm" placeholder="0" />
            </div>
            <div>
              <label className="text-xs text-gray-500 mb-1 block">Max Fee (₹)</label>
              <input type="number" value={filters.maxFee} onChange={e => setFilters({ ...filters, maxFee: e.target.value })}
                className="input-field text-sm" placeholder="2000" />
            </div>
          </div>
        )}
      </div>

      {loading ? <LoadingSpinner /> : (
        <>
          <p className="text-sm text-gray-500">{doctors.length} doctors found</p>
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
            {doctors.map(d => (
              <DoctorCard key={d.id} doctor={d} onBook={(doc) => navigate('/appointments', { state: { doctor: doc } })} />
            ))}
          </div>
          {doctors.length === 0 && (
            <div className="text-center py-12 text-gray-500">No doctors found matching your criteria</div>
          )}
        </>
      )}
    </div>
  );
}
