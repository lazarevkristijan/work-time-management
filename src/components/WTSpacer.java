package components;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;

public class WTSpacer extends Box {
    public WTSpacer(Dimension dimension) {
        super(BoxLayout.Y_AXIS);

        Component rigidArea = Box.createRigidArea(dimension);
        this.add(rigidArea);
    }
}
