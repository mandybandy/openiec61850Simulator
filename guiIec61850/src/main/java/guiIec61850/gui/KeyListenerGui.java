/**
 * @project IEC61850 simulator
 * @date 10.03.2020
 * @path serverguiiec61850.gui.KeyListenerGui.java
 * @author Philipp Mandl
 */
package guiIec61850.gui;

import java.awt.Frame;
import static java.awt.Frame.getFrames;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.VK_F1;
import java.awt.event.KeyListener;
import static java.lang.System.out;
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

        Frame[] frame = getFrames();
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
        if (e.getKeyCode() == VK_F1) {
            out.println("f1");
            try {
                if (help == null) {
                    help = new HelpWindow();
                    help.setVisible(true);
                } else if (!help.isActive()) {
                    help = new HelpWindow();
                    help.setVisible(true);
                }
            } catch (Exception ex) {
                out.println("error opening help window");
            }
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     *
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
    }
}
