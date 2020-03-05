package serverguiiec61850.gui;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.Fc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serverguiiec61850.network.Server;

/**
 * erstellt einen neuen Simulator Thread
 *
 * @author Philipp
 */
public class Simulator {

    private static final Logger LOGGER_SIM = LoggerFactory.getLogger(Simulator.class);

    /**
     *
     */
    public static boolean simulate = false;
    Server server;

    Simulator(Server server) {
        this.server = server;
    }

    /**
     * erstellt einen Rampen Simulator
     *
     * @param referenceRamp
     * @param fcString
     * @param from
     * @param to
     * @param time
     * @param steps
     * @throws InterruptedException
     */
    public void rampSimulator(String referenceRamp, String fcString, int from, int to, long time, int steps) throws InterruptedException {
        simulate = true;
        Thread rampSim = new Thread(new RampSim(referenceRamp, fcString, from, to, time, steps));
        rampSim.start();
    }

    /**
     * erstellt einen Puls Simulator
     *
     * @param referencePuls
     * @param fcString
     * @param min
     * @param max
     * @param onTime
     * @param offTime
     * @throws InterruptedException
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

        public PulseSim(String referencePulse, String fcString, String min, String max, long onTime, long offTime) {
            this.referencePuls = referencePulse;
            this.fcString = fcString;
            this.min = min;
            this.max = max;
            this.onTime = onTime;
            this.offTime = offTime;

            BasicDataAttribute bda = null;
            try {
                bda = (BasicDataAttribute) server.serverModel.findModelNode(referencePulse, Fc.fromString(fcString));
                if ((bda == null) || bda.getChildren() != null) {
                    throw new ClassCastException();
                }
            } catch (ClassCastException ex) {
                bda = null;
                LOGGER_SIM.error("invalid reference or fc selected", ex);
                Gui.enabled = false;
            }
        }

        //wird mit externer Variable beendet? mehr oder weniger gut
        @Override
        public void run() {
            if (Gui.enabled) {
                try {
                    server.writeValue(referencePuls, fcString, max);
                    //ontime
                    Thread.sleep(onTime);
                    server.writeValue(referencePuls, fcString, min);
                    //offtime
                    Thread.sleep(offTime);
                } catch (InterruptedException ex) {
                    LOGGER_SIM.error("", ex);
                }
            } else {
                simulate = false;
            }
        }
    }

    private class RampSim implements Runnable {

        private boolean start = true;
        private final String referenceRamp, fcString;
        private final long time;
        private final int from;
        private final int to;
        private final int steps;

        public RampSim(String referenceRamp, String fcString, int from, int to, long time, int steps) {
            this.referenceRamp = referenceRamp;
            this.fcString = fcString;
            this.time = time;
            this.from = from;
            this.to = to;
            this.steps = steps;

            BasicDataAttribute bda;
            try {
                bda = (BasicDataAttribute) server.serverModel.findModelNode(referenceRamp, Fc.fromString(fcString));
                if ((bda == null) || bda.getChildren() != null) {
                    throw new ClassCastException();
                }
            } catch (ClassCastException ex) {
                bda = null;
                LOGGER_SIM.error("invalid reference or fc selected", ex);
                start = false;
            }

        }

        //wird mit externer Variable beendet? mehr oder weniger gut
        @Override
        public void run() {
            if (start) {
                for (int stepsCounter = 0; stepsCounter < steps + 1; stepsCounter++) {
                    server.writeValue(referenceRamp, fcString, String.valueOf(from + ((to - from) / steps) * (stepsCounter)));
                    try {
                        //wait time/steps
                        Thread.sleep(time / steps);
                    } catch (InterruptedException ex) {
                        LOGGER_SIM.error("", ex);
                    }
                }
            } else {
                simulate = false;
            }
        }
    }
}
