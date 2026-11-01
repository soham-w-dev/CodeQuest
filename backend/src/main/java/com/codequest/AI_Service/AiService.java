package com.codequest.AI_Service;

public interface AiService {
    AiResponse debugError(AiRequest request);
    AiResponse giveSolution(AiRequest request);
    AiResponse explainConcept(AiRequest request);
    AiResponse getHint(AiRequest request);
}
