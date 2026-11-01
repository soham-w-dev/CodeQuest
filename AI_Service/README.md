# AI_Service (Backend)

The AI Service for **CodeQuest** is managed entirely within the backend under:
`backend/src/main/java/com/codequest/AI_Service/`

## Key Capabilities
1. **Debug Error (`/api/ai/debug-error` or `/api/ai/explain-error`)**:
   - Analyzes student code, execution error traces, and simulation logs.
   - Identifies the root cause in beginner-friendly terms.
   - Provides line-by-line pinpointing and actionable code corrections.

2. **Give Solution (`/api/ai/solution` or `/api/ai/explain-solution`)**:
   - Provides the complete solution code for the question.
   - Delivers a structured, step-by-step code walkthrough explaining why each movement, loop, variable, and conditional is required.

3. **Explain Concepts (`/api/ai/explain-concept`)**:
   - Explains the specific computer science concept used in that particular question (e.g., Sequencing, For Loops, Variables, While Loops, Functions with Parameters, Obstacle Navigation).
   - Provides real-world context, syntax examples in Python and Java, and common pitfalls to avoid.

4. **Tactical Hint (`/api/ai/hint`)**:
   - Delivers progressive, encouraging hints without giving away the full code.

## Configuration
AI is managed purely in the backend without requiring frontend user keys.
Configure in `backend/src/main/resources/application.yml` or set environment variable:
```bash
GROQ_API_KEY=gsk_your_groq_api_key_here
```
If an API key is not configured, the built-in intelligent fallback engine automatically serves offline solutions, error diagnostics, and concept explanations.
