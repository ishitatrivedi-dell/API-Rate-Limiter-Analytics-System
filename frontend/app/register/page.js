'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import api from '../../services/api';

export default function Register() {
  const [formData, setFormData] = useState({ name: '', email: '', password: '' });
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const handleRegister = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(null);
    try {
      const response = await api.post('/auth/register', formData);
      if (response.data.userId) {
        localStorage.setItem('userId', response.data.userId);
      }
      setSuccess('Registration successful! Please login.');
      setTimeout(() => router.push('/login'), 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container-sm animate">
      <div className="card glass">
        <h1 className="text-center mb-2">Create Account</h1>
        <form onSubmit={handleRegister}>
          <div className="form-group">
            <label>Full Name</label>
            <input
              type="text"
              required
              placeholder="John Doe"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            />
          </div>
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
          {success && <p className="status-success status-badge mb-1">{success}</p>}
          <button type="submit" disabled={loading} className="btn btn-primary w-full" style={{ width: '100%' }}>
            {loading ? 'Wait...' : 'Get Started'}
          </button>
        </form>
        <p className="text-center mt-1" style={{ fontSize: '0.875rem', marginTop: '1.5rem', color: 'var(--text-muted)' }}>
          Already have an account? <a href="/login" style={{ color: 'var(--primary)' }}>Login</a>
        </p>
      </div>
    </div>
  );
}
