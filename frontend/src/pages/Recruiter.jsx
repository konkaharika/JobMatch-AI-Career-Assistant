
import { useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/client';

export default function Recruiter() {
  const [f, setF] = useState({
    title: '',
    company: '',
    description: '',
    requiredSkills: '',
    location: '',
    employmentType: 'FULL_TIME',
    minExperience: 0,
    salaryMin: '',
    salaryMax: ''
  });

  const [msg, setMsg] = useState('');

  const submit = async (e) => {
    e.preventDefault();

    try {
      await api.post('/api/recruiter/jobs', f);

      setMsg('Job published successfully.');

      setF({
        ...f,
        title: '',
        description: '',
        requiredSkills: ''
      });
    } catch (error) {
      console.error('Failed to publish job:', error);
      setMsg('Failed to publish job. Please try again.');
    }
  };

  return (
    <div
      className="card"
      style={{ maxWidth: 850, margin: 'auto' }}
    >
      <div style={{ marginBottom: '24px' }}>
        <Link
          to="/recruiter/applicants"
          className="btn"
          style={{
            display: 'inline-block',
            textDecoration: 'none'
          }}
        >
          View Applicants
        </Link>
      </div>

      <h1>Post a job</h1>

      {msg && <div className="success">{msg}</div>}

      <form onSubmit={submit}>
        {[
          ['title', 'Job title'],
          ['company', 'Company'],
          ['requiredSkills', 'Required skills'],
          ['location', 'Location'],
          ['salaryMin', 'Minimum salary'],
          ['salaryMax', 'Maximum salary']
        ].map(([k, l]) => (
          <div key={k}>
            <label>{l}</label>
            <input
              className="input"
              value={f[k]}
              onChange={(e) =>
                setF({ ...f, [k]: e.target.value })
              }
            />
          </div>
        ))}

        <label>Description</label>

        <textarea
          className="input"
          rows="8"
          value={f.description}
          onChange={(e) =>
            setF({ ...f, description: e.target.value })
          }
        />

        <button className="btn" type="submit">
          Publish job
        </button>
      </form>
    </div>
  );
}
