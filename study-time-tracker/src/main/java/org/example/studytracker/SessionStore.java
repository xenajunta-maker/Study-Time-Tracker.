package org.example.studytracker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Saves each user's study sessions to their own file so they are still there
 * after logging out or closing the app. One line per session:
 * subject, date (yyyy-MM-dd), start (HH:mm), end (HH:mm), separated by tabs.
 */
public class SessionStore {

    private final Path dir;

    public SessionStore(Path dir) {
        this.dir = dir;
    }

    /** Sessions are kept in ~/.studytrack/sessions/ */
    public static SessionStore createDefault() {
        return new SessionStore(Paths.get(System.getProperty("user.home"), ".studytrack", "sessions"));
    }

    private Path fileFor(User user) {
        // usernames are limited to letters, numbers and underscore, so this is a safe file name
        return dir.resolve(user.getUsername().toLowerCase(Locale.ROOT) + ".tsv");
    }

    public List<StudySession> load(User user) throws IOException {
        List<StudySession> sessions = new ArrayList<>();
        Path file = fileFor(user);
        if (!Files.exists(file)) return sessions;

        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.isBlank()) continue;
            String[] p = line.split("\t", -1);
            if (p.length != 4) continue;
            try {
                LocalTime start = LocalTime.parse(p[2]);
                LocalTime end   = LocalTime.parse(p[3]);
                if (!end.isAfter(start)) continue;
                sessions.add(new StudySession(p[0], LocalDate.parse(p[1]), start, end));
            } catch (DateTimeParseException ignored) {
                // skip damaged lines
            }
        }
        return sessions;
    }

    public void save(User user, List<StudySession> sessions) throws IOException {
        Files.createDirectories(dir);
        List<String> lines = new ArrayList<>();
        for (StudySession s : sessions) {
            String subject = s.getSubject().replaceAll("\\s+", " ").trim();
            lines.add(String.join("\t", subject, s.getDate().toString(),
                    s.getStartTime().toString(), s.getEndTime().toString()));
        }
        Files.write(fileFor(user), lines, StandardCharsets.UTF_8);
    }
}
