import { useRouter } from 'next/router'
import useSWR from 'swr'
import axios from 'axios'
import dynamic from 'next/dynamic'
import { useEffect, useState } from 'react'
import Panel from '../../components/Panel'
import StatusBadge from '../../components/StatusBadge'
import Spinner from '../../components/Spinner'
import { templates } from '../../components/CodeTemplates'

const Editor = dynamic(() => import('@monaco-editor/react'), { ssr: false })
const API = ''
type Problem = { id: number; title: string; description: string; sampleInput: string; sampleOutput: string }
type Contest = { id: number; name: string; description: string; startTime?: string; endTime?: string; problems: Problem[] }
type LeaderboardRow = { username: string; solved: number }
const fetcher = (url: string) => axios.get(url).then(r=>r.data)

export default function ContestPage() {
  const router = useRouter()
  const { id } = router.query
  const username = (router.query.u as string) || 'guest'
  const { data: contest, isLoading: contestLoading, error: contestError } = useSWR<Contest>(id ? `${API}/api/contests/${id}` : null, fetcher)
  const { data: leaderboard, mutate: refreshBoard } = useSWR<LeaderboardRow[]>(id ? `${API}/api/contests/${id}/leaderboard` : null, fetcher, { refreshInterval: 20000 })
  const { data: solved } = useSWR<number[]>(id ? `${API}/api/contests/${id}/solved?username=${encodeURIComponent(String(username))}` : null, fetcher, { refreshInterval: 15000 })

  const [selectedProblem, setSelectedProblem] = useState<Problem | null>(null)
  const [code, setCode] = useState<string>('')
  const [language, setLanguage] = useState<string>('python')
  const [lastSubmission, setLastSubmission] = useState<{ id: number; status: string; result?: string } | null>(null)
  const [tab, setTab] = useState<'samples'|'official'>('samples')
  const [topTab, setTopTab] = useState<'contest'|'leaderboard'>('contest')
  const { data: testsCount } = useSWR<number>(selectedProblem ? `${API}/api/problems/${selectedProblem.id}/tests/count` : null, fetcher)

  // Timer state
  const [now, setNow] = useState<number>(Date.now())
  useEffect(()=>{
    const t = setInterval(()=>setNow(Date.now()), 1000)
    return ()=>clearInterval(t)
  }, [])
  const startMs = contest?.startTime ? new Date(contest.startTime).getTime() : undefined
  const endMs = contest?.endTime ? new Date(contest.endTime).getTime() : undefined
  const total = startMs && endMs ? Math.max(endMs - startMs, 1) : undefined
  const elapsed = startMs ? Math.max(Math.min(now - startMs, total ?? 0), 0) : undefined
  const remaining = endMs ? Math.max(endMs - now, 0) : undefined
  const pct = total && elapsed !== undefined ? Math.min(100, Math.max(0, (elapsed / total) * 100)) : 0
  function fmt(ms?: number){ if(ms===undefined) return '—'; const s=Math.floor(ms/1000); const h=Math.floor(s/3600); const m=Math.floor((s%3600)/60); const ss=s%60; return `${h}h ${m}m ${ss}s` }

  // Theme for Monaco editor
  const [theme, setTheme] = useState<'light'|'dark'>(typeof document !== 'undefined' && document.documentElement.classList.contains('dark') ? 'dark' : 'light')
  useEffect(()=>{
    const handler = (e: any)=> setTheme(e?.detail==='dark'?'dark':'light')
    window.addEventListener('theme-changed', handler)
    return ()=>window.removeEventListener('theme-changed', handler)
  }, [])

  useEffect(()=>{ if (contest && contest.problems?.length) setSelectedProblem(contest.problems[0]) }, [contest])

  // Load a friendly template when language changes or first load
  useEffect(()=>{
    setCode(templates[language] ?? '')
  }, [language])

  useEffect(()=>{
    if (!lastSubmission) return
    const t = setInterval(async ()=>{
      const res = await axios.get(`${API}/api/submissions/${lastSubmission.id}`)
      setLastSubmission({ id: res.data.id, status: res.data.status, result: res.data.result })
      if (['Accepted','Wrong Answer','Error','Time Limit Exceeded'].includes(res.data.status)) clearInterval(t)
      refreshBoard()
    }, 2500)
    return ()=>clearInterval(t)
  }, [lastSubmission, refreshBoard])

  const submit = async () => {
    if (!selectedProblem) return
    const res = await axios.post(`${API}/api/submissions`, { username, problemId: selectedProblem.id, contestId: contest.id, code, language })
    setLastSubmission({ id: res.data.id, status: 'Pending' })
  }

  return (
    <div className="space-y-4">
      <div className="sticky top-[52px] z-20 bg-gray-800 text-white border-b border-gray-800 flex items-center gap-2">
        <button className={`px-3 py-2 text-sm ${topTab==='contest'?'border-b-2 border-white text-white':'text-white/80 hover:text-white'}`} onClick={()=>setTopTab('contest')}>Contest</button>
        <button className={`px-3 py-2 text-sm ${topTab==='leaderboard'?'border-b-2 border-white text-white':'text-white/80 hover:text-white'}`} onClick={()=>setTopTab('leaderboard')}>Leaderboard</button>
        {topTab==='contest' && (
          <div className="ml-auto hidden sm:flex items-center gap-3 pr-2 text-xs text-white">
            <span>{now < (startMs??0) ? 'Starts in' : (now < (endMs??0) ? 'Time Remaining' : 'Contest Ended')}</span>
            <div className="w-56 h-2 bg-white/20 rounded overflow-hidden">
              <div className="h-full bg-blue-500" style={{ width: `${pct}%` }} />
            </div>
            <span className="tabular-nums">{now < (startMs??0) ? fmt((startMs??0)-now) : (now < (endMs??0) ? fmt(remaining) : '00h 00m 00s')}</span>
          </div>
        )}
      </div>
      {topTab==='leaderboard' ? (
        <Panel title="Leaderboard" right={!leaderboard ? <Spinner/> : null}>
          <table className="w-full text-sm">
            <thead>
              <tr className="text-left border-b">
                <th className="py-2">#</th>
                <th className="py-2">User</th>
                <th className="py-2">Solved</th>
              </tr>
            </thead>
            <tbody>
              {leaderboard?.map((r: LeaderboardRow, idx:number)=> (
                <tr key={idx} className="border-b">
                  <td className="py-2 w-12">{idx+1}</td>
                  <td className="py-2">{r.username}</td>
                  <td className="py-2">{r.solved}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </Panel>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
          <Panel title="Problems" right={contestLoading ? <Spinner/> : null}>
            {contestError && <div className="text-sm text-rose-600">Failed to load contest.</div>}
            {!contestLoading && contest?.problems?.length === 0 && (<div className="text-sm text-gray-500">No problems yet.</div>)}
            <ul className="space-y-1">
              {contest?.problems?.map((p: Problem)=> (
                <li key={p.id}>
                  <button className={`w-full text-left px-3 py-2 rounded text-sm flex items-center justify-between ${selectedProblem?.id===p.id?'bg-blue-50 font-medium':'hover:bg-gray-50'}`} onClick={()=>setSelectedProblem(p)}>
                    <span>{p.title}</span>
                    {solved?.includes(p.id) && <span className="text-emerald-600 text-xs font-medium">Solved</span>}
                  </button>
                </li>
              ))}
            </ul>
          </Panel>

          <Panel title="Editor" right={lastSubmission ? <StatusBadge status={lastSubmission.status}/> : null}>
            <div className="flex flex-wrap gap-2 items-center mb-2">
              <div className="text-sm">User: <span className="font-mono">{username}</span></div>
              <select className="border p-2 rounded text-sm" value={language} onChange={e=>setLanguage(e.target.value)}>
                <option value="python">Python</option>
                <option value="java">Java</option>
                <option value="cpp">C++</option>
              </select>
              <button className="bg-green-600 hover:bg-green-700 text-white px-3 py-2 rounded text-sm" onClick={submit} disabled={!selectedProblem}>Submit</button>
            </div>
            <div className="h-96 border rounded dark:border-gray-800">
              <Editor height="100%" theme={theme==='dark'?'vs-dark':'light'} defaultLanguage="python" language={language==='cpp'?"cpp":language} value={code} onChange={(v?: string)=>setCode(v||'')} options={{fontSize:14}}/>
            </div>
            {lastSubmission?.result && (
              <div className="mt-3">
                <div className="text-xs font-semibold mb-1">Judge Output</div>
                <pre className="p-3 bg-gray-900 text-gray-100 rounded text-xs overflow-auto max-h-48 whitespace-pre-wrap">{lastSubmission.result}</pre>
              </div>
            )}
          </Panel>

          <Panel title="Problem Details">
            {selectedProblem ? (
              <div className="text-sm space-y-2">
                <h3 className="font-semibold text-base">{selectedProblem.title}</h3>
                <p className="whitespace-pre-wrap">{selectedProblem.description}</p>
                <div className="mt-3">
                  <div className="flex gap-2 text-xs">
                    <button className={`px-3 py-1 rounded border ${tab==='samples'?'bg-gray-100 border-gray-300':'border-transparent hover:bg-gray-50'}`} onClick={()=>setTab('samples')}>Samples</button>
                    <button className={`px-3 py-1 rounded border ${tab==='official'?'bg-gray-100 border-gray-300':'border-transparent hover:bg-gray-50'}`} onClick={()=>setTab('official')}>Official tests{typeof testsCount==='number'?` (${testsCount})`:''}</button>
                  </div>
                  {tab==='samples' ? (
                    <div className="space-y-2 mt-2">
                      <div>
                        <div className="text-xs font-semibold">Sample Input</div>
                        <pre className="p-2 bg-gray-100 rounded whitespace-pre-wrap">{selectedProblem.sampleInput}</pre>
                      </div>
                      <div>
                        <div className="text-xs font-semibold">Sample Output</div>
                        <pre className="p-2 bg-gray-100 rounded whitespace-pre-wrap">{selectedProblem.sampleOutput}</pre>
                      </div>
                    </div>
                  ) : (
                    <div className="space-y-2 mt-2 text-xs text-gray-600">
                      <p>Your submission runs against {typeof testsCount==='number'?testsCount:'…'} official hidden tests.</p>
                      <p>Use the samples to verify IO format. Final verdict depends on official tests.</p>
                    </div>
                  )}
                </div>
              </div>
            ) : (
              <div className="text-sm text-gray-500">Select a problem</div>
            )}
          </Panel>

          {/* Leaderboard removed from contest view; accessible in the top Leaderboard tab */}
        </div>
      )}
    </div>
  )
}
