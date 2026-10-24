import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import api from '../api';
import { useAuth } from '../AuthContext';

export default function TicketDetail() {
  const { id } = useParams();
  const { user } = useAuth();
  const [ticket, setTicket] = useState(null);
  const [history, setHistory] = useState([]);
  const [suggestion, setSuggestion] = useState(null);
  const [status, setStatus] = useState('');
  const [assigneeId, setAssigneeId] = useState('');
  const [feedback, setFeedback] = useState('');
  const [editedResponse, setEditedResponse] = useState('');
  const [error, setError] = useState('');

  const load = async () => {
    try {
      const [tRes, hRes] = await Promise.all([
        api.get(`/tickets/${id}`),
        api.get(`/tickets/${id}/history`)
      ]);
      setTicket(tRes.data);
      setStatus(tRes.data.status);
      setHistory(hRes.data);
      try {
        const sRes = await api.get(`/ai/tickets/${id}/suggest`);
        setSuggestion(sRes.data);
        setEditedResponse(sRes.data.suggestedResponse || '');
      } catch (e) {
        setSuggestion(null);
      }
    } catch (e) {
      setError('Failed to load');
    }
  };

  useEffect(() => {
    load();
  }, [id]);

  const update = async () => {
    try {
      await api.put(`/tickets/${id}`, { status, assigneeId: assigneeId ? Number(assigneeId) : null });
      load();
    } catch (e) {
      setError(e.response?.data?.message || 'Update failed');
    }
  };

  const generateAi = async () => {
    try {
      const res = await api.post(`/ai/tickets/${id}/suggest`);
      setSuggestion(res.data);
      setEditedResponse(res.data.suggestedResponse || '');
    } catch (e) {
      setError(e.response?.data?.message || 'AI failed');
    }
  };

  const review = async (action) => {
    try {
      await api.post(`/ai/tickets/${id}/review`, { action, feedback, editedResponse });
      load();
    } catch (e) {
      setError(e.response?.data?.message || 'Review failed');
    }
  };

  if (!ticket) return <div style={{ padding: '20px' }}>Loading...</div>;

  return (
    <div style={{ padding: '20px' }}>
      <h2>{ticket.title}</h2>
      <p>{ticket.description}</p>
      <p>Category: {ticket.category} | Priority: {ticket.priority} | Status: {ticket.status}</p>
      <p>Creator: {ticket.creatorName} | Assignee: {ticket.assigneeName || 'None'}</p>
      {error && <p style={{ color: 'red' }}>{error}</p>}

      {(user.role === 'STAFF' || user.role === 'ADMIN') && (
        <div style={{ marginTop: '20px' }}>
          <h3>Staff Actions</h3>
          <div>
            <select value={status} onChange={e => setStatus(e.target.value)}>
              <option value="OPEN">Open</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="WAITING_FOR_CUSTOMER">Waiting for Customer</option>
              <option value="RESOLVED">Resolved</option>
            </select>
            <input placeholder="Assignee ID" value={assigneeId} onChange={e => setAssigneeId(e.target.value)} />
            <button onClick={update}>Update</button>
          </div>
          <div style={{ marginTop: '10px' }}>
            <button onClick={generateAi}>Generate AI Suggestion</button>
          </div>
        </div>
      )}

      {suggestion && (
        <div style={{ marginTop: '20px', padding: '10px', border: '1px solid #ccc' }}>
          <h3>AI Suggestion</h3>
          <p>Suggested Category: {suggestion.suggestedCategory} | Suggested Priority: {suggestion.suggestedPriority}</p>
          <p><strong>Explanation:</strong> {suggestion.explanation}</p>
          <p><strong>Suggested Response:</strong></p>
          <textarea value={editedResponse} onChange={e => setEditedResponse(e.target.value)} style={{ width: '100%', height: '80px' }} />
          <p><strong>KB Articles:</strong> {suggestion.matchingArticleTitles}</p>
          <p><strong>Excerpts:</strong> {suggestion.matchingArticleExcerpts}</p>
          <p>Status: {suggestion.status} {suggestion.reviewedBy && `(by ${suggestion.reviewedBy})`}</p>
          {(user.role === 'STAFF' || user.role === 'ADMIN') && suggestion.status === 'PENDING' && (
            <div>
              <input placeholder="Feedback" value={feedback} onChange={e => setFeedback(e.target.value)} />
              <button onClick={() => review('ACCEPT')}>Accept</button>
              <button onClick={() => review('EDIT')}>Save Edited</button>
              <button onClick={() => review('REJECT')}>Reject</button>
            </div>
          )}
        </div>
      )}

      <div style={{ marginTop: '20px' }}>
        <h3>Status History</h3>
        <ul>
          {history.map(h => (
            <li key={h.id}>{h.fromStatus} → {h.toStatus} by {h.changedByName} at {h.changedAt}</li>
          ))}
        </ul>
      </div>
    </div>
  );
}
