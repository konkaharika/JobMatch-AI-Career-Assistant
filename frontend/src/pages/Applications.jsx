
import { useEffect, useState } from 'react';
import api from '../api/client';

export default function Applications() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    api
      .get('/api/jobs/applications')
      .then((response) => {
        const data = response.data;
        setItems(Array.isArray(data) ? data : []);
      })
      .catch((err) => {
        console.error('Failed to load applications:', err);
        setError('Could not load applications. Please refresh and try again.');
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  return (
    <div>
      <h1>My Applications</h1>

      {loading && <p>Loading applications...</p>}

      {error && <p>{error}</p>}

      {!loading && !error && (
        <div className="grid">
          {items.map((application) => (
            <div className="card" key={application.id}>
              <h2>
                {application.title || 'Job details unavailable'}
              </h2>

              <p>
                {application.company || 'Company not available'}
              </p>

              <span className="pill">
                {application.status || 'Status unavailable'}
              </span>

              <p className="muted">
                Applied{' '}
                {application.appliedAt
                  ? new Date(application.appliedAt).toLocaleString()
                  : 'date unavailable'}
              </p>
            </div>
          ))}

          {items.length === 0 && (
            <div className="card">
              No applications yet. Explore your recommended jobs.
            </div>
          )}
        </div>
      )}
    </div>
  );
}
