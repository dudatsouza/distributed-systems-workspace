package pacote;

import java.io.File;
import java.io.IOException;

public class Util {
    public static int PortaRecepcaoDesktop = 6662;
    public static int PortaEnvioDesktop = 6661;
    public static String PathRepDesktop = System.getProperty("user.home") + File.separator + "Dados" + File.separator + "RepositorioDesktop.txt";
    public static DesktopRecepcaoThread desktopRecepcaoThread;
    public static DesktopEnvioThread desktopEnvioThread;
    // Protocolo combinado com o cliente: se receber esta mensagem, o cliente mostra "Em manutenção..."
    public static final String MSG_MANUTENCAO = "#MANUTENCAO#";
    public static volatile boolean emManutencao = false;

    // Serviços implementados: false = aparece cinza no Painel de Controle e o botão não funciona
    public static final boolean SERVICO_DESKTOP_HABILITADO = true;
    public static final boolean SERVICO_WEB_HABILITADO = false;
    public static final boolean SERVICO_TERCEIROS_HABILITADO = false;
    public static final boolean SERVICO_PUBLICIDADE_HABILITADO = false;

    // Garante que a pasta e o arquivo do repositório existem antes das threads usarem
    public static void criarRepositorio() throws IOException {
        File arquivo = new File(PathRepDesktop);
        arquivo.getParentFile().mkdirs();
        arquivo.createNewFile();
    }
}
