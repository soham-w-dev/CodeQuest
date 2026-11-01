import api from './api';
import { LevelDetail } from '../types/level';

export const levelService = {
  getLevelById: async (id: number | string): Promise<LevelDetail> => {
    const response = await api.get<LevelDetail>(`/levels/${id}`);
    return response.data;
  }
};
