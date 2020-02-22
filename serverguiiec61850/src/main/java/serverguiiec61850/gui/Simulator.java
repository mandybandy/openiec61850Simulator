/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverguiiec61850.gui;

import serverguiiec61850.network.Server;

/**
 *
 * @author Philipp
 */
public class Simulator {

    Server server;

    Simulator() {
    }

    public void rampSimulator(String referenceRamp, String fcString, int from, int to, long time, int steps) throws InterruptedException {

        for (int i = 0; i < steps; i++) {
            server.writeValue(referenceRamp, fcString, String.valueOf(from + ((to - from) / steps)));
            //wait time/steps
            Thread.sleep(time / steps);
        }
    }

    public void pulseSimulator(String referencePuls, String fcString, String min, String max, long onTime, long offTime) throws InterruptedException {

        while (!Gui.simulatePulsStopBTN.isSelected()) {

            server.writeValue(referencePuls, fcString, max);
            //ontime
            Thread.sleep(onTime);
            server.writeValue(referencePuls, fcString, min);
            //offtime
            Thread.sleep(offTime);

        }
    }
}
