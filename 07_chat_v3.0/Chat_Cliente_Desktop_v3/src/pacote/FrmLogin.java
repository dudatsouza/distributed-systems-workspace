package pacote;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// Tela de login: nickname, cor do nome, avatar e servidor
public class FrmLogin extends javax.swing.JFrame {

    private static final int LARGURA = 460;

    // Cores do nome no chat: {nome, hexadecimal}
    private static final String[][] CORES = {
        {"Azul", "#0000CC"}, {"Verde", "#009900"}, {"Vermelho", "#FF0000"},
    };

    // Avatares em src/images (64x64, já recortados em círculo)
    private static final String[] AVATARES = {
        "avatar-homem-1.png", "avatar-homem-2.png", "avatar-homem-3.png",
        "avatar-mulher-1.png", "avatar-mulher-2.png", "avatar-mulher-3.png",
    };

    private final Estilo.CampoTexto txtNick = new Estilo.CampoTexto("Como você quer ser chamado?");
    private final Estilo.CampoTexto txtServidor = new Estilo.CampoTexto("IP do servidor");
    private final JLabel lblErro = new JLabel(" ");
    private final JLabel lblPrevia = new JLabel();
    private String corEscolhida = CORES[0][1];
    private String avatarEscolhido = AVATARES[0];

