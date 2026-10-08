package pacote;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// Tela do chat: conversa (PULL a cada 1s) + barra de envio com modo, emojis e mensagem
public class FrmChat extends javax.swing.JFrame {

    // Modos da mensagem: {nome, cor do botão}
    private static final String[][] MODOS = {
        {"Fala", "#0F766E"}, {"Grita", "#FF6347"}, {"Xinga", "#CA8A04"},
    };

    private final PainelConversa painelConversa = new PainelConversa();
    private JScrollPane scrollConversa;
    private final Estilo.CampoTexto txtMensagem = new Estilo.CampoTexto("Digite uma mensagem...");
    private final Estilo.Botao btnEnviar = new Estilo.Botao("Enviar", Estilo.PRINCIPAL);
    private final Estilo.Botao btnEmoji = new Estilo.Botao("😀", Estilo.FUNDO);
    private final JLabel lblContador = new JLabel();
    private final StatusConexao lblStatus = new StatusConexao();
    private final PopupEmojis popupEmojis;

    private String modo = MODOS[0][0];
    private volatile String ultimoHistorico = null;   // null = nada exibido ainda (mostra o aviso de conversa vazia)
    private volatile boolean emManutencao = false;
    public String msg = "";

    public FrmChat() {
        setTitle("Chat - " + Util.nickname);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(520, 480));

        JPanel conteudo = new JPanel(new BorderLayout());
        conteudo.setBackground(Estilo.FUNDO);
        conteudo.add(criarCabecalho(), BorderLayout.NORTH);
        conteudo.add(criarConversa(), BorderLayout.CENTER);
        conteudo.add(criarBarraEnvio(), BorderLayout.SOUTH);
        setContentPane(conteudo);

