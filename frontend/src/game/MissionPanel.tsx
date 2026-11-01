import React, { useState } from 'react';
import { Lightbulb, Target, Sparkles, BookOpen, Key, Check, Code2, Bot } from 'lucide-react';
import { Hint } from '../types/level';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { motion, AnimatePresence } from 'framer-motion';

interface MissionPanelProps {
  title: string;
  missionText: string;
  concept: string;
  hints: Hint[];
  solutionCode?: string;
  onApplySolution?: (code: string) => void;
  onAskAiHint?: () => void;
  onExplainSolutionAi?: () => void;
  onExplainConcept?: () => void;
}

export const MissionPanel: React.FC<MissionPanelProps> = ({
  title,
  missionText,
  concept,
  hints = [],
  solutionCode,
  onApplySolution,
  onAskAiHint,
  onExplainSolutionAi,
  onExplainConcept,
}) => {
  const [activeHintIndex, setActiveHintIndex] = useState(-1);
  const [showSolution, setShowSolution] = useState(false);
  const [copied, setCopied] = useState(false);

  const revealHint = () => {
    if (activeHintIndex < hints.length - 1) {
      setActiveHintIndex(prev => prev + 1);
    }
  };

  const handleApply = () => {
    if (solutionCode && onApplySolution) {
      onApplySolution(solutionCode);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  return (
    <Card className="flex flex-col h-full overflow-hidden border-slate-800 bg-slate-900/90 shadow-xl">
      <div className="bg-slate-950 p-3 px-4 border-b border-slate-800 flex items-center justify-between">
        <div className="flex items-center space-x-2">
          <Target className="h-5 w-5 text-cyan-400 shrink-0" />
          <h2 className="text-sm font-extrabold text-slate-100 uppercase tracking-wider truncate">{title}</h2>
        </div>
        <span className="text-[10px] font-extrabold uppercase px-2 py-0.5 rounded bg-cyan-950 text-cyan-400 border border-cyan-500/30">
          Mission Brief
        </span>
      </div>
      
      <div className="p-4 flex-1 overflow-y-auto space-y-4 text-xs md:text-sm">
        {/* Mission Text Story Description */}
        <div className="bg-slate-950/60 p-3 rounded-xl border border-slate-800/80">
          <h3 className="text-[11px] font-extrabold text-cyan-400 uppercase tracking-wider mb-1 flex items-center">
            <BookOpen className="h-3.5 w-3.5 mr-1" /> Question Story & Goal
          </h3>
          <p className="text-slate-200 leading-relaxed font-medium">
            {missionText}
          </p>
        </div>

        {/* Concept Pill */}
        <div>
          <div className="flex items-center justify-between mb-1">
            <h3 className="text-[11px] font-extrabold text-slate-400 uppercase tracking-wider flex items-center">
              <Sparkles className="h-3.5 w-3.5 mr-1 text-amber-400" /> Programming Concept
            </h3>
            {onExplainConcept && (
              <button
                onClick={onExplainConcept}
                className="flex items-center space-x-1 text-[11px] font-extrabold text-cyan-400 hover:text-cyan-300 bg-cyan-950/60 hover:bg-cyan-900/60 border border-cyan-500/30 px-2 py-0.5 rounded-lg transition"
              >
                <Sparkles className="h-3 w-3" />
                <span>Explain Concept</span>
              </button>
            )}
          </div>
          <div className="bg-gradient-to-r from-cyan-950/50 to-slate-900 border border-cyan-500/30 rounded-lg p-2.5">
            <p className="text-cyan-200 font-bold text-xs">{concept}</p>
          </div>
        </div>

        {/* Progressive Hints & AI Hints */}
        <div className="pt-2 border-t border-slate-800">
          <div className="flex items-center justify-between mb-2">
            <h3 className="text-[11px] font-extrabold text-slate-400 uppercase tracking-wider flex items-center">
              <Lightbulb className="h-3.5 w-3.5 mr-1 text-amber-400" /> Tactical Hints
            </h3>
            {onAskAiHint && (
              <button
                onClick={onAskAiHint}
                className="flex items-center space-x-1 text-[11px] font-extrabold text-purple-400 hover:text-purple-300 bg-purple-950/50 hover:bg-purple-900/50 border border-purple-500/30 px-2 py-0.5 rounded-lg transition"
              >
                <Bot className="h-3 w-3" />
                <span>Ask AI Hint</span>
              </button>
            )}
          </div>
          
          <div className="space-y-2">
            <AnimatePresence>
              {hints.slice(0, activeHintIndex + 1).map((hint, index) => (
                <motion.div
                  key={`hint-${index}`}
                  initial={{ opacity: 0, height: 0 }}
                  animate={{ opacity: 1, height: 'auto' }}
                  className="bg-amber-950/40 border border-amber-500/30 rounded-lg p-2.5 flex space-x-2"
                >
                  <Lightbulb className="h-4 w-4 text-amber-400 shrink-0 mt-0.5" />
                  <p className="text-amber-100 text-xs font-semibold">{hint.text}</p>
                </motion.div>
              ))}
            </AnimatePresence>

            {activeHintIndex < hints.length - 1 && (
              <Button 
                variant="ghost" 
                size="sm" 
                className="w-full text-slate-400 hover:text-amber-400 hover:bg-amber-500/10 border border-dashed border-slate-700 hover:border-amber-500/30 text-xs font-bold"
                onClick={revealHint}
              >
                <Lightbulb className="h-4 w-4 mr-1.5 text-amber-400" />
                Reveal Hint {hints[activeHintIndex + 1]?.cost ? `(${hints[activeHintIndex + 1].cost} Coins)` : ''}
              </Button>
            )}
          </div>
        </div>

        {/* View Solution Section */}
        {solutionCode && (
          <div className="pt-2 border-t border-slate-800 space-y-2">
            {!showSolution ? (
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                <Button
                  variant="ghost"
                  size="sm"
                  className="w-full text-cyan-400 hover:text-cyan-300 hover:bg-cyan-950/50 border border-cyan-500/40 text-xs font-extrabold uppercase tracking-wider"
                  onClick={() => setShowSolution(true)}
                >
                  <Key className="h-4 w-4 mr-1.5 text-cyan-400" />
                  View Solution Code
                </Button>

                {onExplainSolutionAi && (
                  <Button
                    variant="ghost"
                    size="sm"
                    className="w-full text-purple-400 hover:text-purple-300 hover:bg-purple-950/50 border border-purple-500/40 text-xs font-extrabold uppercase tracking-wider"
                    onClick={onExplainSolutionAi}
                  >
                    <Sparkles className="h-4 w-4 mr-1.5 text-purple-400" />
                    AI Walkthrough
                  </Button>
                )}
              </div>
            ) : (
              <motion.div
                initial={{ opacity: 0, scale: 0.98 }}
                animate={{ opacity: 1, scale: 1 }}
                className="bg-slate-950 border border-cyan-500/50 rounded-xl p-3 space-y-2"
              >
                <div className="flex items-center justify-between border-b border-slate-800 pb-2">
                  <span className="text-[11px] font-black text-cyan-400 uppercase tracking-wider flex items-center">
                    <Code2 className="h-4 w-4 mr-1" /> Solution Code
                  </span>
                  
                  <div className="flex items-center space-x-1">
                    {onExplainSolutionAi && (
                      <Button
                        variant="ghost"
                        size="sm"
                        className="px-2 py-1 text-[11px] font-bold text-purple-300 hover:bg-purple-950/50 border border-purple-500/30"
                        onClick={onExplainSolutionAi}
                      >
                        <Sparkles className="h-3 w-3 mr-1 text-purple-400" /> AI Explain
                      </Button>
                    )}

                    {onApplySolution && (
                      <Button
                        variant="primary"
                        size="sm"
                        className="px-2.5 py-1 text-[11px] font-bold bg-cyan-500 hover:bg-cyan-400 text-slate-950"
                        onClick={handleApply}
                      >
                        {copied ? <Check className="h-3.5 w-3.5 mr-1" /> : <Key className="h-3.5 w-3.5 mr-1" />}
                        {copied ? 'Applied!' : 'Apply to Editor'}
                      </Button>
                    )}
                  </div>
                </div>

                <pre className="text-emerald-400 font-mono text-xs p-2.5 bg-slate-900 rounded-lg overflow-x-auto border border-slate-800 whitespace-pre">
                  {solutionCode}
                </pre>
              </motion.div>
            )}
          </div>
        )}
      </div>
    </Card>
  );
};
