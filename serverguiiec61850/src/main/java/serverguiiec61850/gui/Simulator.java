/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverguiiec61850.gui;

import java.util.logging.Level;
import java.util.logging.Logger;
import serverguiiec61850.network.Server;

/**
 *
 * @author Philipp
 */
public class Simulator{
    Server server;
    
    Simulator(Server server) {
        this.server = server;
    }

    public void rampSimulator(String referenceRamp, String fcString, int from, int to, long time, int steps) throws InterruptedException {
        for (int i = 0; i < steps + 1; i++) {
            server.writeValue(referenceRamp, fcString, String.valueOf(from + ((to - from) / steps) * (i)));
            //wait time/steps
            Thread.sleep(time / steps);
        }
    }

    public void pulseSimulator(String referencePuls, String fcString, String min, String max, long onTime, long offTime) throws InterruptedException {
        Thread pulseSim = new Thread(new PulseSim(referencePuls, fcString, min, max, onTime, offTime));
        pulseSim.start();
    }
    //TODO: PulseSim Stoppt nicht weil Thread nicht terminiert wird
    //Thread exportieren und bei btnClick mit interrupt stoppen oder 
    //bool stopPulseSim wird exportiert statt Gui.simulatePulseStopBTN.isSelected 
    //(macht es gleiche wird nur true wenn der btnStop drückt ist und false wenn btnStart)
    private class PulseSim implements Runnable
    {
        private boolean running = true;
        private String referencePuls, fcString, min, max;
        private long onTime, offTime;
        Thread listener;
        
        public PulseSim(String referencePuls, String fcString, String min, String max, long onTime, long offTime)
        {
            this.referencePuls = referencePuls;
            this.fcString = fcString;
            this.min = min;
            this.max = max; 
            this.onTime = onTime;
            this.offTime = offTime;
        }
        
        @Override
        public void run()
        {
            while (!Gui.simulatePulsStopBTN.isSelected())
            {
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
