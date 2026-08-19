import { Link } from 'react-router-dom';
import { Star, MapPin, Clock, IndianRupee, Calendar, Award, Stethoscope } from 'lucide-react';

export default function FeaturedDoctorCard({ doctor, onBook }) {
  return (
    <div className="card hover:shadow-lg transition-all border border-gray-100">
      <div className="flex items-start gap-4">
        <div className="w-16 h-16 bg-gradient-to-br from-primary-100 to-primary-200 rounded-2xl flex items-center justify-center flex-shrink-0">
          <Stethoscope className="w-8 h-8 text-primary-600" />
        </div>
        <div className="flex-1 min-w-0">
          <div className="flex items-start justify-between gap-2">
            <div>
              <h3 className="text-lg font-bold text-gray-900">{doctor.name}</h3>
              <p className="text-primary-600 font-medium">{doctor.specialization}</p>
            </div>
            <div className="flex items-center gap-1 bg-yellow-50 px-2 py-1 rounded-lg flex-shrink-0">
              <Star className="w-4 h-4 text-yellow-500 fill-yellow-500" />
              <span className="text-sm font-bold">{doctor.rating}</span>
            </div>
          </div>

          <div className="mt-3 grid sm:grid-cols-2 gap-2 text-sm text-gray-600">
            <p className="flex items-center gap-1.5">
              <Award className="w-4 h-4 text-gray-400 flex-shrink-0" />
              <span className="truncate">{doctor.qualification}</span>
            </p>
            <p className="flex items-center gap-1.5">
              <Stethoscope className="w-4 h-4 text-gray-400 flex-shrink-0" />
              {doctor.experience} years experience
            </p>
            <p className="flex items-center gap-1.5">
              <MapPin className="w-4 h-4 text-gray-400 flex-shrink-0" />
              {doctor.hospital}, {doctor.city}
            </p>
            <p className="flex items-center gap-1.5">
              <IndianRupee className="w-4 h-4 text-gray-400 flex-shrink-0" />
              ₹{doctor.consultationFee} consultation
            </p>
            <p className="flex items-center gap-1.5 sm:col-span-2">
              <Clock className="w-4 h-4 text-gray-400 flex-shrink-0" />
              {doctor.availableDays} · {doctor.availableTime}
            </p>
          </div>

          {doctor.bio && (
            <p className="mt-3 text-sm text-gray-500 line-clamp-2 leading-relaxed">{doctor.bio}</p>
          )}

          {doctor.licenseNumber && (
            <p className="mt-2 text-xs text-gray-400">License: {doctor.licenseNumber}</p>
          )}

          <div className="mt-4 flex gap-2">
            <Link to={`/doctor/${doctor.id}`} className="btn-secondary flex-1 text-center text-sm py-2">
              View Profile
            </Link>
            {onBook && (
              <button onClick={() => onBook(doctor)} className="btn-primary flex-1 text-sm py-2 flex items-center justify-center gap-1">
                <Calendar className="w-4 h-4" /> Book Now
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
