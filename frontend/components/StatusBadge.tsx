export default function StatusBadge({ status }: { status?: string }) {
  const map: Record<string, { label: string; cls: string }> = {
    Pending: { label: 'Pending', cls: 'bg-amber-100 text-amber-800 border-amber-200' },
    Running: { label: 'Running', cls: 'bg-blue-100 text-blue-800 border-blue-200' },
    Accepted: { label: 'Accepted', cls: 'bg-emerald-100 text-emerald-800 border-emerald-200' },
    'Wrong Answer': { label: 'Wrong Answer', cls: 'bg-rose-100 text-rose-800 border-rose-200' },
    'Time Limit Exceeded': { label: 'TLE', cls: 'bg-purple-100 text-purple-800 border-purple-200' },
    Error: { label: 'Error', cls: 'bg-gray-100 text-gray-800 border-gray-200' },
  }
  const m = status ? map[status] : undefined
  return (
    <span className={`inline-flex items-center px-2 py-1 text-xs rounded border ${m?.cls ?? 'bg-gray-100 text-gray-700 border-gray-200'}`}>
      {m?.label ?? '—'}
    </span>
  )
}
