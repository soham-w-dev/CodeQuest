import React from 'react';
import { Star } from 'lucide-react';
import { motion } from 'framer-motion';
import { cn } from './Button';

interface StarRatingProps {
  rating: number; // 0 to 3
  maxStars?: number;
  size?: 'sm' | 'md' | 'lg';
  animated?: boolean;
}

export const StarRating: React.FC<StarRatingProps> = ({ 
  rating, 
  maxStars = 3, 
  size = 'md',
  animated = false
}) => {
  const sizes = {
    sm: 'w-4 h-4',
    md: 'w-6 h-6',
    lg: 'w-10 h-10',
  };

  return (
    <div className="flex items-center space-x-1">
      {[...Array(maxStars)].map((_, i) => {
        const isFilled = i < rating;
        
        const starContent = (
          <Star 
            className={cn(
              sizes[size],
              isFilled 
                ? 'fill-amber-400 text-amber-400 drop-shadow-[0_0_8px_rgba(251,191,36,0.6)]' 
                : 'fill-slate-800 text-slate-700'
            )} 
          />
        );

        if (animated && isFilled) {
          return (
            <motion.div
              key={i}
              initial={{ scale: 0, rotate: -180 }}
              animate={{ scale: 1, rotate: 0 }}
              transition={{ 
                type: "spring", 
                stiffness: 260, 
                damping: 20, 
                delay: i * 0.2 
              }}
            >
              {starContent}
            </motion.div>
          );
        }

        return <div key={i}>{starContent}</div>;
      })}
    </div>
  );
};
