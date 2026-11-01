import React from 'react';
import { Play, RotateCcw, Terminal } from 'lucide-react';
import { Button } from '../components/ui/Button';

interface EditorToolbarProps {
  language: string;
  isRunning: boolean;
  onRun: () => void;
  onReset: () => void;
  onToggleConsole?: () => void;
  isConsoleOpen?: boolean;
  hasError?: boolean;
}

export const EditorToolbar: React.FC<EditorToolbarProps> = ({
  language,
  isRunning,
  onRun,
  onReset,
  onToggleConsole,
  isConsoleOpen = true,
  hasError = false,
}) => {
  return (
    <div className="flex flex-wrap items-center justify-between bg-slate-900 border-b border-slate-800 p-2 px-3 md:px-4 gap-2">
      {/* Left items: Language badge & Console button */}
      <div className="flex items-center space-x-2">
        <div className="flex items-center space-x-2 bg-slate-800/90 px-3 py-1 rounded-lg border border-slate-700/80">
          <span className="w-2 h-2 rounded-full bg-cyan-400 animate-pulse"></span>
          <span className="text-xs font-black text-slate-200 uppercase tracking-widest">
            {language}
          </span>
        </div>

        {onToggleConsole && (
          <button
            onClick={onToggleConsole}
            title={isConsoleOpen ? 'Minimize Console' : 'Show Console'}
            className={`flex items-center space-x-1.5 px-2.5 py-1 rounded-lg text-xs font-bold transition border ${
              isConsoleOpen
                ? 'bg-slate-800 text-cyan-300 border-cyan-500/30'
                : 'text-slate-400 hover:text-slate-200 border-transparent hover:bg-slate-800/50'
            }`}
          >
            <Terminal className="h-3.5 w-3.5" />
            <span className="hidden sm:inline">Console</span>
            {hasError && (
              <span className="w-2 h-2 rounded-full bg-rose-500 animate-pulse" />
            )}
          </button>
        )}
      </div>
      
      {/* Right controls: Reset, Run Code */}
      <div className="flex items-center space-x-2">
        <Button 
          variant="ghost" 
          size="sm" 
          onClick={onReset}
          disabled={isRunning}
          className="text-slate-400 hover:text-slate-100 text-xs py-1"
        >
          <RotateCcw className="h-3.5 w-3.5 mr-1" /> Reset
        </Button>
        <Button 
          variant="primary" 
          size="sm" 
          onClick={onRun}
          isLoading={isRunning}
          className="w-28 md:w-32 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-black text-xs py-1.5 shadow-[0_0_15px_rgba(6,182,212,0.3)]"
        >
          {!isRunning && <Play className="h-3.5 w-3.5 mr-1" />} 
          Run Code
        </Button>
      </div>
    </div>
  );
};
