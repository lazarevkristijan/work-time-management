package sections;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;

import components.WTButton;
import components.WTLabel;
import components.WTOptionPane;
import components.WTPanel;
import components.WTScrollPane;
import components.WTSpacer;
import components.WTSpinner;
import components.WTWindow;
import consts.Constants;
import utils.DatabaseUtils;
import utils.GeneralUtils;

public class AgentReport implements ActionListener {

        String reportType;
        String userData;
        int userId;
        String fullName;

        WTWindow reportWindow = new WTWindow("", Constants.DEF_WINDOW_W, Constants.DEF_WINDOW_H, true, false);
        WTPanel reportPanel = new WTPanel("box");
        WTPanel contentPanel = new WTPanel("");
        WTScrollPane scrollPane = new WTScrollPane(reportPanel);

        WTLabel reportHeading = new WTLabel("", true, "lg", "b", 'c');

        WTLabel rangeLabel = new WTLabel("(amount of items to display)", false, "sm", "b", 'c');
        JFormattedTextField rangeTextField = new JFormattedTextField(GeneralUtils.getNumberFormatter());
        JComboBox<String> dropDownBox = new JComboBox<String>(Constants.orderResultsByChoices);
        WTButton submitSearchBtn = new WTButton("Search", Constants.actionBtnBgColor);
        WTLabel totalDurationLabel = new WTLabel("", false, "sm", "b", 'c');

        WTSpinner startDateChooser = new WTSpinner();
        WTLabel fromDateLabel = new WTLabel("(from date)", false, "sm", "b", 'c');
        WTSpinner endDateChooser = new WTSpinner();
        WTLabel toDateLabel = new WTLabel("(to date)", false, "sm", "b", 'c');
        WTSpinner.DateEditor startEditor = new WTSpinner.DateEditor(startDateChooser, "dd-MM-yyyy");
        WTSpinner.DateEditor endEditor = new WTSpinner.DateEditor(endDateChooser, "dd-MM-yyyy");

        WTLabel errorLabel = new WTLabel("", false, "md", "b", 'c');

        public AgentReport(String userData, String reportType) {
                // userdata = id:fullName:reportTypeChar
                // reportType = breaks or work times
                String[] parts = userData.split(":");
                this.userData = userData;
                this.reportType = reportType;
                this.userId = Integer.parseInt(parts[0]);
                this.fullName = parts[1];

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -7);
                startDateChooser.setEditor(startEditor);
                endDateChooser.setEditor(endEditor);
                startDateChooser.setValue(new Date(cal.getTimeInMillis()));
                endDateChooser.setValue(new Date());

                reportWindow.setTitle("Agent " + reportType + " report - " + fullName);
                reportHeading.setText(fullName + "'s " + (reportType.equals("work times") ? "work hours" : reportType));

                reportPanel.add(reportHeading);

                reportPanel.add(new WTSpacer(new Dimension(Constants.SMALL_Y_SPACER_WIDTH,
                                Constants.SMALL_Y_SPACER_HEIGHT)));
                reportPanel.add(rangeTextField);
                reportPanel.add(rangeLabel);
                reportPanel.add(new WTSpacer(new Dimension(Constants.SMALL_Y_SPACER_WIDTH,
                                Constants.SMALL_Y_SPACER_HEIGHT)));
                rangeTextField.setValue(15);
                rangeTextField.setMaximumSize(new Dimension(Constants.DEF_INPUT_WIDTH, Constants.DEF_INPUT_HEIGHT));
                submitSearchBtn.addActionListener(this);

                dropDownBox.setVisible(true);
                dropDownBox.setMaximumSize(new Dimension(Constants.DEF_INPUT_WIDTH, Constants.DEF_INPUT_HEIGHT));
                reportPanel.add(dropDownBox);

                reportPanel.add(startDateChooser);
                reportPanel.add(fromDateLabel);
                reportPanel.add(endDateChooser);
                reportPanel.add(toDateLabel);

                reportPanel.add(submitSearchBtn);
                reportPanel.add(totalDurationLabel);

                getData();

                reportPanel.add(errorLabel);
                reportPanel.add(contentPanel);

                reportWindow.add(scrollPane);

