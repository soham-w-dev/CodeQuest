import api from './api';

export interface AiRequest {
  levelId?: number;
  language?: string;
  userCode?: string;
  errorMessage?: string;
  stdout?: string;
  mode?: 'DEBUG_ERROR' | 'SOLUTION' | 'EXPLAIN_CONCEPT' | 'HINT';
}

export interface AiResponse {
  success: boolean;
  message: string;
  explanation?: string;
  suggestedCode?: string;
  conceptTitle?: string;
  conceptSummary?: string;
  error?: string;
}

export const aiService = {
  debugError: async (req: AiRequest): Promise<AiResponse> => {
    const response = await api.post<AiResponse>('/ai/debug-error', req);
    return response.data;
  },

  giveSolution: async (req: AiRequest): Promise<AiResponse> => {
    const response = await api.post<AiResponse>('/ai/solution', req);
    return response.data;
  },

  explainConcept: async (req: AiRequest): Promise<AiResponse> => {
    const response = await api.post<AiResponse>('/ai/explain-concept', req);
    return response.data;
  },

  getHint: async (req: AiRequest): Promise<AiResponse> => {
    const response = await api.post<AiResponse>('/ai/hint', req);
    return response.data;
  },

  // Aliases for compatibility
  explainError: async (req: AiRequest): Promise<AiResponse> => {
    return aiService.debugError(req);
  },

  explainSolution: async (req: AiRequest): Promise<AiResponse> => {
    return aiService.giveSolution(req);
  }
};
