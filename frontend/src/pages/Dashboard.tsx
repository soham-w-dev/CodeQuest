import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Play, Code2, Globe, Flame, Trophy, Award, ChevronRight } from 'lucide-react';
import { useAuthStore } from '../stores/authStore';
import { useProgressStore } from '../stores/progressStore';
import { courseService } from '../services/courseService';
import { Course, World } from '../types/course';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { ProgressBar } from '../components/ui/ProgressBar';
import api from '../services/api';
import { worldService } from '../services/worldService';

export const Dashboard: React.FC = () => {
  const { user, setUser } = useAuthStore();
  const { selectedLanguage, setLanguage } = useProgressStore();
  const navigate = useNavigate();
  
  const [course, setCourse] = useState<Course | null>(null);
  const [worlds, setWorlds] = useState<World[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const response = await api.get('/users/me');
        setUser(response.data);
        if (response.data.selectedLanguage) {
          setLanguage(response.data.selectedLanguage);
        }
      } catch (e) {
        console.error(e);
      }
    };
    fetchUserData();
  }, []);

  useEffect(() => {
    if (!selectedLanguage) {
      return;
    }

    const fetchCourse = async () => {
      try {
        const data = await courseService.getCourseByLanguage(selectedLanguage.toUpperCase());
        setCourse(data);
        if (data) {
          const courseWorlds = await worldService.getWorlds(data.id);
          setWorlds(courseWorlds);
        }
      } catch (error) {
        console.error('Failed to fetch course', error);
      } finally {
        setLoading(false);
      }
    };

    fetchCourse();
  }, [selectedLanguage, navigate]);

  if (loading) return (
    <div className="flex-1 flex items-center justify-center bg-slate-950">
      <div className="w-10 h-10 border-4 border-cyan-400 border-t-transparent rounded-full animate-spin" />
    </div>
  );

  if (!selectedLanguage && !loading) {
    return (
      <div className="flex-1 flex items-center justify-center p-8 bg-slate-950">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-slate-100 mb-4">Choose a Learning Path First</h2>
          <Button onClick={() => navigate('/select-language')}>Select Language Track</Button>
        </div>
      </div>
    );
  }

  const daysOfWeek = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'];
  const streakCount = user?.streak || 1;

  return (
    <div className="container mx-auto px-4 py-8 max-w-6xl">
      <div className="flex flex-col lg:flex-row gap-8">
        
        {/* Main Content Area */}
        <div className="flex-1 space-y-8">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between border-b border-slate-800 pb-4 gap-4">
            <div>
              <h1 className="text-3xl font-extrabold text-slate-100">Welcome back, {user?.username} 👋</h1>
              <p className="text-slate-400 text-sm mt-1">Ready to level up your programming skills today?</p>
            </div>
            <div 
              onClick={() => navigate('/select-language')}
              className="flex items-center space-x-2 bg-slate-900 hover:bg-slate-800 cursor-pointer px-4 py-2 rounded-xl border border-slate-800 transition-colors self-start"
            >
              <Code2 className="text-cyan-400 h-5 w-5" />
              <span className="font-bold text-slate-200 capitalize">{selectedLanguage} Track</span>
              <span className="text-xs text-slate-500 underline ml-2">Switch</span>
            </div>
          </div>

          {/* Featured Hero Banner */}
          <Card className="p-8 border-cyan-500/40 bg-gradient-to-r from-slate-900 via-slate-900 to-cyan-950/40 relative overflow-hidden shadow-2xl">
            <div className="absolute top-0 right-0 w-80 h-80 bg-cyan-500/10 rounded-full blur-[100px]" />
            <div className="relative z-10 flex flex-col md:flex-row justify-between items-center gap-6">
              <div>
                <span className="inline-flex items-center text-xs font-bold text-cyan-400 bg-cyan-950 border border-cyan-500/30 px-3 py-1 rounded-full uppercase tracking-wider mb-3">
                  Current Quest
                </span>
                <h2 className="text-2xl font-black text-slate-100 mb-2">Code Forest Adventure</h2>
                <p className="text-slate-300 text-sm max-w-md">
                  Master core programming logic through interactive grid missions in {course?.name || 'CodeQuest'}.
                </p>
                <div className="mt-5 flex items-center space-x-4 max-w-xs">
                  <ProgressBar progress={Math.min(100, Math.round(((user?.completedLevels || 0) / (worlds[0]?.levels?.length || 10)) * 100))} showLabel color="cyan" />
                </div>
              </div>
              <Button size="lg" onClick={() => navigate(worlds.length > 0 ? `/world/${worlds[0].id}` : '#')} className="w-full md:w-auto px-8 py-3 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-bold uppercase tracking-wider">
                <Play className="h-5 w-5 mr-2 inline" /> Continue Quest
              </Button>
            </div>
          </Card>

          {/* Daily Challenge Featured Card */}
          <div 
            onClick={() => navigate('/daily-challenge')}
            className="p-6 rounded-2xl bg-gradient-to-r from-amber-950/40 via-slate-900 to-slate-900 border border-amber-500/30 cursor-pointer hover:border-amber-400/60 transition-all shadow-xl flex items-center justify-between"
          >
            <div className="flex items-center space-x-4">
              <div className="w-12 h-12 rounded-xl bg-amber-500/20 border border-amber-400/40 flex items-center justify-center text-amber-400">
                <Flame className="h-6 w-6 animate-pulse" />
              </div>
              <div>
                <div className="text-xs font-bold text-amber-400 uppercase tracking-wider">Daily Challenge Available</div>
                <div className="font-extrabold text-slate-100 text-lg">The Lost Robot</div>
                <div className="text-xs text-slate-400 mt-0.5">Earn 2x XP (+200 XP) and Bonus Coins</div>
              </div>
            </div>
            <ChevronRight className="h-6 w-6 text-slate-400" />
          </div>

          {/* Worlds Section */}
          <div>
            <h3 className="text-xl font-extrabold text-slate-100 mb-4 flex items-center">
              <Globe className="mr-2 h-5 w-5 text-indigo-400" /> Campaign Worlds
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {worlds.map((world, index) => (
                <Card 
                  key={world.id} 
                  className={`p-5 cursor-pointer transition-all ${
                    index === 0 ? 'hover:border-cyan-500/50 border-slate-700' : 'opacity-75 border-slate-800'
                  }`} 
                  onClick={() => navigate(`/world/${world.id}`)}
                >
                  <div className="flex justify-between items-center mb-2">
                    <h4 className="font-extrabold text-slate-100">{world.name}</h4>
                    {index === 0 ? (
                      <span className="text-xs font-extrabold px-2.5 py-1 bg-cyan-950 text-cyan-400 border border-cyan-500/30 rounded-full">UNLOCKED</span>
                    ) : (
                      <span className="text-xs font-bold px-2.5 py-1 bg-slate-800 text-slate-500 rounded-full">LOCKED</span>
                    )}
                  </div>
                  <p className="text-xs text-slate-400 line-clamp-2 mb-3">{world.description}</p>
                  <ProgressBar progress={index === 0 ? Math.min(100, Math.round(((user?.completedLevels || 0) / (world.levels?.length || 10)) * 100)) : 0} color={index === 0 ? 'cyan' : 'amber'} />
                </Card>
              ))}
            </div>
          </div>
        </div>

        {/* Sidebar */}
        <div className="w-full lg:w-80 space-y-6">
          {/* Streak Tracker */}
          <Card className="p-6 border-slate-800">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center space-x-2">
                <Flame className="h-6 w-6 text-orange-400" />
                <span className="font-extrabold text-slate-100 text-base">{streakCount} Day Streak</span>
              </div>
              <span className="text-xs text-orange-400 font-bold bg-orange-950/60 px-2 py-0.5 rounded border border-orange-500/30">Active</span>
            </div>

            <div className="grid grid-cols-7 gap-1 text-center">
              {daysOfWeek.map((day, idx) => {
                const isActive = idx < streakCount;
                return (
                  <div key={day} className="flex flex-col items-center">
                    <div className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-black mb-1 ${
                      isActive ? 'bg-orange-500 text-slate-950 shadow-[0_0_10px_rgba(249,115,22,0.6)]' : 'bg-slate-800 text-slate-600'
                    }`}>
                      {isActive ? '✓' : ''}
                    </div>
                    <span className="text-[10px] text-slate-400 font-bold">{day}</span>
                  </div>
                );
              })}
            </div>
          </Card>

          {/* Quick Navigation Cards */}
          <Card className="p-6 space-y-3">
            <h3 className="font-extrabold text-slate-300 uppercase tracking-wider text-xs mb-3">Quick Explorer</h3>
            
            <div 
              onClick={() => navigate('/leaderboard')}
              className="p-3 rounded-xl bg-slate-900 hover:bg-slate-800 cursor-pointer border border-slate-800 transition-colors flex items-center justify-between"
            >
              <div className="flex items-center space-x-3">
                <Trophy className="h-5 w-5 text-amber-400" />
                <span className="font-bold text-sm text-slate-200">Leaderboard</span>
              </div>
              <ChevronRight className="h-4 w-4 text-slate-500" />
            </div>

            <div 
              onClick={() => navigate('/achievements')}
              className="p-3 rounded-xl bg-slate-900 hover:bg-slate-800 cursor-pointer border border-slate-800 transition-colors flex items-center justify-between"
            >
              <div className="flex items-center space-x-3">
                <Award className="h-5 w-5 text-purple-400" />
                <span className="font-bold text-sm text-slate-200">Achievements</span>
              </div>
              <ChevronRight className="h-4 w-4 text-slate-500" />
            </div>
          </Card>

          {/* Player Quick Stats */}
          <Card className="p-6">
            <h3 className="font-extrabold text-slate-300 uppercase tracking-wider text-xs mb-4">Player Overview</h3>
            <div className="space-y-3">
              <div className="flex justify-between items-center p-3 bg-slate-900 rounded-xl">
                <span className="text-slate-400 text-xs font-bold uppercase">Level</span>
                <span className="font-black text-lg text-cyan-400">Lvl {user?.level || 1}</span>
              </div>
              <div className="flex justify-between items-center p-3 bg-slate-900 rounded-xl">
                <span className="text-slate-400 text-xs font-bold uppercase">Total XP</span>
                <span className="font-black text-lg text-emerald-400">{user?.xp || 0} XP</span>
              </div>
              <div className="flex justify-between items-center p-3 bg-slate-900 rounded-xl">
                <span className="text-slate-400 text-xs font-bold uppercase">Coins</span>
                <span className="font-black text-lg text-amber-400">{user?.coins || 0} 🪙</span>
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
};
