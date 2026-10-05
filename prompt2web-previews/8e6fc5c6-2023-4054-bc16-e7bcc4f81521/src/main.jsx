import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import App from './App.jsx'
import './index.css'

const pathname = window.location.pathname

const previewMarker = '/preview/public/'

let basename = '/'

const markerIndex = pathname.indexOf(previewMarker)

if (markerIndex !== -1) {
  const tokenStart = markerIndex + previewMarker.length
  const tokenEnd = pathname.indexOf('/', tokenStart)

  if (tokenEnd !== -1) {
    basename = pathname.substring(0, tokenEnd)
  } else {
    basename = pathname.substring(0, tokenStart - 1)
  }
}

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter basename={basename}>
      <App />
    </BrowserRouter>
  </React.StrictMode>
)