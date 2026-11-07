package org.example.studytracker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Tracker {

    /** A calendar day has 24 hours, so no date may hold more study time than this. */
    public static final long MAX_MINUTES_PER_DAY = 24 * 60;

    private final List<StudySession> sessions = new ArrayList<>();

    public void addSession(StudySession session)    { sessions.add(session); }
    public void removeSession(StudySession session) { sessions.remove(session); }
    public List<StudySession> getAllSessions()      { return new ArrayList<>(sessions); }

    public List<StudySession> getSessionsForDate(LocalDate date) {
        return sessions.stream()
                .filter(s -> s.getDate().equals(date))
                .collect(Collectors.toList());
    }

    public List<StudySession> getSessionsForWeek(LocalDate anyDay) {
        LocalDate monday = anyDay.minusDays(anyDay.getDayOfWeek().getValue() - 1);
        LocalDate sunday = monday.plusDays(6);
        return sessions.stream()
                .filter(s -> !s.getDate().isBefore(monday) && !s.getDate().isAfter(sunday))
                .collect(Collectors.toList());
    }

    public long calculateTotalMinutes(List<StudySession> list) {
        return list.stream().mapToLong(StudySession::getDurationMinutes).sum();
    }

    public long getDailyTotal(LocalDate date)  { return calculateTotalMinutes(getSessionsForDate(date)); }
    public long getWeeklyTotal(LocalDate date) { return calculateTotalMinutes(getSessionsForWeek(date)); }

    /** Minutes still available on the given date before it reaches 24 hours. */
    public long getRemainingMinutes(LocalDate date) {
        return Math.max(0, MAX_MINUTES_PER_DAY - getDailyTotal(date));
    }

    /** True if a session of this length can still be logged on the given date. */
    public boolean fitsInDay(LocalDate date, long minutes) {
        return minutes <= getRemainingMinutes(date);
    }

    /** Converts minutes to text such as "4h 20m". */
    public static String formatMinutes(long totalMinutes) {
        long h = totalMinutes / 60;
        long m = totalMinutes % 60;
        if (h == 0) return m + "m";
        if (m == 0) return h + "h";
        return h + "h " + m + "m";
    }
}
