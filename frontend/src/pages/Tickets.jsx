import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api';
import { useAuth } from '../AuthContext';

export default function Tickets() {
  const { user } = useAuth();
  const [tickets, setTickets] = useState([]);
  const [q, setQ] = useState('');
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState('OTHER');
  const [priority, setPriority] = useState('MEDIUM');
  const [error, setError] = useState('');

  const load = async () => {
    try {
      const res = await api.get('/tickets', { params: q ? { q } : {} });
      setTickets(res.data);
    } catch (e) {
      setError('Failed to load tickets');
    }
  };

  useEffect(() => {
    load();
  }, []);

  const search = async (e) => {
    e.preventDefault();
    load();
  };

  const create = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await api.post('/tickets', { title, description, category, priority });
      setTitle('');
      setDescription('');
      load();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create');
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>Tickets</h2>
      {user?.role === 'CUSTOMER' && (
        <form onSubmit={create} style={{ marginBottom: '20px' }}>
          <h3>Create Ticket</h3>
          <div><input placeholder="Title" value={title} onChange={e => setTitle(e.target.value)} required /></div>
          <div><textarea placeholder="Description" value={description} onChange={e => setDescription(e.target.value)} required /></div>
          <div>
            <select value={category} onChange={e => setCategory(e.target.value)}>
              <option value="BILLING">Billing</option>
              <option value="TECHNICAL_ISSUE">Technical Issue</option>
              <option value="ACCOUNT_ACCESS">Account Access</option>
              <option value="OTHER">Other</option>
            </select>
            <select value={priority} onChange={e => setPriority(e.target.value)}>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
              <option value="CRITICAL">Critical</option>
            </select>
          </div>
          <button type="submit">Create</button>
        </form>
      )}
      <form onSubmit={search}>
        <input placeholder="Search..." value={q} onChange={e => setQ(e.target.value)} />
        <button type="submit">Search</button>
      </form>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <ul>
        {tickets.map(t => (
          <li key={t.id}>
            <Link to={`/tickets/${t.id}`}>{t.title}</Link> - {t.status} - {t.priority} - {t.category}
          </li>
        ))}
      </ul>
    </div>
  );
}
