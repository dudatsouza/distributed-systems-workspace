package chat.comum;

/**
 * Mensagem trocada entre cliente e servidor.
 * Protocolo em texto, uma mensagem por linha:
 *   TIPO|NICK|COR|AVATAR|MODO|TEXTO
 * TIPO: MSG (mensagem de chat), ENTROU, SAIU
 */
public class Mensagem {

    public static final String SEP = "|";

    public final String tipo;
    public final String nick;
    public final String cor;     // AZUL, PRETO, VERMELHO
    public final String avatar;  // caractere do avatar
    public final String modo;    // FALA, GRITA, SUSSURRA
    public final String texto;

    public Mensagem(String tipo, String nick, String cor, String avatar, String modo, String texto) {
        this.tipo = tipo;
        this.nick = limpar(nick);
        this.cor = cor;
        this.avatar = avatar;
        this.modo = modo;
        this.texto = texto == null ? "" : texto.replace("\n", " ").replace("\r", " ");
    }

    public String serializar() {
        return String.join(SEP, tipo, nick, cor, avatar, modo, texto);
    }

    /** Converte uma linha recebida em Mensagem. Retorna null se a linha for inválida. */
    public static Mensagem parse(String linha) {
        if (linha == null) return null;
        String[] p = linha.split("\\|", 6); // limite 6: o texto pode conter '|'
        if (p.length < 6) return null;
        return new Mensagem(p[0], p[1], p[2], p[3], p[4], p[5]);
    }

    /** Remove o separador do protocolo de campos curtos (nick). */
    private static String limpar(String s) {
        return s == null ? "" : s.replace(SEP, "").trim();
    }
}
