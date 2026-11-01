import React, { useState } from 'react';
import { useAuthStore } from '../stores/authStore';
import { userService } from '../services/userService';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Modal } from '../components/ui/Modal';
import { Mail, Trophy, Star, Zap, Flame, Sparkles, Check } from 'lucide-react';
import { XpBadge } from '../components/ui/XpBadge';

const AVATARS = [
  { id: 'robot', name: 'Robot Rover', emoji: '🤖', description: 'Standard high-tech coding robot.' },
  { id: 'wizard', name: 'Code Wizard', emoji: '🧙', description: 'Master of mystical programming spells.' },
  { id: 'hacker', name: 'Cyber Hacker', emoji: '👾', description: 'Elite matrix navigator.' },
  { id: 'explorer', name: 'Dungeon Explorer', emoji: '🤠', description: 'Brave adventurer of algorithm realms.' },
  { id: 'ninja', name: 'Shadow Ninja', emoji: '🥷', description: 'Swift and silent code refactorer.' },
];

export const Profile: React.FC = () => {
  const { user, setUser } = useAuthStore();
  const [showAvatarModal, setShowAvatarModal] = useState(false);

  if (!user) return null;

  const currentAvatarObj = AVATARS.find(a => a.id === (user.avatar || 'robot')) || AVATARS[0];

  const handleSelectAvatar = async (avatarId: string) => {
    try {
      const updatedUser = await userService.updateAvatar(avatarId);
      setUser(updatedUser);
      setShowAvatarModal(false);
    } catch (err) {
      console.error('Failed to update avatar', err);
    }
  };

  return (
    <div className="container mx-auto px-4 py-8 max-w-4xl">
      <h1 className="text-3xl font-extrabold text-slate-100 mb-8">Player Profile</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Profile Card */}
        <Card className="p-6 md:col-span-1 flex flex-col items-center text-center">
          <div className="relative group cursor-pointer" onClick={() => setShowAvatarModal(true)}>
            <div className="w-28 h-28 bg-gradient-to-tr from-cyan-500/20 to-emerald-500/20 rounded-2xl flex items-center justify-center border-2 border-cyan-400 mb-4 shadow-[0_0_20px_rgba(56,189,248,0.3)] text-5xl">
              {currentAvatarObj.emoji}
            </div>
            <div className="absolute inset-0 bg-slate-950/70 rounded-2xl opacity-0 group-hover:opacity-100 flex items-center justify-center transition-opacity text-xs font-bold text-cyan-300 mb-4">
              <Sparkles className="h-4 w-4 mr-1" /> Change
            </div>
          </div>

          <h2 className="text-2xl font-extrabold text-slate-100">{user.username}</h2>
          <p className="text-xs text-cyan-400 font-bold uppercase tracking-wider mb-2">{currentAvatarObj.name}</p>

          <div className="flex items-center space-x-2 text-slate-400 mb-6">
            <Mail className="h-4 w-4" />
            <span className="text-xs">{user.email}</span>
          </div>

          <Button 
            variant="secondary" 
            size="sm" 
            className="w-full mb-6 text-xs font-bold"
            onClick={() => setShowAvatarModal(true)}
          >
            Customize Avatar
          </Button>

          <XpBadge xp={user.xp} level={user.level} className="w-full justify-center" />
        </Card>

        {/* Stats */}
        <div className="md:col-span-2 space-y-6">
          <Card className="p-6">
            <h3 className="text-lg font-bold text-slate-200 mb-4 flex items-center">
              <Trophy className="h-5 w-5 mr-2 text-amber-400" /> Adventure Statistics
            </h3>
            <div className="grid grid-cols-2 gap-4">
              <div className="bg-slate-900 p-4 rounded-xl border border-slate-800 flex flex-col items-center text-center">
                <Trophy className="h-7 w-7 text-emerald-400 mb-2" />
                <span className="text-2xl font-black text-slate-100">{user.completedLevels || 0}</span>
                <span className="text-xs font-bold text-slate-400 uppercase mt-1">Missions Cleared</span>
              </div>
              <div className="bg-slate-900 p-4 rounded-xl border border-slate-800 flex flex-col items-center text-center">
                <Star className="h-7 w-7 text-amber-400 mb-2" />
                <span className="text-2xl font-black text-slate-100">{user.totalStars || 0}</span>
                <span className="text-xs font-bold text-slate-400 uppercase mt-1">Stars Collected</span>
              </div>
              <div className="bg-slate-900 p-4 rounded-xl border border-slate-800 flex flex-col items-center text-center">
                <Zap className="h-7 w-7 text-cyan-400 mb-2" />
                <span className="text-2xl font-black text-slate-100">{user.xp}</span>
                <span className="text-xs font-bold text-slate-400 uppercase mt-1">Total XP</span>
              </div>
              <div className="bg-slate-900 p-4 rounded-xl border border-slate-800 flex flex-col items-center text-center">
                <Flame className="h-7 w-7 text-orange-400 mb-2" />
                <span className="text-2xl font-black text-slate-100">{user.streak || 0}</span>
                <span className="text-xs font-bold text-slate-400 uppercase mt-1">Day Streak</span>
              </div>
            </div>
          </Card>
        </div>
      </div>

      {/* Avatar Modal */}
      <Modal isOpen={showAvatarModal} onClose={() => setShowAvatarModal(false)} title="Select Character Avatar">
        <div className="space-y-4">
          <p className="text-xs text-slate-400">Choose your favorite avatar sprite to represent you in the code quest world!</p>
          <div className="grid grid-cols-1 gap-3">
            {AVATARS.map((av) => (
              <div
                key={av.id}
                onClick={() => handleSelectAvatar(av.id)}
                className={`p-3 rounded-xl border cursor-pointer flex items-center justify-between transition-all ${
                  user.avatar === av.id 
                    ? 'bg-cyan-950/50 border-cyan-400 shadow-[0_0_15px_rgba(6,182,212,0.2)]' 
                    : 'bg-slate-800/60 border-slate-700 hover:bg-slate-800'
                }`}
              >
                <div className="flex items-center space-x-3">
                  <span className="text-3xl">{av.emoji}</span>
                  <div>
                    <div className="font-bold text-sm text-slate-100">{av.name}</div>
                    <div className="text-xs text-slate-400">{av.description}</div>
                  </div>
                </div>
                {user.avatar === av.id && (
                  <Check className="h-5 w-5 text-cyan-400" />
                )}
              </div>
            ))}
          </div>
        </div>
      </Modal>
    </div>
  );
};
