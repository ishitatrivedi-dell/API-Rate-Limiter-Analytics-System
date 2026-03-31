'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import api from '../../services/api';

export default function Logs() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const router = useRouter();

  useEffect(() => {
    const apiKey = localStorage.getItem('apiKey');
    if (!apiKey) {
      router.push('/login');
      return;
    }

    const fetchLogs = async () => {
      try {
        const response = await api.get('/logs');
        setLogs(response.data.logs);
      } catch (err) {
        setError('Error fetching system logs');
      } finally {
        setLoading(false);
      }
    };

    fetchLogs();
  }, [router]);

  if (loading) return <div className="text-center mt-2 animate">Syncing traffic data...</div>;
  if (error) return <div className="text-center mt-2 animate status-error status-badge">{error}</div>;

  return (
    <div className="animate">
      <div className="flex justify-between align-center mb-2">
        <h1 style={{ fontSize: '2rem' }}>Request Logs</h1>
        <button onClick={() => window.location.reload()} className="btn btn-outline" style={{ fontSize: '0.875rem' }}>
          Refresh
        </button>
      </div>

      <div className="card glass">
        <table>
          <thead>
            <tr>
              <th>Method</th>
              <th>Endpoint</th>
              <th className="text-center">Status</th>
              <th className="text-right">Timestamp</th>
            </tr>
          </thead>
          <tbody>
            {(logs || []).map((log) => (
              <tr key={log.id}>
                <td style={{ fontWeight: '600', color: log.method === 'GET' ? 'var(--primary)' : 'var(--secondary)' }}>
                  {log.method}
                </td>
                <td style={{ color: 'var(--text-muted)' }}>{log.endpoint}</td>
                <td className="text-center">
                  <span className={`status-badge ${log.status < 400 ? 'status-success' : 'status-error'}`}>
                    {log.status}
                  </span>
                </td>
                <td className="text-right" style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>
                  {new Date(log.timestamp).toLocaleString()}
                </td>
              </tr>
            ))}
            {(!logs || logs.length === 0) && (
              <tr>
                <td colSpan="4" className="text-center" style={{ padding: '2rem', color: 'var(--text-muted)' }}>
                  No traffic logs found
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
