import React, { useEffect, useRef } from 'react';
import Editor, { useMonaco } from '@monaco-editor/react';

interface CodeEditorProps {
  code: string;
  language: string;
  onChange: (value: string | undefined) => void;
  highlightedLine?: number;
  readOnly?: boolean;
}

export const CodeEditor: React.FC<CodeEditorProps> = ({
  code,
  language,
  onChange,
  highlightedLine,
  readOnly = false,
}) => {
  const monaco = useMonaco();
  const editorRef = useRef<any>(null);
  const decorationsRef = useRef<string[]>([]);

  useEffect(() => {
    if (monaco) {
      monaco.editor.defineTheme('codequest-dark', {
        base: 'vs-dark',
        inherit: true,
        rules: [
          { token: 'comment', foreground: '64748b', fontStyle: 'italic' },
          { token: 'keyword', foreground: '38bdf8' },
          { token: 'string', foreground: '10b981' },
          { token: 'number', foreground: 'f59e0b' },
          { token: 'identifier', foreground: 'f1f5f9' },
        ],
        colors: {
          'editor.background': '#090d16',
          'editor.lineHighlightBackground': '#1e293b',
          'editorLineNumber.foreground': '#475569',
          'editorLineNumber.activeForeground': '#38bdf8',
          'editor.selectionBackground': '#38bdf840',
        }
      });
      monaco.editor.setTheme('codequest-dark');
    }
  }, [monaco]);

  useEffect(() => {
    if (editorRef.current && monaco) {
      const editor = editorRef.current;
      
      if (highlightedLine !== undefined) {
        decorationsRef.current = editor.deltaDecorations(
          decorationsRef.current,
          [
            {
              range: new monaco.Range(highlightedLine, 1, highlightedLine, 1),
              options: {
                isWholeLine: true,
                className: 'bg-cyan-500/20 border-l-4 border-cyan-400',
              },
            },
          ]
        );
        editor.revealLineInCenter(highlightedLine);
      } else {
        decorationsRef.current = editor.deltaDecorations(decorationsRef.current, []);
      }
    }
  }, [highlightedLine, monaco]);

  const handleEditorDidMount = (editor: any) => {
    editorRef.current = editor;
  };

  return (
    <div className="h-full w-full min-h-[380px] md:min-h-[460px] rounded-xl overflow-hidden border border-slate-700 shadow-2xl bg-slate-950 relative flex flex-col">
      <Editor
        height="100%"
        width="100%"
        language={language}
        value={code}
        onChange={onChange}
        theme="codequest-dark"
        onMount={handleEditorDidMount}
        loading={
          <div className="w-full h-full min-h-[380px] bg-slate-950 p-4 flex flex-col">
            <div className="text-xs font-bold text-slate-400 mb-2 uppercase">Code Editor</div>
            <textarea
              className="w-full flex-1 p-3 bg-slate-900 border border-slate-700 rounded-lg text-slate-100 font-mono text-sm resize-none focus:outline-none focus:border-cyan-400"
              value={code}
              onChange={(e) => onChange(e.target.value)}
              disabled={readOnly}
            />
          </div>
        }
        options={{
          minimap: { enabled: false },
          fontSize: 15,
          fontFamily: "'Fira Code', 'Cascadia Code', Consolas, monospace",
          fontLigatures: true,
          lineHeight: 24,
          padding: { top: 16, bottom: 16 },
          readOnly,
          scrollBeyondLastLine: false,
          smoothScrolling: true,
          cursorBlinking: 'smooth',
          cursorWidth: 3,
          renderLineHighlight: 'all',
          scrollbar: {
            verticalScrollbarSize: 8,
            horizontalScrollbarSize: 8,
          }
        }}
      />
    </div>
  );
};
