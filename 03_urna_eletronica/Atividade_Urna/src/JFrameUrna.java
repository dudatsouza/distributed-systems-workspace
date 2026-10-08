import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.net.*;
import java.util.Enumeration;

public class JFrameUrna extends JFrame {

    // Declaração dos componentes
    private JRadioButton rbCandidato1, rbCandidato2, rbCandidato3, rbCandidato4, rbBranco, rbNulo;
    private JButton btnVotar;
    private ButtonGroup buttonGroup;

    public JFrameUrna() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Urna Eletrônica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Painel principal para organizar tudo verticalmente com uma margem
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Selecione o candidato:");
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelPrincipal.add(lblTitulo);
        painelPrincipal.add(Box.createRigidArea(new Dimension(0, 10))); // Espaço entre o título e os botões

        // Inicializa os RadioButtons
        rbCandidato1 = new JRadioButton("Candidato 1");
        rbCandidato2 = new JRadioButton("Candidato 2");
        rbCandidato3 = new JRadioButton("Candidato 3");
        rbCandidato4 = new JRadioButton("Candidato 4");
        rbBranco = new JRadioButton("Branco");
        rbNulo = new JRadioButton("Nulo");

        // Agrupa os botões para que apenas um seja selecionado por vez
        buttonGroup = new ButtonGroup();
        buttonGroup.add(rbCandidato1);
        buttonGroup.add(rbCandidato2);
        buttonGroup.add(rbCandidato3);
        buttonGroup.add(rbCandidato4);
        buttonGroup.add(rbBranco);
        buttonGroup.add(rbNulo);

        // Cria um painel com GridLayout: 3 linhas, 2 colunas, e espaçamento entre eles
        JPanel painelGrade = new JPanel(new GridLayout(3, 2, 20, 10));

        // No GridLayout, os itens são adicionados linha por linha (Esquerda -> Direita)
        // Linha 1
        painelGrade.add(rbCandidato1);
        painelGrade.add(rbCandidato3);
        // Linha 2
        painelGrade.add(rbCandidato2);
        painelGrade.add(rbCandidato4);
        // Linha 3
        painelGrade.add(rbBranco);
        painelGrade.add(rbNulo);

        painelPrincipal.add(painelGrade);
        painelPrincipal.add(Box.createRigidArea(new Dimension(0, 15))); // Espaço antes do botão de votar

        // Configura o botão de votar
        btnVotar = new JButton("VOTAR E AGUARDAR APURAÇÃO");
        btnVotar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVotar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVotarActionPerformed(evt);
            }
        });
        painelPrincipal.add(btnVotar);

        setContentPane(painelPrincipal); // Define o painel principal na janela
        pack(); // Ajusta o tamanho da janela automaticamente
        setLocationRelativeTo(null); // Centraliza a janela
    }

    private void btnVotarActionPerformed(ActionEvent evt) {
        final String input;

        // 1. Verifica qual rádio foi selecionado
        if (rbCandidato1.isSelected()) input = "1";
        else if (rbCandidato2.isSelected()) input = "2";
        else if (rbCandidato3.isSelected()) input = "3";
        else if (rbCandidato4.isSelected()) input = "4";
        else if (rbBranco.isSelected()) input = "5";
        else if (rbNulo.isSelected()) input = "6";
        else input = "";

        // Se nada foi selecionado, não faz nada
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione uma opção de voto!");
            return;
        }

        // Desabilita o botão na hora para o usuário ver que clicou
        btnVotar.setEnabled(false);
        btnVotar.setText("Aguardando Central...");

        // Cria uma nova Thread para não travar a interface gráfica
        new Thread(() -> {
            try {
                // 2. Lógica de Sockets (Rodando em segundo plano)
                String IP_CENTRAL = "127.0.0.1"; // ou "localhost"
                int PORTA_CENTRAL_TCP = 7070;
                int MULTICAST_PORT = 6668;

                DatagramSocket udpSocketLocal = new DatagramSocket();
                int portaUdpLocal = udpSocketLocal.getLocalPort();

                // Envia o voto via TCP
                Socket tcpSocket = new Socket(IP_CENTRAL, PORTA_CENTRAL_TCP);
                PrintWriter out = new PrintWriter(tcpSocket.getOutputStream(), true);
                out.println(input + ";" + portaUdpLocal);
                tcpSocket.close();

                // Recebe o IP multicast via UDP Unicast
                byte[] bufferUdp = new byte[256];
                DatagramPacket pacoteRecebido = new DatagramPacket(bufferUdp, bufferUdp.length);
                udpSocketLocal.receive(pacoteRecebido);
                String ipMulticast = new String(pacoteRecebido.getData(), 0, pacoteRecebido.getLength()).trim();
                udpSocketLocal.close();

                // Entra no grupo Multicast
                InetAddress addr = InetAddress.getByName(ipMulticast);
                InetSocketAddress group = new InetSocketAddress(addr, MULTICAST_PORT);
                NetworkInterface netIf = getActiveNetworkInterface();

                MulticastSocket ms = new MulticastSocket(MULTICAST_PORT);
                if (netIf != null) {
                    ms.setNetworkInterface(netIf);
                    ms.joinGroup(group, netIf);
                } else {
                    ms.joinGroup(addr);
                }

                // Aguarda o resultado (Fica travado aqui, mas na Thread secundária, então a tela não trava)
                byte[] bufferMulticast = new byte[1024];
                DatagramPacket pacoteFinal = new DatagramPacket(bufferMulticast, bufferMulticast.length);
                ms.receive(pacoteFinal);
                String resultadoFinal = new String(pacoteFinal.getData(), 0, pacoteFinal.getLength()).trim();

                if (netIf != null) {
                    ms.leaveGroup(group, netIf);
                }
                ms.close();

                // 3. Transição de Telas
                // Como vamos mexer na interface gráfica, precisamos voltar para a Thread principal do Swing
                SwingUtilities.invokeLater(() -> {
                    JFrameUrna.this.dispose(); // Fecha a tela atual da urna
                    JFrameApuracao instancia = new JFrameApuracao(resultadoFinal);
                    instancia.setVisible(true);
                });

            } catch (Exception e) {
                e.printStackTrace();
                // Caso dê erro, reativa o botão na Thread principal
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(JFrameUrna.this, "Erro de conexão: " + e.getMessage());
                    btnVotar.setEnabled(true);
                    btnVotar.setText("VOTAR E AGUARDAR APURAÇÃO");
                });
            }
        }).start(); // Inicia a Thread
    }

    // Método auxiliar mantido do código original para lidar com a interface de rede
    private NetworkInterface getActiveNetworkInterface() {
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