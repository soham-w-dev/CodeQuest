import React from 'react';
import { Outlet } from 'react-router-dom';
import { Navbar } from './Navbar';

export const AppLayout: React.FC = () => {
  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans selection:bg-cyan-500/30">
      <Navbar />
      <main className="flex-1 w-full flex flex-col">
        <Outlet />
      </main>
    </div>
  );
};
