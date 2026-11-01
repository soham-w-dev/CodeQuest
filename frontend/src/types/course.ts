export interface LevelSummary {
  id: number;
  title: string;
  difficulty: string;
  concept: string;
  orderIndex: number;
  xpReward: number;
  coinReward: number;
  completed?: boolean;
  stars?: number;
  unlocked?: boolean;
}

export interface World {
  id: number;
  courseId: number;
  name: string;
  description: string;
  theme: string;
  orderIndex: number;
  unlockStarsRequired: number;
  levels: LevelSummary[];
}

export interface Course {
  id: number;
  language: string;
  name: string;
  description: string;
  icon: string;
}
