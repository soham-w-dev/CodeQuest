import api from './api';
import { World } from '../types/course';

export const worldService = {
  getWorlds: async (courseId: number | string): Promise<World[]> => {
    const response = await api.get<World[]>(`/worlds?courseId=${courseId}`);
    return response.data;
  },
  
  getWorldById: async (id: number | string): Promise<World> => {
    const response = await api.get<World>(`/worlds/${id}`);
    return response.data;
  }
};
