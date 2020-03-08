package serverguiiec61850.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.EchoEncoder;
import ch.qos.logback.core.encoder.Encoder;
import com.beanit.openiec61850.ServerAssociation;
import java.util.Date;
import java.util.logging.Level;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import org.w3c.dom.Document;
import serverguiiec61850.network.Client;
import serverguiiec61850.network.Server;

/**
 * Appender LogBack for JTextArea (swing component) wiedergibt die logs in Gui
 *
 * @author Philipp Mandl
 */
public final class JTextAreaAppender extends AppenderBase<ILoggingEvent> {

    private static final Logger LOGGER_SERVER = LoggerFactory.getLogger(ServerAssociation.class);
    private static final Logger LOGGER_SERVER_FRONTEND = LoggerFactory.getLogger(Server.class);
    private static final Logger LOGGER_GUI = LoggerFactory.getLogger(Gui.class);
    private static final Logger LOGGER_CLIENT = LoggerFactory.getLogger(Client.class);
    private static final Logger LOGGER_SIM = LoggerFactory.getLogger(Simulator.class);
    private static final Logger LOGGER_GUITREE = LoggerFactory.getLogger(GuiTree.class);

    private final Encoder<ILoggingEvent> ENCODER = new EchoEncoder<ILoggingEvent>();
    private final JTextPane MASTER_LOG;
    private final JTextPane SIMULATOR_LOG;
    private final JTextPane REPORT_DATASET_LOG;

    /**
     *
     * @param masterLog
     * @param simulatorLog
     * @param reportdatasetLog
     */
    public JTextAreaAppender(JTextPane masterLog, JTextPane simulatorLog, JTextPane reportdatasetLog) {
        LOGGER_SERVER.warn("Initialization of Server Console...");
        LOGGER_GUI.warn("Initialization of GUI Console...");
        LOGGER_SERVER_FRONTEND.warn("Initialization of Server Console...");
        LOGGER_CLIENT.warn("Initialization of Client Console...");
        LOGGER_SIM.warn("Initialization of Client Console...");
        LOGGER_GUITREE.warn("Initialization of Client Console...");

        this.MASTER_LOG = masterLog;
        this.SIMULATOR_LOG = simulatorLog;
        this.REPORT_DATASET_LOG = reportdatasetLog;

        // set ctx & launch
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        setContext(lc);
        start();

        // auto-add für alle logger
        lc.getLogger("ROOT").addAppender(this);
    }

    /**
     * startet encoder
     */
    @Override
    public void start() {
        ENCODER.start();
        super.start();
    }

    private String getTime(long ts) {
        String timeString;
        Date date = new Date(ts);
        timeString = date.toString();
        return timeString;
    }

    /**
     * wird bei angegebenen loggerEvents aufgerufen
     *
     * @param event
     */
    @Override
    public void append(ILoggingEvent event) {
        AttributeSet color = StyleConstant.BLACK;

        ENCODER.encode(event);
        String line = getTime(event.getTimeStamp()) + event.toString() + "\n";
        if (line.contains("ERROR")) {
            color = StyleConstant.RED;
        } else if (line.contains("WARN")) {
            color = StyleConstant.ORANGE;
        } else if (line.contains("DEBUG")) {
            color = StyleConstant.GREW;
        }

        try {
            MASTER_LOG.getDocument().insertString(MASTER_LOG.getDocument().getLength(), line, color);
            if (Simulator.simulate) {//simulator page
                if (!line.contains("DEBUG")) {
                    SIMULATOR_LOG.getDocument().insertString(SIMULATOR_LOG.getDocument().getLength(), line, color);
                }

            } else if (Gui.mainFrame.getSelectedIndex() == 1) {//report dataset manipulation
                REPORT_DATASET_LOG.getDocument().insertString(REPORT_DATASET_LOG.getDocument().getLength(), line, color);
            }
            MASTER_LOG.select(MASTER_LOG.getDocument().getLength(), MASTER_LOG.getDocument().getLength());
            SIMULATOR_LOG.select(SIMULATOR_LOG.getDocument().getLength(), SIMULATOR_LOG.getDocument().getLength());
            REPORT_DATASET_LOG.select(REPORT_DATASET_LOG.getDocument().getLength(), REPORT_DATASET_LOG.getDocument().getLength());

        } catch (BadLocationException ex) {
            java.util.logging.Logger.getLogger(JTextAreaAppender.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
