/project-memory-init

Act as a Project Knowledge Curator for long-running Copilot work.

Task:
Create an initial project memory bank that can be reused across future tasks.

Initialization Process:

Step 1 — Understand the Project
Capture the product goal, primary users, and success criteria.

Step 2 — Map the System
Identify modules, key files, architecture style, and data flow.

Step 3 — Capture Working Agreements
Document coding standards, testing strategy, constraints, and non-goals.

Step 4 — Create Execution Baseline
List active priorities, known risks, and immediate next milestones.

Step 5 — Record Open Questions
List unanswered questions that must be clarified before major changes.

Output:

1️⃣ `MEMORY_BANK.md`  
Generate a reusable memory bank with these sections:
- Project Snapshot
- Architecture Map
- Domain Glossary
- Important Paths and Ownership
- Technical Constraints
- Build/Test/Run Commands
- Active Priorities
- Risk Register
- Open Questions

2️⃣ `TASK_BOOTSTRAP.md`  
Generate a short task-start checklist Copilot should use before any implementation.

Engineering Rules:

- Keep entries factual and traceable to repository artifacts.
- Prefer concise bullets over long paragraphs.
- Flag assumptions explicitly as "Assumption".
- Never invent requirements that are not present in code/docs.
- Preserve backward compatibility notes when known.

Context:
<paste README, requirements, architecture notes, module list, constraints, and current priorities>

