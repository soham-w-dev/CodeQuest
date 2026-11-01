import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Flame, Clock, Zap, Coins, ArrowRight, ShieldAlert } from 'lucide-react';
import { dailyChallengeService, DailyChallenge as IDailyChallenge } from '../services/dailyChallengeService';
import { Button } from '../components/ui/Button';

export const DailyChallenge: React.FC = () => {
  const navigate = useNavigate();
  const [challenge, setChallenge] = useState<IDailyChallenge | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchChallenge = async () => {
      try {
        const data = await dailyChallengeService.getDailyChallenge();
        setChallenge(data);
      } catch (err) {
        console.error('Failed to load daily challenge', err);
      } finally {
        setLoading(false);
      }
    };
    fetchChallenge();
  }, []);

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 p-6 md:p-10 max-w-4xl mx-auto flex flex-col justify-center">
      {loading ? (
        <div className="flex items-center justify-center py-20">
          <div className="w-10 h-10 border-4 border-amber-400 border-t-transparent rounded-full animate-spin" />
        </div>
      ) : challenge ? (
        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          className="bg-gradient-to-br from-slate-900 to-slate-950 border-2 border-amber-500/40 rounded-3xl p-8 shadow-[0_0_40px_rgba(245,158,11,0.15)] relative overflow-hidden"
        >
          {/* Header Badge */}
          <div className="flex items-center justify-between mb-6">
            <div className="inline-flex items-center space-x-2 text-amber-400 bg-amber-950/60 border border-amber-500/30 px-3.5 py-1.5 rounded-full text-xs font-bold uppercase tracking-wider">
              <Flame className="h-4 w-4 animate-bounce text-amber-400" />
              <span>Daily Featured Quest</span>
            </div>

            <div className="flex items-center space-x-2 text-slate-400 text-xs font-bold bg-slate-800/80 px-3 py-1.5 rounded-full border border-slate-700">
              <Clock className="h-4 w-4 text-cyan-400" />
              <span>Resets in 24 Hours</span>
            </div>
          </div>

          <h1 className="text-3xl md:text-4xl font-black mb-3 text-transparent bg-clip-text bg-gradient-to-r from-amber-300 via-amber-400 to-yellow-200">
            {challenge.title}
          </h1>

          <p className="text-slate-300 text-base mb-6 leading-relaxed">
            {challenge.missionText}
          </p>

          <div className="grid grid-cols-2 md:grid-cols-3 gap-4 mb-8">
            <div className="bg-slate-900/90 border border-slate-800 rounded-xl p-4">
              <div className="text-slate-400 text-xs font-bold uppercase mb-1">Bonus XP</div>
              <div className="text-cyan-400 font-black text-2xl flex items-center">
                <Zap className="h-5 w-5 mr-1" /> +{challenge.xpReward}
              </div>
            </div>

            <div className="bg-slate-900/90 border border-slate-800 rounded-xl p-4">
              <div className="text-slate-400 text-xs font-bold uppercase mb-1">Bonus Coins</div>
              <div className="text-amber-400 font-black text-2xl flex items-center">
                <Coins className="h-5 w-5 mr-1" /> +{challenge.coinReward}
              </div>
            </div>

            <div className="bg-slate-900/90 border border-slate-800 rounded-xl p-4 col-span-2 md:col-span-1">
              <div className="text-slate-400 text-xs font-bold uppercase mb-1">Difficulty</div>
              <div className="text-purple-400 font-black text-lg uppercase flex items-center">
                <ShieldAlert className="h-5 w-5 mr-1" /> {challenge.difficulty}
              </div>
            </div>
          </div>

          <div className="flex flex-col md:flex-row items-center justify-between gap-4 border-t border-slate-800 pt-6">
            <div className="text-xs text-slate-400">
              Concept Tested: <span className="text-slate-200 font-bold">{challenge.concept}</span>
            </div>

            <Button
              variant="primary"
              size="lg"
              className="w-full md:w-auto px-8 py-3 bg-gradient-to-r from-amber-500 to-yellow-500 hover:from-amber-400 hover:to-yellow-400 text-slate-950 font-black uppercase tracking-wider"
              onClick={() => navigate(`/level/${challenge.id}`)}
            >
              Start Daily Quest <ArrowRight className="h-5 w-5 ml-2 inline" />
            </Button>
          </div>
        </motion.div>
      ) : (
        <div className="text-center text-slate-400">Daily challenge not available today.</div>
      )}
    </div>
  );
};
