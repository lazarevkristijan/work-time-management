package components;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import consts.Constants;
import utils.WrapLayout;

public class WTPanel extends JPanel {

    public WTPanel(String layout) {
        if (layout.equals("box")) {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        } else if (layout.equals("")) {
            this.setLayout(new WrapLayout(WrapLayout.CENTER, 15, 20));
        } else if (layout.equals("boxa")) {
            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        }
        this.setBackground(Constants.panelBgColor);
    }
}
