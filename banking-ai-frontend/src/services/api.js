import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      if (window.location.pathname !== '/login') window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

export const authApi = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  me: () => api.get('/auth/me')
}

export const accountApi = {
  getAccounts: () => api.get('/accounts'),
  getBalance: () => api.get('/accounts/balance'),
  getDetails: () => api.get('/accounts/details'),
  getRecentTransactions: (limit = 5) => api.get(`/accounts/transactions/recent?limit=${limit}`),
  getStatement: (from, to) => api.get(`/accounts/statement?from=${from}&to=${to}`)
}

export const aiApi = {
  chat: (message) => api.post('/ai/chat', { message })
}

export default api
