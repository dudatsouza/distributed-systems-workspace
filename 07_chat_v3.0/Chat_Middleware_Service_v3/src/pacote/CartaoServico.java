package pacote;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Predicate;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Cartão de um serviço no Painel de Controle: status + nome + descrição + botão Ativar/Desativar.
// Se o serviço não estiver habilitado (ver Util), o cartão fica cinza e o botão não funciona.
public class CartaoServico extends JPanel {

    // Paleta do painel
    static final Color FUNDO_CARTAO = Color.WHITE;
    static final Color BORDA_CARTAO = new Color(0xE2E8F0);
    static final Color TEXTO = new Color(0x0F172A);
    static final Color TEXTO_SECUNDARIO = new Color(0x64748B);
    static final Color TEXTO_DESABILITADO = new Color(0xA0AEC0);
    static final Color VERDE = new Color(0x16A34A);
    static final Color VERMELHO = new Color(0xDC2626);
    static final Color CINZA = new Color(0xCBD5E1);

    private final boolean habilitado;
    private final Predicate<Boolean> aoAlternar;
    private boolean ativo = false;

    private final JLabel lblStatus = new JLabel();
    private final BotaoServico btnAlternar = new BotaoServico();
    private Runnable aoMudarStatus = () -> { };

    // aoAlternar recebe true (ativar) ou false (desativar) e devolve se a mudança aconteceu
    public CartaoServico(String nome, String descricao, boolean habilitado, Predicate<Boolean> aoAlternar) {
        this.habilitado = habilitado;
        this.aoAlternar = aoAlternar;

        setLayout(new BorderLayout(12, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 14));

        JLabel lblNome = new JLabel(nome);
        lblNome.setFont(lblNome.getFont().deriveFont(Font.BOLD, 15f));
        lblNome.setForeground(habilitado ? TEXTO : TEXTO_DESABILITADO);

        JLabel lblDescricao = new JLabel(descricao);
        lblDescricao.setFont(lblDescricao.getFont().deriveFont(Font.PLAIN, 12f));
        lblDescricao.setForeground(habilitado ? TEXTO_SECUNDARIO : TEXTO_DESABILITADO);

        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.BOLD, 11f));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(lblNome);
        textos.add(Box.createVerticalStrut(2));
        textos.add(lblDescricao);
        textos.add(Box.createVerticalStrut(6));
        textos.add(lblStatus);
        add(textos, BorderLayout.CENTER);

        JPanel direita = new JPanel(new java.awt.GridBagLayout());
        direita.setOpaque(false);
        btnAlternar.setEnabled(habilitado);
        btnAlternar.addActionListener(e -> alternar());
        direita.add(btnAlternar);
        add(direita, BorderLayout.EAST);

        atualizar();
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAoMudarStatus(Runnable aoMudarStatus) {
        this.aoMudarStatus = aoMudarStatus;
    }

    private void alternar() {
        boolean novoEstado = !ativo;
        if (aoAlternar.test(novoEstado)) {
            ativo = novoEstado;
            atualizar();
            aoMudarStatus.run();
        }
    }

    private void atualizar() {
        if (!habilitado) {
            lblStatus.setText("●  Em breve");
            lblStatus.setForeground(TEXTO_DESABILITADO);
            btnAlternar.setText("Indisponível");
        } else if (ativo) {
            lblStatus.setText("●  Ativo");
            lblStatus.setForeground(VERDE);
            btnAlternar.setText("Desativar");
        } else {
            lblStatus.setText("●  Parado");
            lblStatus.setForeground(TEXTO_SECUNDARIO);
            btnAlternar.setText("Ativar");
        }
        btnAlternar.setCor(!habilitado ? CINZA : (ativo ? VERMELHO : VERDE));
        repaint();
    }

    // Fundo arredondado do cartão
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(habilitado ? FUNDO_CARTAO : new Color(0xF8FAFC));
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
        g2.setColor(BORDA_CARTAO);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
        // faixa colorida à esquerda indicando o status
        g2.setColor(!habilitado ? BORDA_CARTAO : (ativo ? VERDE : CINZA));
        g2.fillRoundRect(0, 0, 5, getHeight() - 1, 5, 5);
        g2.dispose();
        super.paintComponent(g);
    }

    // Botão arredondado desenhado à mão (o visual padrão do macOS ignora cores de fundo)
    static class BotaoServico extends JButton {
        private Color cor = VERDE;
        private Color corTexto = Color.WHITE;
        private boolean mouseEmCima = false;

        BotaoServico() {
            setPreferredSize(new Dimension(118, 34));
            setFont(getFont().deriveFont(Font.BOLD, 13f));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    mouseEmCima = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    mouseEmCima = false;
                    repaint();
                }
            });
        }

        void setCorTexto(Color corTexto) {
            this.corTexto = corTexto;
            repaint();
        }

        void setCor(Color cor) {
            this.cor = cor;
            setCursor(Cursor.getPredefinedCursor(isEnabled() ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Color fundo = cor;
            if (isEnabled() && getModel().isPressed()) {
                fundo = cor.darker();
            } else if (isEnabled() && mouseEmCima) {
                fundo = cor.brighter();
            }
            g2.setColor(fundo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.setFont(getFont());
            g2.setColor(isEnabled() ? corTexto : Color.WHITE.darker());
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }
}
