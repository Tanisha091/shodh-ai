import React from 'react'

export default function Layout({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen bg-gray-50">
      <header className="sticky top-0 z-30 bg-white/80 backdrop-blur border-b">
        <div className="max-w-7xl mx-auto px-4 py-3 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="h-8 w-8 rounded bg-blue-600" />
            <span className="font-semibold text-gray-900">Shodh‑a‑Code</span>
          </div>
          <nav className="flex items-center gap-4 text-sm text-gray-700">
            <a href="/" className="hover:text-black">Home</a>
          </nav>
        </div>
      </header>
      <main className="max-w-7xl mx-auto p-4">
        {children}
      </main>
      <footer className="border-t py-6 text-center text-xs text-gray-500">© {new Date().getFullYear()} Shodh‑a‑Code</footer>
    </div>
  )
}
