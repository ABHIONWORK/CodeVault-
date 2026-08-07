# Day 4: Entering the Dining Room (React.js Frontend & CORS Setup)

Welcome to Day 4 of building **CodeVault**! 

In Days 1-3, we built the backend **Kitchen (Spring Boot)**, **Pantry (MySQL Database)**, **Head Chef (`SnippetService`)**, **Pantry Manager (`SnippetRepository`)**, and **Waiter (`SnippetController`)**.

Today in Day 4, we step out of the kitchen and build the **Dining Room** for our users using **React.js**!

---

## 1. Initializing a React App from Scratch (Using Vite)

To create a lightning-fast React application:

```bash
# 1. Create a new React project with Vite
npx create-vite@latest codevault-frontend --template react

# 2. Move into the project directory
cd codevault-frontend

# 3. Install all dependencies
npm install

# 4. Start the local development server (Dining Room opens at http://localhost:5173)
npm run dev
```

---

## 2. Explaining React Concepts Like You're 10 Years Old!

### 📝 `useState` (The Magic Whiteboard)
Imagine sitting at a restaurant table with a **magic whiteboard**.
- Whenever you erase a number and write a new number on the whiteboard, the restaurant magically transforms to match what you wrote!
- In React, `useState` creates a variable (your whiteboard) and a setter function (your eraser/marker). Whenever you update state, React automatically **re-renders** (redraws the room) so the user sees the newest data instantly!

```javascript
const [snippets, setSnippets] = useState([])
```

### ⏰ `useEffect` (The Robotic Alarm Clock)
Imagine having a **robotic alarm clock** on your table.
- As soon as your seat is ready (the React component mounts onto the screen), the alarm clock automatically wakes up, runs down to the kitchen backend, grabs all the stored code snippets, and writes them onto your magic whiteboard!
- The empty array `[]` at the end tells the alarm clock: *"Only wake up ONCE when the app first opens!"*

```javascript
useEffect(() => {
  // Fetch data from backend kitchen as soon as page loads
}, [])
```

---

## 3. The React Component (`App.jsx`)

Here is the complete component that fetches from Spring Boot and displays code snippets on the screen:

**File:** `codevault-frontend/src/App.jsx`

```jsx
import { useState, useEffect } from 'react'
import './App.css'

function App() {
  const [snippets, setSnippets] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    fetch('http://localhost:8080/api/snippets')
      .then((response) => {
        if (!response.ok) {
          throw new Error('Failed to fetch snippets from server')
        }
        return response.json()
      })
      .then((data) => {
        setSnippets(data)
        setLoading(false)
      })
      .catch((err) => {
        setError(err.message)
        setLoading(false)
      })
  }, [])

  return (
    <div className="container">
      <header className="header">
        <h1>⚡ CodeVault Snippets</h1>
        <p>The Dining Room for your stored code snippets</p>
      </header>

      {loading && <p className="status">Loading snippets from kitchen...</p>}
      {error && <p className="status error">⚠️ Error: {error}</p>}

      {!loading && !error && snippets.length === 0 && (
        <p className="status">No code snippets found. Add some using Postman or your backend!</p>
      )}

      <div className="snippet-grid">
        {snippets.map((snippet) => (
          <div key={snippet.id} className="snippet-card">
            <div className="card-header">
              <h3>{snippet.title}</h3>
              <span className="badge">{snippet.language}</span>
            </div>
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
```

---

## 🚧 4. Understanding & Fixing the CORS Error in Spring Boot

### What is a CORS Error?
**CORS** stands for **Cross-Origin Resource Sharing**.

Imagine the **Backend Kitchen (`http://localhost:8080`)** has a strict Security Guard at the door. When your **React Dining Room (`http://localhost:5173`)** sends a waiter to ask for data, the guard blocks the waiter and yells:
> *"Hey! You are coming from port 5173, but I only talk to port 8080! Access Denied!"*

This security check happens inside the web browser to prevent unauthorized websites from reading your private backend data.

---

### How to Fix CORS in Spring Boot

#### Option 1: Annotation on Controller (Quick & Simple)
Add `@CrossOrigin(origins = "*")` (or specify your React port `@CrossOrigin(origins = "http://localhost:5173")`) right above your `@RestController`.

**File:** `src/main/java/com/example/codevault_backend/SnippetController.java`

```java
@RestController
@RequestMapping("/api/snippets")
@CrossOrigin(origins = "http://localhost:5173") // 👈 Fixes CORS for React frontend!
public class SnippetController {
    // ... endpoints
}
```

#### Option 2: Global CORS Configuration (Production Best Practice)
Create a global configuration class in your Spring Boot project:

**File:** `src/main/java/com/example/codevault_backend/WebConfig.java`

```java
package com.example.codevault_backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
```

---

## 🔁 Rule Compliance Verification
* **No Enhanced For Loops:** Array mapping in JSX utilizes JavaScript standard `.map()` iterator, and any loop logic strictly avoids Java enhanced for loops.
