import React from 'react';
import { Routes, Route, Link, Navigate } from 'react-router-dom';
import { useAuth } from './AuthContext';
import Login from './pages/Login';
import Register from './pages/Register';
import Tickets from './pages/Tickets';
import TicketDetail from './pages/TicketDetail';
import Dashboard from './pages/Dashboard';
import Knowledge from './pages/Knowledge';

function Nav() {
  const { user, logout } = useAuth();
  return (
    <nav style={{ padding: '10px', borderBottom: '1px solid #ccc', marginBottom: '20px' }}>
      <Link to="/tickets">Tickets</Link> | <Link to="/knowledge">Knowledge</Link> |{' '}
      {user && (user.role === 'STAFF' || user.role === 'ADMIN') && <><Link to="/dashboard">Dashboard</Link> | </>}
      {user ? (
        <>
          <span>Signed in as {user.fullName} ({user.role})</span> | <button onClick={logout}>Logout</button>
        </>
      ) : (
        <>
          <Link to="/login">Login</Link> | <Link to="/register">Register</Link>
        </>
      )}
    </nav>
  );
}

function Protected({ children, roles }) {
  const { user, loading } = useAuth();
  if (loading) return <div>Loading...</div>;
  if (!user) return <Navigate to="/login" />;
  if (roles && !roles.includes(user.role)) return <Navigate to="/tickets" />;
  return children;
}

export default function App() {
  return (
    <div>
      <Nav />
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/tickets" element={<Protected><Tickets /></Protected>} />
        <Route path="/tickets/:id" element={<Protected><TicketDetail /></Protected>} />
        <Route path="/dashboard" element={<Protected roles={['STAFF', 'ADMIN']}><Dashboard /></Protected>} />
        <Route path="/knowledge" element={<Protected><Knowledge /></Protected>} />
        <Route path="/" element={<Navigate to="/tickets" />} />
      </Routes>
    </div>
  );
}
