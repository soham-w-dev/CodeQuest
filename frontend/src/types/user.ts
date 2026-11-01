export interface User {
  id: number;
  username: string;
  email: string;
  xp: number;
  level: number;
  coins: number;
  selectedLanguage: string | null;
  streak: number;
  avatar: string | null;
  completedLevels?: number;
  totalStars?: number;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}
