package serverguiiec61850.gui;

import java.awt.Color;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * controls all style stuff for the logs
 *
 * @author Philipp Mandl
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
        StyleConstants.setForeground(ORANGE, Color.ORANGE);
        StyleConstants.setFontFamily(ORANGE, FONTFAMILY);
        StyleConstants.setFontSize(ORANGE, FONTSIZE);

        StyleConstants.setForeground(RED, Color.RED);
        StyleConstants.setFontFamily(RED, FONTFAMILY);
        StyleConstants.setFontSize(RED, FONTSIZE);

        StyleConstants.setForeground(BLACK, Color.BLACK);
        StyleConstants.setFontFamily(BLACK, FONTFAMILY);
        StyleConstants.setFontSize(BLACK, FONTSIZE);

        StyleConstants.setForeground(GREW, Color.DARK_GRAY);
        StyleConstants.setFontFamily(GREW, FONTFAMILY);
        StyleConstants.setFontSize(GREW, FONTSIZE);
    }
}
