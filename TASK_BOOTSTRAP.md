# TASK_BOOTSTRAP

Use this checklist before starting any implementation task.

## 1) Understand and Bound the Task
- Restate requested change in one sentence.
- Map impacted requirement sections in `Requirement.md`.
- Identify non-goals explicitly (what will not be changed).

## 2) Load Project Memory
- Read `MEMORY_BANK.md` first.
- Read relevant source docs: `Requirement.md`, `LLD.md`, `business_logic.md`, `README.md`.
- If there is a conflict, treat `Requirement.md` as canonical and log the conflict.

## 3) Plan Changes with Safety
- List files to edit and expected side effects.
- Check for constraint impact: offline-only, stealth behavior, foreground service reliability, no crashes.
- Define validation strategy before coding (unit tests + manual checks where needed).

## 4) Implement Incrementally
- Make smallest safe changes first.
- Keep parser/haptic/card logic deterministic and testable.
- Preserve backward compatibility for existing behavior unless requirement says otherwise.

## 5) Verify
- Run unit tests relevant to touched modules.
- At minimum, verify:
  - trigger parsing bounds and debounce behavior
  - card lookup bounds (`1..52`)
  - haptic pattern generation validity and duration constraints
- If command execution is unavailable, document unverified steps clearly.

## 6) Update Memory
- Append a short "Task History" entry to `MEMORY_BANK.md`:
  - task
  - changed files
  - validation done
  - follow-up risks/questions
- Add new assumptions under an explicit "Assumption" label.

## 7) Pre-Handoff Questions
- Did we fully meet the requested behavior and constraints?
- Any edge cases still unclear?
- Should we iterate on naming/structure/tests before finalizing?

## Suggested Task History Entry Template
```markdown
### Task: <short task name>
- Date: <YYYY-MM-DD>
- Scope: <what changed>
- Files: `<path1>`, `<path2>`
- Validation: <tests/checks run>
- Assumption: <if any>
- Open question: <if any>
```