    public FrmLogin() {
        setTitle("Login");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel conteudo = new JPanel(new BorderLayout());
        conteudo.setBackground(Estilo.FUNDO);
        conteudo.add(Estilo.cabecalho("Chat Desktop", "Escolha como você vai aparecer na conversa"), BorderLayout.NORTH);

        Estilo.Cartao cartao = new Estilo.Cartao();
        cartao.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        // Nickname
        c.insets = new Insets(0, 0, 6, 0);
        cartao.add(Estilo.rotulo("Nickname"), c);
        c.insets = new Insets(0, 0, 2, 0);
        cartao.add(txtNick, c);
        lblErro.setFont(Estilo.fonte(Font.PLAIN, 11f));
        lblErro.setForeground(Estilo.VERMELHO);
        c.insets = new Insets(0, 2, 10, 0);
        cartao.add(lblErro, c);

        // Cor
        c.insets = new Insets(0, 0, 6, 0);
        cartao.add(Estilo.rotulo("Cor do nome"), c);
        c.insets = new Insets(0, 0, 16, 0);
        cartao.add(criarCores(), c);

        // Avatar
        c.insets = new Insets(0, 0, 6, 0);
        cartao.add(Estilo.rotulo("Avatar"), c);
        c.insets = new Insets(0, 0, 16, 0);
        cartao.add(criarAvatares(), c);

        // Servidor
        c.insets = new Insets(0, 0, 6, 0);
        cartao.add(Estilo.rotulo("Servidor"), c);
        txtServidor.setText(Util.ServidorIP);
        txtServidor.setToolTipText("IP da máquina que está rodando o Chat_Middleware_Service (localhost = este computador)");
        c.insets = new Insets(0, 0, 16, 0);
        cartao.add(txtServidor, c);

        // Prévia de como a mensagem vai aparecer
        c.insets = new Insets(0, 0, 6, 0);
        cartao.add(Estilo.rotulo("Prévia"), c);
        lblPrevia.setFont(Estilo.fonte(Font.PLAIN, 13f));
        lblPrevia.setIconTextGap(8);
        lblPrevia.setOpaque(true);
        lblPrevia.setBackground(Estilo.FUNDO);
        lblPrevia.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        c.insets = new Insets(0, 0, 18, 0);
        cartao.add(lblPrevia, c);

        Estilo.Botao btnEntrar = new Estilo.Botao("Entrar no chat", Estilo.PRINCIPAL);
        btnEntrar.setPreferredSize(new Dimension(100, 42));
        btnEntrar.setFont(Estilo.fonte(Font.BOLD, 14f));
        btnEntrar.addActionListener(e -> entrar());
        c.insets = new Insets(0, 0, 0, 0);
        cartao.add(btnEntrar, c);
        getRootPane().setDefaultButton(btnEntrar);   // Enter em qualquer campo = Entrar

        JPanel margem = new JPanel(new BorderLayout());
        margem.setOpaque(false);
        margem.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        margem.add(cartao);
        conteudo.add(margem, BorderLayout.CENTER);
        setContentPane(conteudo);

        txtNick.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aoDigitarNick();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aoDigitarNick();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                aoDigitarNick();
            }
        });
        atualizarPrevia();

        pack();
        setSize(LARGURA, getHeight());
        setLocationRelativeTo(null);
    }

    private JPanel criarCores() {
        JPanel painel = new JPanel(new GridLayout(1, CORES.length, 8, 0));
        painel.setOpaque(false);
        ButtonGroup grupo = new ButtonGroup();
        for (String[] cor : CORES) {
            Estilo.Opcao opcao = new Estilo.Opcao(cor[0], Color.decode(cor[1]));
            opcao.addActionListener(e -> {
                corEscolhida = cor[1];
                atualizarPrevia();
            });
            opcao.setSelected(cor[1].equals(corEscolhida));
            grupo.add(opcao);
            painel.add(opcao);
        }
        return painel;
    }

    private JPanel criarAvatares() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        painel.setOpaque(false);
        JPanel grade = new JPanel(new GridLayout(1, AVATARES.length, 8, 0));
        grade.setOpaque(false);
        ButtonGroup grupo = new ButtonGroup();
        for (String arquivo : AVATARES) {
            OpcaoAvatar opcao = new OpcaoAvatar(imagem(arquivo, 48));
            opcao.setToolTipText(arquivo);
            opcao.addActionListener(e -> {
                avatarEscolhido = arquivo;
                atualizarPrevia();
            });
            opcao.setSelected(arquivo.equals(avatarEscolhido));
            grupo.add(opcao);
            grade.add(opcao);
        }
        painel.add(grade);
        return painel;
    }

    private static Image imagem(String arquivo, int tamanho) {
        Image img = new ImageIcon(FrmLogin.class.getResource("/images/" + arquivo)).getImage();
        // ImageIcon espera a imagem reduzida terminar de carregar (getScaledInstance é assíncrono)
        return new ImageIcon(img.getScaledInstance(tamanho, tamanho, Image.SCALE_SMOOTH)).getImage();
    }

    private void aoDigitarNick() {
        txtNick.setErro(false);
        lblErro.setText(" ");
        atualizarPrevia();
    }

    private void atualizarPrevia() {
        String nick = txtNick.getText().trim();
        if (nick.isEmpty()) {
            nick = "Seu nick";
        }
        lblPrevia.setIcon(new ImageIcon(imagem(avatarEscolhido, 22)));
        lblPrevia.setText("<html><font color='" + corEscolhida + "'><b>" + Util.escaparHtml(nick)
                + "</b></font> <b>Fala:</b> Olá, pessoal! &#128075;</html>");
    }

    private void entrar() {
        String nick = txtNick.getText().trim();
        if (nick.isEmpty()) {
            mostrarErro("Digite um nickname.");
            return;
        }
        if (nick.length() > Util.TAMANHO_MAX_NICK) {
            mostrarErro("O nickname pode ter no máximo " + Util.TAMANHO_MAX_NICK + " caracteres (tem " + nick.length() + ").");
            return;
        }
        Util.nickname = nick;
        Util.cor = corEscolhida;
        Util.avatar = avatarEscolhido;
        if (!txtServidor.getText().isBlank()) {
            Util.ServidorIP = txtServidor.getText().trim();
        }
        new FrmChat().setVisible(true);
        dispose();
    }

    private void mostrarErro(String mensagem) {
        lblErro.setText(mensagem);
        txtNick.setErro(true);
        txtNick.requestFocusInWindow();
    }

    // Botão de avatar: imagem redonda; selecionado ganha um anel na cor principal
    private static class OpcaoAvatar extends JToggleButton {
        private final Image img;

        OpcaoAvatar(Image img) {
            this.img = img;
            setPreferredSize(new Dimension(56, 56));
            estiloOpcao(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.drawImage(img, 4, 4, 48, 48, this);
            if (isSelected()) {
                g2.setColor(Estilo.PRINCIPAL);
                g2.setStroke(new BasicStroke(3f));
                g2.drawOval(2, 2, 51, 51);
            } else if (getModel().isRollover()) {
                g2.setColor(Estilo.CINZA);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(2, 2, 51, 51);
            }
            g2.dispose();
        }
    }

    private static void estiloOpcao(JComponent botao) {
        JToggleButton b = (JToggleButton) botao;
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setRolloverEnabled(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmLogin().setVisible(true));
    }
}
