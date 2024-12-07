package utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionListener;

import components.WTButton;
import components.WTLabel;
import components.WTPanel;
import components.WTTextField;

public class AdminViewUtils {

    public AdminViewUtils() {

    }

    public static void getUsers(WTPanel contentPanel, WTLabel errorLabel, WTTextField userSearchField,
            ActionListener actionListener)
            throws SQLException {
        contentPanel.removeAll();
        errorLabel.setText("");
        Connection con = DatabaseUtils.getConnection();

        Statement getUserSTMT = con.createStatement();
        ResultSet usersRS = getUserSTMT.executeQuery(
                "SELECT * FROM users WHERE role = 'agent' AND full_name LIKE '%" + userSearchField.getText()
                        + "%' ORDER BY full_name");
        if (!usersRS.isBeforeFirst()) {
            errorLabel.setText("No users");
        } else {
            while (usersRS.next()) {
                WTPanel singleUserContainer = new WTPanel("box");

                singleUserContainer.add(new WTLabel(usersRS.getString(4), false, "sm", "b", 'c'));
                WTButton workTimesButton = new WTButton("Work Hours");
                WTButton breaksButton = new WTButton("Breaks");

                // get id as string cos fn expects string
                workTimesButton.setActionCommand(usersRS.getString(1) + ":" + usersRS.getString(4) + ":w");
                breaksButton.setActionCommand(usersRS.getString(1) + ":" + usersRS.getString(4) + ":b");

                workTimesButton.addActionListener(actionListener);
                breaksButton.addActionListener(actionListener);

                singleUserContainer.add(workTimesButton);
                singleUserContainer.add(breaksButton);

                contentPanel.add(singleUserContainer);

            }

            contentPanel.revalidate();
            contentPanel.repaint();
        }
        con.close();
    }

}
