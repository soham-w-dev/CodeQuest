export type Direction = 'UP' | 'DOWN' | 'LEFT' | 'RIGHT';
export type CellType = 'empty' | 'wall' | 'gem' | 'goal' | 'player';

export interface Position {
  row: number;
  col: number;
}

export interface GameState {
  grid: CellType[][];
  playerPosition: Position;
  playerDirection: Direction;
  collectedGems: number;
  totalGems: number;
  isCompleted: boolean;
  isFailed: boolean;
  failReason?: string;
  gridRows: number;
  gridCols: number;
}

export interface GameAction {
  type: string;
  details?: string;
  line?: number;
}

export interface ExecutionResult {
  success: boolean;
  error: boolean;
  errorMessage?: string;
  errorLine?: number;
  errorExplanation?: string;
  hint?: string;
  goalReached: boolean;
  gemsCollected: number;
  totalGems: number;
  actions: GameAction[];
  stars: number;
  xpAwarded: number;
  coinsAwarded: number;
  stdout?: string;
}
