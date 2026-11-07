package org.example.studytracker;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class StudySession {

    private final String subject;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final long durationMinutes;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public StudySession(String subject, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.subject = subject;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = Duration.between(startTime, endTime).toMinutes();
    }

    public String getSubject()       { return subject; }
    public LocalDate getDate()       { return date; }
    public LocalTime getStartTime()  { return startTime; }
    public LocalTime getEndTime()    { return endTime; }
    public long getDurationMinutes() { return durationMinutes; }

    /** Human-readable duration, e.g. "2h 30m". */
    public String getFormattedDuration() {
        long hours   = durationMinutes / 60;
        long minutes = durationMinutes % 60;
        if (hours == 0)   return minutes + "m";
        if (minutes == 0) return hours + "h";
        return hours + "h " + minutes + "m";
    }

    public String getFormattedStart() { return startTime.format(TIME_FMT); }
    public String getFormattedEnd()   { return endTime.format(TIME_FMT); }
    public String getFormattedDate()  { return date.format(DATE_FMT); }

    @Override
    public String toString() {
        return String.format("[%s] %s  %s - %s  (%s)",
                getFormattedDate(), subject, getFormattedStart(), getFormattedEnd(),
                getFormattedDuration());
    }
}
