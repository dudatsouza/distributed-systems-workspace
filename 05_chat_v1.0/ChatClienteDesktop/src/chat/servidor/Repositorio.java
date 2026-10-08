package chat.servidor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositório: guarda o histórico das mensagens em arquivo.
 * Novos clientes "puxam" (pull) esse histórico ao entrar.
 */
public class Repositorio {

    private static final int MAX_HISTORICO = 50;
    private final Path arquivo;

    public Repositorio(String nomeArquivo) {
        this.arquivo = Paths.get(nomeArquivo);
    }

    /** Armazena uma linha (mensagem serializada). */
    public synchronized void salvar(String linha) {
        try (BufferedWriter w = Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            w.write(linha);
            w.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao salvar no repositório: " + e.getMessage());
        }
    }

    /** Retorna as últimas mensagens armazenadas. */
    public synchronized List<String> carregarHistorico() {
        if (!Files.exists(arquivo)) return new ArrayList<>();
        try {
            List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
            int inicio = Math.max(0, linhas.size() - MAX_HISTORICO);
            return new ArrayList<>(linhas.subList(inicio, linhas.size()));
        } catch (IOException e) {
            System.err.println("Erro ao ler o repositório: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
