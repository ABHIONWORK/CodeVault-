import { useState, useEffect } from 'react'
import './App.css'

// Dynamic API base URL: Uses VITE_API_URL in production or defaults to local backend
const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080'

function App() {
  const [snippets, setSnippets] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [searchQuery, setSearchQuery] = useState('')


  // 🔑 AUTHENTICATION STATE
  const [token, setToken] = useState(localStorage.getItem('token') || '')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [loginError, setLoginError] = useState('')

  // 📝 NEW SNIPPET FORM STATE
  const [title, setTitle] = useState('')
  const [language, setLanguage] = useState('Java')
  const [tags, setTags] = useState('')
  const [codeContent, setCodeContent] = useState('')
  const [formMsg, setFormMsg] = useState('')

  // Fetch snippets
  const fetchSnippets = () => {
    setLoading(true)
    const url = searchQuery.trim()
      ? `${API_BASE}/api/snippets?search=${encodeURIComponent(searchQuery)}`
      : `${API_BASE}/api/snippets`


    fetch(url)
      .then((res) => {
        if (!res.ok) throw new Error('Failed to fetch snippets')
        return res.json()
      })
      .then((data) => {
        setSnippets(data)
        setLoading(false)
      })
      .catch((err) => {
        setError(err.message)
        setLoading(false)
      })
  }

  useEffect(() => {
    fetchSnippets()
  }, [searchQuery])

  // 🔐 HANDLE LOGIN
  const handleLogin = (e) => {
    e.preventDefault()
    setLoginError('')

    fetch(`${API_BASE}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    })

      .then((res) => {
        if (!res.ok) throw new Error('Invalid username or password')
        return res.json()
      })
      .then((data) => {
        setToken(data.token)
        localStorage.setItem('token', data.token) // 👈 Save JWT token in localStorage
        setUsername('')
        setPassword('')
      })
      .catch((err) => setLoginError(err.message))
  }

  // 🚪 HANDLE LOGOUT
  const handleLogout = () => {
    setToken('')
    localStorage.removeItem('token') // 👈 Clear JWT token on logout
  }

  // 🤖 AI CODE ASSISTANT TRIGGER
  const handleAiSuggestion = () => {
    const aiComments = [
      '\n// 🤖 AI Suggestion: Consider using a HashMap to reduce time complexity to O(1) lookups.',
      '\n// 🤖 AI Suggestion: Remember to handle edge cases like null or empty input values.',
      '\n// 🤖 AI Suggestion: Optimize space complexity by using a two-pointer approach.',
    ]
    const randomComment = aiComments[Math.floor(Math.random() * aiComments.length)]
    setCodeContent((prev) => prev + randomComment)
  }

  // ➕ CREATE SNIPPET (AUTHENTICATED)
  const handleCreateSnippet = (e) => {
    e.preventDefault()
    setFormMsg('')

    // Attach JWT token in Authorization Bearer header
    fetch(`${API_BASE}/api/snippets`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`, // 👈 Pass VIP wristband to backend bouncer!
      },
      body: JSON.stringify({ title, language, tags, codeContent }),
    })

      .then((res) => {
        if (!res.ok) throw new Error('Unauthorized! Please log in to add snippets.')
        return res.json()
      })
      .then(() => {
        setFormMsg('✅ Snippet created successfully!')
        setTitle('')
        setTags('')
        setCodeContent('')
        fetchSnippets()
      })
      .catch((err) => setFormMsg(`⚠️ ${err.message}`))
  }

  return (
    <div className="container">
      {/* HEADER & AUTH BAR */}
      <header className="header">
        <div className="top-bar">
          <h1>⚡ CodeVault Snippets</h1>
          {token ? (
            <div className="user-badge">
              <span>👤 Authenticated VIP</span>
              <button onClick={handleLogout} className="btn-logout">
                Logout
              </button>
            </div>
          ) : (
            <span className="guest-badge">🔒 Guest View (Login to Add Snippets)</span>
          )}
        </div>
        <p>Your Secure Repository for Code & AI Insights</p>
      </header>

      {/* LOGIN BOX (IF NOT LOGGED IN) */}
      {!token && (
        <div className="card-box auth-box">
          <h2>🔐 Log In to Unlock Add/Edit Access</h2>
          <form onSubmit={handleLogin} className="auth-form">
            <input
              type="text"
              placeholder="Username (e.g. admin)"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
            <input
              type="password"
              placeholder="Password (e.g. password123)"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            <button type="submit" className="btn-primary">
              Login
            </button>
          </form>
          {loginError && <p className="error-text">{loginError}</p>}
        </div>
      )}

      {/* CREATE SNIPPET FORM (IF LOGGED IN) */}
      {token && (
        <div className="card-box create-box">
          <h2>➕ Add New Snippet</h2>
          <form onSubmit={handleCreateSnippet} className="create-form">
            <div className="form-row">
              <input
                type="text"
                placeholder="Title (e.g. Binary Search in Java)"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                required
              />
              <select value={language} onChange={(e) => setLanguage(e.target.value)}>
                <option value="Java">Java</option>
                <option value="Python">Python</option>
                <option value="JavaScript">JavaScript</option>
                <option value="C++">C++</option>
              </select>
            </div>

            <input
              type="text"
              placeholder="Tags comma separated (e.g. search, binary, algorithm)"
              value={tags}
              onChange={(e) => setTags(e.target.value)}
            />

            <div className="code-area-wrapper">
              <textarea
                placeholder="Paste your code content here..."
                value={codeContent}
                onChange={(e) => setCodeContent(e.target.value)}
                rows={5}
                required
              />
              <button type="button" onClick={handleAiSuggestion} className="btn-ai">
                🤖 AI Suggestion
              </button>
            </div>

            <button type="submit" className="btn-submit">
              Save Snippet
            </button>
          </form>
          {formMsg && <p className="form-msg">{formMsg}</p>}
        </div>
      )}

      {/* 🔍 SEARCH BAR */}
      <div className="search-container">
        <input
          type="text"
          className="search-input"
          placeholder="🔍 Search snippets by title or tags (e.g., 'sorting', 'arrays')..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
      </div>

      {loading && <p className="status">Loading snippets from kitchen...</p>}
      {error && <p className="status error">⚠️ Error: {error}</p>}

      <div className="snippet-grid">
        {snippets.map((snippet) => (
          <div key={snippet.id} className="snippet-card">
            <div className="card-header">
              <h3>{snippet.title}</h3>
              <span className="badge">{snippet.language}</span>
            </div>

            {snippet.tags && (
              <div className="tags-container">
                {snippet.tags.split(',').map((tag, idx) => (
                  <span key={idx} className="tag-badge">
                    #{tag.trim()}
                  </span>
                ))}
              </div>
            )}

            <pre className="code-block">
              <code>{snippet.codeContent}</code>
            </pre>
          </div>
        ))}
      </div>
    </div>
  )
}

export default App



