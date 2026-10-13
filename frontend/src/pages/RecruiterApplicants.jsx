
import { useEffect, useState } from 'react';
import api from '../api/client';

export default function RecruiterApplicants() {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [updatingId, setUpdatingId] = useState(null);
  const [message, setMessage] = useState('');

  async function loadApplications() {
    try {
      setError('');

      const response = await api.get(
        '/api/recruiter/jobs/applications'
      );

      setApplications(
        Array.isArray(response.data) ? response.data : []
      );
    } catch (err) {
      console.error('Failed to load applicants:', err);

      if (err.response?.status === 401) {
        setError('Please log in again. Your session may have expired.');
      } else if (err.response?.status === 403) {
        setError('Access denied. Please log in with a recruiter account.');
      } else {
        setError('Unable to load applicants. Check your backend connection.');
      }
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadApplications();
  }, []);

  async function updateStatus(applicationId, status) {
    const action = status === 'SHORTLISTED' ? 'accept' : 'reject';

    const confirmed = window.confirm(
      `Are you sure you want to ${action} this application?`
    );

    if (!confirmed) return;

    try {
      setUpdatingId(applicationId);
      setMessage('');
      setError('');

      await api.put(
        `/api/recruiter/jobs/applications/${applicationId}/status`,
        { status }
      );

      setApplications((previous) =>
        previous.map((application) =>
          application.id === applicationId
            ? { ...application, status }
            : application
        )
      );

      setMessage(
        status === 'SHORTLISTED'
          ? 'Application accepted successfully.'
          : 'Application rejected successfully.'
      );
    } catch (err) {
      console.error('Failed to update application:', err);

      setError(
        err.response?.data?.message ||
        'Could not update the application status. Please try again.'
      );
    } finally {
      setUpdatingId(null);
    }
  }

  if (loading) {
    return (
      <div className="page-container">
        <h1>Applicants</h1>
        <p>Loading applicants...</p>
      </div>
    );
  }

  if (error && applications.length === 0) {
    return (
      <div className="page-container">
        <h1>Applicants</h1>
        <p>{error}</p>
        <button className="btn" onClick={loadApplications}>
          Try Again
        </button>
      </div>
    );
  }

  return (
    <div className="page-container">
      <h1>Applicants</h1>
      <p>Review candidates who applied for your jobs.</p>

      {message && <p className="success">{message}</p>}
      {error && <p>{error}</p>}

      {applications.length === 0 ? (
        <div className="card">
          <p>No applications received for your jobs yet.</p>
        </div>
      ) : (
        <div className="grid">
          {applications.map((application) => (
            <div className="card" key={application.id}>
              <h2>
                {application.job?.title || 'Job title unavailable'}
              </h2>

              <p>
                <strong>Company:</strong>{' '}
                {application.job?.company || 'Not available'}
              </p>

              <p>
                <strong>Candidate:</strong>{' '}
                {application.candidate?.name ||
                  application.candidate?.fullName ||
                  application.candidate?.email ||
                  'Candidate'}
              </p>

              <p>
                <strong>Email:</strong>{' '}
                {application.candidate?.email || 'Not available'}
              </p>

              <p>
                <strong>Status:</strong>{' '}
                {application.status || 'APPLIED'}
              </p>

              <p>
                <strong>Applied:</strong>{' '}
                {application.appliedAt
                  ? new Date(application.appliedAt).toLocaleString()
                  : 'Not available'}
              </p>

              {application.coverLetter && (
                <p>
                  <strong>Cover letter:</strong>{' '}
                  {application.coverLetter}
                </p>
              )}

              {application.status === 'APPLIED' ||
              application.status === 'REVIEWING' ? (
                <div
                  style={{
                    display: 'flex',
                    gap: '10px',
                    marginTop: '16px',
                    flexWrap: 'wrap'
                  }}
                >
                  <button
                    className="btn"
                    disabled={updatingId === application.id}
                    onClick={() =>
                      updateStatus(application.id, 'SHORTLISTED')
                    }
                  >
                    {updatingId === application.id
                      ? 'Updating...'
                      : 'Accept'}
                  </button>

                  <button
                    className="btn"
                    disabled={updatingId === application.id}
                    onClick={() =>
                      updateStatus(application.id, 'REJECTED')
                    }
                  >
                    {updatingId === application.id
                      ? 'Updating...'
                      : 'Reject'}
                  </button>
                </div>
              ) : (
                <p>
                  This application has already been processed.
                </p>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
