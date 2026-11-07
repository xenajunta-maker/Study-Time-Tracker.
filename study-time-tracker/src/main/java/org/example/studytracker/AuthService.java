package org.example.studytracker;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/** Sign-up and sign-in rules. Contains no JavaFX code. */
public class AuthService {

    private static final Pattern USERNAME = Pattern.compile("[A-Za-z0-9_]{3,20}");
    private static final int MIN_PASSWORD = 8;

    private final UserStore store;

    public AuthService(UserStore store) {
        this.store = store;
    }

    /** Accounts are kept in ~/.studytrack/users.txt */
    public static AuthService createDefault() {
        Path file = Paths.get(System.getProperty("user.home"), ".studytrack", "users.txt");
        return new AuthService(new UserStore(file));
    }

    public User register(String fullName, String username, String password, String confirm)
            throws AuthException {

        String name  = cleanName(fullName);
        String uname = username == null ? "" : username.trim();
        String pass  = password == null ? "" : password;

        if (name.isEmpty())
            throw new AuthException("Full name is required.");
        if (!USERNAME.matcher(uname).matches())
            throw new AuthException("Username must be 3-20 characters: letters, numbers or underscore.");
        if (pass.length() < MIN_PASSWORD)
            throw new AuthException("Password must be at least " + MIN_PASSWORD + " characters.");
        if (!pass.matches(".*[A-Za-z].*") || !pass.matches(".*[0-9].*"))
            throw new AuthException("Password must contain at least one letter and one number.");
        if (!pass.equals(confirm))
            throw new AuthException("Passwords do not match.");

        try {
            if (store.findByUsername(uname) != null)
                throw new AuthException("That username is already taken.");

            String salt = PasswordUtil.newSalt();
            User user = new User(uname, name, salt, PasswordUtil.hash(pass, salt));
            store.add(user);
            return user;
        } catch (IOException e) {
            throw new AuthException("Could not save your account: " + e.getMessage());
        }
    }

    public User login(String username, String password) throws AuthException {
        String uname = username == null ? "" : username.trim();
        String pass  = password == null ? "" : password;

        if (uname.isEmpty() || pass.isEmpty())
            throw new AuthException("Enter your username and password.");

        try {
            User user = store.findByUsername(uname);
            if (user == null || !PasswordUtil.matches(pass, user.getSalt(), user.getPasswordHash()))
                throw new AuthException("Invalid username or password.");
            return user;
        } catch (IOException e) {
            throw new AuthException("Could not read the accounts file: " + e.getMessage());
        }
    }

    /** Collapses whitespace and strips tabs/newlines so the name fits on one file line. */
    private static String cleanName(String raw) {
        return raw == null ? "" : raw.replaceAll("\\s+", " ").trim();
    }
}
