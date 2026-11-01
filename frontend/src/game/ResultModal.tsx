import React from 'react';
import { motion } from 'framer-motion';
import { Trophy, Zap, XCircle, Gem, Lightbulb } from 'lucide-react';
import { Modal } from '../components/ui/Modal';
import { Button } from '../components/ui/Button';
import { StarRating } from '../components/ui/StarRating';

interface ResultModalProps {
  isOpen: boolean;
  isSuccess: boolean;
  stars: number;
  xpGained: number;
  efficiency?: number;
  failReason?: string;
  hint?: string;
  onRetry: () => void;
  onNext: () => void;
  gemsCollected?: number;
  totalGems?: number;
}

export const ResultModal: React.FC<ResultModalProps> = ({
  isOpen,
  isSuccess,
  stars,
  xpGained,
  failReason,
  hint,
  onRetry,
  onNext,
  gemsCollected,
  totalGems,
}) => {
  return (
    <Modal isOpen={isOpen} onClose={() => {}}>
      <div className="text-center">
        {isSuccess ? (
          <>
            <motion.div
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              transition={{ type: "spring", bounce: 0.5 }}
              className="mx-auto w-20 h-20 bg-emerald-500/20 rounded-full flex items-center justify-center mb-4"
            >
              <Trophy className="h-10 w-10 text-emerald-400 drop-shadow-[0_0_10px_rgba(168,85,247,0.8)]" />
            </motion.div>
            
            <h2 className="text-2xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 to-cyan-400 mb-6 uppercase tracking-widest">
              Mission Accomplished
            </h2>

            <div className="flex justify-center mb-6">
              <StarRating rating={stars} maxStars={3} size="lg" animated />
            </div>

            <div className="grid grid-cols-2 gap-4 mb-8">
              <div className="bg-slate-800 rounded-lg p-4 border border-slate-700">
                <div className="text-slate-400 text-xs font-bold uppercase mb-1">XP Gained</div>
                <div className="flex justify-center items-center text-cyan-400 font-bold text-xl">
                  <Zap className="h-5 w-5 mr-1" /> +{xpGained}
                </div>
              </div>
              <div className="bg-slate-800 rounded-lg p-4 border border-slate-700">
                <div className="text-slate-400 text-xs font-bold uppercase mb-1">Gems</div>
                <div className="flex justify-center items-center text-emerald-400 font-bold text-xl">
                  <Gem className="h-5 w-5 mr-1" /> {gemsCollected || 0}/{totalGems || 0}
                </div>
              </div>
            </div>

            <div className="flex space-x-4">
              <Button variant="secondary" className="w-full" onClick={onRetry}>
                Replay
              </Button>
              <Button variant="primary" className="w-full" onClick={onNext}>
                Next Mission
              </Button>
            </div>
          </>
        ) : (
          <>
            <motion.div
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              className="mx-auto w-20 h-20 bg-red-500/20 rounded-full flex items-center justify-center mb-4"
            >
              <XCircle className="h-10 w-10 text-red-500 drop-shadow-[0_0_10px_rgba(239,68,68,0.8)]" />
            </motion.div>

            <h2 className="text-2xl font-bold text-red-400 mb-2 uppercase tracking-widest">
              Mission Failed
            </h2>
            
            <div className="text-slate-300 text-sm whitespace-pre-wrap mb-4 p-4 bg-red-950/30 border border-red-500/20 rounded-lg text-left font-sans">
              {failReason || "Your code didn't solve the puzzle."}
            </div>

            {hint && (
              <div className="text-amber-200 text-xs whitespace-pre-wrap mb-6 p-3.5 bg-amber-950/40 border border-amber-500/30 rounded-lg text-left font-sans flex items-start space-x-2.5">
                <Lightbulb className="h-4 w-4 text-amber-400 shrink-0 mt-0.5" />
                <div>
                  <div className="font-bold uppercase tracking-wider text-[10px] text-amber-400 mb-0.5">
                    Tactical Guidance
                  </div>
                  <div className="leading-relaxed">{hint}</div>
                </div>
              </div>
            )}

            <div className="flex space-x-4">
              <Button variant="secondary" onClick={onNext}>
                Skip Level
              </Button>
              <Button variant="primary" className="w-full" onClick={onRetry}>
                Try Again
              </Button>
            </div>
          </>
        )}
      </div>
    </Modal>
  );
};
