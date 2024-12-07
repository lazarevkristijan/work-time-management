package components;

import java.awt.Dimension;

import javax.swing.JPasswordField;

public class WTPasswordField extends JPasswordField {
    public WTPasswordField(int w, int h) {
        this.setMaximumSize(new Dimension(w, h));
    }
}
