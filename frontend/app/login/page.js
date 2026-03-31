'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import api from '../../services/api';

export default function Login() {
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const response = await api.post('/auth/login', formData);
      const { apiKey, userId } = response.data;
      if (apiKey) {
        localStorage.setItem('apiKey', apiKey);
        localStorage.setItem('userId', userId);
        // Force refresh state for Navbar
        window.dispatchEvent(new Event('storage'));
        router.push('/');
      } else {
        setError('Login failed: Invalid data received');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed. Please check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container-sm animate">
      <div className="card glass">
        <h1 className="text-center mb-2">Welcome Back</h1>
        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label>Email Address</label>
            <input
              type="email"
              required
              placeholder="name@company.com"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              required
              placeholder="••••••••"
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
            />
          </div>
          {error && <p className="status-error status-badge mb-1">{error}</p>}
          <button type="submit" disabled={loading} className="btn btn-primary w-full" style={{ width: '100%' }}>
            {loading ? 'Logging in...' : 'Sign In'}
          </button>
        </form>
        <p className="text-center mt-1" style={{ fontSize: '0.875rem', marginTop: '1.5rem', color: 'var(--text-muted)' }}>
          Don't have an account? <a href="/register" style={{ color: 'var(--primary)' }}>Register</a>
        </p>
      </div>
    </div>
  );
}
