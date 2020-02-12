
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Philipp
 */
public class NetworkUtil {

    /**
     *
     */
    public void printAllOwnerMacs() {
        InetAddress[] ias;
        try {
            ias = InetAddress.getAllByName(getOwnerHostName());
            if (ias != null)
                for (InetAddress ia : ias) {
                    System.out.println(ia.getHostAddress());
                }
        } catch (UnknownHostException e) {
            System.err.println("Unbekannter Hostname");
        }
    }

    /**
     *
     * @return
     */
    public String getOwnerHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @return
     */
    public String getOwnerNetworkDeviceName() {
        try {
            NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress
                    .getLocalHost());
            if (ni != null)
                return ni.getDisplayName();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @return
     */
    public String getOwnerMac() {
        try {
            NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress
                    .getLocalHost());
            byte[] hwa = ni.getHardwareAddress();
            if (hwa == null)
                return null;
            String mac = "";
            for (int i = 0; i < hwa.length; i++) {
                mac += String.format("%x:", hwa[i]);
            }
            if (mac.length() > 0 && !ni.isLoopback()) {
                return mac.toLowerCase().substring(0, mac.length() - 1);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @return
     */
    public String getOwnerIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        NetworkUtil nu = new NetworkUtil();
        //nu.printAllOwnerMacs();
        System.out.println("Host-Name: " + nu.getOwnerHostName());
        System.out.println("Device-Name: " + nu.getOwnerNetworkDeviceName());
        System.out.println("Mac-Adresse: " + nu.getOwnerMac());
        System.out.println("IP: " + nu.getOwnerIp());
    }
}