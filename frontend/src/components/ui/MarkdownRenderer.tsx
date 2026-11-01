import React, { useState } from 'react';
import { Copy, Check, Lightbulb, AlertTriangle, Target, Bookmark, Sparkles } from 'lucide-react';

interface MarkdownRendererProps {
  content?: string;
  className?: string;
}

interface TableData {
  headers: string[];
  alignments: ('left' | 'center' | 'right')[];
  rows: string[][];
}

export const MarkdownRenderer: React.FC<MarkdownRendererProps> = ({ content = '', className = '' }) => {
  if (!content) return null;

  const blocks = parseMarkdownBlocks(content);

  return (
    <div className={`space-y-3.5 text-slate-200 text-xs md:text-sm font-sans leading-relaxed ${className}`}>
      {blocks.map((block, index) => (
        <React.Fragment key={index}>{renderBlock(block, index)}</React.Fragment>
      ))}
    </div>
  );
};

// --- Block Parsers & Types ---

type BlockType =
  | { type: 'header'; level: number; text: string }
  | { type: 'code'; language: string; code: string }
  | { type: 'table'; data: TableData }
  | { type: 'callout'; variant: 'tip' | 'warning' | 'strategy' | 'info'; text: string }
  | { type: 'ordered-list'; items: string[] }
  | { type: 'unordered-list'; items: string[] }
  | { type: 'divider' }
  | { type: 'paragraph'; text: string };

