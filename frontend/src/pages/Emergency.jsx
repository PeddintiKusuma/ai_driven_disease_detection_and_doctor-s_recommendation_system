import { useEffect, useState } from 'react';
import { placesAPI } from '../api/api';
import { AlertTriangle, Phone, MapPin, Building2, Pill, Siren, Microscope } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';

const EMERGENCY_CONTACTS = [
  { name: 'National Emergency', number: '112', description: 'All emergencies' },
  { name: 'Ambulance', number: '108', description: 'Medical emergency ambulance' },
  { name: 'Police', number: '100', description: 'Law enforcement' },
  { name: 'Fire Service', number: '101', description: 'Fire emergencies' },
];

const CATEGORIES = [
  { type: 'HOSPITAL', label: 'Hospitals', icon: Building2, color: 'text-red-600 bg-red-50' },
  { type: 'MEDICAL_STORE', label: 'Medical Stores', icon: Pill, color: 'text-primary-600 bg-primary-50' },
  { type: 'CLINIC', label: 'Clinics', icon: MapPin, color: 'text-blue-600 bg-blue-50' },
  { type: 'LAB', label: 'Diagnostic Centers', icon: Microscope, color: 'text-purple-600 bg-purple-50' },
];

export default function Emergency() {
  const [places, setPlaces] = useState([]);
  const [loading, setLoading] = useState(true);
  const [city, setCity] = useState('Hyderabad');

  useEffect(() => {
    placesAPI.getNearby(city, 'HOSPITAL')
      .then(res => setPlaces(res.data.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [city]);

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="card bg-red-50 border-red-300 border-2">
        <div className="flex items-start gap-4">
          <AlertTriangle className="w-12 h-12 text-red-600 flex-shrink-0" />
          <div>
            <h1 className="text-2xl font-bold text-red-800">Emergency Assistance</h1>
            <p className="text-red-700 mt-2">
              If you are experiencing severe symptoms such as chest pain, difficulty breathing,
              or loss of consciousness, <strong>seek immediate medical attention</strong>.
            </p>
            <p className="text-sm text-red-600 mt-2">
              This system provides educational AI predictions only — it cannot diagnose emergencies.
            </p>
          </div>
        </div>
      </div>

      <div>
        <h2 className="text-lg font-semibold mb-4 flex items-center gap-2">
          <Phone className="w-5 h-5" /> Emergency Contacts
        </h2>
        <div className="grid md:grid-cols-2 gap-3">
          {EMERGENCY_CONTACTS.map(c => (
            <div key={c.number} className="card flex items-center justify-between">
              <div>
                <p className="font-semibold">{c.name}</p>
                <p className="text-sm text-gray-500">{c.description}</p>
              </div>
              <a href={`tel:${c.number}`} className="text-2xl font-bold text-red-600 hover:text-red-700">
                {c.number}
              </a>
            </div>
          ))}
        </div>
      </div>

      <div>
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-semibold flex items-center gap-2">
            <Siren className="w-5 h-5" /> Nearby Hospitals
          </h2>
          <input value={city} onChange={e => setCity(e.target.value)}
            className="input-field w-40 text-sm" placeholder="City" />
        </div>
        {loading ? <LoadingSpinner /> : (
          <div className="grid md:grid-cols-2 gap-3">
            {places.map(p => (
              <div key={p.id} className="card">
                <p className="font-semibold">{p.name}</p>
                <p className="text-sm text-gray-500 mt-1">{p.address}</p>
                <p className="text-sm text-primary-600 mt-2 flex items-center gap-1">
                  <Phone className="w-3 h-3" /> {p.phone}
                </p>
                <p className="text-xs text-gray-400 mt-1">{p.openingHours}</p>
              </div>
            ))}
          </div>
        )}
      </div>

      <div>
        <h2 className="text-lg font-semibold mb-4">Healthcare Categories</h2>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
          {CATEGORIES.map(({ type, label, icon: Icon, color }) => (
            <a key={type} href={`/nearby?type=${type}`}
              className="card text-center hover:shadow-md transition-shadow cursor-pointer">
              <div className={`w-12 h-12 rounded-xl flex items-center justify-center mx-auto mb-2 ${color}`}>
                <Icon className="w-6 h-6" />
              </div>
              <p className="font-medium text-sm">{label}</p>
            </a>
          ))}
        </div>
      </div>
    </div>
  );
}
