import { Link } from 'react-router-dom';
import { Star, MapPin, Clock, IndianRupee, Calendar } from 'lucide-react';

export default function DoctorCard({ doctor, onBook }) {
  return (
    <div className="card hover:shadow-md transition-shadow animate-fade-in">
      <div className="flex items-start justify-between">
        <div>
          <h3 className="text-lg font-semibold text-gray-900">{doctor.name}</h3>
          <p className="text-primary-600 text-sm font-medium">{doctor.specialization}</p>
        </div>
        <div className="flex items-center gap-1 bg-yellow-50 px-2 py-1 rounded-lg">
          <Star className="w-4 h-4 text-yellow-500 fill-yellow-500" />
          <span className="text-sm font-semibold">{doctor.rating}</span>
        </div>
      </div>

      <div className="mt-4 space-y-2 text-sm text-gray-600">
        <p className="flex items-center gap-2">
          <MapPin className="w-4 h-4 text-gray-400" />
          {doctor.hospital}, {doctor.city}
        </p>
        <p>{doctor.experience} years experience • {doctor.qualification}</p>
        <p className="flex items-center gap-2">
          <IndianRupee className="w-4 h-4 text-gray-400" />
          ₹{doctor.consultationFee} consultation fee
        </p>
        <p className="flex items-center gap-2">
          <Clock className="w-4 h-4 text-gray-400" />
          {doctor.availableDays} • {doctor.availableTime}
        </p>
      </div>

      <div className="mt-4 flex gap-2">
        <Link to={`/doctor/${doctor.id}`} className="btn-secondary flex-1 text-center text-sm py-2">
          View Profile
        </Link>
        {onBook && (
          <button onClick={() => onBook(doctor)} className="btn-primary flex-1 text-sm py-2 flex items-center justify-center gap-1">
            <Calendar className="w-4 h-4" />
            Book
          </button>
        )}
      </div>
    </div>
  );
}
