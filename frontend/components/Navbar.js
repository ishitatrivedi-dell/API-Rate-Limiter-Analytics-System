'use client';

import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';

export default function Navbar() {
  const pathname = usePathname();
  const router = useRouter();
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const checkLogin = () => {
      const apiKey = localStorage.getItem('apiKey');
      setIsLoggedIn(!!apiKey);
    };

    checkLogin();
    // Re-check on every mount or potentially listen for storage changes
    window.addEventListener('storage', checkLogin);
    return () => window.removeEventListener('storage', checkLogin);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('apiKey');
    setIsLoggedIn(false);
    router.push('/login');
    // Trigger storage event for other components if needed
    window.dispatchEvent(new Event('storage'));
  };

  const navItems = [
    { name: 'Dashboard', path: '/' },
    { name: 'Logs', path: '/logs' },
    { name: 'Rate Limit', path: '/rate-limit' },
    { name: 'API Key', path: '/api-key' },
  ];

  return (
    <nav className="navbar">
      <Link href="/" className="logo">
        <h2 style={{ background: 'linear-gradient(135deg, #6366f1 0%, #ec4899 100%)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent', fontWeight: 'bold' }}>
          RateShield
        </h2>
      </Link>
      <div className="nav-links">
        {isLoggedIn ? (
          <>
            {navItems.map((item) => (
              <Link
                key={item.path}
                href={item.path}
                className={`nav-link ${pathname === item.path ? 'active' : ''}`}
              >
                {item.name}
              </Link>
            ))}
            <button onClick={handleLogout} className="btn btn-outline" style={{ padding: '0.5rem 1rem', fontSize: '0.875rem' }}>
              Logout
            </button>
          </>
        ) : (
          <>
            <Link href="/login" className="nav-link">Login</Link>
            <Link href="/register" className="nav-link btn btn-primary" style={{ padding: '0.5rem 1rem', fontSize: '0.875rem' }}>
              Get Started
            </Link>
          </>
        )}
      </div>
    </nav>
  );
}
