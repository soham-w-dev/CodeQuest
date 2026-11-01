import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Swords, ArrowLeft, CheckCircle2, Lock, Star } from 'lucide-react';
import { worldService } from '../services/worldService';
import { World } from '../types/course';
import { Card } from '../components/ui/Card';
import { ProgressBar } from '../components/ui/ProgressBar';
import { Button } from '../components/ui/Button';

export const WorldMap: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [world, setWorld] = useState<World | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!id) return;
    
    const fetchWorld = async () => {
      try {
        const data = await worldService.getWorldById(id);
        setWorld(data);
      } catch (err) {
        console.error('Failed to fetch world', err);
        setError('Failed to load world');
      } finally {
        setLoading(false);
      }
    };

    fetchWorld();
  }, [id]);

  if (loading) return (
    <div className="flex-1 flex items-center justify-center bg-slate-950 p-8 text-slate-400">
      <div className="w-10 h-10 border-4 border-cyan-400 border-t-transparent rounded-full animate-spin" />
    </div>
  );

  if (error || !world) return (
    <div className="p-8 text-center text-red-400 bg-slate-950 min-h-screen">
      {error || 'World not found'}
    </div>
  );

  const levels = world.levels || [];
  const completedCount = levels.filter(l => l.completed).length;
  const progressPercent = levels.length > 0 ? Math.round((completedCount / levels.length) * 100) : 0;

  return (
    <div className="container mx-auto px-4 py-8 max-w-5xl">
      {/* Header */}
      <div className="mb-8">
        <Button variant="ghost" size="sm" onClick={() => navigate('/dashboard')} className="mb-4 text-slate-400 hover:text-slate-100">
          <ArrowLeft className="h-4 w-4 mr-2" /> Back to Dashboard
        </Button>
        
        <div className="bg-slate-900 rounded-2xl p-6 md:p-8 border border-slate-700 relative overflow-hidden shadow-2xl">
          <div className="absolute top-0 left-0 w-64 h-64 bg-indigo-500/10 rounded-full blur-[80px]" />
          
          <div className="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
            <div>
              <div className="inline-block text-xs font-bold text-cyan-400 bg-cyan-950 border border-cyan-500/30 px-3 py-1 rounded-full uppercase tracking-wider mb-2">
                Campaign Map
              </div>
              <h1 className="text-3xl md:text-4xl font-black text-slate-100 mb-2">{world.name}</h1>
              <p className="text-slate-400 text-sm max-w-xl leading-relaxed">{world.description}</p>
            </div>
            <div className="w-full md:w-64 bg-slate-800/80 p-4 rounded-xl border border-slate-700">
              <div className="flex justify-between items-end mb-2">
                <span className="text-xs font-extrabold text-slate-300 uppercase tracking-widest">
                  {completedCount} / {levels.length} Missions Cleared
                </span>
                <span className="text-xs font-black text-cyan-400">{progressPercent}%</span>
              </div>
              <ProgressBar progress={progressPercent} color="cyan" />
            </div>
          </div>
        </div>
      </div>

      {/* Level Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {levels.map((level, index) => {
          const isCompleted = Boolean(level.completed);
          const isUnlocked = level.unlocked !== false; // Default true if undefined
          const starsEarned = level.stars || 0;

          const difficultyColors: Record<string, string> = {
            EASY: 'bg-emerald-500/20 text-emerald-400 border-emerald-500/30',
            MEDIUM: 'bg-amber-500/20 text-amber-400 border-amber-500/30',
            HARD: 'bg-purple-500/20 text-purple-400 border-purple-500/30',
          };
          const diffColor = difficultyColors[level.difficulty] || difficultyColors.EASY;

          return (
            <Card 
              key={level.id}
              className={`p-6 flex flex-col transition-all duration-300 shadow-xl relative overflow-hidden ${
                isCompleted 
                  ? 'border-emerald-500/50 bg-slate-900/90 shadow-[0_0_15px_rgba(16,185,129,0.15)] hover:-translate-y-1' 
                  : isUnlocked 
                  ? 'border-slate-700 hover:border-cyan-500/60 hover:-translate-y-1 cursor-pointer' 
                  : 'border-slate-800 bg-slate-950/70 opacity-60 cursor-not-allowed'
              }`}
              onClick={() => {
                if (isUnlocked) {
                  navigate(`/level/${level.id}`);
                }
              }}
              glow={isUnlocked && !isCompleted}
            >
              {/* Status Header Badge */}
              <div className="flex justify-between items-start mb-4">
                <div className={`w-10 h-10 rounded-xl flex items-center justify-center font-black text-sm border shadow-inner ${
                  isCompleted 
                    ? 'bg-emerald-950 text-emerald-400 border-emerald-500/40' 
                    : isUnlocked 
                    ? 'bg-slate-900 text-slate-200 border-slate-700' 
                    : 'bg-slate-950 text-slate-600 border-slate-800'
                }`}>
                  {isCompleted ? <CheckCircle2 className="h-5 w-5 text-emerald-400" /> : isUnlocked ? (index + 1) : <Lock className="h-4 w-4 text-slate-600" />}
                </div>

                <div className="flex items-center space-x-2">
                  {isCompleted && (
                    <span className="text-[10px] font-black px-2.5 py-1 rounded-full bg-emerald-950 border border-emerald-500/40 text-emerald-400 uppercase tracking-wider flex items-center gap-1">
                      <CheckCircle2 className="h-3 w-3" /> PASSED
                    </span>
                  )}
                  {!isUnlocked && (
                    <span className="text-[10px] font-black px-2.5 py-1 rounded-full bg-slate-900 border border-slate-700 text-slate-500 uppercase tracking-wider flex items-center gap-1">
                      <Lock className="h-3 w-3" /> LOCKED
                    </span>
                  )}
                  <span className={`text-[10px] font-black px-2.5 py-1 rounded-full border uppercase tracking-wider ${diffColor}`}>
                    {level.difficulty}
                  </span>
                </div>
              </div>
              
              <h3 className="text-xl font-extrabold text-slate-100 mb-1">{level.title}</h3>
              {level.concept && (
                <p className="text-xs text-slate-400 mb-4 font-semibold">Concept: <span className="text-cyan-400">{level.concept}</span></p>
              )}

              {/* Star Rating Display */}
              <div className="flex items-center space-x-1 my-2">
                {[1, 2, 3].map((starNum) => (
                  <Star 
                    key={starNum} 
                    className={`h-4 w-4 ${
                      isCompleted && starNum <= starsEarned 
                        ? 'text-amber-400 fill-amber-400 drop-shadow-[0_0_6px_rgba(251,191,36,0.6)]' 
                        : 'text-slate-700'
                    }`} 
                  />
                ))}
                {isCompleted && (
                  <span className="text-xs font-bold text-amber-400 ml-1.5">{starsEarned}/3 Stars</span>
                )}
              </div>
              
              {/* Footer */}
              <div className="mt-auto pt-4 flex justify-between items-center border-t border-slate-800/80">
                <div className="flex items-center space-x-1.5 text-xs font-bold text-slate-300">
                  <Swords className="h-4 w-4 text-cyan-400" />
                  <span>+{level.xpReward} XP</span>
                </div>

                {isCompleted ? (
                  <span className="text-xs font-black px-3 py-1.5 bg-slate-800 text-emerald-400 border border-emerald-500/30 rounded-lg hover:bg-slate-700 uppercase tracking-wider flex items-center gap-1">
                    REPLAY 🔄
                  </span>
                ) : isUnlocked ? (
                  <span className="text-xs font-black px-3 py-1.5 bg-gradient-to-r from-cyan-500 to-blue-500 text-slate-950 rounded-lg shadow-md hover:from-cyan-400 hover:to-blue-400 uppercase tracking-wider flex items-center gap-1">
                    PLAY ▶
                  </span>
                ) : (
                  <span className="text-xs font-bold px-3 py-1.5 bg-slate-900 text-slate-600 rounded-lg border border-slate-800 uppercase tracking-wider flex items-center gap-1">
                    LOCKED 🔒
                  </span>
                )}
              </div>
            </Card>
          );
        })}
      </div>
    </div>
  );
};

