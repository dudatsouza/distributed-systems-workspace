import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class CentralVotacao {
    private static final int PORTA_TCP = 7070;
    private static final String MULTICAST_IP = "239.0.0.10";
    private static final int MULTICAST_PORT = 6668;

    // Mudei para 4 votos de limite apenas para facilitar seu teste, altere como quiser
    private static final int TOTAL_VOTOS_LIMITE = 3;

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");

        Map<Integer, Integer> votos = new HashMap<>();
        votos.put(1, 0); // Candidato 1
        votos.put(2, 0); // Candidato 2
        votos.put(3, 0); // Candidato 3
        votos.put(4, 0); // Candidato 4
        votos.put(5, 0); // Branco
        votos.put(6, 0); // Nulo

        int totalVotos = 0;

        try (ServerSocket serverSocket = new ServerSocket(PORTA_TCP);
             DatagramSocket udpSocket = new DatagramSocket()) {

            System.out.println("Central iniciada na porta TCP " + PORTA_TCP);

            while (totalVotos < TOTAL_VOTOS_LIMITE) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String msg = in.readLine();
                if (msg != null) {
                    String[] partes = msg.split(";");
                    int voto = Integer.parseInt(partes[0].trim());
                    int portaUdpUrna = Integer.parseInt(partes[1].trim());

                    if (votos.containsKey(voto)) {
                        votos.put(voto, votos.get(voto) + 1);
                        totalVotos++;
                        System.out.println("Voto computado: " + voto + " | Total: " + totalVotos + "/" + TOTAL_VOTOS_LIMITE);
                    }

                    // Envia via UDP Unicast o IP de Multicast de volta para a urna
                    InetAddress ipUrna = clientSocket.getInetAddress();
                    byte[] dadosUdp = MULTICAST_IP.getBytes();
                    DatagramPacket pacoteUdp = new DatagramPacket(dadosUdp, dadosUdp.length, ipUrna, portaUdpUrna);
                    udpSocket.send(pacoteUdp);
                }
                clientSocket.close();
            }

            // --- LÓGICA NOVA: Cálculo de porcentagens ---
            double p1 = totalVotos > 0 ? (votos.get(1) * 100.0) / totalVotos : 0;
            double p2 = totalVotos > 0 ? (votos.get(2) * 100.0) / totalVotos : 0;
            double p3 = totalVotos > 0 ? (votos.get(3) * 100.0) / totalVotos : 0;
            double p4 = totalVotos > 0 ? (votos.get(4) * 100.0) / totalVotos : 0;
            double pBranco = totalVotos > 0 ? (votos.get(5) * 100.0) / totalVotos : 0;
            double pNulo = totalVotos > 0 ? (votos.get(6) * 100.0) / totalVotos : 0;

            // Monta o resultado final formatado igual ao quadro
            String resultado = String.format(
                    "RESULTADO\n\n" +
                            "Candidato 1: %d votos \t(%.0f%%)\n" +
                            "Candidato 2: %d votos \t(%.0f%%)\n" +
                            "Candidato 3: %d votos \t(%.0f%%)\n" +
                            "Candidato 4: %d votos \t(%.0f%%)\n" +
                            "Brancos    : %d votos \t(%.0f%%)\n" +
                            "Nulos      : %d votos \t(%.0f%%)\n",
                    votos.get(1), p1,
                    votos.get(2), p2,
                    votos.get(3), p3,
                    votos.get(4), p4,
                    votos.get(5), pBranco,
                    votos.get(6), pNulo
            );

            // Aguarda 1 segundo para garantir que a última urna entrou no grupo multicast
            Thread.sleep(1000);

            // Envia via Multicast
            InetAddress grupoMulticast = InetAddress.getByName(MULTICAST_IP);
            byte[] bufferResultado = resultado.getBytes();
            DatagramPacket pacoteMulticast = new DatagramPacket(bufferResultado, bufferResultado.length, grupoMulticast, MULTICAST_PORT);

            // Dispara algumas vezes para garantir a entrega via UDP
            for (int i = 0; i < 3; i++) {
                udpSocket.send(pacoteMulticast);
                Thread.sleep(200);
            }

            System.out.println("Resultado enviado via Multicast com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}