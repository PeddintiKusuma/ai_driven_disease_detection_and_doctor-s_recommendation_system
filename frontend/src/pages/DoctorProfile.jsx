import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { doctorAPI, favoriteAPI, reviewAPI, appointmentAPI } from '../api/api';
import { Star, MapPin, Clock, IndianRupee, Calendar, ArrowLeft, Stethoscope, Award, Building2, Heart } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';
import toast from 'react-hot-toast';

export default function DoctorProfile() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isFavorite, setIsFavorite] = useState(false);
  const [showReview, setShowReview] = useState(false);
  const [reviewForm, setReviewForm] = useState({ rating: 5, comment: '' });
  const [completedAppt, setCompletedAppt] = useState(null);

  useEffect(() => {
    Promise.all([
      doctorAPI.getById(id),
      favoriteAPI.getAll().catch(() => ({ data: { data: [] } })),
      appointmentAPI.getAll().catch(() => ({ data: { data: [] } })),
    ]).then(([profileRes, favRes, apptRes]) => {
      setProfile(profileRes.data.data);
      setIsFavorite(favRes.data.data.some(d => d.id === parseInt(id)));
      const completed = apptRes.data.data.find(
        a => a.doctor?.id === parseInt(id) && a.status === 'COMPLETED'
      );
      setCompletedAppt(completed);
    }).catch(console.error).finally(() => setLoading(false));
  }, [id]);

  const toggleFavorite = async () => {
    try {
      if (isFavorite) {
        await favoriteAPI.remove(id);
        setIsFavorite(false);
        toast.success('Removed from favorites');
      } else {
        await favoriteAPI.add(id);
        setIsFavorite(true);
        toast.success('Added to favorites');
      }
    } catch { toast.error('Failed to update favorites'); }
  };

  const handleReview = async (e) => {
    e.preventDefault();
    if (!completedAppt) return;
    try {
      await reviewAPI.submit({
        doctorId: parseInt(id),
        appointmentId: completedAppt.id,
        rating: reviewForm.rating,
        comment: reviewForm.comment,
      });
      toast.success('Review submitted!');
      setShowReview(false);
      const res = await doctorAPI.getById(id);
      setProfile(res.data.data);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to submit review');
    }
  };

  if (loading) return <LoadingSpinner fullScreen />;
  if (!profile) return <div className="text-center py-12">Doctor not found</div>;

  const { doctor, reviews, reviewCount } = profile;

  return (
    <div className="max-w-3xl mx-auto space-y-6 animate-fade-in">
      <button onClick={() => navigate(-1)} className="flex items-center gap-2 text-gray-600 hover:text-gray-900">
        <ArrowLeft className="w-4 h-4" /> Back
      </button>

      <div className="card">
        <div className="flex items-start gap-6">
          <div className="w-24 h-24 bg-primary-100 rounded-2xl flex items-center justify-center">
            <Stethoscope className="w-10 h-10 text-primary-600" />
          </div>
          <div className="flex-1">
            <div className="flex items-start justify-between">
              <div>
                <h1 className="text-2xl font-bold">{doctor.name}</h1>
                <p className="text-primary-600 font-medium text-lg">{doctor.specialization}</p>
              </div>
              <button onClick={toggleFavorite} className={`p-2 rounded-lg ${isFavorite ? 'text-red-500 bg-red-50' : 'text-gray-400 bg-gray-50 hover:text-red-500'}`}>
                <Heart className={`w-6 h-6 ${isFavorite ? 'fill-red-500' : ''}`} />
              </button>
            </div>
            <div className="flex items-center gap-1 mt-2">
              {[...Array(5)].map((_, i) => (
                <Star key={i} className={`w-5 h-5 ${i < Math.round(doctor.rating) ? 'text-yellow-500 fill-yellow-500' : 'text-gray-300'}`} />
              ))}
              <span className="font-semibold ml-1">{doctor.rating}</span>
              <span className="text-gray-500 text-sm ml-1">({reviewCount || reviews?.length || 0} reviews)</span>
            </div>
          </div>
        </div>

        <div className="grid md:grid-cols-2 gap-4 mt-6">
          {[
            { icon: Award, label: 'Qualification', value: doctor.qualification },
            { icon: Stethoscope, label: 'Experience', value: `${doctor.experience} years` },
            { icon: Building2, label: 'Hospital', value: doctor.hospital },
            { icon: MapPin, label: 'Location', value: doctor.city },
            { icon: IndianRupee, label: 'Consultation Fee', value: `₹${doctor.consultationFee}` },
            { icon: Clock, label: 'Availability', value: `${doctor.availableDays} · ${doctor.availableTime}` },
          ].map(({ icon: Icon, label, value }) => (
            <div key={label} className="flex items-center gap-2 text-sm p-3 bg-gray-50 rounded-lg">
              <Icon className="w-4 h-4 text-gray-400" />
              <div><p className="text-gray-500 text-xs">{label}</p><p className="font-medium">{value}</p></div>
            </div>
          ))}
        </div>

        {doctor.bio && <p className="mt-4 text-gray-600 text-sm border-t pt-4">{doctor.bio}</p>}

        <button onClick={() => navigate('/appointments', { state: { doctor } })}
          className="btn-primary w-full mt-6 py-3 flex items-center justify-center gap-2">
          <Calendar className="w-5 h-5" /> Book Appointment — ₹{doctor.consultationFee}
        </button>
      </div>

      {completedAppt && (
        <div className="card">
          {!showReview ? (
            <button onClick={() => setShowReview(true)} className="btn-secondary w-full">
              Rate Your Consultation
            </button>
          ) : (
            <form onSubmit={handleReview} className="space-y-4">
              <h3 className="font-semibold">Rate your consultation</h3>
              <div className="flex gap-1">
                {[1, 2, 3, 4, 5].map(r => (
                  <button key={r} type="button" onClick={() => setReviewForm({ ...reviewForm, rating: r })}>
                    <Star className={`w-8 h-8 ${r <= reviewForm.rating ? 'text-yellow-500 fill-yellow-500' : 'text-gray-300'}`} />
                  </button>
                ))}
              </div>
              <textarea value={reviewForm.comment} onChange={e => setReviewForm({ ...reviewForm, comment: e.target.value })}
                className="input-field" rows={3} placeholder="Write your review..." />
              <div className="flex gap-2">
                <button type="submit" className="btn-primary flex-1">Submit Review</button>
                <button type="button" onClick={() => setShowReview(false)} className="btn-secondary">Cancel</button>
              </div>
            </form>
          )}
        </div>
      )}

      {reviews?.length > 0 && (
        <div className="card">
          <h2 className="text-lg font-semibold mb-4">Patient Reviews</h2>
          {reviews.map(r => (
            <div key={r.id} className="border-b last:border-0 pb-4 mb-4">
              <div className="flex items-center gap-1">
                {[...Array(5)].map((_, i) => (
                  <Star key={i} className={`w-4 h-4 ${i < r.rating ? 'text-yellow-500 fill-yellow-500' : 'text-gray-300'}`} />
                ))}
              </div>
              <p className="text-sm text-gray-600 mt-2">{r.comment}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
