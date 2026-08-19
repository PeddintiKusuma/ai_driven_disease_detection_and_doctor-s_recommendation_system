import { Loader2 } from 'lucide-react';

export default function LoadingSpinner({ fullScreen, message }) {
  if (fullScreen) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-gray-50">
        <Loader2 className="w-10 h-10 text-primary-600 animate-spin" />
        {message && <p className="mt-4 text-gray-600">{message}</p>}
      </div>
    );
  }

  return (
    <div className="flex items-center justify-center p-8">
      <Loader2 className="w-8 h-8 text-primary-600 animate-spin" />
      {message && <span className="ml-3 text-gray-600">{message}</span>}
    </div>
  );
}
