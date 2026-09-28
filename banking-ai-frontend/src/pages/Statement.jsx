import { useState } from 'react'
import { accountApi } from '../services/api'
import './Statement.css'

export default function Statement() {
  const [from, setFrom] = useState('2026-09-01')
  const [to, setTo] = useState('2026-09-30')
  const [transactions, setTransactions] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [searched, setSearched] = useState(false)

  const handleFetch = async (e) => {
    e.preventDefault()
    setError(''); setLoading(true); setSearched(true)
    try {
      const res = await accountApi.getStatement(from, to)
      setTransactions(res.data)
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch statement')
      setTransactions([])
    } finally { setLoading(false) }
  }

  const formatAmount = (amt, type) => {
    const sign = type === 'CREDIT' ? '+' : '-'
    return `${sign}₹${Number(amt).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`
  }
  const formatDate = (dt) => new Date(dt).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })

  return (
    <div className="statement-page">
      <h1 className="page-title">Account Statement</h1>
      <form className="statement-form" onSubmit={handleFetch}>
        <div className="date-group"><label>From</label><input type="date" value={from} onChange={(e) => setFrom(e.target.value)} required /></div>
        <div className="date-group"><label>To</label><input type="date" value={to} onChange={(e) => setTo(e.target.value)} required /></div>
        <button type="submit" className="btn-fetch" disabled={loading}>{loading ? 'Loading...' : 'Get Statement'}</button>
      </form>
      {error && <div className="page-error">{error}</div>}
      {searched && !loading && !error && (
        <div className="tx-table-card">
          {transactions.length === 0 ? <p className="empty">No transactions found</p> : (
            <><p className="result-count">{transactions.length} transaction(s) found</p>
            <table className="tx-table">
              <thead><tr><th>Date</th><th>Description</th><th>Type</th><th>Amount</th><th>Balance</th></tr></thead>
              <tbody>{transactions.map((tx) => (
                <tr key={tx.id}>
                  <td>{formatDate(tx.transactionDate)}</td><td>{tx.description}</td>
                  <td><span className={`badge ${tx.transactionType.toLowerCase()}`}>{tx.transactionType}</span></td>
                  <td className={tx.transactionType === 'CREDIT' ? 'credit' : 'debit'}>{formatAmount(tx.amount, tx.transactionType)}</td>
                  <td>₹{Number(tx.balanceAfterTransaction).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                </tr>
              ))}</tbody>
            </table></>
          )}
        </div>
      )}
    </div>
  )
}