                reportWindow.setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                if (e.getSource() == submitSearchBtn) {
                        getData();
                }
        }

        private ResultSet getData(Connection con) throws SQLException {

                Date startDate = (Date) startDateChooser.getValue();
                Date endDate = (Date) endDateChooser.getValue();

                String endOfQueryString = "WHERE user_id = "
                                + userId
                                + " AND "
                                + "start >= "
                                + startDate.getTime()
                                + " AND "
                                + "end <= "
                                + endDate.getTime()
                                + " ORDER BY start "
                                + (dropDownBox.getSelectedItem()
                                                .toString()
                                                .equals("Newest first")
                                                                ? "DESC"
                                                                : "ASC")
                                + " LIMIT "
                                + Integer.parseInt(
                                                rangeTextField
                                                                .getValue().toString());

                Statement getReportDataSTMT = con.createStatement();
                ResultSet reportRS = getReportDataSTMT
                                .executeQuery(
                                                reportType.equals("work times")
                                                                ? "SELECT start, end FROM work_times "
                                                                                + endOfQueryString
                                                                : "SELECT start, end, type FROM breaks "
                                                                                + endOfQueryString);
                return reportRS;
        }

        private void getData() {
                contentPanel.removeAll();
                errorLabel.setText("");
                totalDurationLabel.setText("");
                try {
                        Connection con = DatabaseUtils.getConnection();

                        ResultSet reportRS = getData(con);

                        if (!reportRS.isBeforeFirst()) {
                                errorLabel.setText("No records!");
                        } else {
                                DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern(Constants.HM_PATTERN);
                                long totalDuration = 0;

                                long lunchDuration = 0;
                                long otherDuration = 0;

                                int nItems = 0;

                                while (reportRS.next()) {

                                        String startTime = GeneralUtils.formatDate(Constants.HM_PATTERN,
                                                        reportRS.getLong(1));
                                        String endTime = GeneralUtils.formatDate(Constants.HM_PATTERN,
                                                        reportRS.getLong(2));

                                        CharSequence csStartTime = startTime;
                                        CharSequence csEndTime = endTime;

                                        Duration duration = Duration.between(
                                                        LocalTime.parse(csStartTime, dtFormatter),
                                                        LocalTime.parse(csEndTime, dtFormatter));

                                        long days = duration.toDays();
                                        long hours = duration.toHours() % 24;
                                        long minutes = duration.toMinutes() % 60;
                                        if (reportType.equals("work times")) {
                                                totalDuration += duration.toMillis();
                                        } else {
                                                if (reportRS.getString(3).equals("lunch")) {
                                                        lunchDuration += duration.toMillis();
                                                } else {
                                                        otherDuration += duration.toMillis();
                                                }
                                        }

                                        String durationStr = (days != 0 ? days + "d, " : "") + hours + "h, "
                                                        + minutes + "m";

                                        WTPanel reportChunkPanel = new WTPanel("box");
                                        reportChunkPanel.add(new WTLabel(
                                                        GeneralUtils.formatDate(
                                                                        Constants.DMY_PATTERN, reportRS.getLong(1)),
                                                        true, "sm", "b", 'c'));

                                        reportChunkPanel.add(new WTLabel(
                                                        (reportType.equals("work times") ? ""
                                                                        : GeneralUtils.capitalizeString(
                                                                                        reportRS.getString(3)) + ": ")
                                                                        + startTime
                                                                        + " to " + endTime,
                                                        false,
                                                        "md", "b",
                                                        'c'));

                                        reportChunkPanel.add(
                                                        new WTLabel(durationStr, false, "sm", "b", 'c'));
                                        contentPanel.add(reportChunkPanel);
                                        nItems++;
                                }

                                if (reportType.equals("work times")) {
                                        long hourDur = totalDuration / 1000 / 3600;
                                        long minDur = (totalDuration / 1000 % 3600) / 60;
                                        totalDurationLabel
                                                        .setText("Total: " + hourDur + "h, " + minDur + "m" + " (from "
                                                                        + nItems + " items)");
                                } else {
                                        long lunchHourDur = lunchDuration / 1000 / 3600;
                                        long lunchMinDur = (lunchDuration / 1000 % 3600) / 60;

                                        long otherHourDur = otherDuration / 1000 / 3600;
                                        long otherMinDur = (otherDuration / 1000 % 3600) / 60;

                                        totalDurationLabel
                                                        .setText("Lunch: " + lunchHourDur + "h, " + lunchMinDur
                                                                        + "m" + " | " + "Other: " + otherHourDur + "h, "
                                                                        + otherMinDur + "m" + " (from "
                                                                        + nItems + " items)");

                                }

                        }
                        con.close();
                } catch (SQLException err) {
                        WTOptionPane.showMessageBox("Error in get agent report - custom data fn: " + err);
                }
                contentPanel.revalidate();
                contentPanel.repaint();
        }
}
