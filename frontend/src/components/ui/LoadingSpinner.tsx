import React from 'react';
import { motion } from 'framer-motion';
import { cn } from './Button';

interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg';
  className?: string;
}

export const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({ size = 'md', className }) => {
  const sizes = {
    sm: 'w-6 h-6',
    md: 'w-12 h-12',
    lg: 'w-16 h-16',
  };

  return (
    <div className={cn("flex justify-center items-center", className)}>
      <motion.div
        className={cn("border-4 border-slate-800 border-t-cyan-500 rounded-full", sizes[size])}
        animate={{ rotate: 360 }}
        transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
        style={{
          boxShadow: "0 0 15px rgba(56, 189, 248, 0.2)"
        }}
      />
    </div>
  );
};
