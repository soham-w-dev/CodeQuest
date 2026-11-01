import React from 'react';
import { motion } from 'framer-motion';
import { cn } from './Button';

interface ProgressBarProps {
  progress: number; // 0 to 100
  color?: 'cyan' | 'emerald' | 'amber';
  showLabel?: boolean;
  className?: string;
}

export const ProgressBar: React.FC<ProgressBarProps> = ({ 
  progress, 
  color = 'cyan', 
  showLabel = false,
  className 
}) => {
  const colors = {
    cyan: 'bg-cyan-500 shadow-[0_0_10px_rgba(56,189,248,0.5)]',
    emerald: 'bg-emerald-500 shadow-[0_0_10px_rgba(16,185,129,0.5)]',
    amber: 'bg-amber-500 shadow-[0_0_10px_rgba(245,158,11,0.5)]',
  };

  return (
    <div className={cn("w-full", className)}>
      {showLabel && (
        <div className="mb-1 flex justify-between text-xs font-medium text-slate-400">
          <span>Progress</span>
          <span>{Math.round(progress)}%</span>
        </div>
      )}
      <div className="h-3 w-full overflow-hidden rounded-full bg-slate-900 border border-slate-800">
        <motion.div
          className={cn("h-full rounded-full", colors[color])}
          initial={{ width: 0 }}
          animate={{ width: `${Math.max(0, Math.min(100, progress))}%` }}
          transition={{ duration: 1, ease: "easeOut" }}
        />
      </div>
    </div>
  );
};
