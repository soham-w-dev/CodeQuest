import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { LogOut, Gamepad2, Compass, Trophy, Flame, Volume2, VolumeX, Award } from 'lucide-react';
import { useAuthStore } from '../../stores/authStore';
import { XpBadge } from '../ui/XpBadge';
import { Button } from '../ui/Button';
import { soundFx } from '../../utils/audio';

export const Navbar: React.FC = () => {
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();
  const location = useLocation();
  const [soundOn, setSoundOn] = useState(soundFx.isEnabled());

  const handleLogout = () => {
    soundFx.playClick();
    logout();
    navigate('/');
  };

  const toggleSound = () => {
    const newState = soundFx.toggleSound();
    setSoundOn(newState);
  };

  const isActive = (path: string) => location.pathname === path || (path !== '/' && location.pathname.startsWith(path));

  const getAvatarEmoji = (avatar?: string | null) => {
    switch ((avatar || '').toLowerCase()) {
      case 'wizard': return '🧙';
      case 'hacker': return '👾';
      case 'explorer': return '🤠';
      case 'ninja': return '🥷';
      default: return '🤖';
    }
  };

  return (
    <nav className="sticky top-0 z-40 w-full border-b border-slate-800 bg-slate-950/90 backdrop-blur-md">
      <div className="container mx-auto px-4 h-16 flex items-center justify-between">
        <div className="flex items-center space-x-6 md:space-x-8">
          <Link to="/" className="flex items-center space-x-2 group" onClick={() => soundFx.playClick()}>
            <div className="bg-gradient-to-tr from-cyan-500/20 to-blue-500/20 p-2 rounded-xl border border-cyan-500/30 group-hover:border-cyan-400 transition-colors shadow-[0_0_15px_rgba(6,182,212,0.2)]">
              <Gamepad2 className="h-6 w-6 text-cyan-400 group-hover:scale-110 transition-transform" />
            </div>
            <span className="text-xl font-black bg-clip-text text-transparent bg-gradient-to-r from-cyan-400 via-blue-400 to-indigo-400 tracking-tight">
              CodeQuest
            </span>
          </Link>

          {user && (
            <div className="hidden md:flex space-x-1">
              <Link 
                to="/dashboard" 
                onClick={() => soundFx.playClick()}
                className={`px-3 py-1.5 rounded-lg text-xs font-extrabold uppercase tracking-wider transition-colors flex items-center space-x-1.5 ${
                  isActive('/dashboard') ? 'bg-slate-800 text-cyan-400 border border-slate-700' : 'text-slate-400 hover:text-slate-100 hover:bg-slate-800/50'
                }`}
              >
                <Compass className="h-3.5 w-3.5" />
                <span>Dashboard</span>
              </Link>

              <Link 
                to="/daily-challenge" 
                onClick={() => soundFx.playClick()}
                className={`px-3 py-1.5 rounded-lg text-xs font-extrabold uppercase tracking-wider transition-colors flex items-center space-x-1.5 ${
                  isActive('/daily-challenge') ? 'bg-amber-950/60 text-amber-400 border border-amber-500/40' : 'text-slate-400 hover:text-amber-300 hover:bg-slate-800/50'
                }`}
              >
                <Flame className="h-3.5 w-3.5 text-amber-400" />
                <span>Daily Quest</span>
              </Link>

              <Link 
                to="/leaderboard" 
                onClick={() => soundFx.playClick()}
                className={`px-3 py-1.5 rounded-lg text-xs font-extrabold uppercase tracking-wider transition-colors flex items-center space-x-1.5 ${
                  isActive('/leaderboard') ? 'bg-slate-800 text-amber-400 border border-slate-700' : 'text-slate-400 hover:text-slate-100 hover:bg-slate-800/50'
                }`}
              >
                <Trophy className="h-3.5 w-3.5 text-amber-400" />
                <span>Leaderboard</span>
              </Link>

              <Link 
                to="/achievements" 
                onClick={() => soundFx.playClick()}
                className={`px-3 py-1.5 rounded-lg text-xs font-extrabold uppercase tracking-wider transition-colors flex items-center space-x-1.5 ${
                  isActive('/achievements') ? 'bg-slate-800 text-purple-400 border border-slate-700' : 'text-slate-400 hover:text-slate-100 hover:bg-slate-800/50'
                }`}
              >
                <Award className="h-3.5 w-3.5 text-purple-400" />
                <span>Trophies</span>
              </Link>
            </div>
          )}
        </div>

        <div className="flex items-center space-x-3">
          {/* Sound Toggle */}
          <button
            onClick={toggleSound}
            title={soundOn ? 'Mute Sound FX' : 'Enable Sound FX'}
            className="p-2 rounded-xl bg-slate-900 border border-slate-800 text-slate-400 hover:text-cyan-400 transition-colors"
          >
            {soundOn ? <Volume2 className="h-4 w-4 text-cyan-400" /> : <VolumeX className="h-4 w-4 text-slate-500" />}
          </button>

          {user ? (
            <>
              <div className="hidden sm:flex items-center space-x-2 text-xs font-bold bg-slate-900 border border-slate-800 px-3 py-1.5 rounded-xl">
                <span className="text-amber-400">🪙 {user.coins || 0}</span>
                <span className="text-slate-700">|</span>
                <span className="text-orange-400 flex items-center"><Flame className="h-3 w-3 mr-0.5 inline" /> {user.streak || 0}d</span>
              </div>

              <XpBadge xp={user.xp} level={user.level} className="hidden sm:flex" />

              <Link to="/profile" onClick={() => soundFx.playClick()}>
                <Button variant="ghost" size="sm" className="flex items-center space-x-1.5">
                  <span className="text-base">{getAvatarEmoji(user.avatar)}</span>
                  <span className="font-bold text-slate-200 hidden md:inline">{user.username}</span>
                </Button>
              </Link>

              <Button variant="ghost" size="sm" onClick={handleLogout} className="text-slate-400 hover:text-red-400 p-2">
                <LogOut className="h-4 w-4" />
              </Button>
            </>
          ) : (
            <>
              <Link to="/login" onClick={() => soundFx.playClick()}>
                <Button variant="ghost" size="sm">Login</Button>
              </Link>
              <Link to="/register" onClick={() => soundFx.playClick()}>
                <Button variant="primary" size="sm">Start Playing</Button>
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};
