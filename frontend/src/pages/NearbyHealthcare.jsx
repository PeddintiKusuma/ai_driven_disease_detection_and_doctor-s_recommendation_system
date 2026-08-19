import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { placesAPI } from '../api/api';
import { MapPin, Phone, Building2, Pill, Microscope } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';

const TYPES = [
  { value: '', label: 'All', icon: MapPin },
  { value: 'HOSPITAL', label: 'Hospitals', icon: Building2 },
  { value: 'MEDICAL_STORE', label: 'Medical Stores', icon: Pill },
  { value: 'CLINIC', label: 'Clinics', icon: MapPin },
  { value: 'LAB', label: 'Diagnostic Centers', icon: Microscope },
];

export default function NearbyHealthcare() {
  const [searchParams] = useSearchParams();
  const [places, setPlaces] = useState([]);
  const [loading, setLoading] = useState(true);
  const [city, setCity] = useState('Hyderabad');
  const [type, setType] = useState(searchParams.get('type') || '');

  useEffect(() => {
    setLoading(true);
    placesAPI.getNearby(city, type || undefined)
      .then(res => setPlaces(res.data.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [city, type]);

  const typeIcon = (t) => ({
    HOSPITAL: Building2, MEDICAL_STORE: Pill, CLINIC: MapPin, LAB: Microscope
  }[t] || MapPin);

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold flex items-center gap-2">
          <MapPin className="w-7 h-7 text-primary-600" /> Nearby Healthcare
        </h1>
        <p className="text-gray-600">Find hospitals, clinics, pharmacies & diagnostic centers near you</p>
      </div>

      <div className="flex flex-wrap gap-3">
        <input value={city} onChange={e => setCity(e.target.value)}
          className="input-field w-48" placeholder="Enter city..." />
        <div className="flex flex-wrap gap-2">
          {TYPES.map(({ value, label, icon: Icon }) => (
            <button key={value} onClick={() => setType(value)}
              className={`flex items-center gap-1 px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                type === value ? 'bg-primary-600 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}>
              <Icon className="w-4 h-4" /> {label}
            </button>
          ))}
        </div>
      </div>

      {loading ? <LoadingSpinner /> : places.length === 0 ? (
        <div className="card text-center py-12 text-gray-500">No places found in {city}</div>
      ) : (
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
          {places.map(place => {
            const Icon = typeIcon(place.type);
            return (
              <div key={place.id} className="card">
                <div className="flex items-start gap-3">
                  <div className="w-10 h-10 bg-primary-100 rounded-lg flex items-center justify-center flex-shrink-0">
                    <Icon className="w-5 h-5 text-primary-600" />
                  </div>
                  <div>
                    <p className="font-semibold">{place.name}</p>
                    <p className="text-xs text-primary-600 mt-0.5">{place.type.replace('_', ' ')}</p>
                    <p className="text-sm text-gray-500 mt-2">{place.address}</p>
                    <p className="text-sm text-gray-600 mt-1 flex items-center gap-1">
                      <Phone className="w-3 h-3" /> {place.phone}
                    </p>
                    <p className="text-xs text-gray-400 mt-1">{place.openingHours}</p>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
