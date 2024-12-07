package auth;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import components.WTButton;
import components.WTLabel;
import components.WTOptionPane;
import components.WTPanel;
import components.WTScrollPane;
import components.WTSpacer;
import components.WTTextField;
import components.WTWindow;
import consts.Constants;
import sections.AgentReport;
import state.AppState;
import utils.AdminViewUtils;
import utils.DatabaseUtils;
import utils.GeneralUtils;

public class AdminView implements ActionListener {
    AppState state = AppState.getInstance();

    WTWindow adminWindow = new WTWindow("Work Time - Admin", Constants.DEF_WINDOW_W, Constants.DEF_WINDOW_H, true,
            true);
    WTPanel adminPanel = new WTPanel("box");
    WTScrollPane adminScrollPane = new WTScrollPane(adminPanel);
    WTPanel contentPanel = new WTPanel("");

    WTLabel adminHeading = new WTLabel("Admin Dashboard", true, "lg", "b", 'c');
    WTTextField userSearchField = new WTTextField(Constants.DEF_INPUT_WIDTH, Constants.DEF_INPUT_HEIGHT);
    WTButton submitSearchBtn = new WTButton("Search", Constants.actionBtnBgColor);

    WTLabel errorLabel = new WTLabel("", false, "md", "b", 'c');

    WTButton logoutButton = new WTButton("Logout", Constants.actionBtnBgColor);

    public AdminView() {
        adminPanel.add(adminHeading);
        adminPanel.add(userSearchField);
        adminPanel.add(submitSearchBtn);
        submitSearchBtn.addActionListener(this);

        try {
            AdminViewUtils.getUsers(contentPanel, errorLabel, userSearchField, this);
        } catch (SQLException err) {
            WTOptionPane.showMessageBox("ERROR IN ADMINVIEW GET USERS: " + err);
        }

        adminPanel.add(contentPanel);
        adminPanel.add(errorLabel);

        adminPanel.add(new WTSpacer(new Dimension(Constants.LARGE_Y_SPACER_WIDTH, Constants.LARGE_Y_SPACER_HEIGHT)));
        adminPanel.add(logoutButton);
        logoutButton.addActionListener(this);

        adminWindow.add(adminScrollPane);
        adminWindow.setVisible(true);

        GeneralUtils.addListenerForEnter(submitSearchBtn);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String userData = e.getActionCommand(); // GET AS STRING COS ITS STORED AS STRING
            Connection con = DatabaseUtils.getConnection();

            if (e.getSource() == logoutButton) {
                PreparedStatement logoutPSTMT = con.prepareStatement("DELETE FROM sessions WHERE user_id = ?");
                logoutPSTMT.setInt(1, state.getUserId());
                logoutPSTMT.executeUpdate();

                SessionManager.invalidateSession();

                AppState.resetInstance();
                adminWindow.dispose();
                new LoginPage();
            } else if (e.getSource() == submitSearchBtn) {
                AdminViewUtils.getUsers(contentPanel, errorLabel, userSearchField, this);
            } else {
                if (!userData.isEmpty()) {
                    String[] parts = userData.split(":");
                    if (parts[2].equals("w")) {
                        new AgentReport(userData, "work times");
                    } else if (parts[2].equals("b")) {
                        new AgentReport(userData, "breaks");
                    }
                }
            }
            con.close();
        } catch (Exception err) {
            WTOptionPane.showMessageBox("ERROR IN ADMIN VIEW - ACTION PERFORMED OVERRIDE: " + err);
        }

    }

}
