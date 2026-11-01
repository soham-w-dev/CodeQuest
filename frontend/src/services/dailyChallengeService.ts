import api from './api';

export interface DailyChallenge {
  id: number;
  title: string;
  description: string;
  difficulty: string;
  concept: string;
  missionText: string;
  xpReward: number;
  coinReward: number;
  levelConfig: string;
  starterCodePython: string;
  starterCodeJava: string;
}

export const dailyChallengeService = {
  getDailyChallenge: async (): Promise<DailyChallenge> => {
    const response = await api.get<DailyChallenge>('/daily-challenge');
    return response.data;
  }
};
