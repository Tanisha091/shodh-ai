import React from 'react'
export default function Panel({ title, children, right }: { title: string; children: React.ReactNode; right?: React.ReactNode }) {
  return (
    <div className="bg-white rounded border shadow-sm">
      <div className="px-4 py-2 border-b flex items-center justify-between">
        <h2 className="font-semibold text-gray-900">{title}</h2>
        {right}
      </div>
      <div className="p-4 text-gray-900">{children}</div>
    </div>
  )
}
