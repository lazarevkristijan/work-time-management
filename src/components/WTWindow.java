package components;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WTWindow extends JFrame {

    public WTWindow(String title, int w, int h, boolean centered, boolean closeAllOnExit) {
        this.setTitle(title);
        this.setSize(w, h);
        if (centered) {
            this.setLocationRelativeTo(null);
        }
        // this.setResizable(false);
        ImageIcon icon = new ImageIcon("src/resources/favico.png");
        this.setIconImage(icon.getImage());
        if (SystemTray.isSupported()) {
            this.setDefaultCloseOperation(closeAllOnExit ? WTWindow.DO_NOTHING_ON_CLOSE : WTWindow.DISPOSE_ON_CLOSE);
            SystemTray tray = SystemTray.getSystemTray();
            Image image = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            TrayIcon trayIcon = new TrayIcon(image, "Work Times");

            PopupMenu trayPopupMenu = new PopupMenu();
            MenuItem exitItem = new MenuItem("Exit");

            trayIcon.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    WTWindow.this.setVisible(true);
                    WTWindow.this.setExtendedState(WTWindow.NORMAL);
                    tray.remove(trayIcon);
                }
            });

            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    WTWindow.this.setVisible(false);
                    try {
                        tray.add(trayIcon);
                    } catch (Exception err) {
                        WTOptionPane.showMessageBox("Error in WTWindow: " + err);
                    }
                }
            });

            exitItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }

            });
            trayPopupMenu.add(exitItem);
            trayIcon.setPopupMenu(trayPopupMenu);
        } else {
            this.setDefaultCloseOperation(closeAllOnExit ? WTWindow.EXIT_ON_CLOSE : WTWindow.DISPOSE_ON_CLOSE);
        }
    }
}
