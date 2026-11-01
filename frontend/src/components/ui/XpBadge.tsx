import React from 'react';
import { Zap } from 'lucide-react';
import { cn } from './Button';

interface XpBadgeProps {
  xp: number;
  level: number;
  className?: string;
}

export const XpBadge: React.FC<XpBadgeProps> = ({ xp, level, className }) => {
  return (
    <div className={cn("flex items-center space-x-3 rounded-full bg-slate-900 border border-slate-700 p-1 pr-4", className)}>
      <div className="flex h-8 w-8 items-center justify-center rounded-full bg-gradient-to-br from-cyan-400 to-blue-600 shadow-[0_0_10px_rgba(56,189,248,0.5)]">
        <span className="text-sm font-bold text-slate-950">{level}</span>
      </div>
      <div className="flex items-center space-x-1">
        <Zap className="h-4 w-4 text-cyan-400" />
        <span className="text-sm font-bold text-slate-200">{xp} XP</span>
      </div>
    </div>
  );
};
