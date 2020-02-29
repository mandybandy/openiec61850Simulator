package serverguiiec61850.gui;

import java.util.logging.Level;
import java.util.logging.Logger;
import serverguiiec61850.network.Server;

/**
 * erstellt einen neuen Simulator Thread
 *
 * @author Philipp
 */
public class Simulator {

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
        for (int stepsCounter = 0; stepsCounter < steps + 1; stepsCounter++) {
            server.writeValue(referenceRamp, fcString, String.valueOf(from + ((to - from) / steps) * (stepsCounter)));
            //wait time/steps
            Thread.sleep(time / steps);
        }
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
        Thread pulseSim = new Thread(new PulseSim(referencePuls, fcString, min, max, onTime, offTime));
        pulseSim.start();
    }
//boolsche variable!!!

    private class PulseSim implements Runnable {

        private final String referencePuls, fcString, min, max;
        private final long onTime, offTime;
        Thread listener;

        public PulseSim(String referencePuls, String fcString, String min, String max, long onTime, long offTime) {
            this.referencePuls = referencePuls;
            this.fcString = fcString;
            this.min = min;
            this.max = max;
            this.onTime = onTime;
            this.offTime = offTime;
        }

        //wird mit externer Variable beendet? mehr oder weniger gut
        @Override
        public void run() {
            while (Gui.enabled) {
                try {
                    server.writeValue(referencePuls, fcString, max);
                    //ontime
                    Thread.sleep(onTime);
                    server.writeValue(referencePuls, fcString, min);
                    //offtime
                    Thread.sleep(offTime);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Simulator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
