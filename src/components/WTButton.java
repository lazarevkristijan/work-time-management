package components;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JButton;

import consts.Constants;

public class WTButton extends JButton {

    public WTButton(String text) {
        this.setText(text);
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setFocusable(false);
        this.setBackground(Constants.btnBgColor);
    }

    public WTButton(String text, Color bgColor) {
        this.setText(text);
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setFocusable(false);
        this.setBackground(bgColor);
    }

}
