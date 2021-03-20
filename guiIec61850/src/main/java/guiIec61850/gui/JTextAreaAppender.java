/**
 * @project IEC61850 simulator
 * @date 10.03.2020
 * @path serverguiiec61850.gui.JTextAreaAppender.java
 * @author Philipp Mandl
 */
package guiIec61850.gui;

import org.slf4j.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.EchoEncoder;
import ch.qos.logback.core.encoder.Encoder;
import com.beanit.openiec61850.ServerAssociation;
import static guiIec61850.gui.Gui.mainFrame;
import static guiIec61850.gui.StyleConstant.BLACK;
import static guiIec61850.gui.StyleConstant.GREW;
import static guiIec61850.gui.StyleConstant.ORANGE;
import static guiIec61850.gui.StyleConstant.RED;
import java.util.Date;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import guiIec61850.network.Client;
import guiIec61850.network.NetworkUtil;
import guiIec61850.network.Server;
import static java.lang.System.out;
import static org.slf4j.LoggerFactory.getILoggerFactory;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * logback appender for JTextArea
 */
public final class JTextAreaAppender extends AppenderBase<ILoggingEvent> {

    private static final Logger LOGGER_SERVER = getLogger(ServerAssociation.class);
    private static final Logger LOGGER_SERVER_FRONTEND = getLogger(Server.class);
    private static final Logger LOGGER_GUI = getLogger(Gui.class);
    private static final Logger LOGGER_CLIENT = getLogger(Client.class);
    private static final Logger LOGGER_NETWORKUTIL = getLogger(NetworkUtil.class);

    private final Encoder<ILoggingEvent> ENCODER = new EchoEncoder<ILoggingEvent>();
    private final JTextPane MASTER_LOG;
    private final JTextPane SIMULATOR_LOG;
    private final JTextPane REPORT_DATASET_LOG;

    /**
     * constructor for appender
     *
     * @param masterLog JTextPane log
     * @param simulatorLog JTextPane simulator
     * @param reportdatasetLog JTextPane change ied
     */
    public JTextAreaAppender(JTextPane masterLog, JTextPane simulatorLog, JTextPane reportdatasetLog) {
        LOGGER_SERVER.warn("Initialization of Server Console...");
        LOGGER_GUI.warn("Initialization of GUI Console...");
        LOGGER_SERVER_FRONTEND.warn("Initialization of Server Console...");
        LOGGER_CLIENT.warn("Initialization of Client Console...");
        LOGGER_NETWORKUTIL.warn("Initialization of Client Console...");

        this.MASTER_LOG = masterLog;
        this.SIMULATOR_LOG = simulatorLog;
        this.REPORT_DATASET_LOG = reportdatasetLog;

        // set ctx & launch
        LoggerContext lc = (LoggerContext) getILoggerFactory();
        setContext(lc);
        start();

        // auto-add für alle logger
        lc.getLogger("ROOT").addAppender(this);
    }

    /**
     * start encoder
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
     * runs by log event
     *
     * @param event loggingEvent
     */
    @Override
    public void append(ILoggingEvent event) {
        AttributeSet color = BLACK;

        ENCODER.encode(event);
        String line = getTime(event.getTimeStamp()) + event.toString() + "\n";
        if (line.contains("ERROR")) {
            color = RED;
        } else if (line.contains("WARN")) {
            color = ORANGE;
        } else if (line.contains("DEBUG")) {
            color = GREW;
        }
    }
}
