package chat.cliente;

import chat.servidor.Servidor;

import javax.swing.*;
import java.awt.*;

/** Tela de LOGIN: nickname, cor e avatar. */
public class LoginFrame extends JFrame {

    static final String[] AVATARES = {"☺", "☻", "★"};

    private final JTextField campoNick = new JTextField(15);
    private final JTextField campoServidor = new JTextField("localhost", 15);
    private final ButtonGroup grupoCor = new ButtonGroup();
    private final ButtonGroup grupoAvatar = new ButtonGroup();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    public LoginFrame() {
        super("Chat Cliente Desktop - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        JLabel titulo = new JLabel("LOGIN");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        painel.add(titulo, c);
        c.gridwidth = 1;

        // Nickname
        c.gridx = 0; c.gridy = 1;
        painel.add(new JLabel("Nickname:"), c);
        c.gridx = 1;
        painel.add(campoNick, c);

        // Cor
        c.gridx = 0; c.gridy = 2;
        painel.add(new JLabel("Cor:"), c);
        JPanel cores = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        adicionarRadio(cores, grupoCor, "Azul", "AZUL", true).setForeground(ChatFrame.cor("AZUL"));
        adicionarRadio(cores, grupoCor, "Preto", "PRETO", false).setForeground(ChatFrame.cor("PRETO"));
        adicionarRadio(cores, grupoCor, "Vermelho", "VERMELHO", false).setForeground(ChatFrame.cor("VERMELHO"));
        c.gridx = 1;
        painel.add(cores, c);

        // Avatar
        c.gridx = 0; c.gridy = 3;
        painel.add(new JLabel("Avatar:"), c);
        JPanel avatares = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        for (int i = 0; i < AVATARES.length; i++) {
            JRadioButton rb = adicionarRadio(avatares, grupoAvatar, AVATARES[i], AVATARES[i], i == 0);
            rb.setFont(rb.getFont().deriveFont(18f));
        }
        c.gridx = 1;
        painel.add(avatares, c);

        // Servidor (útil para testar em máquinas diferentes do laboratório)
        c.gridx = 0; c.gridy = 4;
        painel.add(new JLabel("Servidor:"), c);
        c.gridx = 1;
        painel.add(campoServidor, c);

        // Botão Entrar
        JButton entrar = new JButton("Entrar");
        entrar.addActionListener(e -> entrar());
        c.gridx = 0; c.gridy = 5; c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        painel.add(entrar, c);

        getRootPane().setDefaultButton(entrar);
        setContentPane(painel);
        pack();
        setLocationRelativeTo(null);
    }

    private JRadioButton adicionarRadio(JPanel p, ButtonGroup g, String texto, String valor, boolean sel) {
        JRadioButton rb = new JRadioButton(texto, sel);
        rb.setActionCommand(valor);
        g.add(rb);
        p.add(rb);
        return rb;
    }

    private void entrar() {
        String nick = campoNick.getText().replace("|", "").trim();
        if (nick.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um nickname.");
            return;
        }
        String cor = grupoCor.getSelection().getActionCommand();
        String avatar = grupoAvatar.getSelection().getActionCommand();

        try {
            Conexao conexao = new Conexao(campoServidor.getText().trim(), Servidor.PORTA);
            new ChatFrame(nick, cor, avatar, conexao).setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível conectar ao servidor.\nO Servidor está rodando?\n\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
