package org.example.studytracker;

/** A registered account. The password is never stored, only a salted hash. */
public class User {

    private final String username;
    private final String fullName;
    private final String salt;
    private final String passwordHash;

    public User(String username, String fullName, String salt, String passwordHash) {
        this.username     = username;
        this.fullName     = fullName;
        this.salt         = salt;
        this.passwordHash = passwordHash;
    }

    public String getUsername()     { return username; }
    public String getFullName()     { return fullName; }
    public String getSalt()         { return salt; }
    public String getPasswordHash() { return passwordHash; }

    /** Up to two capital letters for the avatar, e.g. "Ama Mensah" -> "AM". */
    public String getInitials() {
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) return "?";
        if (parts.length == 1) {
            String w = parts[0];
            return (w.length() > 1 ? w.substring(0, 2) : w).toUpperCase();
        }
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
