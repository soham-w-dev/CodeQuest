import api from './api';
import { User } from '../types/user';

export const userService = {
  getCurrentUser: async (): Promise<User> => {
    const response = await api.get<User>('/users/me');
    return response.data;
  },

  updateLanguage: async (language: string): Promise<User> => {
    const response = await api.put<User>('/users/me/language', { language });
    return response.data;
  },

  updateAvatar: async (avatar: string): Promise<User> => {
    const response = await api.put<User>('/users/me/avatar', { avatar });
    return response.data;
  },

  getUserProgress: async () => {
    const response = await api.get('/users/me/progress');
    return response.data;
  }
};
