package serverguiiec61850.gui;

import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.EchoEncoder;
import ch.qos.logback.core.encoder.Encoder;
import com.beanit.openiec61850.ServerAssociation;

import java.util.Date;
import serverguiiec61850.network.Client;
import serverguiiec61850.network.Server;

/**
 * Appender LogBack for JTextArea (swing component) wiedergibt die logs in Gui
 *
 * @author Philipp Mandl
 */
//ToDo: muss noch sortiert und vereinfacht werden
public final class JTextAreaAppender extends AppenderBase<ILoggingEvent> {

    private static final Logger LOGGER_SERVER = LoggerFactory.getLogger(ServerAssociation.class);
    private static final Logger LOGGER_SERVER_FRONTEND = LoggerFactory.getLogger(Server.class);
    private static final Logger LOGGER_GUI = LoggerFactory.getLogger(Gui.class);
    private static final Logger LOGGER_CLIENT = LoggerFactory.getLogger(Client.class);
    private static final Logger LOGGER_SIM = LoggerFactory.getLogger(Simulator.class);

    private final Encoder<ILoggingEvent> ENCODER = new EchoEncoder<ILoggingEvent>();
    private final JTextArea MASTER_LOG;
    private final JTextArea SIMULATOR_LOG;
    private final JTextArea REPORT_DATASET_LOG;

    /**
     *
     * @param masterLog
     * @param simulatorLog
     * @param reportdatasetLog
     */
    public JTextAreaAppender(JTextArea masterLog, JTextArea simulatorLog, JTextArea reportdatasetLog) {
        LOGGER_SERVER.warn("Initialization of Server Console...");
        LOGGER_GUI.warn("Initialization of GUI Console...");
        LOGGER_SERVER_FRONTEND.warn("Initialization of Server Console...");
        LOGGER_CLIENT.warn("Initialization of Client Console...");
        LOGGER_SIM.warn("Initialization of Client Console...");

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
        //TODO error red, warn orange
        //TODO make autoscroll
        ENCODER.encode(event);
        String line = getTime(event.getTimeStamp()) + event.toString() + "\n";
        MASTER_LOG.append(line);
        if (Gui.mainFrame.getSelectedIndex() == 2) {//simulator page
            SIMULATOR_LOG.append(line);
        } else if (Gui.mainFrame.getSelectedIndex() == 1) {//report dataset manipulation
            REPORT_DATASET_LOG.append(line);
        }
    }
}
