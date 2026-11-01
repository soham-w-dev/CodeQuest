import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { levelService } from '../services/levelService';
import { gameService } from '../services/gameService';
import { aiService } from '../services/aiService';
import { LevelDetail, LevelConfig } from '../types/level';
import { GameState, CellType, Direction, ExecutionResult, GameAction } from '../types/game';
import { GameBoard } from '../game/GameBoard';
import { MissionPanel } from '../game/MissionPanel';
import { ResultModal } from '../game/ResultModal';
import { TerminalConsole } from '../game/TerminalConsole';
import { CodeEditor } from '../editor/CodeEditor';
import { EditorToolbar } from '../editor/EditorToolbar';
import { useProgressStore } from '../stores/progressStore';
import { useAuthStore } from '../stores/authStore';
import { GameEngine } from '../game/GameEngine';
import { soundFx } from '../utils/audio';

const mapDirection = (dir: string): Direction => {
  const map: Record<string, Direction> = { 'N': 'UP', 'S': 'DOWN', 'E': 'RIGHT', 'W': 'LEFT', 'UP': 'UP', 'DOWN': 'DOWN', 'LEFT': 'LEFT', 'RIGHT': 'RIGHT' };
  return map[dir] || 'RIGHT';
};

const buildInitialState = (config: LevelConfig): GameState => {
  const grid: CellType[][] = Array(config.rows).fill(null).map(() => Array(config.cols).fill('empty' as CellType));
  
  if (config.walls) {
    config.walls.forEach(w => {
      if (w.row >= 0 && w.row < config.rows && w.col >= 0 && w.col < config.cols) {
        grid[w.row][w.col] = 'wall';
      }
    });
  }
  
  if (config.gems) {
    config.gems.forEach(g => {
      if (g.row >= 0 && g.row < config.rows && g.col >= 0 && g.col < config.cols) {
        grid[g.row][g.col] = 'gem';
      }
    });
  }
  
  if (config.goal) {
    grid[config.goal.row][config.goal.col] = 'goal';
  }
  
  return {
    grid,
    playerPosition: { row: config.playerStart.row, col: config.playerStart.col },
    playerDirection: mapDirection(config.playerDirection),
    collectedGems: 0,
    totalGems: config.gems ? config.gems.length : 0,
    isCompleted: false,
    isFailed: false,
    gridRows: config.rows,
    gridCols: config.cols
  };
};