function parseMarkdownBlocks(raw: string): BlockType[] {
  const lines = raw.split(/\r?\n/);
  const blocks: BlockType[] = [];
  let i = 0;

  while (i < lines.length) {
    const line = lines[i];
    const trimmed = line.trim();

    // Empty lines
    if (!trimmed) {
      i++;
      continue;
    }

    // Horizontal Rule
    if (/^(\-{3,}|\*{3,}|_{3,})$/.test(trimmed)) {
      blocks.push({ type: 'divider' });
      i++;
      continue;
    }

    // Fenced Code Block
    if (trimmed.startsWith('```')) {
      const language = trimmed.slice(3).trim().toLowerCase() || 'code';
      const codeLines: string[] = [];
      i++;
      while (i < lines.length && !lines[i].trim().startsWith('```')) {
        codeLines.push(lines[i]);
        i++;
      }
      if (i < lines.length) i++; // consume closing ```
      blocks.push({ type: 'code', language, code: codeLines.join('\n') });
      continue;
    }

    // Markdown Table
    if (trimmed.startsWith('|') && trimmed.endsWith('|')) {
      const tableLines: string[] = [];
      while (i < lines.length && lines[i].trim().startsWith('|') && lines[i].trim().endsWith('|')) {
        tableLines.push(lines[i].trim());
        i++;
      }
      const tableData = parseTable(tableLines);
      if (tableData) {
        blocks.push({ type: 'table', data: tableData });
        continue;
      }
    }

    // Headers (#, ##, ###, ####)
    const headerMatch = trimmed.match(/^(#{1,4})\s+(.+)$/);
    if (headerMatch) {
      blocks.push({
        type: 'header',
        level: headerMatch[1].length,
        text: headerMatch[2].trim(),
      });
      i++;
      continue;
    }

    // Callout / Blockquote
    if (
      trimmed.startsWith('>') ||
      trimmed.startsWith('💡') ||
      trimmed.startsWith('⚠️') ||
      trimmed.startsWith('🎯') ||
      trimmed.startsWith('📌') ||
      trimmed.startsWith('🏆')
    ) {
      let calloutText = trimmed.replace(/^>\s*/, '');
      let variant: 'tip' | 'warning' | 'strategy' | 'info' = 'info';
      if (calloutText.includes('💡') || calloutText.toLowerCase().includes('hint') || calloutText.toLowerCase().includes('tip')) {
        variant = 'tip';
      } else if (calloutText.includes('⚠️') || calloutText.toLowerCase().includes('warning') || calloutText.toLowerCase().includes('pitfall')) {
        variant = 'warning';
      } else if (calloutText.includes('🎯') || calloutText.toLowerCase().includes('strategy') || calloutText.toLowerCase().includes('objective')) {
        variant = 'strategy';
      }
      i++;
      // Consume consecutive lines in blockquote
      while (i < lines.length && lines[i].trim().startsWith('>')) {
        calloutText += ' ' + lines[i].trim().replace(/^>\s*/, '');
        i++;
      }
      blocks.push({ type: 'callout', variant, text: calloutText });
      continue;
    }

    // Ordered List
    const olMatch = trimmed.match(/^(\d+)\.\s+(.+)$/);
    if (olMatch) {
      const items: string[] = [];
      while (i < lines.length) {
        const itemMatch = lines[i].trim().match(/^(\d+)\.\s+(.+)$/);
        if (itemMatch) {
          items.push(itemMatch[2]);
          i++;
        } else if (lines[i].trim().startsWith('    ') || lines[i].trim().startsWith('\t')) {
          // Indented continuation
          if (items.length > 0) {
            items[items.length - 1] += ' ' + lines[i].trim();
          }
          i++;
        } else {
          break;
        }
      }
      blocks.push({ type: 'ordered-list', items });
      continue;
    }

    // Unordered List
    const ulMatch = trimmed.match(/^[-*•]\s+(.+)$/);
    if (ulMatch) {
      const items: string[] = [];
      while (i < lines.length) {
        const itemMatch = lines[i].trim().match(/^[-*•]\s+(.+)$/);
        if (itemMatch) {
          items.push(itemMatch[1]);
          i++;
        } else if (lines[i].trim().startsWith('  ') || lines[i].trim().startsWith('\t')) {
          if (items.length > 0) {
            items[items.length - 1] += ' ' + lines[i].trim();
          }
          i++;
        } else {
          break;
        }
      }
      blocks.push({ type: 'unordered-list', items });
      continue;
    }

    // Default Paragraph
    blocks.push({ type: 'paragraph', text: trimmed });
    i++;
  }

  return blocks;
}

function parseTable(lines: string[]): TableData | null {
  if (lines.length < 2) return null;

  const parseRow = (line: string): string[] => {
    return line
      .slice(1, -1)
      .split('|')
      .map(cell => cell.trim());
  };

  const headers = parseRow(lines[0]);
  const sepLine = lines[1];
  if (!sepLine.includes('-')) return null;

  const rawAlignments = parseRow(sepLine);
  const alignments: ('left' | 'center' | 'right')[] = rawAlignments.map(col => {
    if (col.startsWith(':') && col.endsWith(':')) return 'center';
    if (col.endsWith(':')) return 'right';
    return 'left';
  });

  const rows: string[][] = [];
  for (let idx = 2; idx < lines.length; idx++) {
    rows.push(parseRow(lines[idx]));
  }

  return { headers, alignments, rows };
}

// --- Block Renderers ---

function renderBlock(block: BlockType, key: number): React.ReactNode {
  switch (block.type) {
    case 'header':
      return renderHeader(block.level, block.text);

    case 'code':
      return <CodeCard key={key} language={block.language} code={block.code} />;

    case 'table':
      return <TableCard key={key} data={block.data} />;

    case 'callout':
      return <CalloutCard key={key} variant={block.variant} text={block.text} />;

    case 'ordered-list':
      return (
        <ol className="space-y-2.5 my-2">
          {block.items.map((item, idx) => (
            <li key={idx} className="flex items-start">
              <span className="shrink-0 w-5 h-5 rounded-full bg-purple-900/60 text-purple-300 border border-purple-500/30 font-bold text-[10px] flex items-center justify-center mr-2.5 mt-0.5 shadow-sm">
                {idx + 1}
              </span>
              <div className="flex-1 text-slate-200 leading-relaxed">
                {renderInlineFormatting(item)}
              </div>
            </li>
          ))}
        </ol>
      );

    case 'unordered-list':
      return (
        <ul className="space-y-2 my-2">
          {block.items.map((item, idx) => (
            <li key={idx} className="flex items-start">
              <span className="shrink-0 w-1.5 h-1.5 rounded-full bg-cyan-400 mt-2 mr-2.5 shadow-[0_0_8px_rgba(34,211,238,0.7)]" />
              <div className="flex-1 text-slate-200 leading-relaxed">
                {renderInlineFormatting(item)}
              </div>
            </li>
          ))}
        </ul>
      );

    case 'divider':
      return <div className="h-px bg-gradient-to-r from-transparent via-purple-500/30 to-transparent my-4" />;

    case 'paragraph':
      return (
        <p className="text-slate-200 leading-relaxed my-1.5">
          {renderInlineFormatting(block.text)}
        </p>
      );
  }
}

function renderHeader(level: number, text: string) {
  if (level === 1) {
    return (
      <h2 className="text-base md:text-lg font-black text-transparent bg-clip-text bg-gradient-to-r from-cyan-400 via-purple-300 to-pink-300 mt-4 mb-2 pb-1 border-b border-purple-500/20 flex items-center gap-2">
        <Sparkles className="h-4 w-4 text-cyan-400 shrink-0" />
        <span>{renderInlineFormatting(text)}</span>
      </h2>
    );
  }
  if (level === 2) {
    return (
      <h3 className="text-sm md:text-base font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-purple-300 via-pink-300 to-cyan-300 mt-4 mb-2 pb-1 border-b border-purple-500/20 flex items-center gap-2">
        <span>{renderInlineFormatting(text)}</span>
      </h3>
    );
  }
  if (level === 3) {
    return (
      <h4 className="text-xs md:text-sm font-bold text-cyan-300 mt-3 mb-1.5 flex items-center gap-1.5">
        <span>{renderInlineFormatting(text)}</span>
      </h4>
    );
  }
  return (
    <h5 className="text-xs font-bold text-purple-300 uppercase tracking-wider mt-2.5 mb-1">
      {renderInlineFormatting(text)}
    </h5>
  );
}

// --- Specialized Interactive Cards ---

const CodeCard: React.FC<{ language: string; code: string }> = ({ language, code }) => {
  const [copied, setCopied] = useState(false);

  const handleCopy = () => {
    navigator.clipboard.writeText(code);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="my-2.5 rounded-xl border border-slate-800 bg-slate-950 overflow-hidden shadow-lg">
      <div className="flex items-center justify-between px-3 py-1.5 bg-slate-900 border-b border-slate-800 text-[11px] font-bold text-slate-400 uppercase tracking-wider">
        <span className="text-cyan-400 font-mono text-[10px] bg-slate-800 px-2 py-0.5 rounded">
          {language}
        </span>
        <button
          onClick={handleCopy}
          className="flex items-center space-x-1 text-slate-400 hover:text-cyan-300 transition text-[11px]"
        >
          {copied ? <Check className="h-3 w-3 text-emerald-400 mr-1" /> : <Copy className="h-3 w-3 mr-1" />}
          <span>{copied ? 'Copied' : 'Copy Code'}</span>
        </button>
      </div>
      <pre className="p-3 text-emerald-400 font-mono text-xs leading-relaxed overflow-x-auto whitespace-pre">
        {code}
      </pre>
    </div>
  );
};

const TableCard: React.FC<{ data: TableData }> = ({ data }) => {
  return (
    <div className="my-3 rounded-xl border border-purple-500/20 bg-slate-950/70 overflow-hidden shadow-md overflow-x-auto">
      <table className="w-full text-left border-collapse text-xs">
        <thead>
          <tr className="bg-purple-950/60 border-b border-purple-500/30 text-purple-200">
            {data.headers.map((h, i) => (
              <th
                key={i}
                className="px-3 py-2 font-bold uppercase tracking-wider text-[11px]"
                style={{ textAlign: data.alignments[i] || 'left' }}
              >
                {renderInlineFormatting(h)}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-800/60">
          {data.rows.map((row, rIdx) => (
            <tr
              key={rIdx}
              className={`${rIdx % 2 === 0 ? 'bg-slate-900/40' : 'bg-slate-900/20'} hover:bg-slate-850/60 transition`}
            >
              {row.map((cell, cIdx) => (
                <td
                  key={cIdx}
                  className="px-3 py-2.5 text-slate-200 leading-relaxed"
                  style={{ textAlign: data.alignments[cIdx] || 'left' }}
                >
                  {renderInlineFormatting(cell)}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

const CalloutCard: React.FC<{ variant: 'tip' | 'warning' | 'strategy' | 'info'; text: string }> = ({
  variant,
  text,
}) => {
  const styles = {
    tip: {
      bg: 'bg-amber-950/20',
      border: 'border-amber-500/30',
      text: 'text-amber-200',
      icon: <Lightbulb className="h-4 w-4 text-amber-400 shrink-0 mt-0.5" />,
    },
    warning: {
      bg: 'bg-rose-950/20',
      border: 'border-rose-500/30',
      text: 'text-rose-200',
      icon: <AlertTriangle className="h-4 w-4 text-rose-400 shrink-0 mt-0.5" />,
    },
    strategy: {
      bg: 'bg-cyan-950/20',
      border: 'border-cyan-500/30',
      text: 'text-cyan-200',
      icon: <Target className="h-4 w-4 text-cyan-400 shrink-0 mt-0.5" />,
    },
    info: {
      bg: 'bg-purple-950/20',
      border: 'border-purple-500/30',
      text: 'text-purple-200',
      icon: <Bookmark className="h-4 w-4 text-purple-400 shrink-0 mt-0.5" />,
    },
  }[variant];

  return (
    <div className={`my-2.5 p-3 rounded-xl border ${styles.border} ${styles.bg} ${styles.text} flex items-start space-x-2.5 text-xs leading-relaxed shadow-sm`}>
      {styles.icon}
      <div className="flex-1">{renderInlineFormatting(text)}</div>
    </div>
  );
};

// --- Inline Formatting Parser ---

function renderInlineFormatting(rawText: string): React.ReactNode {
  if (!rawText) return '';

  // Replace <br> or <br/> with line breaks
  const parts = rawText.split(/(<br\s*\/?>|\n)/gi);

  return parts.map((part, pIdx) => {
    if (part.toLowerCase().startsWith('<br') || part === '\n') {
      return <br key={`br-${pIdx}`} />;
    }

    // Tokenize inline markdown: **bold**, `code`, *italic*
    const tokens = [];
    const regex = /(\*\*.*?\*\*|`.*?`|\*.*?\*)/g;
    let lastIndex = 0;
    let match;

    while ((match = regex.exec(part)) !== null) {
      if (match.index > lastIndex) {
        tokens.push(part.substring(lastIndex, match.index));
      }

      const matchText = match[0];
      if (matchText.startsWith('**') && matchText.endsWith('**')) {
        tokens.push(
          <strong key={`b-${pIdx}-${match.index}`} className="font-bold text-white">
            {matchText.slice(2, -2)}
          </strong>
        );
      } else if (matchText.startsWith('`') && matchText.endsWith('`')) {
        tokens.push(
          <code
            key={`c-${pIdx}-${match.index}`}
            className="font-mono text-[11px] md:text-xs bg-slate-800/90 text-cyan-300 px-1.5 py-0.5 rounded border border-cyan-500/25 shadow-sm"
          >
            {matchText.slice(1, -1)}
          </code>
        );
      } else if (matchText.startsWith('*') && matchText.endsWith('*')) {
        tokens.push(
          <em key={`i-${pIdx}-${match.index}`} className="italic text-purple-200">
            {matchText.slice(1, -1)}
          </em>
        );
      }
      lastIndex = regex.lastIndex;
    }

    if (lastIndex < part.length) {
      tokens.push(part.substring(lastIndex));
    }

    return <React.Fragment key={pIdx}>{tokens}</React.Fragment>;
  });
}
