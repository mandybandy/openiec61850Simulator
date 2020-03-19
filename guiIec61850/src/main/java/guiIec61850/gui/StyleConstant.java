/**
 * @project IEC61850 simulator
 * @date 10.03.2020
 * @path serverguiiec61850.gui.StyleConstant.java
 * @author Philipp Mandl
 */
package guiIec61850.gui;

import java.awt.Color;
import javax.swing.text.SimpleAttributeSet;
import static javax.swing.text.StyleConstants.setFontFamily;
import static javax.swing.text.StyleConstants.setFontSize;
import static javax.swing.text.StyleConstants.setForeground;

/**
 * controls style for the logs
 */
public class StyleConstant {

    private static final int FONTSIZE = 10;
    private static final String FONTFAMILY = "Helvetica";

    /**
     * orange
     */
    public static final SimpleAttributeSet ORANGE = new SimpleAttributeSet();

    /**
     * red
     */
    public static final SimpleAttributeSet RED = new SimpleAttributeSet();

    /**
     * black
     */
    public static final SimpleAttributeSet BLACK = new SimpleAttributeSet();

    /**
     * grew
     */
    public static final SimpleAttributeSet GREW = new SimpleAttributeSet();

    static {//könnte man wenn mans erweitert verkürzen!
        setForeground(ORANGE, Color.ORANGE);
        setFontFamily(ORANGE, FONTFAMILY);
        setFontSize(ORANGE, FONTSIZE);

        setForeground(RED, Color.RED);
        setFontFamily(RED, FONTFAMILY);
        setFontSize(RED, FONTSIZE);

        setForeground(BLACK, Color.BLACK);
        setFontFamily(BLACK, FONTFAMILY);
        setFontSize(BLACK, FONTSIZE);

        setForeground(GREW, Color.DARK_GRAY);
        setFontFamily(GREW, FONTFAMILY);
        setFontSize(GREW, FONTSIZE);
    }
}
