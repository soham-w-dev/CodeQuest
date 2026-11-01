export interface LevelConfig {
  rows: number;
  cols: number;
  playerStart: { row: number; col: number };
  playerDirection: string;
  goal: { row: number; col: number };
  walls: { row: number; col: number }[];
  gems: { row: number; col: number }[];
}

export interface Hint {
  text: string;
  cost: number;
}

export interface Level {
  id: number;
  worldId: number;
  title: string;
  description: string;
  difficulty: string;
  concept: string;
  missionText: string;
  orderIndex: number;
  xpReward: number;
  coinReward: number;
}

export interface LevelDetail extends Level {
  levelConfig: LevelConfig;
  starterCode: string;
  solutionCode?: string;
  hints: Hint[];
}
