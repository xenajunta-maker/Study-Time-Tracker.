package org.example.studytracker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves accounts to a tab-separated text file:
 * username, full name, salt, password hash (one account per line).
 */
public class UserStore {

    private final Path file;

    public UserStore(Path file) {
        this.file = file;
    }

    public synchronized List<User> loadAll() throws IOException {
        List<User> users = new ArrayList<>();
        if (!Files.exists(file)) return users;

        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.isBlank()) continue;
            String[] p = line.split("\t", -1);
            if (p.length != 4) continue;              // skip damaged lines
            users.add(new User(p[0], p[1], p[2], p[3]));
        }
        return users;
    }

    public synchronized User findByUsername(String username) throws IOException {
        for (User u : loadAll()) {
            if (u.getUsername().equalsIgnoreCase(username)) return u;
        }
        return null;
    }

    public synchronized void add(User user) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);

        String line = String.join("\t",
                user.getUsername(), user.getFullName(), user.getSalt(), user.getPasswordHash())
                + System.lineSeparator();
        Files.writeString(file, line, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
