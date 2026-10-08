package pacote;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

// Conversa em balões (estilo WhatsApp).
// Cada mensagem do cliente v3 começa com um comentário HTML com os dados:
//   <!--chat3|hora|avatar|cor|modo|nick|texto-->  + o HTML "normal" da mensagem
// Comentários são invisíveis em HTML, então clientes antigos (ou de colegas) continuam vendo o HTML normal,
// e o v3 usa os dados para desenhar os balões. Mensagens sem o comentário viram um balão neutro.
public class PainelConversa extends JPanel implements Scrollable {

    public static final String PREFIXO = "chat3";
    private static final Pattern DADOS = Pattern.compile("^\\s*<!--" + PREFIXO + "\\|(.*?)-->", Pattern.DOTALL);
    private static final int LARGURA_MAX_TEXTO = 340;
    private static final int TAMANHO_AVATAR = 34;

    private static final Color BOLHA_MINHA = new Color(0xD1FAE5);
    private static final Color BOLHA_OUTROS = Color.WHITE;
    private static final Color BOLHA_XINGA = new Color(0x1E293B);
    private static final Color BOLHA_ANTIGA = new Color(0xF1F5F9);
    private static final Color COR_GRITA = new Color(0xE5533D);
    private static final Color COR_XINGA = new Color(0xFACC15);

    private final URL baseImagens = PainelConversa.class.getResource("/images/");
    private final Map<String, ImageIcon> cacheAvatares = new HashMap<>();

    // Uma mensagem já separada em campos
    record Mensagem(String hora, String avatar, String cor, String modo, String nick, String texto) {
    }

