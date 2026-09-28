import { Outlet, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Layout.css'

export default function Layout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => { logout(); navigate('/login') }

  return (
    <div className="layout">
      <header className="header">
        <div className="header-brand">
          <span className="logo">🏦</span>
          <span className="brand-name">BankAI</span>
        </div>
        <nav className="nav">
          <NavLink to="/dashboard">Dashboard</NavLink>
          <NavLink to="/account">Account</NavLink>
          <NavLink to="/transactions">Transactions</NavLink>
          <NavLink to="/statement">Statement</NavLink>
          <NavLink to="/ai-assistant" className="ai-link">AI Assistant</NavLink>
        </nav>
        <div className="header-user">
          <span className="user-name">{user?.name}</span>
          <button className="btn-logout" onClick={handleLogout}>Logout</button>
        </div>
      </header>
      <main className="main-content"><Outlet /></main>
    </div>
  )
}
