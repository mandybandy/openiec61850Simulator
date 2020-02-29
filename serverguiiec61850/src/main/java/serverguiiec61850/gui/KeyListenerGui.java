package serverguiiec61850.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * erstellt einen KeyListener
 * 
 * @author Philipp
 */
public class KeyListenerGui extends JFrame implements KeyListener {

    private HelpWindow help = null;

    /**
     *KeyListener
     */
    public KeyListenerGui() {

        JTabbedPane tabbedPane = Gui.mainFrame;
        tabbedPane.addKeyListener(this);
    }

    /**
     *on keyTyped
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     *on keyPressed
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
    }

    /**
     *on keyReleased
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
}
