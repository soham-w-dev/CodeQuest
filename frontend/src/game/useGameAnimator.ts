import { useState, useEffect, useRef } from 'react';
import { GameState, GameAction } from '../types/game';
import { GameEngine } from './GameEngine';

interface UseGameAnimatorProps {
  initialState: GameState;
  actions: GameAction[];
  speed?: number; // ms per step
  onStateChange: (state: GameState) => void;
  onLineChange: (line?: number) => void;
  onComplete: (finalState: GameState) => void;
}

export function useGameAnimator({
  initialState,
  actions,
  speed = 500,
  onStateChange,
  onLineChange,
  onComplete
}: UseGameAnimatorProps) {
  const [isPlaying, setIsPlaying] = useState(false);
  const [currentStep, setCurrentStep] = useState(0);
  const [currentState, setCurrentState] = useState<GameState>(initialState);
  
  const timerRef = useRef<NodeJS.Timeout | null>(null);

  // Reset when initial state or actions change
  useEffect(() => {
    setCurrentState(initialState);
    setCurrentStep(0);
    setIsPlaying(false);
    onLineChange(undefined);
  }, [initialState, actions]);

  const step = () => {
    if (currentStep >= actions.length) {
      setIsPlaying(false);
      onComplete(currentState);
      return;
    }

    const action = actions[currentStep];
    const newState = GameEngine.applyAction(currentState, action);
    
    setCurrentState(newState);
    onStateChange(newState);
    onLineChange(undefined); // Removed action.lineNumber since it's not in the new types
    
    if (newState.isCompleted || newState.isFailed) {
      setIsPlaying(false);
      onComplete(newState);
      return;
    }

    setCurrentStep(prev => prev + 1);
  };

  useEffect(() => {
    if (isPlaying) {
      timerRef.current = setTimeout(step, speed);
    }
    return () => {
      if (timerRef.current) clearTimeout(timerRef.current);
    };
  }, [isPlaying, currentStep, currentState]);

  const play = () => setIsPlaying(true);
  const pause = () => setIsPlaying(false);
  const reset = () => {
    setIsPlaying(false);
    setCurrentState(initialState);
    setCurrentStep(0);
    onStateChange(initialState);
    onLineChange(undefined);
  };

  return {
    isPlaying,
    currentStep,
    currentState,
    play,
    pause,
    reset,
    step
  };
}
