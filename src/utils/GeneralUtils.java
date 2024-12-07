package utils;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import javax.swing.text.NumberFormatter;

import components.WTButton;

public class GeneralUtils {
    GeneralUtils() {

    }

    public static String formatDate(String pattern, long time) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);

        return formatter.format(time);
    }

    public static KeyEventDispatcher addListenerForEnter(WTButton button) {
        KeyEventDispatcher enterDispatcher = new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_RELEASED && e.getKeyChar() == '\n') {
                    button.doClick();
                }
                return false;
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(enterDispatcher);
        return enterDispatcher;
    }

    public static String capitalizeString(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static NumberFormatter getNumberFormatter() {
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(1);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);

        return formatter;
    }
}
