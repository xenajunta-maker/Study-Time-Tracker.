package org.example.studytracker;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class AuthController {

    @FXML private Label lblAuthTitle;
    @FXML private Label lblAuthSubtitle;

    @FXML private VBox loginForm;
    @FXML private TextField     loginUsername;
    @FXML private PasswordField loginPassword;
    @FXML private Label         lblLoginError;

    @FXML private VBox registerForm;
    @FXML private TextField     regFullName;
    @FXML private TextField     regUsername;
    @FXML private PasswordField regPassword;
    @FXML private PasswordField regConfirm;
    @FXML private Label         lblRegisterError;

    private final AuthService auth = AuthService.createDefault();

    // ── Switching between the two forms ───────────────────────
    @FXML
    private void showLogin() {
        lblAuthTitle.setText("Welcome back");
        lblAuthSubtitle.setText("Sign in to continue to your dashboard.");
        setVisible(loginForm, true);
        setVisible(registerForm, false);
        lblLoginError.setText("");
        loginUsername.requestFocus();
    }

    @FXML
    private void showRegister() {
        lblAuthTitle.setText("Create your account");
        lblAuthSubtitle.setText("You need an account to use StudyTrack.");
        setVisible(loginForm, false);
        setVisible(registerForm, true);
        lblRegisterError.setText("");
        regFullName.requestFocus();
    }

    private void setVisible(VBox box, boolean visible) {
        box.setVisible(visible);
        box.setManaged(visible);
    }

    // ── Actions ───────────────────────────────────────────────
    @FXML
    private void handleLogin() {
        lblLoginError.setText("");
        try {
            User user = auth.login(loginUsername.getText(), loginPassword.getText());
            loginPassword.clear();
            MainApp.showDashboard(user);
        } catch (AuthException e) {
            lblLoginError.setText(e.getMessage());
            loginPassword.clear();
        }
    }

    @FXML
    private void handleRegister() {
        lblRegisterError.setText("");
        try {
            User user = auth.register(regFullName.getText(), regUsername.getText(),
                    regPassword.getText(), regConfirm.getText());
            regPassword.clear();
            regConfirm.clear();
            MainApp.showDashboard(user);       // signed in straight after creating the account
        } catch (AuthException e) {
            lblRegisterError.setText(e.getMessage());
        }
    }
}
