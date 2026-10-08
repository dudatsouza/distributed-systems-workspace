import javax.swing.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientTCP {

    public void execute() {
        // Conecta ao servidor na porta 3322 e abre as streams
        try (
             Socket client = new Socket("127.0.0.1", 3322);
             ObjectOutputStream writer = new ObjectOutputStream(client.getOutputStream());
             ObjectInputStream reader = new ObjectInputStream(client.getInputStream())) {

            // IMPORTANTE: O flush no ObjectOutputStream deve ser feito antes
            writer.flush();

            // 1. LEITURA DA MENSAGEM RECEBIDA DO SERVIDOR
            String mensagemServidor = reader.readUTF();
            JOptionPane.showMessageDialog(null, "Mensagem recebida no cliente: " + mensagemServidor);

            // 2. ENVIO DE MENSAGEM PARA O SERVIDOR
            String msg = JOptionPane.showInputDialog("Mensagem a enviar para o servidor");
            msg += "\n\n Meu IP é: " + client.getLocalAddress().getHostAddress();
            writer.writeUTF(msg);
            writer.flush(); // Garante o envio imediato

        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, "Erro no cliente: " + error.getMessage());
        }
    }
}