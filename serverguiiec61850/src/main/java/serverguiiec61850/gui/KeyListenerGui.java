/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverguiiec61850.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Philipp
 */
public class KeyListenerGui extends JFrame implements KeyListener {

    JLabel label;
    HelpWindow help = null;

    public KeyListenerGui() {

        JTabbedPane tabbedPane = Gui.mainFrame;
        tabbedPane.addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F1) {
            System.out.println("f1");
            try {
                if (help == null) {
                    help = new HelpWindow();
                    help.setVisible(true);
                }
            } catch (Exception ex) {
                System.out.println("error opening help window");
            }
        }
    }
}