        popupEmojis = new PopupEmojis(this::inserirEmoji);
        setSize(660, 620);
        setLocationRelativeTo(null);
        iniciarRecepcao();
    }

    // ---------------------------------------------------------------- tela

    private JPanel criarCabecalho() {
        JPanel cabecalho = Estilo.cabecalho("Chat Desktop",
                "Você é " + Util.nickname + "  ·  servidor " + Util.ServidorIP);
        ((BorderLayout) cabecalho.getLayout()).setHgap(12);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        Image avatar = new ImageIcon(new ImageIcon(FrmChat.class.getResource("/images/" + Util.avatar))
                .getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)).getImage();
        cabecalho.add(new JLabel(new ImageIcon(avatar)), BorderLayout.WEST);
        JPanel direita = new JPanel(new java.awt.GridBagLayout());
        direita.setOpaque(false);
        direita.add(lblStatus);
        cabecalho.add(direita, BorderLayout.EAST);
        return cabecalho;
    }

    private JPanel criarConversa() {
        scrollConversa = new JScrollPane(painelConversa);
        scrollConversa.setBorder(BorderFactory.createEmptyBorder());
        scrollConversa.getViewport().setBackground(painelConversa.getBackground());
        scrollConversa.getVerticalScrollBar().setUnitIncrement(16);

        Estilo.Cartao cartao = new Estilo.Cartao();
        cartao.setLayout(new BorderLayout());
        cartao.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        cartao.add(scrollConversa);

        JPanel margem = new JPanel(new BorderLayout());
        margem.setOpaque(false);
        margem.setBorder(BorderFactory.createEmptyBorder(14, 14, 10, 14));
        margem.add(cartao);
        return margem;
    }

    private JPanel criarBarraEnvio() {
        JPanel barra = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Estilo.BORDA);
                g.drawLine(0, 0, getWidth(), 0);
            }
        };
        barra.setBorder(BorderFactory.createEmptyBorder(12, 14, 14, 14));

        // Linha 1: modo da mensagem + contador de caracteres
        JPanel linhaModo = new JPanel(new BorderLayout());
        linhaModo.setOpaque(false);
        JPanel modos = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        modos.setOpaque(false);
        JLabel lblModo = Estilo.rotulo("Modo");
        lblModo.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 10));
        modos.add(lblModo);
        JPanel botoesModo = new JPanel(new GridLayout(1, MODOS.length, 6, 0));
        botoesModo.setOpaque(false);
        ButtonGroup grupo = new ButtonGroup();
        for (String[] m : MODOS) {
            Estilo.Opcao opcao = new Estilo.Opcao(m[0], Color.decode(m[1]));
            opcao.setPreferredSize(new Dimension(86, 30));
            opcao.setFont(Estilo.fonte(Font.BOLD, 12f));
            opcao.setSelected(m[0].equals(modo));
            opcao.addActionListener(e -> {
                modo = m[0];
                txtMensagem.requestFocusInWindow();
            });
            grupo.add(opcao);
            botoesModo.add(opcao);
        }
        modos.add(botoesModo);
        linhaModo.add(modos, BorderLayout.WEST);
        lblContador.setFont(Estilo.fonte(Font.PLAIN, 11f));
        lblContador.setHorizontalAlignment(JLabel.RIGHT);
        // largura fixa: o rótulo não "pula" nem corta quando o número cresce
        lblContador.setPreferredSize(new Dimension(lblContador.getFontMetrics(lblContador.getFont())
                .stringWidth("000/" + Util.TAMANHO_MAX_MENSAGEM) + 4, 20));
        linhaModo.add(lblContador, BorderLayout.EAST);
        barra.add(linhaModo, BorderLayout.NORTH);

        // Linha 2: [emoji] [mensagem............] [Enviar]
        JPanel linhaMensagem = new JPanel(new BorderLayout(8, 0));
        linhaMensagem.setOpaque(false);
        btnEmoji.setPreferredSize(new Dimension(40, 38));
        btnEmoji.setFont(Estilo.fonte(Font.PLAIN, 18f));
        btnEmoji.setToolTipText("Emojis");
        btnEmoji.addActionListener(e -> popupEmojis.show(btnEmoji, 0, -popupEmojis.getPreferredSize().height - 4));
        btnEnviar.setPreferredSize(new Dimension(100, 38));
        btnEnviar.addActionListener(e -> gerarEenviarMensagem());
        txtMensagem.addActionListener(e -> gerarEenviarMensagem());   // Enter envia
        txtMensagem.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarContador();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarContador();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarContador();
            }
        });
        linhaMensagem.add(btnEmoji, BorderLayout.WEST);
        linhaMensagem.add(txtMensagem, BorderLayout.CENTER);
        linhaMensagem.add(btnEnviar, BorderLayout.EAST);
        barra.add(linhaMensagem, BorderLayout.CENTER);

        atualizarContador();
        return barra;
    }

    private void atualizarContador() {
        int n = txtMensagem.getText().length();
        boolean passou = n > Util.TAMANHO_MAX_MENSAGEM;
        lblContador.setText(n + "/" + Util.TAMANHO_MAX_MENSAGEM);
        lblContador.setForeground(passou ? Estilo.VERMELHO : Estilo.TEXTO_DESABILITADO);
        txtMensagem.setErro(passou);
    }

    // Chamado pelo popup: coloca o código do emoji onde está o cursor
    private void inserirEmoji(String codigo) {
        txtMensagem.replaceSelection(codigo + " ");
        txtMensagem.requestFocusInWindow();
    }

    // ---------------------------------------------------------------- envio

    public void gerarEenviarMensagem() {
        if (emManutencao) {
            return;
        }
        String texto = txtMensagem.getText().trim();
        if (texto.isEmpty() || texto.length() > Util.TAMANHO_MAX_MENSAGEM) {
            return;   // vazio não envia; longo demais já aparece em vermelho no contador
        }
        if (!modo.equals("Fala")) {
            texto = texto.toUpperCase();
        }
        // Escapa o HTML digitado e depois troca os códigos (":-)", ":gif_fogo:"...) pelos emojis/GIFs
        texto = Emojis.substituir(Util.escaparHtml(texto));

        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        // HTML "normal" da mensagem (é o que clientes antigos/de colegas exibem).
        // As imagens vão com caminho relativo; cada cliente resolve pela própria pasta images
        this.msg = "";
        this.msg += "<font color='#94A3B8' size='-1'>" + hora + "</font> ";
        this.msg += "<img src='" + Util.avatar + "' width='22' height='22'>";
        this.msg += "<font color='" + Util.cor + "'><b> " + Util.escaparHtml(Util.nickname) + " </b></font>";
        if (modo.equals("Fala")) {
            this.msg += "<b> Fala: </b>";
            this.msg += texto;
        } else if (modo.equals("Grita")) {
            this.msg += "<b><i><u> Grita: </u></i></b>";
            // "Tomato" por nome não existe no Swing (saía preto), por isso o hexadecimal
            this.msg += "<font color='#FF6347' size='+1'>" + texto + "</font>";
        } else if (modo.equals("Xinga")) {
            this.msg += "<b> Xinga: </b>";
            // Amarelo puro some no fundo branco: amarelo sobre faixa preta
            this.msg += "<span style='background-color:#000000'><font color='#FFD700' size='+3'>" + texto + "</font></span>";
        }
        this.msg += "<br>";
        // Na frente, os dados da mensagem num comentário HTML (invisível), usados pelo v3 para os balões
        this.msg = PainelConversa.montarLinha(hora, Util.avatar, Util.cor, modo,
                Util.escaparHtml(Util.nickname), texto, this.msg);
        try {
            Socket cliente = new Socket(Util.ServidorIP, Util.PortaRecepcaoDesktop);
            ObjectOutputStream output = new ObjectOutputStream(cliente.getOutputStream());
            output.writeUTF(this.msg);
            output.close();
            cliente.close();
            txtMensagem.setText("");
        } catch (Exception e) {
            // Não conseguiu falar com o middleware: servidor parado
            mostrarManutencao();
        }
    }

    // ---------------------------------------------------------------- recepção

    // Thread de recepção (PULL): a cada 1s pede o histórico ao middleware
    private void iniciarRecepcao() {
        Thread.ofVirtual().start(() -> {
            while (true) {
                try {
                    Socket cliente = new Socket(Util.ServidorIP, Util.PortaEnvioDesktop);
                    ObjectInputStream input = new ObjectInputStream(cliente.getInputStream());
                    String msgs = input.readUTF();
                    input.close();
                    cliente.close();
                    if (msgs.equals(Util.MSG_MANUTENCAO)) {
                        // O middleware avisou que o Serviço Desktop foi desativado
                        SwingUtilities.invokeLater(this::mostrarManutencao);
                    } else if (emManutencao || !msgs.equals(ultimoHistorico)) {
                        SwingUtilities.invokeLater(() -> mostrarConversa(msgs));
                    }
                } catch (Exception e) {
                    // Middleware fechado ou fora da rede: também é manutenção, e o cliente segue tentando
                    SwingUtilities.invokeLater(this::mostrarManutencao);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
    }

    private void mostrarConversa(String msgs) {
        ultimoHistorico = msgs;
        setManutencao(false);
        painelConversa.mostrarHistorico(msgs);
        // rola para a última mensagem depois que o layout for refeito
        SwingUtilities.invokeLater(() -> {
            javax.swing.JScrollBar barra = scrollConversa.getVerticalScrollBar();
            barra.setValue(barra.getMaximum());
        });
    }

    private void mostrarManutencao() {
        if (emManutencao) {
            return;
        }
        setManutencao(true);
        painelConversa.mostrarManutencao();
    }

    private void setManutencao(boolean manutencao) {
        emManutencao = manutencao;
        btnEnviar.setEnabled(!manutencao);
        btnEmoji.setEnabled(!manutencao);
        lblStatus.setOnline(!manutencao);
        setTitle("Chat - " + Util.nickname + (manutencao ? " (em manutenção)" : ""));
    }

    // Etiqueta arredondada no cabeçalho: "● Online" ou "● Em manutenção"
    private static class StatusConexao extends JLabel {
        private boolean online = true;

        StatusConexao() {
            setFont(Estilo.fonte(Font.BOLD, 11f));
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
            // largura fixa pelo texto mais longo, para não cortar ao trocar de estado
            FontMetrics fm = getFontMetrics(getFont());
            setPreferredSize(new Dimension(fm.stringWidth("●  Em manutenção") + 28, fm.getHeight() + 10));
            setOnline(true);
        }

        void setOnline(boolean online) {
            this.online = online;
            setText(online ? "●  Online" : "●  Em manutenção");
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(online ? new Color(0x16A34A) : new Color(0xEA580C));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.setFont(getFont());
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmChat().setVisible(true));
    }
}
