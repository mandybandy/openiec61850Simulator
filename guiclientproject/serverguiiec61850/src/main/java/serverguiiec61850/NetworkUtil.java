package serverguiiec61850;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

/**
 *
 * @author Philipp
 */
public class NetworkUtil {

    /**
     *
     * @return
     */
    public String getOwnerHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
        }
        return null;
    }

    /**
     *
     * @param args
     * @return
     */
    public static DefaultListModel getNetDevice() {
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            DefaultListModel netDevices = new DefaultListModel();
            for (NetworkInterface netint : Collections.list(nets)) {
                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                Collections.list(inetAddresses).stream().filter((inetAddress) -> (netint.getInetAddresses().hasMoreElements())).forEachOrdered((Consumer<? super InetAddress>) (inetAddress) -> {
                    System.out.println("Display name:" + netint.getDisplayName());
                    System.out.println("Name: " + netint.getName());
                    System.out.println("InetAddress: " + inetAddress);
                    String netdevice = ("Display name:" + netint.getDisplayName() + "   " + "Name: " + netint.getName() + "   " + "InetAddress: " + inetAddress);
                    netDevices.addElement(netdevice);
                });
            }
            return netDevices;
        } catch (SocketException ex) {
            Logger.getLogger(NetworkUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
