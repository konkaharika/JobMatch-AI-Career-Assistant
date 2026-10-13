import axios from 'axios';
const api=axios.create({baseURL:import.meta.env.VITE_API_URL||'http://localhost:4040',headers:{'Content-Type':'application/json'}});
api.interceptors.request.use(c=>{const token=localStorage.getItem('jobportal_token');if(token)c.headers.Authorization=`Bearer ${token}`;return c;});
api.interceptors.response.use(r=>r,e=>{if(e.response?.status===401){localStorage.removeItem('jobportal_token');localStorage.removeItem('jobportal_user');}return Promise.reject(e);});
export default api;
