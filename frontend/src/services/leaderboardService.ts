import api from './api';

export interface LeaderboardEntry {
  rank: number;
  userId: number;
  username: string;
  avatar: string;
  xp: number;
  level: number;
  completedLevels: number;
  streak: number;
}

export const leaderboardService = {
  getLeaderboard: async (timeframe: string = 'global'): Promise<LeaderboardEntry[]> => {
    const response = await api.get<LeaderboardEntry[]>(`/leaderboard?timeframe=${timeframe}`);
    return response.data;
  }
};
