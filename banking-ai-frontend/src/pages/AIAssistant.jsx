import { useState, useRef, useEffect } from 'react'
import { aiApi } from '../services/api'
import './AIAssistant.css'

export default function AIAssistant() {
  const [messages, setMessages] = useState([
    { role: 'ai', text: 'Hello! I am your banking assistant. Ask me about your balance, account details, recent transactions, or statements.' }
  ])
  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const bottomRef = useRef(null)

  useEffect(() => { bottomRef.current?.scrollIntoView({ behavior: 'smooth' }) }, [messages, loading])

  const send = async () => {
    const text = input.trim()
    if (!text || loading) return
    setInput(''); setError('')
    setMessages((prev) => [...prev, { role: 'user', text }])
    setLoading(true)
    try {
      const res = await aiApi.chat(text)
      setMessages((prev) => [...prev, { role: 'ai', text: res.data.message }])
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to get AI response')
      setMessages((prev) => [...prev, { role: 'ai', text: 'Sorry, I encountered an error. Please try again.' }])
    } finally { setLoading(false) }
  }

  const handleKey = (e) => { if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); send() } }
  const clearChat = () => {
    setMessages([{ role: 'ai', text: 'Hello! I am your banking assistant. Ask me about your balance, account details, recent transactions, or statements.' }])
    setError('')
  }

  const suggestions = ['What is my account balance?', 'Show my account details', 'Show my last 5 transactions', 'Give me my statement for September']

  return (
    <div className="ai-page">
      <div className="ai-header">
        <h1>Banking AI Assistant</h1>
        <button className="btn-clear" onClick={clearChat}>Clear chat</button>
      </div>
      <div className="chat-container">
        <div className="chat-messages">
          {messages.map((m, i) => (
            <div key={i} className={`chat-bubble ${m.role}`}>
              <div className="bubble-label">{m.role === 'user' ? 'You' : 'AI'}</div>
              <div className="bubble-text">{m.text}</div>
            </div>
          ))}
          {loading && (
            <div className="chat-bubble ai">
              <div className="bubble-label">AI</div>
              <div className="bubble-text typing"><span></span><span></span><span></span></div>
            </div>
          )}
          <div ref={bottomRef} />
        </div>
        {error && <div className="chat-error">{error}</div>}
        {messages.length <= 1 && (
          <div className="suggestions">
            {suggestions.map((s) => (
              <button key={s} className="suggestion-chip" onClick={() => setInput(s)}>{s}</button>
            ))}
          </div>
        )}
        <div className="chat-input-row">
          <input type="text" value={input} onChange={(e) => setInput(e.target.value)} onKeyDown={handleKey}
            placeholder="Ask something about your account..." disabled={loading} />
          <button className="btn-send" onClick={send} disabled={loading || !input.trim()}>Send</button>
        </div>
      </div>
    </div>
  )
}
