package org.example.studytracker;

/** Thrown when sign-up or sign-in fails. The message is safe to show to the user. */
public class AuthException extends Exception {
    public AuthException(String message) {
        super(message);
    }
}
