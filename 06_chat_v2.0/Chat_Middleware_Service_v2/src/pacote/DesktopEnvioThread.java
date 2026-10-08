package pacote;

import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.JOptionPane;

// Envia o histórico do repositório para os clientes desktop que pedirem (porta 6661)
public class DesktopEnvioThread implements Runnable {
    // writeUTF aceita no máximo 65535 bytes, então mandamos só as mensagens mais recentes
    private static final int LIMITE_BYTES = 60000;

    private volatile boolean paradaManual = false;
    private ServerSocket emissor;
    private Socket cliente;

    @Override
    public void run() {
        try {
            emissor = new ServerSocket(Util.PortaEnvioDesktop);
            while (!paradaManual) {
                cliente = emissor.accept();
                // Lê o arquivo só depois do accept para o cliente receber o histórico atualizado
                String msgs = lerHistorico();
                ObjectOutputStream output = new ObjectOutputStream(cliente.getOutputStream());
                output.writeUTF(msgs);
                output.close();
                cliente.close();
            }
        } catch (Exception e) {
            if (!paradaManual) {
                JOptionPane.showMessageDialog(null, "Erro em DesktopEnvioThread::run:\n" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String lerHistorico() throws Exception {
        List<String> linhas;
        synchronized (Util.class) {
            linhas = Files.readAllLines(Path.of(Util.PathRepDesktop), StandardCharsets.UTF_8);
        }
        StringBuilder msgs = new StringBuilder();
        int bytes = 0;
        for (int i = linhas.size() - 1; i >= 0; i--) {
            String linha = linhas.get(i);
            bytes += linha.getBytes(StandardCharsets.UTF_8).length;
            if (bytes > LIMITE_BYTES) {
                break;
            }
            msgs.insert(0, linha);
        }
        return msgs.toString();
    }

    public void fecharServidor() {
        paradaManual = true;
        try {
            if ((cliente != null) && (!cliente.isClosed())) {
                cliente.close();
            }
            if ((emissor != null) && (!emissor.isClosed())) {
                emissor.close();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro em DesktopEnvioThread::fecharServidor:\n" + e.getMessage());
        }
    }
}
