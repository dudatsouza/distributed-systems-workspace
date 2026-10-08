package chat.servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Middleware / servidor do chat.
 * Aceita conexões, cria uma thread por cliente e faz o PUSH
 * de cada mensagem recebida para todos os clientes conectados.
 */
public class Servidor {

    public static final int PORTA = 5000;

    private final List<ClienteHandler> clientes = new CopyOnWriteArrayList<>();
    private final Repositorio repositorio = new Repositorio("historico.txt");

    public static void main(String[] args) {
        new Servidor().iniciar();
    }

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("Servidor do chat rodando na porta " + PORTA);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nova conexão: " + socket.getInetAddress());
                ClienteHandler handler = new ClienteHandler(socket, this);
                clientes.add(handler);
                new Thread(handler, "cliente-" + socket.getPort()).start();
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }

    /** PUSH: envia a linha para todos os clientes conectados. */
    public void broadcast(String linha) {
        for (ClienteHandler c : clientes) {
            c.enviar(linha);
        }
    }

    public void remover(ClienteHandler handler) {
        clientes.remove(handler);
    }

    public Repositorio getRepositorio() {
        return repositorio;
    }
}
