package pacote;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// Tabela de emojis do chat.
// O popup insere o CÓDIGO no campo de mensagem (ex.: ":-)") e, ao enviar,
// substituir() troca cada código pelo HTML: entidade Unicode (&#128513;) ou <img src=...> (GIF animado)
public class Emojis {

    // Guia 1 - Carinhas: código -> code point Unicode
    public static final Object[][] CARINHAS = {
        {":-)", 0x1F601}, {":-D", 0x1F602}, {";-)", 0x1F609}, {":-P", 0x1F60B}, {"B-)", 0x1F60E},
        {":-*", 0x1F618}, {":-|", 0x1F610}, {":-O", 0x1F62E}, {":-(", 0x1F61E}, {":'(", 0x1F622},
        {">:)", 0x1F608}, {":pensando:", 0x1F914}, {"<3", 0x1F496}, {"(y)", 0x1F44D}, {"(n)", 0x1F44E},
        {":ok:", 0x1F44C}, {":palmas:", 0x1F44F}, {":fogo:", 0x1F525}, {":festa:", 0x1F389}, {":cafe:", 0x2615},
    };

    // Guia 2 - GIFs animados: código -> arquivo em src/images/gifs (64x64)
    // Fonte: Noto Emoji Animation (Google), licença CC BY 4.0 - https://googlefonts.github.io/noto-emoji-animation/
    public static final String[][] GIFS = {
        {":gif_risada:", "risada.gif"}, {":gif_rolando:", "rolando.gif"}, {":gif_apaixonado:", "apaixonado.gif"},
        {":gif_chorando:", "chorando.gif"}, {":gif_pensando:", "pensando.gif"}, {":gif_comemorando:", "comemorando.gif"},
        {":gif_coracao:", "coracao.gif"}, {":gif_fogo:", "fogo.gif"}, {":gif_festa:", "festa.gif"},
        {":gif_palmas:", "palmas.gif"}, {":gif_tchau:", "tchau.gif"}, {":gif_cem:", "cem.gif"},
    };
    public static final int TAMANHO_GIF = 64;

    private static final Map<String, String> HTML_POR_CODIGO = new LinkedHashMap<>();
    private static final Pattern PADRAO;

    static {
        for (Object[] c : CARINHAS) {
            HTML_POR_CODIGO.put(((String) c[0]).toLowerCase(), "&#" + c[1] + ";");
        }
        for (String[] f : GIFS) {
            HTML_POR_CODIGO.put(f[0].toLowerCase(),
                    "<img src='gifs/" + f[1] + "' width='" + TAMANHO_GIF + "' height='" + TAMANHO_GIF + "' align='middle'>");
        }
        // O texto chega já escapado (< vira &lt;), então os códigos também são procurados escapados.
        // Códigos maiores primeiro e uma única passada, para um emoji não "quebrar" o outro.
        String alternativas = HTML_POR_CODIGO.keySet().stream()
                .map(Util::escaparHtml)
                .sorted(Comparator.comparingInt(String::length).reversed())
                .map(Pattern::quote)
                .collect(Collectors.joining("|"));
        // CASE_INSENSITIVE: no modo Grita o texto vira maiúsculo (":fogo:" -> ":FOGO:")
        PADRAO = Pattern.compile(alternativas, Pattern.CASE_INSENSITIVE);
    }

    public static String texto(int codePoint) {
        return new String(Character.toChars(codePoint));
    }

    // Recebe o texto já escapado e devolve com os códigos trocados pelo HTML do emoji
    public static String substituir(String textoEscapado) {
        Matcher m = PADRAO.matcher(textoEscapado);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String codigo = desescapar(m.group()).toLowerCase();
            m.appendReplacement(sb, Matcher.quoteReplacement(HTML_POR_CODIGO.get(codigo)));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String desescapar(String s) {
        return s.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");
    }
}
