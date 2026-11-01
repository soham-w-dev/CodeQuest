import React from 'react';
import { cn } from './Button';

interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  glow?: boolean;
}

export const Card = React.forwardRef<HTMLDivElement, CardProps>(
  ({ className, glow, ...props }, ref) => {
    return (
      <div
        ref={ref}
        className={cn(
          'rounded-xl border border-slate-700 bg-slate-800/50 backdrop-blur-md',
          glow && 'hover:border-cyan-500/50 hover:shadow-[0_0_15px_rgba(56,189,248,0.2)] transition-all duration-300',
          className
        )}
        {...props}
      />
    );
  }
);
Card.displayName = 'Card';
