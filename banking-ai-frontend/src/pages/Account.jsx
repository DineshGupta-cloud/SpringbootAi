import { useEffect, useState } from 'react'
import { accountApi } from '../services/api'
import './Account.css'

export default function Account() {
  const [details, setDetails] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    accountApi.getDetails()
      .then((res) => setDetails(res.data))
      .catch((err) => setError(err.response?.data?.message || 'Failed to load'))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="page-loading">Loading...</div>
  if (error) return <div className="page-error">{error}</div>

  return (
    <div className="account-page">
      <h1 className="page-title">Account Details</h1>
      <div className="details-card">
        <div className="detail-row"><span className="label">Customer Name</span><span className="value">{details?.customerName}</span></div>
        <div className="detail-row"><span className="label">Account Number</span><span className="value mono">{details?.accountNumber}</span></div>
        <div className="detail-row"><span className="label">Account Type</span><span className="value">{details?.accountType}</span></div>
        <div className="detail-row"><span className="label">Status</span><span className={`value status ${details?.status?.toLowerCase()}`}>{details?.status}</span></div>
        <div className="detail-row"><span className="label">Available Balance</span><span className="value balance">₹{Number(details?.balance || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</span></div>
        <div className="detail-row"><span className="label">Currency</span><span className="value">{details?.currency}</span></div>
      </div>
    </div>
  )
}
