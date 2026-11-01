import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AppLayout } from './components/layout/AppLayout';
import { ProtectedRoute } from './components/layout/ProtectedRoute';
import { Landing } from './pages/Landing';
import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { Dashboard } from './pages/Dashboard';
import { LanguageSelect } from './pages/LanguageSelect';
import { WorldMap } from './pages/WorldMap';
import { GameLevel } from './pages/GameLevel';
import { Profile } from './pages/Profile';
import { Achievements } from './pages/Achievements';
import { Leaderboard } from './pages/Leaderboard';
import { DailyChallenge } from './pages/DailyChallenge';

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<AppLayout />}>
          <Route path="/" element={<Landing />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          
          <Route path="/dashboard" element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          } />
          
          <Route path="/select-language" element={
            <ProtectedRoute>
              <LanguageSelect />
            </ProtectedRoute>
          } />
          
          <Route path="/world/:id" element={
            <ProtectedRoute>
              <WorldMap />
            </ProtectedRoute>
          } />
          
          <Route path="/level/:id" element={
            <ProtectedRoute>
              <GameLevel />
            </ProtectedRoute>
          } />
          
          <Route path="/profile" element={
            <ProtectedRoute>
              <Profile />
            </ProtectedRoute>
          } />

          <Route path="/achievements" element={
            <ProtectedRoute>
              <Achievements />
            </ProtectedRoute>
          } />

          <Route path="/leaderboard" element={
            <ProtectedRoute>
              <Leaderboard />
            </ProtectedRoute>
          } />

          <Route path="/daily-challenge" element={
            <ProtectedRoute>
              <DailyChallenge />
            </ProtectedRoute>
          } />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};

export default App;
