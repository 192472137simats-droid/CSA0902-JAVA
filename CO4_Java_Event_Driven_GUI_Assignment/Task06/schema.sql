-- Database Schema for Campus Event Registration System
CREATE TABLE IF NOT EXISTS event_registrations (
    registration_id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id VARCHAR(20) NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    event_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    registration_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_student_event UNIQUE (student_id, event_name)
);

-- Index to optimize event filtering queries
CREATE INDEX IF NOT EXISTS idx_event_name ON event_registrations(event_name);
