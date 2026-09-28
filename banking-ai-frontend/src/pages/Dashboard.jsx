import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { accountApi } from '../services/api'
import './Dashboard.css'

export default function Dashboard() {
  const { user } = useAuth()
  const [details, setDetails] = useState(null)
  const [transactions, setTransactions] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    Promise.all([accountApi.getDetails(), accountApi.getRecentTransactions(5)])
      .then(([detRes, txRes]) => { setDetails(detRes.data); setTransactions(txRes.data) })
      .catch((err) => setError(err.response?.data?.message || 'Failed to load'))
      .finally(() => setLoading(false))
  }, [])

  const formatAmount = (amt, type) => {
    const sign = type === 'CREDIT' ? '+' : '-'
    return `${sign}₹${Number(amt).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`
  }
  const formatDate = (dt) => new Date(dt).toLocaleDateString('en-IN', { day: '2-digit', month: 'short' })

  if (loading) return <div className="page-loading">Loading dashboard...</div>
  if (error) return <div className="page-error">{error}</div>

  return (
    <div className="dashboard">
      <h1 className="page-title">Welcome, {user?.name}</h1>
      <div className="balance-card">
        <div className="balance-info">
          <span className="account-type">{details?.accountType} Account</span>
          <span className="account-number">{details?.accountNumber}</span>
        </div>
        <div className="balance-amount">
          <span className="label">Available Balance</span>
          <span className="amount">₹{Number(details?.balance || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</span>
        </div>
      </div>
      <div className="section">
        <div className="section-header">
          <h2>Recent Transactions</h2>
          <Link to="/transactions" className="link-more">View all →</Link>
        </div>
        <div className="tx-list">
          {transactions.map((tx) => (
            <div key={tx.id} className="tx-row">
              <div className="tx-left">
                <span className="tx-date">{formatDate(tx.transactionDate)}</span>
                <span className="tx-desc">{tx.description}</span>
              </div>
              <span className={`tx-amount ${tx.transactionType === 'CREDIT' ? 'credit' : 'debit'}`}>
                {formatAmount(tx.amount, tx.transactionType)}
              </span>
            </div>
          ))}
        </div>
      </div>
      <div className="quick-actions">
        <Link to="/ai-assistant" className="action-card ai"><span className="action-icon">🤖</span><span>Ask AI Assistant</span></Link>
        <Link to="/statement" className="action-card"><span className="action-icon">📄</span><span>Get Statement</span></Link>
        <Link to="/account" className="action-card"><span className="action-icon">👤</span><span>Account Details</span></Link>
      </div>
    </div>
  )
}
