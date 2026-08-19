import { Link } from 'react-router-dom';
import { Stethoscope, Users, Shield, ArrowRight, Activity, Brain, Calendar, FileText, MapPin } from 'lucide-react';
import HealixLogo from '../components/HealixLogo';

export default function Home() {
  return (
    <div className="min-h-screen healix-hero">
      <nav className="max-w-7xl mx-auto px-4 py-6 flex items-center justify-between">
        <Link to="/">
          <HealixLogo size="lg" />
        </Link>
        <div className="flex gap-3 flex-wrap justify-end">
          <Link to="/doctor-login" className="btn-secondary hidden sm:inline-block">Doctor Login</Link>
          <Link to="/admin-login" className="btn-secondary hidden md:inline-block">Admin</Link>
          <Link to="/register" className="btn-secondary">Register</Link>
          <Link to="/login" className="btn-primary">Patient Login</Link>
        </div>
      </nav>

      <section className="max-w-7xl mx-auto px-4 py-16 md:py-24 text-center animate-fade-in">
        <div className="inline-flex items-center gap-2 bg-white/80 backdrop-blur border border-primary-100 text-primary-700 px-4 py-1.5 rounded-full text-sm font-semibold mb-6 shadow-sm">
          <Brain className="w-4 h-4" />
          Smart Healthcare Platform
        </div>
        <h1 className="text-4xl md:text-6xl font-extrabold text-slate-900 leading-tight tracking-tight">
          Your Health Journey<br />
          <span className="text-primary-600">Starts with Healix</span>
        </h1>
        <p className="mt-6 text-lg text-slate-600 max-w-3xl mx-auto leading-relaxed">
          Healix is an AI-assisted healthcare platform that helps you understand symptoms, receive
          specialist recommendations, book verified doctors, manage appointments, and access prescriptions —
          all in one secure place.
        </p>
        <div className="mt-8 flex gap-4 justify-center flex-wrap">
          <Link to="/register" className="btn-primary px-8 py-3 text-lg flex items-center gap-2">
            Get Started Free <ArrowRight className="w-5 h-5" />
          </Link>
          <Link to="/login" className="btn-secondary px-8 py-3 text-lg">Sign In</Link>
        </div>
      </section>

      <section className="max-w-7xl mx-auto px-4 py-12">
        <h2 className="text-2xl font-bold text-center text-slate-900 mb-10">Everything You Need for Better Care</h2>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {[
            { icon: Stethoscope, title: 'AI Symptom Analysis', desc: 'Enter your symptoms and receive intelligent preliminary health insights with risk assessment and specialist guidance.' },
            { icon: Users, title: 'Verified Doctors', desc: 'Browse experienced, admin-verified doctors filtered by specialization, location, rating, and consultation fee.' },
            { icon: Calendar, title: 'Smart Appointments', desc: 'Book available time slots instantly, receive appointment letters, and track consultation status in real time.' },
            { icon: FileText, title: 'Digital Prescriptions', desc: 'Doctors create and share prescriptions digitally. Download, print, and manage your medical documents anytime.' },
            { icon: Activity, title: 'Diet & Wellness Plans', desc: 'Get condition-specific diet and lifestyle guidance after every prediction to support your recovery journey.' },
            { icon: MapPin, title: 'Nearby Healthcare', desc: 'Find hospitals, clinics, pharmacies, and diagnostic centers near you whenever you need care quickly.' },
          ].map(({ icon: Icon, title, desc }) => (
            <div key={title} className="card hover:shadow-healix-lg hover:border-primary-200 transition-all duration-300">
              <div className="w-12 h-12 bg-primary-100 rounded-xl flex items-center justify-center mb-4">
                <Icon className="w-6 h-6 text-primary-600" />
              </div>
              <h3 className="text-lg font-semibold text-slate-900 mb-2">{title}</h3>
              <p className="text-slate-600 text-sm leading-relaxed">{desc}</p>
            </div>
          ))}
        </div>
      </section>

      <section className="max-w-7xl mx-auto px-4 py-12">
        <div className="rounded-2xl bg-gradient-to-br from-primary-700 to-primary-900 text-white text-center py-12 px-6 shadow-healix-lg">
          <Shield className="w-12 h-12 mx-auto mb-4 opacity-90" />
          <h2 className="text-2xl font-bold mb-3">Secure. Private. Role-Based Access.</h2>
          <p className="text-primary-100 max-w-2xl mx-auto">
            Healix uses encrypted authentication with separate dashboards for patients, doctors, and administrators.
            Your health data stays protected with industry-standard security practices.
          </p>
        </div>
      </section>

      <footer className="border-t border-primary-100 bg-white/60 py-8 text-center text-sm text-slate-500">
        <div className="flex items-center justify-center gap-2 mb-2">
          <img src="/healix-logo.png" alt="" className="w-6 h-6 opacity-70" />
          <span className="font-semibold text-primary-800">Healix</span>
        </div>
        © 2026 Healix — Intelligent Healthcare Management Platform
      </footer>
    </div>
  );
}
