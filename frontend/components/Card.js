export default function Card({ title, value, detail, type = 'default' }) {
  const getTheme = () => {
    switch (type) {
      case 'success': return { background: 'rgba(16, 185, 129, 0.05)', border: 'rgba(16, 185, 129, 0.2)' };
      case 'error': return { background: 'rgba(239, 68, 68, 0.05)', border: 'rgba(239, 68, 68, 0.2)' };
      case 'warning': return { background: 'rgba(245, 158, 11, 0.05)', border: 'rgba(245, 158, 11, 0.2)' };
      default: return { background: 'var(--surface)', border: 'var(--surface-border)' };
    }
  };

  const theme = getTheme();

  return (
    <div className="card glass" style={{ background: theme.background, borderColor: theme.border }}>
      <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', fontWeight: 500, marginBottom: '0.5rem' }}>
        {title}
      </p>
      <h2 style={{ fontSize: '1.875rem', fontWeight: 'bold' }}>{value}</h2>
      {detail && (
        <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.5rem' }}>
          {detail}
        </p>
      )}
    </div>
  );
}
