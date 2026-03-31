'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import api from '../services/api';
import Card from '../components/Card';

export default function Dashboard() {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const router = useRouter();

  useEffect(() => {
    const apiKey = localStorage.getItem('apiKey');
    if (!apiKey) {
      router.push('/login');
      return;
    }

    const fetchSummary = async () => {
      try {
        const response = await api.get('/analytics/summary');
        setSummary(response.data.analytics);
      } catch (err) {
        setError('Error fetching analytics summary');
      } finally {
        setLoading(false);
      }
    };

    fetchSummary();
  }, [router]);

  if (loading) return <div className="text-center mt-2 animate">Loading system metrics...</div>;
  if (error) return <div className="text-center mt-2 animate status-error status-badge">{error}</div>;

  return (
    <div className="animate">
      <div className="flex justify-between align-center mb-2">
        <h1 style={{ fontSize: '2rem' }}>Traffic Overview</h1>
        <div style={{ padding: '0.5rem 1rem', background: 'var(--glass)', border: '1px solid var(--glass-border)', borderRadius: '20px', fontSize: '0.875rem' }}>
          Live Stats
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '1.5rem', marginBottom: '3rem' }}>
        <Card title="Total Requests" value={summary?.totalRequests || 0} detail="Last 24 hours" />
        <Card title="Successful" value={summary?.successCount || 0} type="success" detail={`${((summary?.successCount / summary?.totalRequests) * 100).toFixed(1) || 0}% success rate`} />
        <Card title="Blocked / Failed" value={summary?.failedCount || 0} type="error" detail="Rate limited attempts" />
      </div>

      <h2 className="mb-1">Endpoint Distribution</h2>
      <div className="card glass">
        <table>
          <thead>
            <tr>
              <th>Endpoint Path</th>
              <th className="text-center">Hit Count</th>
              <th className="text-right">Action</th>
            </tr>
          </thead>
          <tbody>
            {summary?.endpointHits && Object.entries(summary.endpointHits).map(([endpoint, hits]) => (
              <tr key={endpoint}>
                <td style={{ fontFamily: 'monospace', color: 'var(--primary)' }}>{endpoint}</td>
                <td className="text-center">{hits}</td>
                <td className="text-right">
                  <a href={`/logs?endpoint=${endpoint}`} style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>View Logs</a>
                </td>
              </tr>
            ))}
            {(!summary?.endpointHits || Object.keys(summary.endpointHits).length === 0) && (
              <tr>
                <td colSpan="3" className="text-center" style={{ padding: '2rem', color: 'var(--text-muted)' }}>
                  No endpoint data available yet
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
