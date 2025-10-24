# Work time scanner

there's a CLI version of this project [here](https://github.com/iprimavera/Work-time-scanner-CLI)

The irrelevant data that the program store is in a folder called `.workTimeScanner`
in the home directory of the user. The important data is automatically stored as `CSV` in the same
folder as the program.

The scanner resets when it's executed in a different day from the last time executed.
The scanner must be reset if the day has changed since its last reset (before first use of the day).

