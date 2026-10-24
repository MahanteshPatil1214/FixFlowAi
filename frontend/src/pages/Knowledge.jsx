import React, { useEffect, useState } from 'react';
import api from '../api';

export default function Knowledge() {
  const [articles, setArticles] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get('/kb').then(res => setArticles(res.data)).catch(e => setError('Failed to load'));
  }, []);

  return (
    <div style={{ padding: '20px' }}>
      <h2>Knowledge Base</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <ul>
        {articles.map(a => (
          <li key={a.id}>
            <strong>{a.title}</strong>
            <p>{a.content}</p>
          </li>
        ))}
      </ul>
    </div>
  );
}
