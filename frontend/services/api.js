import axios from 'axios';

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080',
});

// Interceptor to add API key to headers if available
api.interceptors.request.use(
  (config) => {
    const apiKey = typeof window !== 'undefined' ? localStorage.getItem('apiKey') : null;
    if (apiKey) {
      config.headers['X-API-KEY'] = apiKey;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Global error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Maybe logic to redirect to login if key is invalid, but user didn't ask for auto logout
    }
    return Promise.reject(error);
  }
);

export default api;
