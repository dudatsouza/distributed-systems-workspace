import javax.swing.JOptionPane;
import java.io.PrintWriter;
import java.net.*;
import java.util.Enumeration;

public class UrnaEletronica {
    private static final String IP_CENTRAL = "200.128.141.132";
    private static final int PORTA_CENTRAL_TCP = 7070;
    private static final int MULTICAST_PORT = 6668;

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");

        try {
            // 1. Entrada do voto
            String input = JOptionPane.showInputDialog("Escolha seu candidato:\n[1] Candidato 1\n[2] Candidato 2\n[3] Candidato 3");
            if (input == null || input.trim().isEmpty()) return;

            // Socket UDP para receber o IP de multicast da central
            DatagramSocket udpSocketLocal = new DatagramSocket();
            int portaUdpLocal = udpSocketLocal.getLocalPort();

            // 2. Envia voto e porta local via TCP
            Socket tcpSocket = new Socket(IP_CENTRAL, PORTA_CENTRAL_TCP);
            PrintWriter out = new PrintWriter(tcpSocket.getOutputStream(), true);
            out.println(input.trim() + ";" + portaUdpLocal);
            tcpSocket.close();

            // 3. Recebe o IP multicast via UDP Unicast
            byte[] bufferUdp = new byte[256];
            DatagramPacket pacoteRecebido = new DatagramPacket(bufferUdp, bufferUdp.length);
            udpSocketLocal.receive(pacoteRecebido);
            String ipMulticast = new String(pacoteRecebido.getData(), 0, pacoteRecebido.getLength()).trim();
            udpSocketLocal.close();

            // Configuração do MulticastSocket
            InetAddress addr = InetAddress.getByName(ipMulticast);
            InetSocketAddress group = new InetSocketAddress(addr, MULTICAST_PORT);

            // Obtém a interface de rede ativa (en0 no Mac, ou primeira interface não-loopback com IPv4)
            NetworkInterface netIf = getActiveNetworkInterface();

            MulticastSocket ms = new MulticastSocket(MULTICAST_PORT);
            if (netIf != null) {
                ms.setNetworkInterface(netIf);
                ms.joinGroup(group, netIf);
            } else {
                ms.joinGroup(addr);
            }

            // 4 e 5. Aguarda o resultado e exibe
            System.out.println("Aguardando resultado via Multicast...");
            byte[] bufferMulticast = new byte[1024];
            DatagramPacket pacoteFinal = new DatagramPacket(bufferMulticast, bufferMulticast.length);
            ms.receive(pacoteFinal);

            String resultadoFinal = new String(pacoteFinal.getData(), 0, pacoteFinal.getLength()).trim();
            JOptionPane.showMessageDialog(null, resultadoFinal, "Resultado da Eleição", JOptionPane.INFORMATION_MESSAGE);

            if (netIf != null) {
                ms.leaveGroup(group, netIf);
            }
            ms.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro na Urna: " + e.getMessage());
        }
    }

    private static NetworkInterface getActiveNetworkInterface() {
        try {
            NetworkInterface en0 = NetworkInterface.getByName("en0");
            if (en0 != null && en0.isUp()) return en0;

            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isUp() && !iface.isLoopback() && iface.getInetAddresses().hasMoreElements()) {
                    return iface;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }
}