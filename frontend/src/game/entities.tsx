import React from 'react';
import { motion } from 'framer-motion';
import { Position, Direction } from '../types/game';

interface EntityProps {
  position: Position;
  size: number;
}

export const Wall: React.FC<EntityProps> = ({ position, size }) => (
  <div
    className="absolute bg-gradient-to-br from-slate-800 to-slate-900 border border-slate-700/80 rounded-md shadow-inner flex items-center justify-center"
    style={{
      left: position.col * size,
      top: position.row * size,
      width: size,
      height: size,
    }}
  >
    <div className="w-3/4 h-3/4 border border-dashed border-slate-700/50 rounded flex items-center justify-center text-slate-600 text-xs select-none">
      🧱
    </div>
  </div>
);

export const Gem: React.FC<EntityProps> = ({ position, size }) => (
  <div
    className="absolute flex items-center justify-center"
    style={{
      left: position.col * size,
      top: position.row * size,
      width: size,
      height: size,
    }}
  >
    <motion.div
      animate={{ 
        y: [0, -4, 0],
        rotate: [0, 180, 360],
        scale: [1, 1.1, 1]
      }}
      transition={{ 
        duration: 2, 
        repeat: Infinity,
        ease: "linear" 
      }}
      className="w-1/2 h-1/2 rotate-45 bg-gradient-to-tr from-cyan-500 to-blue-300 rounded-sm shadow-[0_0_15px_rgba(56,189,248,0.8)] border border-cyan-200"
    />
  </div>
);

export const Goal: React.FC<EntityProps> = ({ position, size }) => (
  <div
    className="absolute flex items-center justify-center"
    style={{
      left: position.col * size,
      top: position.row * size,
      width: size,
      height: size,
    }}
  >
    <motion.div
      animate={{ 
        scale: [1, 1.15, 1],
        opacity: [0.8, 1, 0.8]
      }}
      transition={{ 
        duration: 1.5, 
        repeat: Infinity,
        ease: "easeInOut" 
      }}
      className="w-4/5 h-4/5 rounded-full border-4 border-emerald-400 border-dashed shadow-[0_0_20px_rgba(16,185,129,0.7)] flex items-center justify-center bg-emerald-950/40"
    >
      <span className="text-sm select-none">🚩</span>
    </motion.div>
  </div>
);

interface PlayerProps extends EntityProps {
  direction: Direction;
  avatar?: string;
}

export const Player: React.FC<PlayerProps> = ({ position, size, direction, avatar = 'robot' }) => {
  const getRotation = () => {
    switch (direction) {
      case 'UP': return 0;
      case 'RIGHT': return 90;
      case 'DOWN': return 180;
      case 'LEFT': return 270;
      default: return 0;
    }
  };

  const getAvatarEmoji = () => {
    switch (avatar.toLowerCase()) {
      case 'wizard': return '🧙';
      case 'hacker': return '👾';
      case 'explorer': return '🤠';
      case 'ninja': return '🥷';
      default: return '🤖';
    }
  };

  return (
    <motion.div
      className="absolute flex items-center justify-center z-20"
      initial={false}
      animate={{
        left: position.col * size,
        top: position.row * size,
      }}
      transition={{ type: "spring", stiffness: 300, damping: 25 }}
      style={{
        width: size,
        height: size,
      }}
    >
      <motion.div
        animate={{ rotate: getRotation() }}
        transition={{ type: "spring", stiffness: 300, damping: 25 }}
        className="relative w-4/5 h-4/5 bg-gradient-to-tr from-amber-500 to-amber-300 rounded-xl shadow-[0_0_20px_rgba(251,191,36,0.8)] border-2 border-amber-200 flex items-center justify-center text-xl select-none"
      >
        <span className="transform -rotate-0">{getAvatarEmoji()}</span>
        
        {/* Direction Indicator Nose */}
        <div className="absolute top-0 w-0 h-0 border-l-[5px] border-r-[5px] border-b-[8px] border-l-transparent border-r-transparent border-b-amber-100 -mt-1.5 shadow" />
      </motion.div>
    </motion.div>
  );
};
