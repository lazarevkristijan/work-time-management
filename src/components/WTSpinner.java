package components;

import java.awt.Dimension;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import consts.Constants;

public class WTSpinner extends JSpinner {
    public WTSpinner() {
        super(new SpinnerDateModel());
        this.setMaximumSize(new Dimension(Constants.DEF_INPUT_WIDTH, Constants.DEF_INPUT_HEIGHT));
    }
}
