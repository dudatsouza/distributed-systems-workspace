import javax.swing.JOptionPane;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Sockets03_Sender_UDP_Multicast {
    public static void main(String[] args) {
        try {
            byte[] b = JOptionPane.showInputDialog("MSG: ").getBytes();
            InetAddress addr = InetAddress.getByName("239.0.0.10");
            DatagramSocket ds = new DatagramSocket();
            DatagramPacket pckt = new DatagramPacket(b, b.length, addr, 6668);

            ds.send(pckt);
            System.out.println("MENSAGEM ENVIADA!");
            ds.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "ERRO NO EMISSOR: " + e.getMessage());
        }
    }
}