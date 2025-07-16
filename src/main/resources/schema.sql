CREATE TABLE IF NOT EXISTS settings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    focus_minutes INTEGER NOT NULL,
    break_minutes INTEGER NOT NULL
);

INSERT INTO settings (focus_minutes, break_minutes) VALUES (25, 5);

CREATE TABLE IF NOT EXISTS sessions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    session_date TEXT NOT NULL,
    focus_minutes INTEGER NOT NULL
);
