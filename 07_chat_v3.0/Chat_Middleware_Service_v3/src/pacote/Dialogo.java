package pacote;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

// Diálogos no mesmo estilo do Painel de Controle (substituem o JOptionPane)
public class Dialogo extends JDialog {

    private static final Color FUNDO_BOTOES = new Color(0xF8FAFC);
    private static final Color CINZA_BOTAO = new Color(0xE2E8F0);
    private static final Color AZUL = new Color(0x0F766E);

    private boolean confirmado = false;

    // Pergunta com botões "Cancelar" e textoConfirmar (vermelho). Devolve true se confirmou.
    public static boolean confirmar(Component pai, String titulo, String mensagem, String textoConfirmar) {
        Dialogo d = new Dialogo(pai, "Confirmação", titulo, mensagem, '!', CartaoServico.VERMELHO, textoConfirmar, true);
        d.setVisible(true);
        return d.confirmado;
    }

    // Mensagem de erro com um botão "OK"
    public static void erro(Component pai, String titulo, String mensagem) {
        new Dialogo(pai, "Erro", titulo, mensagem, '×', CartaoServico.VERMELHO, "OK", false).setVisible(true);
    }

    private Dialogo(Component pai, String tituloJanela, String titulo, String mensagem,
                    char simbolo, Color cor, String textoConfirmar, boolean comCancelar) {
        super(janelaDe(pai), tituloJanela, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel conteudo = new JPanel(new BorderLayout());
        conteudo.setBackground(Color.WHITE);

        // Corpo: ícone + título + mensagem
        JPanel corpo = new JPanel(new BorderLayout(16, 0));
        corpo.setOpaque(false);
        corpo.setBorder(BorderFactory.createEmptyBorder(22, 22, 20, 24));
        corpo.add(new Icone(simbolo, cor), BorderLayout.WEST);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 16f));
        lblTitulo.setForeground(CartaoServico.TEXTO);

        // HTML com largura fixa para a mensagem quebrar em várias linhas
        JLabel lblMensagem = new JLabel("<html><div style='width:250px'>"
                + mensagem.replace("\n", "<br>") + "</div></html>");
        lblMensagem.setFont(lblMensagem.getFont().deriveFont(Font.PLAIN, 13f));
        lblMensagem.setForeground(CartaoServico.TEXTO_SECUNDARIO);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        lblTitulo.setAlignmentX(LEFT_ALIGNMENT);
        lblMensagem.setAlignmentX(LEFT_ALIGNMENT);
        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(6));
        textos.add(lblMensagem);
        corpo.add(textos, BorderLayout.CENTER);
        conteudo.add(corpo, BorderLayout.CENTER);

        // Barra de botões
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(FUNDO_BOTOES);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(CartaoServico.BORDA_CARTAO);
                g.drawLine(0, 0, getWidth(), 0);
            }
        };
        botoes.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 12));

        CartaoServico.BotaoServico btnConfirmar = new CartaoServico.BotaoServico();
        btnConfirmar.setText(textoConfirmar);
        btnConfirmar.setCor(comCancelar ? cor : AZUL);
        btnConfirmar.addActionListener(e -> {
            confirmado = true;
            dispose();
        });

        if (comCancelar) {
            CartaoServico.BotaoServico btnCancelar = new CartaoServico.BotaoServico();
            btnCancelar.setText("Cancelar");
            btnCancelar.setCor(CINZA_BOTAO);
            btnCancelar.setCorTexto(CartaoServico.TEXTO);
            btnCancelar.addActionListener(e -> dispose());
            botoes.add(btnCancelar);
            // Enter cancela: parar o serviço precisa ser um clique consciente
            getRootPane().setDefaultButton(btnCancelar);
        } else {
            getRootPane().setDefaultButton(btnConfirmar);
        }
        botoes.add(btnConfirmar);
        conteudo.add(botoes, BorderLayout.SOUTH);

        // Esc fecha sem confirmar
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        setContentPane(conteudo);
        pack();
        setLocationRelativeTo(pai);
    }

    private static Window janelaDe(Component pai) {
        if (pai == null || pai instanceof Window) {
            return (Window) pai;
        }
        return SwingUtilities.getWindowAncestor(pai);
    }

    // Círculo colorido com um símbolo no meio (! ou ×)
    private static class Icone extends JComponent {
        private final char simbolo;
        private final Color cor;

        Icone(char simbolo, Color cor) {
            this.simbolo = simbolo;
            this.cor = cor;
            setPreferredSize(new Dimension(44, 44));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            // fundo claro da mesma cor + anel
            g2.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 30));
            g2.fillOval(0, 0, 44, 44);
            g2.setColor(cor);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(6, 6, 32, 32);
            g2.setFont(getFont().deriveFont(Font.BOLD, 20f));
            FontMetrics fm = g2.getFontMetrics();
            String s = String.valueOf(simbolo);
            g2.drawString(s, (44 - fm.stringWidth(s)) / 2, (44 - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
}
