# Roadmap

## 1.7.0 (current)

Stabilization release focused on:

- WMI4Java 1.7.0 and jPowerShell 3.2.0.
- Robust parsing of `ps` and WMI output.
- Correct Windows CPU time and owner/CPU cleanup.
- PID validation and clear `IllegalArgumentException` behavior.
- Process command timeouts, stream cleanup and configurable charset.
- Java 8, Central Portal publishing and Windows CI.

## 1.7.x (patch releases)

- Fix regressions reported against 1.7.0.
- Improve non-ASCII process name handling (#21).
- Add regression tests for Windows-specific reports (#19, #31).

## 1.8.0 (next milestone)

- **Parent/child process information** (#35).
  Add `ppid` to `ProcessInfo`, populate it from `ps` on Unix and from
  `ParentProcessId` on Windows, and add tests. PR #30 is a partial starting
  point but only adds the model field.
- **Process-tree termination on Windows** (#29).
  Add an option to pass `taskkill /T` and expose it through a safe API.
- **User data in fast mode** (#26).
  Evaluate a cheaper way to retrieve the process owner without the current
  full WMI cost.
- **Consistent PID API** (#20).
  Add integer-based helpers without breaking `ProcessInfo.getPid()`, or
  document the current contract explicitly.
- **Remote WMI credentials** (#11) and **WinRM clarification** (#16).
  Add explicit credential support where WMI/DCOM allows it, and document that
  WinRM/WSMan is out of scope for the underlying WMI engines.
- **Automatic encoding handling** (#21).
  Improve detection of `ps`/WMI output encoding beyond the current
  `jprocesses.charset` override.
- **More Windows integration coverage** in CI.

## Backlog

- Additional process metadata (session, threads, handles).
- Optional SLF4J/logging abstraction.
- Reproducible builds and signed release metadata.
