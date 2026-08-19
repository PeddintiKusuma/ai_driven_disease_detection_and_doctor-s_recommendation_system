import { useEffect, useState } from 'react';
import { adminAPI } from '../api/api';
import { ClipboardList } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';

export default function AdminAuditLogs() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminAPI.getAuditLogs()
      .then(res => setLogs(res.data.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner fullScreen />;

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold flex items-center gap-2">
          <ClipboardList className="w-7 h-7 text-primary-600" /> Audit Logs
        </h1>
        <p className="text-gray-600">System activity log for security and administration</p>
      </div>

      <div className="card overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b text-left text-gray-500">
              <th className="pb-3 pr-4">Timestamp</th>
              <th className="pb-3 pr-4">User</th>
              <th className="pb-3 pr-4">Action</th>
              <th className="pb-3 pr-4">Entity</th>
              <th className="pb-3">Description</th>
            </tr>
          </thead>
          <tbody>
            {logs.map(log => (
              <tr key={log.id} className="border-b last:border-0 hover:bg-gray-50">
                <td className="py-3 pr-4 text-xs text-gray-500 whitespace-nowrap">
                  {new Date(log.createdAt).toLocaleString()}
                </td>
                <td className="py-3 pr-4 font-medium">{log.userName}</td>
                <td className="py-3 pr-4">
                  <span className="badge bg-gray-100 text-gray-700">{log.action}</span>
                </td>
                <td className="py-3 pr-4 text-gray-500">{log.entityType} #{log.entityId}</td>
                <td className="py-3 text-gray-600">{log.details}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {logs.length === 0 && (
          <p className="text-center py-8 text-gray-500">No audit logs yet</p>
        )}
      </div>
    </div>
  );
}
