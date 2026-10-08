import javax.swing.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorTCP {

    public void execute() {
        // Cria o servidor na porta 3322
        try (ServerSocket server = new ServerSocket(3322)) {
            JOptionPane.showMessageDialog(null, "Servidor iniciado. Aguardando cliente...");

            // Aguarda a conexão do cliente e abre as streams de entrada e saída
            try (
                 Socket client = server.accept();
                 ObjectOutputStream writer = new ObjectOutputStream(client.getOutputStream());
                 ObjectInputStream reader = new ObjectInputStream(client.getInputStream())) {

                // IMPORTANTE: O flush no ObjectOutputStream deve ser feito antes de instanciar o ObjectInputStream
                writer.flush();

                // 1. ENVIO DE MENSAGEM PARA O CLIENTE
                String msg = JOptionPane.showInputDialog("Mensagem a enviar para o cliente");
                msg += "\n\n Seu IP é: " + client.getInetAddress().getHostAddress();
                writer.writeUTF(msg);
                writer.flush(); // Garante o envio imediato da mensagem

                // 2. LEITURA DA MENSAGEM VINDA DO CLIENTE
                String respostaCliente = reader.readUTF();
                JOptionPane.showMessageDialog(null, "Mensagem recebida do cliente: " + respostaCliente);

            }
        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, "Erro no servidor: " + error.getMessage());
        }
    }
}