'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import api from '../../services/api';

export default function RateLimit() {
  const [rules, setRules] = useState([]);
  const [formData, setFormData] = useState({ limitCount: 10, timeWindow: 60 });
  const [editingId, setEditingId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const router = useRouter();

  useEffect(() => {
    const apiKey = localStorage.getItem('apiKey');
    if (!apiKey) {
      router.push('/login');
      return;
    }

    const fetchRules = async () => {
      try {
        const response = await api.get('/rate-limit');
        setRules(response.data.rules);
      } catch (err) {
        setError('Error fetching rate limit rules');
      } finally {
        setLoading(false);
      }
    };

    fetchRules();
  }, [router]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    const userId = localStorage.getItem('userId');
    const dataToSend = { ...formData, userId: parseInt(userId) };
    
    try {
      if (editingId) {
        await api.put(`/rate-limit/${editingId}`, dataToSend);
      } else {
        await api.post('/rate-limit', dataToSend);
      }
      setFormData({ limitCount: 10, timeWindow: 60 });
      setEditingId(null);
      // Refresh list
      const response = await api.get('/rate-limit');
      setRules(response.data.rules);
    } catch (err) {
      setError('Failed to save rate limit rule');
    } finally {
      setSubmitting(false);
    }
  };

  const handleEdit = (rule) => {
    setEditingId(rule.id);
    setFormData({ limitCount: rule.limitCount, timeWindow: rule.timeWindow });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleDelete = async (id) => {
    if (!confirm('Are you sure you want to delete this rule?')) return;
    try {
      await api.delete(`/rate-limit/${id}`);
      setRules(rules.filter(rule => rule.id !== id));
    } catch (err) {
      setError('Failed to delete rule');
    }
  };

  if (loading) return <div className="text-center mt-2 animate">Syncing security rules...</div>;
  if (error) return <div className="text-center mt-2 animate status-error status-badge">{error}</div>;

  return (
    <div className="animate">
      <div className="mb-2">
        <h1 style={{ fontSize: '2rem' }}>Rate Limit Policies</h1>
        <p style={{ color: 'var(--text-muted)' }}>Configure access controls for your endpoints</p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '2rem' }}>
        {/* Policy Form */}
        <div className="card glass">
          <h3 className="mb-1">{editingId ? 'Update Policy' : 'Create New Policy'}</h3>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Requests Limit</label>
              <input
                type="number"
                required
                placeholder="Max requests (e.g., 100)"
                value={formData.limitCount || ''}
                onChange={(e) => setFormData({ ...formData, limitCount: e.target.value === '' ? '' : parseInt(e.target.value) })}
              />
            </div>
            <div className="form-group">
              <label>Time Window (seconds)</label>
              <input
                type="number"
                required
                placeholder="Interval (e.g., 60)"
                value={formData.timeWindow || ''}
                onChange={(e) => setFormData({ ...formData, timeWindow: e.target.value === '' ? '' : parseInt(e.target.value) })}
              />
            </div>
            <button type="submit" disabled={submitting} className="btn btn-primary w-full" style={{ width: '100%' }}>
              {submitting ? 'Updating...' : editingId ? 'Save Changes' : 'Activate Policy'}
            </button>
            {editingId && (
              <button onClick={() => { setEditingId(null); setFormData({ limitCount: 10, timeWindow: 60 }); }} className="btn btn-outline" style={{ width: '100%', marginTop: '0.5rem' }}>
                Cancel Edit
              </button>
            )}
          </form>
        </div>

        {/* Rules Table */}
        <div className="card glass">
          <table>
            <thead>
              <tr>
                <th>Limit</th>
                <th>Interval</th>
                <th className="text-right">Manage</th>
              </tr>
            </thead>
            <tbody>
              {rules.map((rule) => (
                <tr key={rule.id}>
                  <td>{rule.limitCount} total</td>
                  <td>every {rule.timeWindow}s</td>
                  <td className="text-right">
                    <button onClick={() => handleEdit(rule)} className="btn btn-outline" style={{ padding: '0.35rem 0.75rem', fontSize: '0.8rem', marginRight: '0.5rem' }}>
                      Edit
                    </button>
                    <button onClick={() => handleDelete(rule.id)} className="btn btn-danger" style={{ padding: '0.35rem 0.75rem', fontSize: '0.8rem' }}>
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
              {rules.length === 0 && (
                <tr>
                  <td colSpan="3" className="text-center" style={{ padding: '2rem', color: 'var(--text-muted)' }}>
                    No security policies configured
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
