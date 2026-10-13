
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import RecruiterApplicants from './pages/RecruiterApplicants';
import Layout from './components/Layout';
import Home from './pages/Home';
import { Login, Register } from './pages/Auth';
import Jobs from './pages/Jobs';
import Profile from './pages/Profile';
import Applications from './pages/Applications';
import Recruiter from './pages/Recruiter';
import AI from './pages/AI';
import { useAuth } from './context/AuthContext';

// Protect pages that require login
function Protected({ children }) {
  const { user } = useAuth();

  return user
    ? children
    : <Navigate to="/login" replace />;
}

// Allow only recruiters and admins
function RecruiterOnly({ children }) {
  const { user } = useAuth();

  return user?.role === 'RECRUITER' || user?.role === 'ADMIN'
    ? children
    : <Navigate to="/jobs" replace />;
}

export default function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<Home />} />

          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          <Route
            path="/jobs"
            element={
              <Protected>
                <Jobs />
              </Protected>
            }
          />

          <Route
            path="/profile"
            element={
              <Protected>
                <Profile />
              </Protected>
            }
          />

          <Route
            path="/applications"
            element={
              <Protected>
                <Applications />
              </Protected>
            }
          />

          <Route
            path="/ai"
            element={
              <Protected>
                <AI />
              </Protected>
            }
          />

          {/* Recruiter: publish jobs */}
          <Route
            path="/recruiter"
            element={
              <RecruiterOnly>
                <Recruiter />
              </RecruiterOnly>
            }
          />

          {/* Recruiter: view applicants */}
          {/* Recruiter: view applicants */}
<Route
  path="/recruiter/applicants"
  element={
    <RecruiterOnly>
      <RecruiterApplicants />
    </RecruiterOnly>
  }
/>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}

// Applicants page component
function Applicants() {
  return (
    <div className="page-container">
      <h1>Applicants</h1>
      <p>Your job applicants will appear here.</p>
    </div>
  );
}
