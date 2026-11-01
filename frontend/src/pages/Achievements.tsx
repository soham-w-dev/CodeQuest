import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { Trophy, Zap, Coins, CheckCircle, Lock } from 'lucide-react';
import { achievementService, Achievement } from '../services/achievementService';

export const Achievements: React.FC = () => {
  const [achievements, setAchievements] = useState<Achievement[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAchievements = async () => {
      try {
        const data = await achievementService.getAchievements();
        setAchievements(data);
      } catch (err) {
        console.error('Failed to load achievements', err);
      } finally {
        setLoading(false);
      }
    };
    fetchAchievements();
  }, []);

  const unlockedCount = achievements.filter(a => a.unlocked).length;

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 p-6 md:p-10 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
        <div>
          <div className="inline-flex items-center space-x-2 text-cyan-400 bg-cyan-950/40 border border-cyan-500/30 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider mb-2">
            <Trophy className="h-4 w-4" />
            <span>Trophy Hall</span>
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight">Achievements Gallery</h1>
          <p className="text-slate-400 text-sm mt-1">Unlock badges, claim rewards, and track your coding milestones.</p>
        </div>

        <div className="bg-slate-900 border border-slate-800 rounded-xl p-4 flex items-center space-x-6 shadow-xl">
          <div>
            <div className="text-xs text-slate-400 font-bold uppercase">Total Badges</div>
            <div className="text-2xl font-black text-cyan-400">{unlockedCount} / {achievements.length}</div>
          </div>
          <div className="w-24 bg-slate-800 rounded-full h-3 overflow-hidden border border-slate-700">
            <div 
              className="bg-gradient-to-r from-cyan-400 to-emerald-400 h-full transition-all duration-500"
              style={{ width: `${achievements.length ? (unlockedCount / achievements.length) * 100 : 0}%` }}
            />
          </div>
        </div>
      </div>

      {loading ? (
        <div className="flex items-center justify-center py-20">
          <div className="w-10 h-10 border-4 border-cyan-400 border-t-transparent rounded-full animate-spin" />
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {achievements.map((ach) => (
            <motion.div
              key={ach.id}
              whileHover={{ y: -4 }}
              className={`relative rounded-xl p-6 border transition-all duration-300 ${
                ach.unlocked 
                  ? 'bg-slate-900/90 border-cyan-500/50 shadow-[0_0_20px_rgba(6,182,212,0.15)]' 
                  : 'bg-slate-900/40 border-slate-800 opacity-75'
              }`}
            >
              <div className="flex items-start justify-between mb-4">
                <div className={`w-14 h-14 rounded-xl flex items-center justify-center text-2xl shadow-lg ${
                  ach.unlocked 
                    ? 'bg-gradient-to-br from-cyan-500/20 to-emerald-500/20 border border-cyan-400/40' 
                    : 'bg-slate-800 border border-slate-700'
                }`}>
                  {ach.icon || '🏆'}
                </div>

                {ach.unlocked ? (
                  <span className="inline-flex items-center space-x-1 text-emerald-400 bg-emerald-950/60 border border-emerald-500/30 px-2.5 py-1 rounded-full text-xs font-bold">
                    <CheckCircle className="h-3.5 w-3.5" />
                    <span>Unlocked</span>
                  </span>
                ) : (
                  <span className="inline-flex items-center space-x-1 text-slate-500 bg-slate-800 border border-slate-700 px-2.5 py-1 rounded-full text-xs font-bold">
                    <Lock className="h-3.5 w-3.5" />
                    <span>Locked</span>
                  </span>
                )}
              </div>

              <h3 className="text-lg font-bold text-slate-100 mb-1">{ach.name}</h3>
              <p className="text-slate-400 text-sm mb-4 min-h-[40px]">{ach.description}</p>

              <div className="flex items-center space-x-4 pt-3 border-t border-slate-800 text-xs font-bold">
                <div className="flex items-center text-cyan-400">
                  <Zap className="h-4 w-4 mr-1" /> +{ach.xpReward} XP
                </div>
                <div className="flex items-center text-amber-400">
                  <Coins className="h-4 w-4 mr-1" /> +{ach.coinReward} Coins
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      )}
    </div>
  );
};