    public PainelConversa() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0xF8FAFC));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    }

    // ------------------------------------------------------------ protocolo

    // Monta a linha enviada ao middleware: comentário com os dados + HTML normal (para outros clientes)
    public static String montarLinha(String hora, String avatar, String cor, String modo, String nick,
                                     String textoHtml, String htmlNormal) {
        return "<!--" + PREFIXO + "|" + hora + "|" + avatar + "|" + cor + "|" + modo + "|"
                + nick.replace("|", "&#124;") + "|" + textoHtml + "-->" + htmlNormal;
    }

    static Mensagem ler(String linha) {
        Matcher m = DADOS.matcher(linha);
        if (!m.find()) {
            return null;
        }
        String[] c = m.group(1).split("\\|", 6);   // o texto (último campo) pode conter "|"
        if (c.length < 6) {
            return null;
        }
        return new Mensagem(c[0], c[1], c[2], c[3], c[4], c[5]);
    }

    // O middleware v3 separa as mensagens com "\n"; outros middlewares só com "<br>"
    static List<String> separar(String historico) {
        List<String> linhas = new ArrayList<>();
        String[] partes = historico.contains("\n") ? historico.split("\n") : historico.split("(?<=<br>)");
        for (String p : partes) {
            if (!p.isBlank()) {
                linhas.add(p.trim());
            }
        }
        return linhas;
    }

    // ------------------------------------------------------------ telas

    public void mostrarHistorico(String historico) {
        removeAll();
        List<String> linhas = separar(historico);
        if (linhas.isEmpty()) {
            add(aviso("&#128172;", "Nenhuma mensagem ainda", "Diga oi! &#128075;", Estilo.TEXTO_SECUNDARIO));
        }
        for (String linha : linhas) {
            Mensagem msg = ler(linha);
            add(msg != null ? linhaBalao(msg) : linhaAntiga(linha));
            add(Box.createVerticalStrut(8));
        }
        add(Box.createVerticalGlue());
        revalidate();
        repaint();
    }

    public void mostrarManutencao() {
        removeAll();
        add(aviso("&#128736;", "Em manutenção...",
                "O servidor de chat está temporariamente fora do ar.<br>A conversa volta sozinha assim que ele for reativado.",
                new Color(0xC2410C)));
        revalidate();
        repaint();
    }

    private JPanel aviso(String icone, String titulo, String texto, Color cor) {
        JLabel lbl = new JLabel("<html><div style='text-align:center'>"
                + "<font size='+4'>" + icone + "</font><br><br>"
                + "<font size='+1' color='" + hex(cor) + "'><b>" + titulo + "</b></font><br><br>"
                + "<font color='#64748B'>" + texto + "</font></div></html>", SwingConstants.CENTER);
        lbl.setFont(Estilo.fonte(Font.PLAIN, 13f));
        JPanel painel = new JPanel(new BorderLayout());
        painel.setOpaque(false);
        painel.setBorder(BorderFactory.createEmptyBorder(60, 10, 10, 10));
        painel.add(lbl, BorderLayout.NORTH);
        return painel;
    }

    // ------------------------------------------------------------ balões

    private JPanel linhaBalao(Mensagem m) {
        boolean minha = m.nick().equals(Util.escaparHtml(Util.nickname)) && m.avatar().equals(Util.avatar);
        boolean xinga = m.modo().equals("Xinga");
        Color corNick = corSegura(m.cor(), xinga);

        Balao balao = new Balao(xinga ? BOLHA_XINGA : (minha ? BOLHA_MINHA : BOLHA_OUTROS), !minha && !xinga);
        balao.setLayout(new BoxLayout(balao, BoxLayout.Y_AXIS));

        // cabeçalho: nome (só nos balões dos outros) + modo quando não é "Fala"
        String cabecalho = minha ? "" : "<b><font color='" + hex(corNick) + "'>" + m.nick() + "</font></b>";
        if (m.modo().equals("Grita")) {
            cabecalho += (cabecalho.isEmpty() ? "" : "  ") + "<font color='" + hex(COR_GRITA) + "'><b>&#128226; grita</b></font>";
        } else if (xinga) {
            cabecalho += (cabecalho.isEmpty() ? "" : "  ") + "<font color='" + hex(COR_XINGA) + "'><b>&#129324; xinga</b></font>";
        }
        if (!cabecalho.isEmpty()) {
            JLabel lblCabecalho = new JLabel("<html>" + cabecalho + "</html>");
            lblCabecalho.setFont(Estilo.fonte(Font.PLAIN, 12f));
            lblCabecalho.setAlignmentX(LEFT_ALIGNMENT);
            balao.add(lblCabecalho);
            balao.add(Box.createVerticalStrut(3));
        }

        // texto: cada modo com seu estilo
        JLabel lblTexto;
        if (m.modo().equals("Grita")) {
            lblTexto = texto(m.texto(), Font.BOLD, 17f, COR_GRITA);
        } else if (xinga) {
            lblTexto = texto(m.texto(), Font.BOLD, 18f, COR_XINGA);
        } else {
            lblTexto = texto(m.texto(), Font.PLAIN, 14f, Estilo.TEXTO);
        }
        lblTexto.setAlignmentX(LEFT_ALIGNMENT);
        balao.add(lblTexto);

        // hora no canto
        JLabel lblHora = new JLabel(m.hora());
        lblHora.setFont(Estilo.fonte(Font.PLAIN, 10f));
        lblHora.setForeground(xinga ? new Color(0x94A3B8) : Estilo.TEXTO_DESABILITADO);
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rodape.setOpaque(false);
        rodape.add(lblHora);
        rodape.setAlignmentX(LEFT_ALIGNMENT);
        balao.add(Box.createVerticalStrut(2));
        balao.add(rodape);

        JPanel conteudo = new JPanel(new FlowLayout(minha ? FlowLayout.RIGHT : FlowLayout.LEFT, 8, 0));
        conteudo.setOpaque(false);
        if (!minha) {
            JLabel lblAvatar = new JLabel(avatar(m.avatar()));
            lblAvatar.setVerticalAlignment(SwingConstants.TOP);
            conteudo.add(lblAvatar);
        }
        conteudo.add(balao);
        return linha(conteudo);
    }

    // Mensagem antiga / de outro cliente: mostra o HTML original num balão neutro
    private JPanel linhaAntiga(String html) {
        Balao balao = new Balao(BOLHA_ANTIGA, true);
        balao.setLayout(new BorderLayout());
        String limpo = html.replaceAll("(?i)<br>\\s*$", "");
        balao.add(texto(limpo, Font.PLAIN, 13f, Estilo.TEXTO));
        JPanel conteudo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        conteudo.setOpaque(false);
        conteudo.add(Box.createHorizontalStrut(TAMANHO_AVATAR));
        conteudo.add(balao);
        return linha(conteudo);
    }

    // Linha que ocupa toda a largura, mas só a altura necessária
    private JPanel linha(JPanel conteudo) {
        JPanel linha = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        linha.setOpaque(false);
        linha.setAlignmentX(LEFT_ALIGNMENT);
        linha.add(conteudo);
        return linha;
    }

    // JLabel com HTML; quebra linha só quando o texto passa da largura máxima
    private JLabel texto(String html, int estilo, float tamanho, Color cor) {
        String corpo = absolutizarImagens(html);
        JLabel lbl = new JLabel("<html>" + corpo + "</html>");
        lbl.setFont(Estilo.fonte(estilo, tamanho));
        lbl.setForeground(cor);
        if (lbl.getPreferredSize().width > LARGURA_MAX_TEXTO) {
            lbl.setText("<html><div style='width:" + LARGURA_MAX_TEXTO + "px'>" + corpo + "</div></html>");
        }
        return lbl;
    }

    // <img src='gifs/fogo.gif'> -> caminho completo (JLabel não tem "base" como o JEditorPane)
    private String absolutizarImagens(String html) {
        return html.replaceAll("src='(?![a-zA-Z]+:)([^']+)'", "src='" + Matcher.quoteReplacement(baseImagens.toString()) + "$1'");
    }

    private ImageIcon avatar(String arquivo) {
        return cacheAvatares.computeIfAbsent(arquivo, a -> {
            URL url = PainelConversa.class.getResource("/images/" + a);
            if (url == null) {
                url = PainelConversa.class.getResource("/images/avatar-homem-1.png");
            }
            Image img = new ImageIcon(url).getImage().getScaledInstance(TAMANHO_AVATAR, TAMANHO_AVATAR, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        });
    }

    // Cor do nick legível: no balão escuro do "Xinga" clareia a cor
    private static Color corSegura(String hex, boolean fundoEscuro) {
        Color c;
        try {
            c = Color.decode(hex);
        } catch (Exception e) {
            c = Estilo.TEXTO;
        }
        if (!fundoEscuro) {
            return c;
        }
        return new Color((c.getRed() + 255 * 2) / 3, (c.getGreen() + 255 * 2) / 3, (c.getBlue() + 255 * 2) / 3);
    }

    private static String hex(Color c) {
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    // Balão: retângulo bem arredondado, com borda leve nos balões claros
    private static class Balao extends JPanel {
        private final Color fundo;
        private final boolean borda;

        Balao(Color fundo, boolean borda) {
            this.fundo = fundo;
            this.borda = borda;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 6, 12));
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fundo);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            if (borda) {
                g2.setColor(Estilo.BORDA);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ------------------------------------------------------------ Scrollable: acompanha a largura da janela

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle r, int orientacao, int direcao) {
        return 16;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle r, int orientacao, int direcao) {
        return r.height - 32;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        Component pai = getParent();
        return pai != null && pai.getHeight() > getPreferredSize().height;
    }
}
