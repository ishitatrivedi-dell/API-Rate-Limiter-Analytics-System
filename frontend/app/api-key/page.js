'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';

export default function APIKey() {
  const [apiKey, setApiKey] = useState('');
  const [copied, setCopied] = useState(false);
  const router = useRouter();

  useEffect(() => {
    const key = localStorage.getItem('apiKey');
    if (!key) {
      router.push('/login');
      return;
    }
    setApiKey(key);
  }, [router]);

  const handleCopy = () => {
    navigator.clipboard.writeText(apiKey);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="container-sm animate">
      <div className="card glass">
        <h1 className="text-center mb-2">My API Access Key</h1>
        <p className="text-center mb-2" style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
          Use this key in your request headers as <code style={{ color: 'var(--primary)', background: '#1e293b', padding: '0.2rem 0.4rem', borderRadius: '4px' }}>X-API-KEY</code>.
        </p>

        <div className="form-group" style={{ position: 'relative' }}>
          <input
            type="text"
            readOnly
            value={apiKey}
            style={{ paddingRight: '4rem', fontFamily: 'monospace', letterSpacing: '1px' }}
          />
          <button
            onClick={handleCopy}
            className="btn btn-primary"
            style={{ position: 'absolute', right: '4px', top: '4px', height: '36px', fontSize: '0.75rem', padding: '0 0.75rem' }}
          >
            {copied ? 'COPIED' : 'COPY'}
          </button>
        </div>

        <div style={{ background: 'rgba(245, 158, 11, 0.05)', border: '1px solid rgba(245, 158, 11, 0.2)', padding: '1rem', borderRadius: '8px', marginTop: '2rem' }}>
          <p style={{ color: 'var(--warning)', fontSize: '0.8rem', fontWeight: 600 }}>SECURITY WARNING</p>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.75rem', marginTop: '0.5rem' }}>
            Never share this key with anyone. It provides full access to your API metrics and allows configuring security policies.
          </p>
        </div>
      </div>
    </div>
  );
}
