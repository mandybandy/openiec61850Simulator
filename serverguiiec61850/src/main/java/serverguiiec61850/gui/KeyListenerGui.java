/**
 * @project IEC61850 simulator
 * @date 10.03.2020
 * @path serverguiiec61850.gui.KeyListenerGui.java
 * @author Philipp Mandl
 */
package serverguiiec61850.gui;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;

/**
 * creates a keylistener not used yet
 *
 * @author Philipp Mandl
 */
public class KeyListenerGui implements KeyListener {

    private HelpWindow help = null;

    /**
     * KeyListener
     */
    public KeyListenerGui() {

        Frame[] frame = Gui.getFrames();
        KeyListener keyListener = null;
        JFrame mainframe = (JFrame) frame[0];
        mainframe.addKeyListener(this);
        mainframe.setFocusable(true);
        mainframe.setFocusTraversalKeysEnabled(false);
    }

    /**
     * on keyReleased
     *
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
        //wenn f1 ausgelassen wird
        if (e.getKeyCode() == KeyEvent.VK_F1) {
            System.out.println("f1");
            try {
                if (help == null) {
                    help = new HelpWindow();
                    help.setVisible(true);
                } else if (!help.isActive()) {
                    help = new HelpWindow();
                    help.setVisible(true);
                }
            } catch (Exception ex) {
                System.out.println("error opening help window");
            }
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
