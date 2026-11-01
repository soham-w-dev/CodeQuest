import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { Trophy, Crown, Zap } from 'lucide-react';
import { leaderboardService, LeaderboardEntry } from '../services/leaderboardService';

export const Leaderboard: React.FC = () => {
  const [entries, setEntries] = useState<LeaderboardEntry[]>([]);
  const [timeframe, setTimeframe] = useState<'global' | 'weekly' | 'monthly'>('global');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchLeaderboard = async () => {
      setLoading(true);
      try {
        const data = await leaderboardService.getLeaderboard(timeframe);
        setEntries(data);
      } catch (err) {
        console.error('Failed to load leaderboard', err);
      } finally {
        setLoading(false);
      }
    };
    fetchLeaderboard();
  }, [timeframe]);

  const topThree = entries.slice(0, 3);

  const getAvatarEmoji = (avatar: string) => {
    switch ((avatar || '').toLowerCase()) {
      case 'wizard': return '🧙';
      case 'hacker': return '👾';
      case 'explorer': return '🤠';
      case 'ninja': return '🥷';
      default: return '🤖';
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 p-6 md:p-10 max-w-5xl mx-auto">
      {/* Header */}
      <div className="text-center mb-8">
        <div className="inline-flex items-center space-x-2 text-amber-400 bg-amber-950/40 border border-amber-500/30 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider mb-3">
          <Trophy className="h-4 w-4" />
          <span>Hall of Fame</span>
        </div>
        <h1 className="text-4xl font-black tracking-tight uppercase">Leaderboard</h1>
        <p className="text-slate-400 text-sm mt-2">Compete with programmers worldwide and climb to rank #1!</p>

        {/* Timeframe selector */}
        <div className="flex justify-center space-x-2 mt-6 bg-slate-900/80 p-1.5 rounded-xl border border-slate-800 w-fit mx-auto">
          {(['global', 'weekly', 'monthly'] as const).map((tf) => (
            <button
              key={tf}
              onClick={() => setTimeframe(tf)}
              className={`px-5 py-2 rounded-lg text-xs font-bold capitalize transition-all ${
                timeframe === tf
                  ? 'bg-gradient-to-r from-cyan-500 to-blue-600 text-white shadow-lg shadow-cyan-500/20'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800'
              }`}
            >
              {tf}
            </button>
          ))}
        </div>
      </div>

      {loading ? (
        <div className="flex items-center justify-center py-20">
          <div className="w-10 h-10 border-4 border-amber-400 border-t-transparent rounded-full animate-spin" />
        </div>
      ) : (
        <>
          {/* Top 3 Podium */}
          {topThree.length > 0 && (
            <div className="grid grid-cols-3 gap-4 mb-10 items-end max-w-3xl mx-auto">
              {/* 2nd Place */}
              {topThree[1] && (
                <motion.div
                  initial={{ y: 20, opacity: 0 }}
                  animate={{ y: 0, opacity: 1 }}
                  transition={{ delay: 0.1 }}
                  className="bg-slate-900 border border-slate-700/80 rounded-2xl p-4 text-center shadow-xl relative flex flex-col items-center"
                >
                  <div className="w-8 h-8 rounded-full bg-slate-400/20 text-slate-300 font-bold flex items-center justify-center text-sm mb-2">#2</div>
                  <div className="w-14 h-14 rounded-full bg-slate-800 border-2 border-slate-400 flex items-center justify-center text-2xl mb-2 shadow-inner">
                    {getAvatarEmoji(topThree[1].avatar)}
                  </div>
                  <div className="font-bold text-slate-100 truncate w-full">{topThree[1].username}</div>
                  <div className="text-cyan-400 text-xs font-black flex items-center justify-center mt-1">
                    <Zap className="h-3.5 w-3.5 mr-1" /> {topThree[1].xp} XP
                  </div>
                </motion.div>
              )}

              {/* 1st Place */}
              {topThree[0] && (
                <motion.div
                  initial={{ y: 20, opacity: 0 }}
                  animate={{ y: 0, opacity: 1 }}
                  className="bg-gradient-to-b from-amber-950/60 to-slate-900 border-2 border-amber-400/80 rounded-2xl p-5 text-center shadow-[0_0_30px_rgba(251,191,36,0.2)] relative flex flex-col items-center transform -translate-y-4"
                >
                  <Crown className="h-7 w-7 text-amber-400 mb-1 animate-bounce" />
                  <div className="w-16 h-16 rounded-full bg-gradient-to-tr from-amber-500 to-yellow-300 border-2 border-amber-200 flex items-center justify-center text-3xl mb-2 shadow-lg">
                    {getAvatarEmoji(topThree[0].avatar)}
                  </div>
                  <div className="font-black text-amber-300 text-lg truncate w-full">{topThree[0].username}</div>
                  <div className="text-amber-400 font-black flex items-center justify-center mt-1 text-sm">
                    <Zap className="h-4 w-4 mr-1" /> {topThree[0].xp} XP
                  </div>
                </motion.div>
              )}

              {/* 3rd Place */}
              {topThree[2] && (
                <motion.div
                  initial={{ y: 20, opacity: 0 }}
                  animate={{ y: 0, opacity: 1 }}
                  transition={{ delay: 0.2 }}
                  className="bg-slate-900 border border-slate-700/80 rounded-2xl p-4 text-center shadow-xl relative flex flex-col items-center"
                >
                  <div className="w-8 h-8 rounded-full bg-amber-700/20 text-amber-600 font-bold flex items-center justify-center text-sm mb-2">#3</div>
                  <div className="w-14 h-14 rounded-full bg-slate-800 border-2 border-amber-700 flex items-center justify-center text-2xl mb-2 shadow-inner">
                    {getAvatarEmoji(topThree[2].avatar)}
                  </div>
                  <div className="font-bold text-slate-100 truncate w-full">{topThree[2].username}</div>
                  <div className="text-cyan-400 text-xs font-black flex items-center justify-center mt-1">
                    <Zap className="h-3.5 w-3.5 mr-1" /> {topThree[2].xp} XP
                  </div>
                </motion.div>
              )}
            </div>
          )}

          {/* List Table */}
          <div className="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-2xl">
            <div className="px-6 py-4 bg-slate-950/60 border-b border-slate-800 grid grid-cols-12 text-slate-400 text-xs font-bold uppercase tracking-wider">
              <div className="col-span-2">Rank</div>
              <div className="col-span-5">Player</div>
              <div className="col-span-3 text-center">Level</div>
              <div className="col-span-2 text-right">XP</div>
            </div>

            <div className="divide-y divide-slate-800/60">
              {entries.map((entry) => (
                <div key={entry.userId} className="px-6 py-4 grid grid-cols-12 items-center hover:bg-slate-800/40 transition-colors">
                  <div className="col-span-2 font-black text-slate-400 text-sm">
                    #{entry.rank}
                  </div>

                  <div className="col-span-5 flex items-center space-x-3">
                    <span className="text-2xl">{getAvatarEmoji(entry.avatar)}</span>
                    <div>
                      <div className="font-bold text-slate-100 text-sm">{entry.username}</div>
                      <div className="text-xs text-slate-400 flex items-center space-x-2">
                        <span>🔥 {entry.streak || 0} day streak</span>
                      </div>
                    </div>
                  </div>

                  <div className="col-span-3 text-center">
                    <span className="bg-slate-800 border border-slate-700 px-3 py-1 rounded-full text-xs font-bold text-slate-300">
                      Lvl {entry.level}
                    </span>
                  </div>

                  <div className="col-span-2 text-right font-black text-cyan-400 text-sm flex items-center justify-end">
                    <Zap className="h-4 w-4 mr-1 text-cyan-400" />
                    {entry.xp.toLocaleString()}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </>
      )}
    </div>
  );
};
