package org.example.studytracker;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    // ── Sidebar ───────────────────────────────────────────────
    @FXML private Label lblTodayTotal;
    @FXML private Label lblTodaySessions;
    @FXML private Label lblWeekTotal;
    @FXML private Label lblWeekSessions;
    @FXML private Label lblAvatar;
    @FXML private Label lblUserName;

    // ── Top bar ───────────────────────────────────────────────
    @FXML private Label  lblPageTitle;
    @FXML private Button tabDay;
    @FXML private Button tabWeek;

    // ── Form ──────────────────────────────────────────────────
    @FXML private TextField fieldSubject;
    @FXML private TextField fieldStart;
    @FXML private TextField fieldEnd;
    @FXML private TextField fieldDate;
    @FXML private Label     lblDurationPreview;
    @FXML private Label     lblFormError;

    // ── Table ─────────────────────────────────────────────────
    @FXML private TableView<StudySession>          sessionTable;
    @FXML private TableColumn<StudySession,String> colSubject;
    @FXML private TableColumn<StudySession,String> colDate;
    @FXML private TableColumn<StudySession,String> colStart;
    @FXML private TableColumn<StudySession,String> colEnd;
    @FXML private TableColumn<StudySession,String> colDuration;
    @FXML private TableColumn<StudySession,String> colActions;
    @FXML private Label lblTableTitle;

    // ── Summary chips ─────────────────────────────────────────
    @FXML private Label lblChipTotal;
    @FXML private Label lblChipCount;
    @FXML private Label lblChipAvg;

    // ── State ─────────────────────────────────────────────────
    private final Tracker tracker = new Tracker();
    private final SessionStore sessionStore = SessionStore.createDefault();
    private final ObservableList<StudySession> tableData = FXCollections.observableArrayList();
    private User currentUser;
    private boolean showingDaily = true;

    private static final DateTimeFormatter DATE_IN = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        sessionTable.setItems(tableData);
        fieldDate.setText(LocalDate.now().format(DATE_IN));

        fieldStart.textProperty().addListener((o, ov, nv) -> updateDurationPreview());
        fieldEnd.textProperty().addListener((o, ov, nv) -> updateDurationPreview());

        refreshView();
    }

    /** Called by MainApp right after sign-in. Loads this user's saved sessions. */
    public void setUser(User user) {
        this.currentUser = user;
        lblAvatar.setText(user.getInitials());
        lblUserName.setText(user.getFullName());

        try {
            for (StudySession s : sessionStore.load(user)) {
                tracker.addSession(s);
            }
        } catch (IOException e) {
            showError("Could not load your saved sessions: " + e.getMessage());
        }
        refreshView();
    }

    // ── Time parsing: accepts "9:00" and "09:00" ──────────────
    private LocalTime parseTime(String text) {
        return LocalTime.parse(text.trim(), DateTimeFormatter.ofPattern("H:mm"));
    }

    // ── Table setup ───────────────────────────────────────────
    private void setupTableColumns() {
        colSubject .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubject()));
        colDate    .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFormattedDate()));
        colStart   .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFormattedStart()));
        colEnd     .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFormattedEnd()));
        colDuration.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFormattedDuration()));

        colActions.setCellFactory(tc -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");
            {
                deleteBtn.getStyleClass().add("btn-delete");
                deleteBtn.setOnAction(e -> {
                    StudySession s = getTableView().getItems().get(getIndex());
                    tracker.removeSession(s);
                    persist();
                    refreshView();
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
    }

    // ── Save session ──────────────────────────────────────────
    @FXML
    private void saveSession() {
        lblFormError.setText("");
        String subject  = fieldSubject.getText().trim();
        String startTxt = fieldStart.getText().trim();
        String endTxt   = fieldEnd.getText().trim();
        String dateTxt  = fieldDate.getText().trim();

        if (subject.isEmpty())  { showError("Subject is required.");    return; }
        if (startTxt.isEmpty()) { showError("Start time is required."); return; }
        if (endTxt.isEmpty())   { showError("End time is required.");   return; }

        LocalTime start, end;
        LocalDate date;
        try {
            start = parseTime(startTxt);
            end   = parseTime(endTxt);
        } catch (DateTimeParseException ex) {
            showError("Times must be HH:mm format e.g. 09:00."); return;
        }
        try {
            date = dateTxt.isEmpty() ? LocalDate.now() : LocalDate.parse(dateTxt, DATE_IN);
        } catch (DateTimeParseException ex) {
            showError("Date must be yyyy-MM-dd format."); return;
        }
        if (!end.isAfter(start)) {
            showError("End time must be after start time."); return;
        }

        // A day only has 24 hours: the total for this date can never go past that.
        long minutes   = Duration.between(start, end).toMinutes();
        long remaining = tracker.getRemainingMinutes(date);
        if (minutes > remaining) {
            String day = date.format(DATE_IN);
            if (remaining == 0) {
                showError("You already have 24h logged on " + day + ". A day can't have more than 24 hours.");
            } else {
                showError("That would be more than 24 hours on " + day + ". You can add at most "
                        + Tracker.formatMinutes(remaining) + " more.");
            }
            return;
        }

        tracker.addSession(new StudySession(subject, date, start, end));
        persist();
        clearForm();
        refreshView();
    }

    @FXML
    private void clearForm() {
        fieldSubject.clear();
        fieldStart.clear();
        fieldEnd.clear();
        fieldDate.setText(LocalDate.now().format(DATE_IN));
        lblDurationPreview.setText("\u2014");
        lblFormError.setText("");
    }

    private void updateDurationPreview() {
        try {
            LocalTime s = parseTime(fieldStart.getText());
            LocalTime e = parseTime(fieldEnd.getText());
            if (e.isAfter(s)) {
                lblDurationPreview.setText(Tracker.formatMinutes(Duration.between(s, e).toMinutes()));
            } else {
                lblDurationPreview.setText("\u2014");
            }
        } catch (DateTimeParseException ignored) {
            lblDurationPreview.setText("\u2014");
        }
    }

    private void showError(String msg) { lblFormError.setText(msg); }

    /** Writes this user's sessions to disk. */
    private void persist() {
        if (currentUser == null) return;
        try {
            sessionStore.save(currentUser, tracker.getAllSessions());
        } catch (IOException e) {
            showError("Could not save your sessions: " + e.getMessage());
        }
    }

    // ── Navigation ────────────────────────────────────────────
    @FXML private void showDashboard() { }
    @FXML private void showHistory()   { filterWeekly(); }
    @FXML private void showReports()   { filterWeekly(); }

    @FXML
    private void logout() {
        MainApp.showAuth();
    }

    @FXML
    private void filterDaily() {
        showingDaily = true;
        tabDay.getStyleClass().add("tab-active");
        tabWeek.getStyleClass().remove("tab-active");
        lblTableTitle.setText("Today's Sessions");
        refreshView();
    }

    @FXML
    private void filterWeekly() {
        showingDaily = false;
        tabWeek.getStyleClass().add("tab-active");
        tabDay.getStyleClass().remove("tab-active");
        lblTableTitle.setText("This Week's Sessions");
        refreshView();
    }

    @FXML private void openAddSessionDialog() { fieldSubject.requestFocus(); }

    // ── Refresh all UI ────────────────────────────────────────
    private void refreshView() {
        LocalDate today = LocalDate.now();

        List<StudySession> todaySessions = tracker.getSessionsForDate(today);
        lblTodayTotal.setText(Tracker.formatMinutes(tracker.getDailyTotal(today)));
        lblTodaySessions.setText(todaySessions.size() + " session" + (todaySessions.size() == 1 ? "" : "s"));

        List<StudySession> weekSessions = tracker.getSessionsForWeek(today);
        lblWeekTotal.setText(Tracker.formatMinutes(tracker.getWeeklyTotal(today)));
        lblWeekSessions.setText(weekSessions.size() + " session" + (weekSessions.size() == 1 ? "" : "s"));

        List<StudySession> displayed = showingDaily ? todaySessions : weekSessions;
        tableData.setAll(displayed);

        long totalMins = tracker.calculateTotalMinutes(displayed);
        int  count     = displayed.size();
        long avgMins   = count > 0 ? totalMins / count : 0;

        lblChipTotal.setText(Tracker.formatMinutes(totalMins));
        lblChipCount.setText(String.valueOf(count));
        lblChipAvg.setText(Tracker.formatMinutes(avgMins));
    }
}
