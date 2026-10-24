import React, { useEffect, useState } from 'react';
import api from '../api';

export default function Dashboard() {
  const [stats, setStats] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get('/dashboard').then(res => setStats(res.data)).catch(e => setError('Failed to load'));
  }, []);

  if (!stats) return <div style={{ padding: '20px' }}>Loading...</div>;

  return (
    <div style={{ padding: '20px' }}>
      <h2>Dashboard</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <p>Total Tickets: {stats.totalTickets}</p>
      <p>Recent (7 days): {stats.recentTickets}</p>
      <h3>By Status</h3>
      <ul>
        {Object.entries(stats.byStatus || {}).map(([k, v]) => <li key={k}>{k}: {v}</li>)}
      </ul>
      <h3>By Priority</h3>
      <ul>
        {Object.entries(stats.byPriority || {}).map(([k, v]) => <li key={k}>{k}: {v}</li>)}
      </ul>
      <h3>By Category</h3>
      <ul>
        {Object.entries(stats.byCategory || {}).map(([k, v]) => <li key={k}>{k}: {v}</li>)}
      </ul>
    </div>
  );
}
