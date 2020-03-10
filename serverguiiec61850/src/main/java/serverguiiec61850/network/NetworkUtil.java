package serverguiiec61850.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import serverguiiec61850.gui.Gui;
import static serverguiiec61850.gui.Gui.LOGGER_GUI;

/**
 * controls network functions
 *
 * @author Philipp Mandl
 */
public class NetworkUtil {

    /**
     * returns a list consisting network device information
     * 
     * @throws java.net.SocketException
     */
    public static void getNetDevice() throws SocketException {

        ArrayList<String> netDevs = new ArrayList<>();
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        Collections.list(nets).forEach((netint) -> {
            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
            Collections.list(inetAddresses).stream().filter((inetAddress) -> (netint.getInetAddresses().hasMoreElements())).forEachOrdered((inetAddress) -> {
                LOGGER_GUI.info("Display name:" + netint.getDisplayName());
                LOGGER_GUI.info("Name: " + netint.getName());
                LOGGER_GUI.info("InetAddress: " + inetAddress);
                String netdevice = ("display name:" + netint.getDisplayName() + "\n" + "name: " + netint.getName() + "\n" + "address: " + inetAddress + "\n \n");
                netDevs.add(netdevice);
            });
        });
        for (int i = 0; i < netDevs.size(); i++) {
            Gui.netDevicesTA.append(netDevs.get(i));
        }
    }
}
