import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface ProgressState {
  selectedLanguage: string | null;
  currentCourseId: number | null;
  
  setLanguage: (lang: string) => void;
  setCourse: (courseId: number) => void;
}

export const useProgressStore = create<ProgressState>()(
  persist(
    (set) => ({
      selectedLanguage: null,
      currentCourseId: null,
      
      setLanguage: (lang) => set({ selectedLanguage: lang }),
      setCourse: (courseId) => set({ currentCourseId: courseId }),
    }),
    {
      name: 'progress-storage',
    }
  )
);
