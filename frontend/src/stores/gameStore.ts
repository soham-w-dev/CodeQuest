import { create } from 'zustand';
import { GameState, GameAction, ExecutionResult } from '../types/game';

interface GameStoreState {
  currentState: GameState | null;
  actions: GameAction[];
  isPlaying: boolean;
  isCompleted: boolean;
  code: string;
  language: string;
  
  setCurrentState: (state: GameState) => void;
  setCode: (code: string) => void;
  setLanguage: (lang: string) => void;
  setExecutionResult: (result: ExecutionResult) => void;
  setIsPlaying: (isPlaying: boolean) => void;
  resetGame: () => void;
}

export const useGameStore = create<GameStoreState>((set) => ({
  currentState: null,
  actions: [],
  isPlaying: false,
  isCompleted: false,
  code: '',
  language: 'python',
  
  setCurrentState: (state) => set({ currentState: state }),
  setCode: (code) => set({ code }),
  setLanguage: (language) => set({ language }),
  setExecutionResult: (result) => set({ 
    actions: result.actions || [],
    isCompleted: result.success 
  }),
  setIsPlaying: (isPlaying) => set({ isPlaying }),
  resetGame: () => set({ 
    currentState: null, 
    actions: [], 
    isPlaying: false,
    isCompleted: false 
  }),
}));
