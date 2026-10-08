package chat.cliente;

import chat.comum.Mensagem;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** Tela do chat: mensagens, campo MSG, Modo, Emoji e botão Envia. */
public class ChatFrame extends JFrame {

    private static final String[] MODOS = {"Fala", "Grita", "Sussurra"};
    private static final String[] EMOJIS = {"", "♥", "☺", "☹", "★", "♪", "☀"};

    private final String nick, cor, avatar;
    private final Conexao conexao;

    private final JTextPane areaMensagens = new JTextPane();
    private final JTextField campoMsg = new JTextField(25);
    private final JComboBox<String> comboModo = new JComboBox<>(MODOS);
    private final JComboBox<String> comboEmoji = new JComboBox<>(EMOJIS);

    public ChatFrame(String nick, String cor, String avatar, Conexao conexao) {
        super("Chat - " + nick);
        this.nick = nick;
        this.cor = cor;
        this.avatar = avatar;
        this.conexao = conexao;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);

        areaMensagens.setEditable(false);
        areaMensagens.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        add(new JScrollPane(areaMensagens), BorderLayout.CENTER);

        // Painel inferior: MSG / Emoji / Envia  e  Modo
        JPanel inferior = new JPanel(new GridBagLayout());
        inferior.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0;
        inferior.add(new JLabel("MSG:"), c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;
        inferior.add(campoMsg, c);
        c.fill = GridBagConstraints.NONE; c.weightx = 0;
        c.gridx = 2;
        inferior.add(new JLabel("Emoji:"), c);
        c.gridx = 3;
        comboEmoji.setFont(comboEmoji.getFont().deriveFont(16f));
        inferior.add(comboEmoji, c);
        JButton envia = new JButton("Envia");
        c.gridx = 4;
        inferior.add(envia, c);

        c.gridx = 0; c.gridy = 1;
        inferior.add(new JLabel("Modo:"), c);
        c.gridx = 1;
        inferior.add(comboModo, c);

        add(inferior, BorderLayout.SOUTH);

        envia.addActionListener(e -> enviar());
        campoMsg.addActionListener(e -> enviar()); // Enter também envia

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                conexao.fechar();
                System.exit(0);
            }
        });

        // Liga as threads de envio/recepção e avisa que entrou
        conexao.iniciar(
                m -> SwingUtilities.invokeLater(() -> exibir(m)),
                () -> SwingUtilities.invokeLater(() ->
                        escrever("*** Conexão com o servidor perdida ***\n", Color.GRAY, false, true)));
        conexao.enviar(new Mensagem("ENTROU", nick, cor, avatar, "", ""));
    }

    private void enviar() {
        String texto = campoMsg.getText().trim();
        String emoji = (String) comboEmoji.getSelectedItem();
        if (texto.isEmpty() && (emoji == null || emoji.isEmpty())) return;
        if (emoji != null && !emoji.isEmpty()) texto = texto + " " + emoji;

        String modo = ((String) comboModo.getSelectedItem()).toUpperCase();
        conexao.enviar(new Mensagem("MSG", nick, cor, avatar, modo, texto.trim()));

        campoMsg.setText("");
        comboEmoji.setSelectedIndex(0);
        campoMsg.requestFocus();
    }

    /** Mostra uma mensagem recebida do servidor. */
    private void exibir(Mensagem m) {
        switch (m.tipo) {
            case "ENTROU":
                escrever("*** " + m.nick + " entrou no chat ***\n", Color.GRAY, false, true);
                break;
            case "SAIU":
                escrever("*** " + m.nick + " saiu do chat ***\n", Color.GRAY, false, true);
                break;
            case "MSG":
                Color c = cor(m.cor);
                String verbo, texto = m.texto;
                boolean italico = false;
                switch (m.modo) {
                    case "GRITA":    verbo = "grita";    texto = texto.toUpperCase() + "!"; break;
                    case "SUSSURRA": verbo = "sussurra"; texto = texto.toLowerCase(); italico = true; break;
                    default:         verbo = "diz";
                }
                escrever(m.avatar + " " + m.nick, c, true, false);
                escrever(" " + verbo + ": ", Color.BLACK, true, false);
                escrever(texto + "\n", c, false, italico);
                break;
        }
    }

    private void escrever(String texto, Color cor, boolean negrito, boolean italico) {
        StyledDocument doc = areaMensagens.getStyledDocument();
        SimpleAttributeSet estilo = new SimpleAttributeSet();
        StyleConstants.setForeground(estilo, cor);
        StyleConstants.setBold(estilo, negrito);
        StyleConstants.setItalic(estilo, italico);
        try {
            doc.insertString(doc.getLength(), texto, estilo);
            areaMensagens.setCaretPosition(doc.getLength());
        } catch (BadLocationException ignored) { }
    }

    static Color cor(String nome) {
        switch (nome) {
            case "AZUL":     return new Color(0, 80, 200);
            case "VERMELHO": return new Color(200, 0, 0);
            default:         return Color.BLACK;
        }
    }
}
