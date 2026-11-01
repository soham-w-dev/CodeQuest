import React from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Terminal, Gamepad2, Trophy, Code2 } from 'lucide-react';
import { Button } from '../components/ui/Button';

export const Landing: React.FC = () => {
  return (
    <div className="min-h-screen bg-slate-950 flex flex-col items-center justify-center relative overflow-hidden">
      {/* Background elements */}
      <div className="absolute inset-0 z-0">
        <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-cyan-500/20 rounded-full blur-[100px]" />
        <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-blue-600/20 rounded-full blur-[100px]" />
      </div>

      <div className="z-10 text-center px-4 max-w-4xl">
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8 }}
          className="flex justify-center mb-6"
        >
          <div className="bg-slate-900/50 p-4 rounded-2xl border border-slate-800 backdrop-blur-sm">
            <Gamepad2 className="h-16 w-16 text-cyan-400 drop-shadow-[0_0_15px_rgba(56,189,248,0.8)]" />
          </div>
        </motion.div>

        <motion.h1
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.8, delay: 0.2 }}
          className="text-5xl md:text-7xl font-extrabold mb-6 tracking-tight"
        >
          <span className="text-transparent bg-clip-text bg-gradient-to-r from-cyan-400 to-blue-600">
            Code. Play. Level Up.
          </span>
        </motion.h1>

        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.8, delay: 0.4 }}
          className="text-lg md:text-2xl text-slate-400 mb-10 max-w-2xl mx-auto"
        >
          Master Python and Java through epic adventures. Write real code to control your hero, solve puzzles, and defeat bugs.
        </motion.p>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.6 }}
          className="flex flex-col sm:flex-row justify-center items-center space-y-4 sm:space-y-0 sm:space-x-6"
        >
          <Link to="/register">
            <Button size="lg" className="w-full sm:w-auto text-lg px-12">
              Start Your Adventure
            </Button>
          </Link>
          <Link to="/login">
            <Button variant="secondary" size="lg" className="w-full sm:w-auto text-lg px-12">
              Login to Continue
            </Button>
          </Link>
        </motion.div>
      </div>

      {/* Feature grid */}
      <div className="z-10 mt-32 grid grid-cols-1 md:grid-cols-3 gap-8 px-4 max-w-6xl w-full">
        <FeatureCard 
          icon={<Terminal className="h-8 w-8 text-emerald-400" />}
          title="Real Code Engine"
          description="Write actual Python or Java. No drag-and-drop blocks, just pure coding."
        />
        <FeatureCard 
          icon={<Trophy className="h-8 w-8 text-amber-400" />}
          title="XP & Loot"
          description="Earn experience, level up your profile, and unlock new worlds."
        />
        <FeatureCard 
          icon={<Code2 className="h-8 w-8 text-purple-400" />}
          title="Epic Challenges"
          description="From basic loops to advanced algorithms, master concepts organically."
        />
      </div>
    </div>
  );
};

const FeatureCard = ({ icon, title, description }: { icon: React.ReactNode, title: string, description: string }) => (
  <motion.div 
    whileHover={{ y: -5 }}
    className="bg-slate-900/50 backdrop-blur-md border border-slate-800 rounded-xl p-6 text-center hover:border-slate-600 transition-colors"
  >
    <div className="flex justify-center mb-4">{icon}</div>
    <h3 className="text-xl font-bold text-slate-200 mb-2">{title}</h3>
    <p className="text-slate-400">{description}</p>
  </motion.div>
);
