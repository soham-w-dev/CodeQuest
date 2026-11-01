import api from './api';
import { ExecutionResult } from '../types/game';

export const gameService = {
  submitCode: async (levelId: number, code: string, language: string): Promise<ExecutionResult> => {
    const response = await api.post<ExecutionResult>('/game/execute', {
      levelId,
      code,
      language
    });
    return response.data;
  }
};
