# StudyTrack - Study Time Tracker

A desktop application built with **Java** and **JavaFX** that helps students log and review their study sessions. Users create an account, sign in, and record sessions by subject and time. The app calculates durations automatically and shows daily and weekly totals.

## Features

- **Accounts:** create an account and sign in before using the app; each user sees only their own sessions
- **Add study sessions:** subject, date, start time and end time
- **Automatic duration:** shown live as you type the times
- **24-hour limit:** the total for any one date can never go above 24 hours; the app tells you how much time is left
- **Daily and weekly views** with total time, session count and average duration
- **Delete sessions** with one click
- **Saved data:** accounts and sessions are stored on disk, so they are still there after you log out or close the app
- **Input validation** for names, usernames, passwords, times and dates

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 17+ | Core language |
| JavaFX 21 | GUI framework |
| FXML | Screen layouts (open in Scene Builder) |
| JavaFX CSS | Styling |
| Maven | Build and dependencies |
| javax.crypto (PBKDF2) | Password hashing, no extra libraries |

## Project Structure

```
study-time-tracker/
├── pom.xml
├── README.md
├── docs/
│   └── StudyTracker_Documentation.docx
└── src/main/
    ├── java/
    │   ├── module-info.java
    │   └── org/example/studytracker/
    │       ├── Launcher.java              # Run this class
    │       ├── MainApp.java               # Starts the app, switches screens
    │       ├── AuthController.java        # Sign in / create account screen
    │       ├── DashboardController.java   # Dashboard logic
    │       ├── StudySession.java          # One study session
    │       ├── Tracker.java               # Session list, totals, 24-hour rule
    │       ├── User.java                  # An account
    │       ├── AuthService.java           # Sign-up and sign-in rules
    │       ├── AuthException.java         # Error with a user-friendly message
    │       ├── PasswordUtil.java          # Salted PBKDF2 hashing
    │       ├── UserStore.java             # Saves accounts to a file
    │       └── SessionStore.java          # Saves each user's sessions
    └── resources/org/example/studytracker/
        ├── auth.fxml                      # Sign in / create account layout
        ├── dashboard.fxml                 # Dashboard layout
        └── styles.css                     # Theme
```

## Getting Started

**Prerequisites:** JDK 17 or higher, IntelliJ IDEA, Maven (bundled with IntelliJ). JavaFX is downloaded automatically by Maven.

1. Clone the repository
   ```bash
   git clone https://github.com/YOUR_USERNAME/study-time-tracker.git
   ```
2. Open the folder in IntelliJ IDEA (`File > Open`) and let Maven finish importing.
3. Open `src/main/java/org/example/studytracker/Launcher.java`, right-click it and choose **Run 'Launcher.main()'**.

Run `Launcher`, not `MainApp`. Starting a JavaFX `Application` subclass directly on the module path can fail with module access errors.

## How to Use

1. On first launch, click **Create an account**, fill in your name, a username and a password, and submit. You are signed in straight away.
2. Enter a subject, start time and end time (for example `09:00` and `11:30`). The duration appears automatically.
3. Click **Save Session**. It appears in the table and the totals update.
4. Switch between **Daily** and **Weekly** to change the table.
5. Click **Log out** when you are done. Next time, sign in with your username and password.

## Rules

| Area | Rule |
|---|---|
| Username | 3 to 20 characters: letters, numbers or underscore; not case-sensitive; must be unique |
| Password | At least 8 characters with at least one letter and one number |
| Session times | `HH:mm` (both `9:00` and `09:00` work); end must be after start |
| Daily limit | The sessions on one date add up to at most 24 hours. If a new session would go over, it is refused with a message showing how much time is left |

## Where Data Is Stored

Data is saved in a `.studytrack` folder in your user directory (for example `C:\Users\<you>\.studytrack\` on Windows):

- `users.txt` holds accounts. Passwords are never stored; only a random salt and a PBKDF2-HMAC-SHA256 hash.
- `sessions\<username>.tsv` holds each user's sessions.

This is a local, single-computer app. The session files are not encrypted, so it is not designed for sensitive data.

## Limitations

- Accounts and sessions live on one computer only; there is no sync or password reset
- The 24-hour limit checks the daily total but does not stop two sessions on the same day from overlapping in time
- Session History and Reports in the sidebar currently show the weekly view

## Possible Improvements

- Block overlapping sessions on the same day
- Edit existing sessions
- Weekly bar chart and CSV export
- Password change and account deletion
- Store data in SQLite instead of text files

## Author

Xena, Computer Engineering student, University of Mines and Technology (UMaT), Tarkwa, Ghana.
