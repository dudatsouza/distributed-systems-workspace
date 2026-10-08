import javax.swing.JOptionPane;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public class Sockets03_Receiver_UDP_Multicast {
    public static void main(String[] args) {
        try {
            InetAddress addr = InetAddress.getByName("239.0.0.10");
            InetSocketAddress group = new InetSocketAddress(addr, 6668);

            // Ajuste o nome da interface ("Wi-Fi", "wlan0", "en0", etc.) conforme o seu sistema operacional
            NetworkInterface netIf = NetworkInterface.getByName("Wi-Fi");

            MulticastSocket s = new MulticastSocket(group.getPort());
            s.joinGroup(group, netIf);

            byte[] b = new byte[256];
            DatagramPacket pckt = new DatagramPacket(b, b.length);

            s.receive(pckt);
            JOptionPane.showMessageDialog(null, new String(b).trim());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "ERRO NO RECEPTOR: " + e.getMessage());
        }
    }
}