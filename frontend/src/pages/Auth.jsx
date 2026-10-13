
import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export function Login() {
  const { login } = useAuth();
  const nav = useNavigate();

  const [f, setF] = useState({
    email: '',
    password: ''
  });

  const [err, setErr] = useState('');

  const submit = async (e) => {
    e.preventDefault();
    setErr('');

    if (!f.email || !f.password) {
      setErr('Please fill all fields.');
      return;
    }

    try {
      await login(f);
      nav('/jobs');
    } catch (x) {
      setErr(
        x.response?.data?.message ||
        'Login failed. Please check your email and password.'
      );
    }
  };

  return (
    <div className="auth card">
      <h1>Welcome back</h1>
      <p className="muted">
        Sign in to discover jobs matched to your skills.
      </p>

      {err && <div className="error">{err}</div>}

      <form onSubmit={submit}>
        <label>Email</label>
        <input
          className="input"
          type="email"
          value={f.email}
          onChange={(e) =>
            setF({ ...f, email: e.target.value })
          }
          required
        />

        <label>Password</label>
        <input
          className="input"
          type="password"
          value={f.password}
          onChange={(e) =>
            setF({ ...f, password: e.target.value })
          }
          required
        />

        <button className="btn" type="submit">
          Login
        </button>
      </form>

      <p>
        New here? <Link className="auth-link" to="/register">Create an account</Link>
      </p>

      <small>
        Demo candidate: candidate@jobportal.local / Candidate@123
      </small>
    </div>
  );
}

export function Register() {
  const { register } = useAuth();
  const nav = useNavigate();

  const [f, setF] = useState({
    name: '',
    email: '',
    password: '',
    role: 'CANDIDATE'
  });

  const [err, setErr] = useState('');

  const submit = async (e) => {
    e.preventDefault();
    setErr('');

    if (!f.name.trim() || !f.email.trim() || !f.password) {
      setErr('Please fill all fields.');
      return;
    }

    if (f.password.length < 8) {
      setErr('Password must be at least 8 characters.');
      return;
    }

    try {
      await register(f);
      nav('/profile');
    } catch (x) {
      setErr(
        x.response?.data?.message ||
        'Registration failed. Please try again.'
      );
    }
  };

  return (
    <div className="auth card">
      <h1>Create your account</h1>

      <p className="muted">
        Build your profile and let JobMatch find relevant opportunities.
      </p>

      {err && <div className="error">{err}</div>}

      <form onSubmit={submit}>
        <label>Name</label>
        <input
          className="input"
          type="text"
          value={f.name}
          onChange={(e) =>
            setF({ ...f, name: e.target.value })
          }
          required
        />

        <label>Email</label>
        <input
          className="input"
          type="email"
          value={f.email}
          onChange={(e) =>
            setF({ ...f, email: e.target.value })
          }
          required
        />

        <label>Password</label>
        <input
          className="input"
          type="password"
          value={f.password}
          onChange={(e) =>
            setF({ ...f, password: e.target.value })
          }
          minLength={8}
          required
        />

        <label>Account type</label>
        <select
          className="input"
          value={f.role}
          onChange={(e) =>
            setF({ ...f, role: e.target.value })
          }
        >
          <option value="CANDIDATE">Candidate</option>
          <option value="RECRUITER">Recruiter</option>
        </select>

        <button className="btn" type="submit">
          Register
        </button>
      </form>

      <p>
        Already have an account? <Link className="auth-link"  to="/login">Login here</Link>
      </p>
    </div>
  );
}
