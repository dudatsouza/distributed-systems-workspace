package chat.cliente;

import chat.comum.Mensagem;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * Middleware do lado do cliente.
 * Usa duas threads, como no diagrama do quadro:
 *  - thread de ENVIO: tira mensagens de uma fila e manda ao servidor;
 *  - thread de RECEPÇÃO: fica escutando o que o servidor empurra (push).
 */
public class Conexao {

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final BlockingQueue<String> filaEnvio = new LinkedBlockingQueue<>();
    private volatile boolean ativa = true;

    public Conexao(String host, int porta) throws IOException {
        socket = new Socket(host, porta);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
    }

    public void iniciar(Consumer<Mensagem> aoReceber, Runnable aoDesconectar) {
        Thread envio = new Thread(() -> {
            try {
                while (ativa) {
                    out.println(filaEnvio.take());
                }
            } catch (InterruptedException ignored) { }
        }, "thread-envio");

        Thread recepcao = new Thread(() -> {
            try {
                String linha;
                while ((linha = in.readLine()) != null) {
                    Mensagem m = Mensagem.parse(linha);
                    if (m != null) aoReceber.accept(m);
                }
            } catch (IOException ignored) {
            } finally {
                if (ativa) aoDesconectar.run();
                ativa = false;
                envio.interrupt();
            }
        }, "thread-recepcao");

        envio.setDaemon(true);
        recepcao.setDaemon(true);
        envio.start();
        recepcao.start();
    }

    public void enviar(Mensagem m) {
        filaEnvio.offer(m.serializar());
    }

    public void fechar() {
        ativa = false;
        out.println(new Mensagem("SAIU", "", "", "", "", "").serializar());
        try { socket.close(); } catch (IOException ignored) { }
    }
}
