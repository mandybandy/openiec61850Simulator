/**
 * @project IEC61850 simulator
 * @date 10.03.2020
 * @path serverguiiec61850.gui.Simulator.java
 * @author Philipp Mandl
 */
package guiIec61850.gui;

import com.beanit.openiec61850.BasicDataAttribute;
import static com.beanit.openiec61850.Fc.fromString;
import static guiIec61850.gui.Gui.enabled;
import static guiIec61850.gui.Gui.simulateRampStartBTN;
import static guiIec61850.gui.Gui.simulateRampStopBTN;
import java.io.IOException;
import org.slf4j.Logger;
import guiIec61850.network.Server;
import static java.lang.String.valueOf;
import static java.lang.Thread.sleep;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * creates simulator
 */
public class Simulator {

    private static final Logger LOGGER_SIM = getLogger(Simulator.class);

    /**
     * is true if simulation is active
     */
    public static boolean simulate = false;
    Server server;

    Simulator(Server server) {
        this.server = server;
    }

    /**
     * creates a ramp simulator
     *
     * @param referenceRamp string reference
     * @param fcString string fc
     * @param from int low value
     * @param to int high value 
     * @param time long time 
     * @param steps int steps 
     * @throws InterruptedException interuption
     */
    public void rampSimulator(String referenceRamp, String fcString, int from, int to, long time, int steps) throws InterruptedException {
        simulate = true;
        Thread rampSim = new Thread(new RampSim(referenceRamp, fcString, from, to, time, steps));
        rampSim.start();
    }

    /**
     * creates a pulse simulator
     *
     * @param referencePuls string reference
     * @param fcString string fc
     * @param min int low value 
     * @param max int high value 
     * @param onTime long on time
     * @param offTime long off time 
     * @throws InterruptedException interuption
     */
    public void pulseSimulator(String referencePuls, String fcString, String min, String max, long onTime, long offTime) throws InterruptedException {
        simulate = true;
        Thread pulseSim = new Thread(new PulseSim(referencePuls, fcString, min, max, onTime, offTime));
        pulseSim.start();
    }
//boolsche variable!!!

    private class PulseSim implements Runnable {

        private final String referencePuls, fcString, min, max;
        private final long onTime, offTime;

        private PulseSim(String referencePulse, String fcString, String min, String max, long onTime, long offTime) {
            this.referencePuls = referencePulse;
            this.fcString = fcString;
            this.min = min;
            this.max = max;
            this.onTime = onTime;
            this.offTime = offTime;

            BasicDataAttribute bda;
            try {
                bda = (BasicDataAttribute) server.serverModel.findModelNode(referencePulse, fromString(fcString));
                if ((bda == null) || bda.getChildren() != null) {
                    throw new ClassCastException();
                }
            } catch (ClassCastException e) {
                bda = null;
                LOGGER_SIM.error("invalid reference or fc selected", e);
                enabled = false;
            }
        }

        //wird mit externer Variable beendet? mehr oder weniger gut
        @Override
        public void run() {
            while (enabled) {
                try {
                    server.writeValue(referencePuls, fcString, max);
                    //ontime
                    sleep(onTime);
                    server.writeValue(referencePuls, fcString, min);
                    //offtime
                    sleep(offTime);
                } catch (InterruptedException e) {
                    LOGGER_SIM.error("simulator interrupted", e);
                } catch (IOException e) {
                    LOGGER_SIM.error("server not found");
                    return;
                } catch (IllegalArgumentException e) {
                    LOGGER_SIM.error(e.getMessage());
                    return;
                }
            }
            simulate = false;
        }
    }

    private class RampSim implements Runnable {

        private boolean start = true;
        private final String referenceRamp, fcString;
        private final long time;
        private final int from;
        private final int to;
        private final int steps;

        private RampSim(String referenceRamp, String fcString, int from, int to, long time, int steps) {
            this.referenceRamp = referenceRamp;
            this.fcString = fcString;
            this.time = time;
            this.from = from;
            this.to = to;
            this.steps = steps;

            BasicDataAttribute bda;
            try {
                bda = (BasicDataAttribute) server.serverModel.findModelNode(referenceRamp, fromString(fcString));
                if ((bda == null) || bda.getChildren() != null) {
                    throw new ClassCastException();
                }
            } catch (ClassCastException e) {
                bda = null;
                LOGGER_SIM.error("invalid reference or fc selected", e);
                start = false;
            }

        }

        //wird mit externer Variable beendet.
        @Override
        public void run() {

            if (start) {
                for (int stepsCounter = 0; stepsCounter < steps + 1; stepsCounter++) {
                    if (enabled) {
                        try {
                            server.writeValue(referenceRamp, fcString, valueOf(from + ((to - from) / steps) * (stepsCounter)));
                            try {
                                //wait time/steps
                                sleep(time / steps);
                            } catch (InterruptedException e) {
                                LOGGER_SIM.error("simulator interrupted", e);
                            }
                        } catch (IOException e) {
                            LOGGER_SIM.error("server not found");
                        } catch (IllegalArgumentException e) {
                            LOGGER_SIM.error(e.getMessage());
                        }
                    }
                }
            } else {
                simulate = false;
                simulateRampStartBTN.setEnabled(false);
                simulateRampStopBTN.setEnabled(true);
            }
        }
    }
}
