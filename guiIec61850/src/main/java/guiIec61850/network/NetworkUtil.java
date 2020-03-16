/**
 * @project IEC61850 simulator
 * @date 10.03.2020
 * @path serverguiiec61850.network.NetworkUtil.java
 * @author Philipp Mandl
 */
package guiIec61850.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import static guiIec61850.gui.Gui.LOGGER_GUI;
import static guiIec61850.gui.Gui.netDevicesTA;
import static java.net.NetworkInterface.getNetworkInterfaces;
import static java.util.Collections.list;

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
        Enumeration<NetworkInterface> nets = getNetworkInterfaces();
        list(nets).forEach((netint) -> {
            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
            list(inetAddresses).stream().filter((inetAddress) -> (netint.getInetAddresses().hasMoreElements())).forEachOrdered((inetAddress) -> {
                LOGGER_GUI.info("Display name:" + netint.getDisplayName());
                LOGGER_GUI.info("Name: " + netint.getName());
                LOGGER_GUI.info("InetAddress: " + inetAddress);
                String netdevice = ("display name:" + netint.getDisplayName() + "\n" + "name: " + netint.getName() + "\n" + "address: " + inetAddress + "\n \n");
                netDevs.add(netdevice);
            });
        });
        for (int i = 0; i < netDevs.size(); i++) {
            netDevicesTA.append(netDevs.get(i));
        }
    }
}
