import api from './api';
import { Course } from '../types/course';

export const courseService = {
  getCourses: async (): Promise<Course[]> => {
    const response = await api.get<Course[]>('/courses');
    return response.data;
  },
  
  getCourseByLanguage: async (language: string): Promise<Course> => {
    const response = await api.get<Course>(`/courses/${language}`);
    return response.data;
  }
};
