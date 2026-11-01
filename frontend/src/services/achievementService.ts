import api from './api';

export interface Achievement {
  id: number;
  name: string;
  description: string;
  icon: string;
  category: string;
  xpReward: number;
  coinReward: number;
  unlocked: boolean;
  unlockedAt?: string;
}

export const achievementService = {
  getAchievements: async (): Promise<Achievement[]> => {
    const response = await api.get<Achievement[]>('/achievements');
    return response.data;
  }
};
