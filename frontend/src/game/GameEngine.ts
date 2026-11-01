import { GameState, GameAction, Direction, Position } from '../types/game';

export const GameEngine = {
  applyAction(state: GameState, action: GameAction): GameState {
    const newState = JSON.parse(JSON.stringify(state)) as GameState;
    
    if (newState.isCompleted || newState.isFailed) {
      return newState;
    }

    switch (action.type) {
      case 'move': {
        const nextPos = this.getNextPosition(newState.playerPosition, newState.playerDirection);
        if (this.isValidPosition(nextPos, newState)) {
          newState.playerPosition = nextPos;
          this.checkCellInteractions(newState);
        } else {
          newState.isFailed = true;
          newState.failReason = 'Collision detected!';
        }
        break;
      }
      case 'turn_left': {
        newState.playerDirection = this.turnLeft(newState.playerDirection);
        break;
      }
      case 'turn_right': {
        newState.playerDirection = this.turnRight(newState.playerDirection);
        break;
      }
      case 'collect': {
        const { col, row } = newState.playerPosition;
        if (newState.grid[row][col] === 'gem') {
          newState.grid[row][col] = 'empty';
          newState.collectedGems++;
        }
        break;
      }
      case 'collision': {
        newState.isFailed = true;
        newState.failReason = action.details || 'Collision detected!';
        break;
      }
      case 'win': {
        newState.isCompleted = true;
        break;
      }
      case 'error': {
        newState.isFailed = true;
        newState.failReason = action.details || 'Runtime Error';
        break;
      }
    }

    this.checkWinCondition(newState);
    return newState;
  },

  getNextPosition(pos: Position, dir: Direction): Position {
    switch (dir) {
      case 'UP': return { col: pos.col, row: pos.row - 1 };
      case 'DOWN': return { col: pos.col, row: pos.row + 1 };
      case 'LEFT': return { col: pos.col - 1, row: pos.row };
      case 'RIGHT': return { col: pos.col + 1, row: pos.row };
    }
  },

  isValidPosition(pos: Position, state: GameState): boolean {
    const { col, row } = pos;
    const { gridCols, gridRows, grid } = state;
    
    if (col < 0 || col >= gridCols || row < 0 || row >= gridRows) {
      return false; // Out of bounds
    }
    
    if (grid[row][col] === 'wall') {
      return false; // Hit a wall
    }
    
    return true;
  },

  turnLeft(dir: Direction): Direction {
    switch (dir) {
      case 'UP': return 'LEFT';
      case 'LEFT': return 'DOWN';
      case 'DOWN': return 'RIGHT';
      case 'RIGHT': return 'UP';
    }
  },

  turnRight(dir: Direction): Direction {
    switch (dir) {
      case 'UP': return 'RIGHT';
      case 'RIGHT': return 'DOWN';
      case 'DOWN': return 'LEFT';
      case 'LEFT': return 'UP';
    }
  },

  checkCellInteractions(state: GameState): void {
    const { col, row } = state.playerPosition;
    const cell = state.grid[row][col];
    
    if (cell === 'gem') {
      state.grid[row][col] = 'empty';
      state.collectedGems++;
    }
  },

  checkWinCondition(state: GameState): void {
    const { col, row } = state.playerPosition;
    const cell = state.grid[row][col];
    
    if (cell === 'goal') {
      if (state.collectedGems >= state.totalGems) {
        state.isCompleted = true;
      } else {
        state.isFailed = true;
        state.failReason = `Need ${state.totalGems - state.collectedGems} more gems!`;
      }
    }
  }
};
