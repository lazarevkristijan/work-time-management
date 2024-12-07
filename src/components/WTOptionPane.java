package components;

import javax.swing.JOptionPane;

public class WTOptionPane extends JOptionPane {

    public WTOptionPane() {

    }

    public static void showMessageBox(String message) {
        JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static int showConfirmDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
    }
}
