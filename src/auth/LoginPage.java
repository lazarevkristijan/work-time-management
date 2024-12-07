package auth;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import consts.Constants;
import state.AppState;
import utils.GeneralUtils;
import components.WTButton;
import components.WTLabel;
import components.WTOptionPane;
import components.WTPanel;
import components.WTPasswordField;
import components.WTSpacer;
import components.WTTextField;
import components.WTWindow;
import validate.InputValidator;
import validate.PasswordHashing;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;

public class LoginPage implements ActionListener {

    AppState state = AppState.getInstance();

    WTWindow loginWindow = new WTWindow(
            "Work Time Login", Constants.DEF_WINDOW_W, Constants.DEF_WINDOW_H, true, true);

    WTPanel loginPanel = new WTPanel("box");

    WTLabel loginHeading = new WTLabel("Login to continue", true, "lg", "b", 'c');

    WTLabel usernameLabel = new WTLabel("Username:", false, "sm", "b", 'c');
    WTTextField usernameField = new WTTextField(Constants.DEF_INPUT_WIDTH, Constants.DEF_INPUT_HEIGHT);
    WTLabel passwordLabel = new WTLabel("Password:", false, "sm", "b", 'c');
    WTPasswordField passwordField = new WTPasswordField(Constants.DEF_INPUT_WIDTH, Constants.DEF_INPUT_HEIGHT);

    WTSpacer spacer = new WTSpacer(new Dimension(Constants.SMALL_Y_SPACER_WIDTH, Constants.SMALL_Y_SPACER_HEIGHT));

    WTButton loginBtn = new WTButton("Login", Constants.actionBtnBgColor);
    WTButton toRegisterButton = new WTButton("Register a new account");

    WTLabel errorLabel = new WTLabel("", false, "md", "r", 'c');

    KeyEventDispatcher enterDispatcher = GeneralUtils.addListenerForEnter(loginBtn);

    public LoginPage() {
        loginPanel.add(loginHeading);

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);

        loginPanel.add(spacer);

        loginPanel.add(loginBtn);
        loginBtn.addActionListener(this);
        loginPanel.add(toRegisterButton);
        toRegisterButton.addActionListener(this);

        loginPanel.add(errorLabel);

        loginWindow.add(loginPanel);
        loginWindow.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn) {

            String inputUsername = usernameField.getText().trim();
            String inputPassword = new String(passwordField.getPassword());

            if (!InputValidator.validateUserName(inputUsername) || !InputValidator.validatePasword(inputPassword)) {
                errorLabel.setText("Invalid format for credentials!");
            } else {
                try {
                    Connection con = DriverManager.getConnection(Constants.DB_HOST, Constants.DB_USER,
                            Constants.DB_PASSWORD);
                    PreparedStatement pstmt = con
                            .prepareStatement("SELECT * FROM users WHERE username = ?");

                    pstmt.setString(1, inputUsername);
                    ResultSet rs = pstmt.executeQuery();
                    if (!rs.isBeforeFirst()) {
                        errorLabel.setText("Username does not exist!");
                    } else {
                        while (rs.next()) {
                            String storedHash = rs.getString(3);
                            int userId = rs.getInt(1);
                            String userName = rs.getString(2);
                            String fullName = rs.getString(4);
                            String role = rs.getString(5);
                            boolean authenticated = PasswordHashing.authenticateUser(storedHash, inputPassword);

                            if (inputUsername.equals(rs.getString(2)) && authenticated) {
                                String[] session = SessionManager.createSession(rs.getInt(1));

                                String token = session[0];
                                long expirationDate = Long.parseLong(session[1]);

                                PreparedStatement delFromSessionPSTMT = con
                                        .prepareStatement("DELETE FROM sessions WHERE user_id = ?");
                                delFromSessionPSTMT.setInt(1, userId);
                                delFromSessionPSTMT.executeUpdate();

                                PreparedStatement sessionPSTMT = con.prepareStatement(
                                        "INSERT INTO sessions(user_id, token, expiration_date) VALUES(?, ?, ?)");

                                sessionPSTMT.setInt(1, userId);
                                sessionPSTMT.setString(2, token);
                                sessionPSTMT.setLong(3, expirationDate);
                                sessionPSTMT.executeUpdate();

                                state.setUserId(userId);
                                state.setFullName(fullName);
                                state.setUserName(userName);
                                state.setRole(role);

                                if (state.getRole().equals("agent")) {
                                    new UserView();
                                } else if (state.getRole().equals("admin")) {
                                    new AdminView();
                                }
                                KeyboardFocusManager.getCurrentKeyboardFocusManager()
                                        .removeKeyEventDispatcher(enterDispatcher);
                                loginWindow.dispose();
                            } else {
                                errorLabel.setText("Incorrect credentials");
                            }
                        }
                    }

                    con.close();
                } catch (Exception err) {
                    WTOptionPane.showMessageBox("Error in login page: " + err);
                }

            }

        } else if (e.getSource() == toRegisterButton) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager()
                    .removeKeyEventDispatcher(enterDispatcher);

            loginWindow.dispose();
            new RegisterPage();
        }
    }
}
