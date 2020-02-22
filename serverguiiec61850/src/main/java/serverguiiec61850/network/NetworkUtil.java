package serverguiiec61850.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author Philipp Mandl
 */
public class NetworkUtil {

    /**
     *
     * @return @throws java.net.UnknownHostException
     */
    public String getOwnerHostName() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

    /**
     *
     * @return @throws java.net.SocketException
     */
    public static List getNetDevice() throws SocketException {

        ArrayList<String> netDevs = new ArrayList<>();
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        Collections.list(nets).forEach((netint) -> {
            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
            Collections.list(inetAddresses).stream().filter((inetAddress) -> (netint.getInetAddresses().hasMoreElements())).forEachOrdered((Consumer<? super InetAddress>) (inetAddress) -> {
                System.out.println("Display name:" + netint.getDisplayName());
                System.out.println("Name: " + netint.getName());
                System.out.println("InetAddress: " + inetAddress);
                String netdevice = ("display name:" + netint.getDisplayName() + "\n" + "name: " + netint.getName() + "\n" + "address: " + inetAddress + "\n \n");
                netDevs.add(netdevice);
            });
        });
        return netDevs;
    }
}
