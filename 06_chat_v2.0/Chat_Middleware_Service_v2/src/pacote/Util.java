package pacote;

import java.io.File;
import java.io.IOException;

public class Util {
    public static int PortaRecepcaoDesktop = 6662;
    public static int PortaEnvioDesktop = 6661;
    public static String PathRepDesktop = System.getProperty("user.home") + File.separator + "Dados" + File.separator + "RepositorioDesktop.txt";
    public static DesktopRecepcaoThread desktopRecepcaoThread;
    public static DesktopEnvioThread desktopEnvioThread;

    // Garante que a pasta e o arquivo do repositório existem antes das threads usarem
    public static void criarRepositorio() throws IOException {
        File arquivo = new File(PathRepDesktop);
        arquivo.getParentFile().mkdirs();
        arquivo.createNewFile();
    }
}
