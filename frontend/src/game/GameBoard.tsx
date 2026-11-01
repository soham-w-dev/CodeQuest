import React from 'react';
import { GameState } from '../types/game';
import { Wall, Gem, Goal, Player } from './entities';

interface GameBoardProps {
  gameState: GameState;
  cellSize?: number;
  userAvatar?: string;
}

export const GameBoard: React.FC<GameBoardProps> = ({ gameState, cellSize = 40, userAvatar = 'robot' }) => {
  const { grid, gridCols, gridRows, playerPosition, playerDirection } = gameState;

  return (
    <div className="relative overflow-hidden rounded-xl border-2 border-slate-700 bg-slate-900 shadow-2xl">
      <div 
        className="relative"
        style={{
          width: gridCols * cellSize,
          height: gridRows * cellSize,
          backgroundImage: `
            linear-gradient(to right, #1e293b 1px, transparent 1px),
            linear-gradient(to bottom, #1e293b 1px, transparent 1px)
          `,
          backgroundSize: `${cellSize}px ${cellSize}px`,
        }}
      >
        {/* Render Grid Elements */}
        {grid.map((rowArr, row) => 
          rowArr.map((cell, col) => {
            if (cell === 'wall') {
              return <Wall key={`wall-${col}-${row}`} position={{ col, row }} size={cellSize} />;
            }
            if (cell === 'gem') {
              return <Gem key={`gem-${col}-${row}`} position={{ col, row }} size={cellSize} />;
            }
            if (cell === 'goal') {
              return <Goal key={`goal-${col}-${row}`} position={{ col, row }} size={cellSize} />;
            }
            return null;
          })
        )}

        {/* Render Player */}
        <Player position={playerPosition} direction={playerDirection} size={cellSize} avatar={userAvatar} />
      </div>
    </div>
  );
};
