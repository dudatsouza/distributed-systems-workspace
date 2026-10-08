package pacote;

public class Util {
    public static String nickname = "";
    public static String cor = "";
    public static String avatar = "";          // nome do arquivo em resources/images
    // IP da máquina onde roda o Chat_Middleware_Service ("localhost" = mesmo computador)
    public static String ServidorIP = "localhost";
    public static int PortaRecepcaoDesktop = 6662;   // cliente ENVIA mensagens para esta porta
    public static int PortaEnvioDesktop = 6661;      // cliente RECEBE o histórico desta porta
}
