import { useEffect, useState } from 'react'
import { accountApi } from '../services/api'
import './Transactions.css'

export default function Transactions() {
  const [transactions, setTransactions] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [limit, setLimit] = useState(10)

  useEffect(() => {
    setLoading(true)
    accountApi.getRecentTransactions(limit)
      .then((res) => setTransactions(res.data))
      .catch((err) => setError(err.response?.data?.message || 'Failed to load'))
      .finally(() => setLoading(false))
  }, [limit])

  const formatAmount = (amt, type) => {
    const sign = type === 'CREDIT' ? '+' : '-'
    return `${sign}₹${Number(amt).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`
  }
  const formatDate = (dt) => new Date(dt).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' })

  return (
    <div className="transactions-page">
      <div className="page-header-row">
        <h1 className="page-title">Transactions</h1>
        <select value={limit} onChange={(e) => setLimit(Number(e.target.value))} className="limit-select">
          <option value={5}>Last 5</option>
          <option value={10}>Last 10</option>
          <option value={20}>Last 20</option>
        </select>
      </div>
      {loading && <div className="page-loading">Loading...</div>}
      {error && <div className="page-error">{error}</div>}
      {!loading && !error && (
        <div className="tx-table-card">
          <table className="tx-table">
            <thead><tr><th>Date</th><th>Description</th><th>Type</th><th>Amount</th><th>Balance</th></tr></thead>
            <tbody>
              {transactions.map((tx) => (
                <tr key={tx.id}>
                  <td>{formatDate(tx.transactionDate)}</td>
                  <td>{tx.description}</td>
                  <td><span className={`badge ${tx.transactionType.toLowerCase()}`}>{tx.transactionType}</span></td>
                  <td className={tx.transactionType === 'CREDIT' ? 'credit' : 'debit'}>{formatAmount(tx.amount, tx.transactionType)}</td>
                  <td>₹{Number(tx.balanceAfterTransaction).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
