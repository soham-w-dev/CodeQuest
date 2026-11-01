import React from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Code2, Terminal } from 'lucide-react';
import { useProgressStore } from '../stores/progressStore';
import { useAuthStore } from '../stores/authStore';
import { Card } from '../components/ui/Card';
import api from '../services/api';

export const LanguageSelect: React.FC = () => {
  const { setLanguage } = useProgressStore();
  const { updateUser } = useAuthStore();
  const navigate = useNavigate();

  const handleSelect = async (lang: string) => {
    try {
      const upper = lang.toUpperCase();
      await api.put('/users/me/language', { language: upper });
      setLanguage(upper); // Store uppercase to match backend
      updateUser({ selectedLanguage: upper });
      navigate('/dashboard');
    } catch (e) {
      console.error(e);
      // Still navigate even if backend call fails
      setLanguage(lang.toUpperCase());
      navigate('/dashboard');
    }
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] flex flex-col items-center justify-center p-4">
      <div className="text-center mb-12">
        <h1 className="text-4xl font-bold text-slate-100 mb-4">Choose Your Path</h1>
        <p className="text-slate-400">Select a language to begin your journey</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8 max-w-4xl w-full">
        <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
          <Card 
            className="p-8 cursor-pointer h-full flex flex-col items-center text-center border-2 border-slate-700 hover:border-emerald-500 bg-slate-900 group"
            onClick={() => handleSelect('PYTHON')}
          >
            <div className="w-20 h-20 bg-emerald-500/10 rounded-2xl flex items-center justify-center mb-6 group-hover:bg-emerald-500/20 transition-colors">
              <Terminal className="h-10 w-10 text-emerald-400" />
            </div>
            <h2 className="text-3xl font-bold text-slate-100 mb-2">Python</h2>
            <p className="text-slate-400 mb-6 flex-1">
              Beginner friendly, highly readable. Great for rapid development and data manipulation.
            </p>
            <div className="px-4 py-2 bg-emerald-500/20 text-emerald-400 rounded-full text-sm font-bold w-full">
              SELECT PYTHON
            </div>
          </Card>
        </motion.div>

        <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
          <Card 
            className="p-8 cursor-pointer h-full flex flex-col items-center text-center border-2 border-slate-700 hover:border-amber-500 bg-slate-900 group"
            onClick={() => handleSelect('JAVA')}
          >
            <div className="w-20 h-20 bg-amber-500/10 rounded-2xl flex items-center justify-center mb-6 group-hover:bg-amber-500/20 transition-colors">
              <Code2 className="h-10 w-10 text-amber-400" />
            </div>
            <h2 className="text-3xl font-bold text-slate-100 mb-2">Java</h2>
            <p className="text-slate-400 mb-6 flex-1">
              Object-oriented powerhouse. Build strong foundations for complex software systems.
            </p>
            <div className="px-4 py-2 bg-amber-500/20 text-amber-400 rounded-full text-sm font-bold w-full">
              SELECT JAVA
            </div>
          </Card>
        </motion.div>
      </div>
    </div>
  );
};
