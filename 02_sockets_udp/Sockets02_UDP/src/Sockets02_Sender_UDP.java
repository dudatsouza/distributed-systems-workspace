import javax.swing.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class Sockets02_Sender_UDP {
    public static void main(String[] args) {
        try {
            byte[] msg = JOptionPane.showInputDialog("msg a enviar: ").getBytes();

            int port = 6666;
            InetAddress addr = InetAddress.getByName("200.128.142.232");
            DatagramPacket pckt = new DatagramPacket(msg, msg.length, addr, port);

            DatagramSocket ds = new DatagramSocket();
            ds.send(pckt);
            System.out.println("Enviada a: " + addr.getHostAddress() + "na porta: " + port);
        } catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao emissor: " + e.getMessage());
        }
    }
}
