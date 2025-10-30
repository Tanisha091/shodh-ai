import { useState } from 'react'
import { useRouter } from 'next/router'

export default function JoinPage() {
  const [contestId, setContestId] = useState('1')
  const [username, setUsername] = useState('user'+Math.floor(Math.random()*1000))
  const router = useRouter()

  return (
    <div className="min-h-[70vh] grid place-items-center">
      <div className="w-full max-w-2xl">
        <div className="bg-white rounded border shadow-sm overflow-hidden">
          <div className="p-6 border-b">
            <h1 className="text-2xl font-semibold text-gray-900">Welcome to Shodh‑a‑Code</h1>
            <p className="text-sm text-gray-900 mt-1">Join a live coding contest. Submit solutions in Python, Java, or C++.</p>
          </div>
          <div className="p-6 grid gap-3 sm:grid-cols-3">
            <div className="sm:col-span-1">
              <label className="text-xs text-gray-900">Contest ID</label>
              <input className="mt-1 border p-2 w-full rounded text-gray-900" placeholder="Contest ID" value={contestId} onChange={e=>setContestId(e.target.value)} />
            </div>
            <div className="sm:col-span-2">
              <label className="text-xs text-gray-900">Username</label>
              <input className="mt-1 border p-2 w-full rounded text-gray-900" placeholder="Username" value={username} onChange={e=>setUsername(e.target.value)} />
            </div>
          </div>
          <div className="p-6 border-t flex items-center justify-between">
            <div className="text-xs text-gray-900">Tip: Try Contest ID <span className="font-mono">1</span></div>
            <button className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded" onClick={()=>router.push(`/contest/${contestId}?u=${encodeURIComponent(username)}`)}>Join Contest</button>
          </div>
        </div>
      </div>
    </div>
  )
}
