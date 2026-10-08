package chat.servidor;

import chat.comum.Mensagem;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/** Thread do servidor que atende um cliente específico. */
public class ClienteHandler implements Runnable {

    private final Socket socket;
    private final Servidor servidor;
    private PrintWriter out;
    private String nick;

    public ClienteHandler(Socket socket, Servidor servidor) {
        this.socket = socket;
        this.servidor = servidor;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            out = new PrintWriter(new OutputStreamWriter(
                    socket.getOutputStream(), StandardCharsets.UTF_8), true);

            // PULL: o cliente recebe o histórico guardado no repositório
            for (String linha : servidor.getRepositorio().carregarHistorico()) {
                enviar(linha);
            }

            String linha;
            while ((linha = in.readLine()) != null) {
                Mensagem m = Mensagem.parse(linha);
                if (m == null) continue;

                if ("ENTROU".equals(m.tipo)) {
                    nick = m.nick;
                    System.out.println(nick + " entrou");
                } else if ("MSG".equals(m.tipo)) {
                    servidor.getRepositorio().salvar(m.serializar());
                } else if ("SAIU".equals(m.tipo)) {
                    break;
                }
                servidor.broadcast(m.serializar()); // PUSH para todos
            }
        } catch (IOException e) {
            System.out.println("Conexão encerrada: " + e.getMessage());
        } finally {
            servidor.remover(this);
            if (nick != null) {
                System.out.println(nick + " saiu");
                servidor.broadcast(new Mensagem("SAIU", nick, "PRETO", "", "", "").serializar());
            }
            try { socket.close(); } catch (IOException ignored) { }
        }
    }

    public synchronized void enviar(String linha) {
        if (out != null) out.println(linha);
    }
}
