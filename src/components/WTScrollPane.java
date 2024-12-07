package components;

import java.awt.Component;

import javax.swing.JScrollPane;

public class WTScrollPane extends JScrollPane {
    public WTScrollPane(Component view) {

        this.setViewportView(view);
        this.setVerticalScrollBarPolicy(WTScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.getVerticalScrollBar().setUnitIncrement(12);
    }
}
