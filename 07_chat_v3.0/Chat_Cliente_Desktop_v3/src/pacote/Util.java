package pacote;

public class Util {
    public static String nickname = "";
    public static String cor = "";             // cor em hexadecimal (o Swing só conhece poucas cores por nome)
    public static String avatar = "";          // nome do arquivo em src/images
    // IP da máquina onde roda o Chat_Middleware_Service ("localhost" = mesmo computador)
    public static String ServidorIP = "localhost";
    public static int PortaRecepcaoDesktop = 6662;   // cliente ENVIA mensagens para esta porta
    public static int PortaEnvioDesktop = 6661;      // cliente RECEBE o histórico desta porta
    // Protocolo combinado com o middleware: resposta enviada quando o Serviço Desktop está desativado
    public static final String MSG_MANUTENCAO = "#MANUTENCAO#";
    public static final int TAMANHO_MAX_NICK = 20;
    public static final int TAMANHO_MAX_MENSAGEM = 500;

    // Impede que o texto digitado vire HTML (ex.: alguém digitar <font size=100>)
    public static String escaparHtml(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
