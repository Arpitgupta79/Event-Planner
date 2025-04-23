Create database eventplanner;
use eventplanner;
CREATE TABLE event (
    id INTEGER PRIMARY KEY auto_increment,
    title TEXT NOT NULL,
    event_date DATE NOT NULL,
    event_time TEXT NOT NULL,
    location TEXT,
    category TEXT,
    reminder_days INTEGER DEFAULT 0
);