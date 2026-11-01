import React, { useState } from 'react';
import { Terminal, Sparkles, AlertTriangle, CheckCircle2, ChevronDown, ChevronUp, Trash2, Copy, Check, ArrowRight, Lightbulb, BookOpen, Maximize2, Minimize2, Code2 } from 'lucide-react';
import { Button } from '../components/ui/Button';
import { MarkdownRenderer } from '../components/ui/MarkdownRenderer';

interface TerminalConsoleProps {
  stdout?: string;
  errorMessage?: string;
  errorLine?: number;
  hint?: string;
  hasError: boolean;
  isExecuting: boolean;
  activeTab: 'terminal' | 'ai';
  onTabChange: (tab: 'terminal' | 'ai') => void;
  onGiveSolution?: () => void;
  onExplainConcept?: () => void;
  aiExplanation?: string;
  aiLoading: boolean;
  aiError?: string;
  aiSuggestedCode?: string;
  onApplyCode?: (code: string) => void;
  onClearConsole?: () => void;
  isCollapsed: boolean;
  onToggleCollapse: () => void;
}

export const TerminalConsole: React.FC<TerminalConsoleProps> = ({
  stdout,
  errorMessage,
  errorLine,
  hint,
  hasError,
  isExecuting,
  activeTab,
  onTabChange,
  onGiveSolution,
  onExplainConcept,
  aiExplanation,
  aiLoading,
  aiError,
  aiSuggestedCode,
  onApplyCode,
  onClearConsole,
  isCollapsed,
  onToggleCollapse,
}) => {
  const [copiedCode, setCopiedCode] = useState(false);
  const [isMaximized, setIsMaximized] = useState(false);

  const handleCopyCode = (text: string) => {
    navigator.clipboard.writeText(text);
    setCopiedCode(true);
    setTimeout(() => setCopiedCode(false), 2000);
  };

  return (
    <div className={`border-t border-slate-800 bg-slate-950 flex flex-col transition-all duration-300 ${
      isCollapsed ? 'h-10' : isMaximized ? 'h-[500px] md:h-[580px]' : 'h-72 md:h-80'
    }`}>
      {/* Console Top Header Bar */}
      <div className="h-10 bg-slate-900/90 border-b border-slate-800 px-3 flex items-center justify-between select-none">
        {/* Tabs */}
        <div className="flex items-center space-x-1">
          <button
            onClick={() => {
              if (isCollapsed) onToggleCollapse();
              onTabChange('terminal');
            }}
            className={`flex items-center space-x-1.5 px-3 py-1.5 rounded-lg text-xs font-bold transition ${
              activeTab === 'terminal' && !isCollapsed
                ? 'bg-slate-800 text-cyan-400 border border-slate-700'
                : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/50'
            }`}
          >
            <Terminal className="h-3.5 w-3.5" />
            <span>Terminal Output</span>
            {hasError && (
              <span className="w-2 h-2 rounded-full bg-rose-500 animate-pulse ml-1" />
            )}
          </button>

          <button
            onClick={() => {
              if (isCollapsed) onToggleCollapse();
              onTabChange('ai');
            }}
            className={`flex items-center space-x-1.5 px-3 py-1.5 rounded-lg text-xs font-bold transition ${
              activeTab === 'ai' && !isCollapsed
                ? 'bg-gradient-to-r from-purple-950/80 to-slate-800 text-purple-300 border border-purple-500/40 shadow-[0_0_15px_rgba(168,85,247,0.2)]'
                : 'text-slate-400 hover:text-purple-300 hover:bg-slate-800/50'
            }`}
          >
            <Sparkles className="h-3.5 w-3.5 text-purple-400" />
            <span>Solution & Concept</span>
            {aiLoading && (
              <span className="w-2 h-2 rounded-full bg-cyan-400 animate-ping ml-1" />
            )}
          </button>
        </div>

        {/* Right Controls */}
        <div className="flex items-center space-x-1.5">
          {activeTab === 'terminal' && onClearConsole && (
            <button
              onClick={onClearConsole}
              title="Clear Console Output"
              className="p-1.5 text-slate-400 hover:text-slate-200 hover:bg-slate-800 rounded transition"
            >
              <Trash2 className="h-3.5 w-3.5" />
            </button>
          )}

          {!isCollapsed && (
            <button
              onClick={() => setIsMaximized(!isMaximized)}
              title={isMaximized ? 'Restore Console Size' : 'Maximize Console Height'}
              className="p-1.5 text-slate-400 hover:text-cyan-300 hover:bg-slate-800 rounded transition"
            >
              {isMaximized ? <Minimize2 className="h-3.5 w-3.5" /> : <Maximize2 className="h-3.5 w-3.5" />}
            </button>
          )}

          <button
            onClick={onToggleCollapse}
            title={isCollapsed ? 'Expand Console' : 'Minimize Console'}
            className="p-1.5 text-slate-400 hover:text-slate-200 hover:bg-slate-800 rounded transition"
          >
            {isCollapsed ? <ChevronUp className="h-4 w-4" /> : <ChevronDown className="h-4 w-4" />}
          </button>
        </div>
      </div>

      {/* Console Content Body */}
      {!isCollapsed && (
        <div className={`flex-1 overflow-y-auto p-3 md:p-4 text-slate-200 ${activeTab === 'terminal' ? 'font-mono text-xs' : 'font-sans text-xs md:text-sm'}`}>
          {activeTab === 'terminal' ? (
            <div className="space-y-3">
              {isExecuting && (
                <div className="flex items-center space-x-2 text-cyan-400 animate-pulse">
                  <div className="w-3 h-3 rounded-full border-2 border-cyan-400 border-t-transparent animate-spin" />
                  <span>Executing program simulation...</span>
                </div>
              )}

              {/* Standard Output */}
              {stdout ? (
                <div className="bg-slate-900/80 p-3 rounded-lg border border-slate-800 text-slate-300 whitespace-pre-wrap leading-relaxed">
                  <div className="text-[10px] uppercase font-bold text-slate-500 mb-1 border-b border-slate-800 pb-1">
                    Program Output
                  </div>
                  {stdout}
                </div>
              ) : null}

              {/* Error Box - Automatically explains on what line the error is */}
              {hasError && errorMessage ? (
                <div className="bg-rose-950/40 border border-rose-500/40 rounded-xl p-3.5 space-y-2.5 shadow-lg">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-2 text-rose-400 font-bold text-xs uppercase tracking-wide">
                      <AlertTriangle className="h-4 w-4 shrink-0 text-rose-400" />
                      <span>
                        {errorLine ? `Error on Line ${errorLine}` : 'Execution Error Detected'}
                      </span>
                    </div>

                    {errorLine && (
                      <span className="px-2 py-0.5 rounded bg-rose-500/20 text-rose-300 border border-rose-500/30 text-[10px] font-black uppercase">
                        Line {errorLine}
                      </span>
                    )}
                  </div>

                  <div className="text-rose-200 whitespace-pre-wrap font-sans text-xs leading-relaxed bg-slate-950/80 p-3 rounded-lg border border-rose-900/50">
                    {errorMessage}
                  </div>
                </div>
              ) : null}

              {/* Tactical Hint Box - Automatically displayed after running when valid code doesn't reach goal */}
              {!hasError && hint ? (
                <div className="bg-amber-950/40 border border-amber-500/40 rounded-xl p-3.5 space-y-2 shadow-lg">
                  <div className="flex items-center space-x-2 text-amber-400 font-bold text-xs uppercase tracking-wide">
                    <Lightbulb className="h-4 w-4 shrink-0 text-amber-400" />
                    <span>Tactical Hint</span>
                  </div>

                  <div className="text-amber-100 whitespace-pre-wrap font-sans text-xs leading-relaxed bg-slate-950/80 p-3 rounded-lg border border-amber-900/50">
                    {hint}
                  </div>
                </div>
              ) : null}

              {/* Success Notification if no error and finished */}
              {!hasError && !isExecuting && !hint && stdout && (
                <div className="flex items-center space-x-2 text-emerald-400 bg-emerald-950/30 border border-emerald-500/20 px-3 py-2 rounded-lg">
                  <CheckCircle2 className="h-4 w-4" />
                  <span>Code executed successfully with 0 errors! Goal reached!</span>
                </div>
              )}

              {/* Default Empty State */}
              {!isExecuting && !stdout && !hasError && !hint && (
                <div className="text-slate-500 text-center py-6">
                  <Terminal className="h-6 w-6 mx-auto mb-2 text-slate-600" />
                  <p>Ready. Click "Run Code" to compile and view execution output.</p>
                </div>
              )}
            </div>
          ) : (
            /* Solution & Concept Tab */
            <div className="space-y-4">
              {/* Solution & Concept Action Buttons */}
              <div className="flex flex-wrap items-center gap-2 pb-2 border-b border-slate-800">
                {onGiveSolution && (
                  <button
                    onClick={onGiveSolution}
                    disabled={aiLoading}
                    className="flex items-center space-x-1.5 px-3 py-1.5 rounded-lg text-xs font-bold bg-cyan-950/50 hover:bg-cyan-900/50 text-cyan-300 border border-cyan-500/30 transition"
                  >
                    <Lightbulb className="h-3.5 w-3.5" />
                    <span>Give Solution</span>
                  </button>
                )}

                {onExplainConcept && (
                  <button
                    onClick={onExplainConcept}
                    disabled={aiLoading}
                    className="flex items-center space-x-1.5 px-3 py-1.5 rounded-lg text-xs font-bold bg-purple-950/50 hover:bg-purple-900/50 text-purple-300 border border-purple-500/30 transition"
                  >
                    <BookOpen className="h-3.5 w-3.5" />
                    <span>Explain Concept</span>
                  </button>
                )}
              </div>

              {aiLoading ? (
                <div className="text-center py-8 space-y-3">
                  <div className="w-8 h-8 border-3 border-purple-400 border-t-transparent rounded-full animate-spin mx-auto" />
                  <p className="text-purple-300 font-medium text-xs">
                    Loading explanation from backend AI Service...
                  </p>
                </div>
              ) : aiExplanation ? (
                <div className="space-y-4">
                  <div className="flex items-center justify-between border-b border-slate-800 pb-2">
                    <div className="flex items-center space-x-2 text-purple-300 font-extrabold text-xs uppercase tracking-wider">
                      <Sparkles className="h-4 w-4 text-purple-400" />
                      <span>AI Solution & Strategy Walkthrough</span>
                    </div>

                    <div className="flex items-center space-x-2">
                      {aiSuggestedCode && onApplyCode && (
                        <Button
                          variant="primary"
                          size="sm"
                          onClick={() => onApplyCode(aiSuggestedCode)}
                          className="bg-purple-500 hover:bg-purple-400 text-slate-950 text-xs font-bold py-1 px-3"
                        >
                          <ArrowRight className="h-3.5 w-3.5 mr-1" /> Apply Code to Editor
                        </Button>
                      )}
                    </div>
                  </div>

                  {/* AI Walkthrough Rendered Card */}
                  <div className="bg-slate-900/80 border border-purple-500/25 rounded-xl p-4 md:p-5 shadow-2xl backdrop-blur-sm">
                    <MarkdownRenderer content={aiExplanation} />
                  </div>

                  {/* Official Solution Code Box */}
                  {aiSuggestedCode && (
                    <div className="bg-slate-950 border border-slate-800 rounded-xl p-3.5 space-y-2.5 shadow-lg">
                      <div className="flex items-center justify-between border-b border-slate-800/80 pb-2">
                        <div className="flex items-center space-x-2 text-xs font-bold text-slate-300 uppercase tracking-wider">
                          <Code2 className="h-4 w-4 text-cyan-400" />
                          <span>Official Solution Code</span>
                        </div>
                        <div className="flex items-center space-x-2">
                          <button
                            onClick={() => handleCopyCode(aiSuggestedCode)}
                            className="text-slate-400 hover:text-cyan-300 flex items-center text-xs font-semibold px-2 py-1 bg-slate-900 hover:bg-slate-800 rounded border border-slate-800 transition"
                          >
                            {copiedCode ? <Check className="h-3.5 w-3.5 text-emerald-400 mr-1" /> : <Copy className="h-3.5 w-3.5 mr-1" />}
                            <span>{copiedCode ? 'Copied' : 'Copy'}</span>
                          </button>
                          {onApplyCode && (
                            <button
                              onClick={() => onApplyCode(aiSuggestedCode)}
                              className="text-purple-300 hover:text-purple-200 flex items-center text-xs font-bold px-2 py-1 bg-purple-950/60 hover:bg-purple-900/60 rounded border border-purple-500/30 transition"
                            >
                              <ArrowRight className="h-3.5 w-3.5 mr-1" />
                              <span>Apply</span>
                            </button>
                          )}
                        </div>
                      </div>
                      <pre className="text-emerald-400 font-mono text-xs p-3 bg-slate-900/90 rounded-lg overflow-x-auto border border-slate-800/80 leading-relaxed">
                        {aiSuggestedCode}
                      </pre>
                    </div>
                  )}
                </div>
              ) : aiError ? (
                <div className="bg-rose-950/40 border border-rose-500/40 rounded-xl p-4 space-y-3">
                  <p className="text-rose-300 text-xs">{aiError}</p>
                </div>
              ) : (
                <div className="text-center py-6 text-slate-400 space-y-3">
                  <Sparkles className="h-7 w-7 text-purple-400 mx-auto" />
                  <p className="text-xs font-semibold text-slate-300">
                    Solution & Concept Explanations
                  </p>
                  <p className="text-xs text-slate-500 max-w-md mx-auto">
                    Click "Give Solution" to view the complete solution walkthrough, or "Explain Concept" to explore the core computer science concepts used in this question.
                  </p>
                </div>
              )}
            </div>
          )}
        </div>
      )}
    </div>
  );
};