export const GameLevel: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { selectedLanguage } = useProgressStore();
  const { user } = useAuthStore();
  
  const [level, setLevel] = useState<LevelDetail | null>(null);
  const [initialState, setInitialState] = useState<GameState | null>(null);
  const [gameState, setGameState] = useState<GameState | null>(null);
  const [code, setCode] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showResult, setShowResult] = useState(false);
  const [isExecuting, setIsExecuting] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);
  const [activeLine, setActiveLine] = useState<number | undefined>(undefined);
  const [executionResult, setExecutionResult] = useState<ExecutionResult | null>(null);

  // Terminal & Console states
  const [stdout, setStdout] = useState<string>('');
  const [consoleCollapsed, setConsoleCollapsed] = useState<boolean>(false);
  const [activeConsoleTab, setActiveConsoleTab] = useState<'terminal' | 'ai'>('terminal');

  // AI Assistant states
  const [aiExplanation, setAiExplanation] = useState<string>('');
  const [aiLoading, setAiLoading] = useState<boolean>(false);
  const [aiError, setAiError] = useState<string>('');
  const [aiSuggestedCode, setAiSuggestedCode] = useState<string>('');

  const language = selectedLanguage || user?.selectedLanguage || 'PYTHON';

  useEffect(() => {
    if (!id) return;

    const fetchLevel = async () => {
      try {
        const data = await levelService.getLevelById(id);
        const parsedConfig: LevelConfig = typeof data.levelConfig === 'string' ? JSON.parse(data.levelConfig) : data.levelConfig;
        const parsedHints = typeof data.hints === 'string' ? JSON.parse(data.hints) : (data.hints || []);
        const levelData = { ...data, levelConfig: parsedConfig, hints: parsedHints };
        setLevel(levelData);
        const state = buildInitialState(parsedConfig);
        setInitialState(state);
        setGameState(state);
        setCode(data.starterCode || '# Write your code here\n');
      } catch (err) {
        console.error('Failed to fetch level', err);
        setError('Failed to load level. Please try again.');
      } finally {
        setLoading(false);
      }
    };

    fetchLevel();
  }, [id]);

  // Animation function - plays actions one by one
  const playActions = useCallback(async (actions: GameAction[], startState: GameState) => {
    setIsAnimating(true);
    let currentState = JSON.parse(JSON.stringify(startState)) as GameState;
    
    for (let i = 0; i < actions.length; i++) {
      const action = actions[i];
      if (action.line) {
        setActiveLine(action.line);
      }
      
      if (action.type === 'move') {
        soundFx.playStep();
      } else if (action.type === 'collect') {
        soundFx.playGem();
      }

      const newState = GameEngine.applyAction(currentState, action);
      currentState = newState;
      setGameState({ ...newState });
      
      if (newState.isFailed || newState.isCompleted) {
        break;
      }
      
      await new Promise(resolve => setTimeout(resolve, 350));
    }
    
    setActiveLine(undefined);
    setIsAnimating(false);
    return currentState;
  }, []);

  const handleRun = async () => {
    if (!id || !initialState) return;
    soundFx.playClick();
    
    setIsExecuting(true);
    setShowResult(false);
    setExecutionResult(null);
    setActiveLine(undefined);
    setStdout(`> Compiling and running ${language} code...\n`);
    setConsoleCollapsed(false);
    setActiveConsoleTab('terminal');
    
    const freshState = initialState ? JSON.parse(JSON.stringify(initialState)) as GameState : null;
    if (freshState) {
      setGameState(freshState);
    }
    
    try {
      const result = await gameService.submitCode(Number(id), code, language);
      setExecutionResult(result);
      setIsExecuting(false);

      if (result.stdout) {
        setStdout(result.stdout);
      }

      if (result.errorLine) {
        setActiveLine(result.errorLine);
      }
      
      if (result.actions && result.actions.length > 0 && freshState) {
        await playActions(result.actions, freshState);
        
        setTimeout(() => {
          if (result.success) {
            soundFx.playVictory();
          } else {
            soundFx.playError();
          }
          setShowResult(true);
        }, 300);
      } else {
        if (!result.success) {
          soundFx.playError();
        }
        setShowResult(true);
      }
    } catch (err: any) {
      soundFx.playError();
      setIsExecuting(false);
      const failMsg = err.response?.data?.message || err.message || 'Something went wrong. Check your code syntax.';
      setExecutionResult({
        success: false,
        error: true,
        errorMessage: failMsg,
        goalReached: false,
        gemsCollected: 0,
        totalGems: 0,
        actions: [],
        stars: 0,
        xpAwarded: 0,
        coinsAwarded: 0,
        stdout: failMsg,
      });
      setStdout(failMsg);
      setShowResult(true);
    }
  };

  const handleReset = () => {
    soundFx.playClick();
    if (initialState) {
      setGameState(JSON.parse(JSON.stringify(initialState)));
    }
    setShowResult(false);
    setExecutionResult(null);
    setActiveLine(undefined);
    setStdout('');
    if (level) {
      setCode(level.starterCode || '# Write your code here\n');
    }
  };

  // AI Assistant Handlers

  const handleAskAiHint = async () => {
    setConsoleCollapsed(false);
    setActiveConsoleTab('ai');
    setAiLoading(true);
    setAiError('');
    setAiExplanation('');
    setAiSuggestedCode('');

    try {
      const resp = await aiService.getHint({
        levelId: Number(id),
        language,
        userCode: code,
      });

      if (resp.success && resp.explanation) {
        setAiExplanation(resp.explanation);
      } else {
        setAiError(resp.message || resp.error || 'Failed to get AI hint.');
      }
    } catch (err: any) {
      setAiError(err.response?.data?.message || err.message || 'AI service unavailable.');
    } finally {
      setAiLoading(false);
    }
  };

  const handleExplainSolutionAi = async () => {
    setConsoleCollapsed(false);
    setActiveConsoleTab('ai');
    setAiLoading(true);
    setAiError('');
    setAiExplanation('');
    setAiSuggestedCode('');

    try {
      const resp = await aiService.giveSolution({
        levelId: Number(id),
        language,
        userCode: level?.solutionCode || code,
      });

      if (resp.success && resp.explanation) {
        setAiExplanation(resp.explanation);
        if (resp.suggestedCode) setAiSuggestedCode(resp.suggestedCode);
      } else {
        setAiError(resp.message || resp.error || 'Failed to get AI solution explanation.');
      }
    } catch (err: any) {
      setAiError(err.response?.data?.message || err.message || 'AI service unavailable.');
    } finally {
      setAiLoading(false);
    }
  };

  const handleExplainConcept = async () => {
    setConsoleCollapsed(false);
    setActiveConsoleTab('ai');
    setAiLoading(true);
    setAiError('');
    setAiExplanation('');
    setAiSuggestedCode('');

    try {
      const resp = await aiService.explainConcept({
        levelId: Number(id),
        language,
        userCode: code,
      });

      if (resp.success && resp.explanation) {
        setAiExplanation(resp.explanation);
      } else {
        setAiError(resp.message || resp.error || 'Failed to get concept explanation.');
      }
    } catch (err: any) {
      setAiError(err.response?.data?.message || err.message || 'AI service unavailable.');
    } finally {
      setAiLoading(false);
    }
  };

  const handleApplyCode = (newCode: string) => {
    setCode(newCode);
    soundFx.playClick();
  };

  if (loading) {
    return (
      <div className="flex-1 flex items-center justify-center bg-slate-950">
        <div className="text-center">
          <div className="w-12 h-12 border-4 border-cyan-400 border-t-transparent rounded-full animate-spin mx-auto mb-4" />
          <p className="text-slate-400 font-medium">Loading Adventure Level...</p>
        </div>
      </div>
    );
  }

  if (error || !level || !initialState || !gameState) {
    return (
      <div className="flex-1 flex items-center justify-center bg-slate-950">
        <div className="text-center">
          <p className="text-red-400 mb-4">{error || 'Could not load level'}</p>
          <button onClick={() => navigate(-1)} className="text-cyan-400 hover:text-cyan-300 underline font-semibold">Go Back</button>
        </div>
      </div>
    );
  }

  const isSuccess = executionResult?.success === true;
  const hasError = executionResult?.error === true || Boolean(executionResult?.errorMessage);

  return (
    <div className="flex-1 flex flex-col lg:flex-row min-h-[calc(100vh-4rem)] bg-slate-950 overflow-y-auto lg:overflow-hidden p-2 md:p-4 gap-4">
      {/* Left Panel: Game & Mission */}
      <div className="w-full lg:w-[45%] flex flex-col gap-4 border border-slate-800 rounded-2xl bg-slate-900/40 p-4 overflow-y-auto">
        <div className="flex-1 flex items-center justify-center bg-slate-900/60 rounded-xl p-4 min-h-[280px]">
          <GameBoard gameState={gameState} userAvatar={user?.avatar || 'robot'} />
        </div>
        <div className="min-h-[220px]">
          <MissionPanel 
            title={level.title}
            missionText={level.missionText}
            concept={level.concept}
            hints={level.hints || []}
            solutionCode={level.solutionCode}
            onApplySolution={(sol) => setCode(sol)}
            onAskAiHint={handleAskAiHint}
            onExplainSolutionAi={handleExplainSolutionAi}
            onExplainConcept={handleExplainConcept}
          />
        </div>
      </div>

      {/* Right Panel: Editor, Toolbar & In-Game Terminal Console */}
      <div className="w-full lg:w-[55%] flex flex-col border border-slate-800 rounded-2xl bg-slate-900/60 overflow-hidden min-h-[520px]">
        <EditorToolbar 
          language={language}
          isRunning={isExecuting || isAnimating}
          onRun={handleRun}
          onReset={handleReset}
          onToggleConsole={() => setConsoleCollapsed(!consoleCollapsed)}
          isConsoleOpen={!consoleCollapsed}
          hasError={hasError}
        />

        {/* Code Editor */}
        <div className="flex-1 p-3 flex flex-col min-h-[280px]">
          <CodeEditor 
            code={code}
            language={language.toLowerCase() === 'java' ? 'java' : 'python'}
            onChange={(val) => setCode(val || '')}
            highlightedLine={activeLine}
            readOnly={isExecuting || isAnimating}
          />
        </div>

        {/* Integrated Terminal & AI Console Output */}
        <TerminalConsole 
          stdout={stdout}
          errorMessage={executionResult?.errorMessage}
          errorLine={executionResult?.errorLine}
          hint={executionResult?.hint}
          hasError={hasError}
          isExecuting={isExecuting || isAnimating}
          activeTab={activeConsoleTab}
          onTabChange={setActiveConsoleTab}
          onGiveSolution={handleExplainSolutionAi}
          onExplainConcept={handleExplainConcept}
          aiExplanation={aiExplanation}
          aiLoading={aiLoading}
          aiError={aiError}
          aiSuggestedCode={aiSuggestedCode}
          onApplyCode={handleApplyCode}
          onClearConsole={() => setStdout('')}
          isCollapsed={consoleCollapsed}
          onToggleCollapse={() => setConsoleCollapsed(!consoleCollapsed)}
        />
      </div>

      {/* Result Modal */}
      <ResultModal 
        isOpen={showResult}
        isSuccess={isSuccess}
        stars={executionResult?.stars || 0}
        xpGained={executionResult?.xpAwarded || 0}
        failReason={executionResult?.errorMessage || gameState.failReason}
        hint={executionResult?.hint}
        onRetry={() => {
          handleReset();
        }}
        onNext={() => {
          navigate(`/world/${level.worldId}`);
        }}
        gemsCollected={executionResult?.gemsCollected || 0}
        totalGems={executionResult?.totalGems || 0}
      />
    </div>
  );
};
