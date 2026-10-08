package pacote;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.AbstractBorder;

// Visual do cliente: mesma paleta e componentes do Painel de Controle do middleware
public class Estilo {

    public static final Color FUNDO = new Color(0xF1F5F9);
    public static final Color FUNDO_CARTAO = Color.WHITE;
    public static final Color BORDA = new Color(0xE2E8F0);
    public static final Color TEXTO = new Color(0x0F172A);
    public static final Color TEXTO_SECUNDARIO = new Color(0x64748B);
    public static final Color TEXTO_DESABILITADO = new Color(0xA0AEC0);
    public static final Color PRINCIPAL = new Color(0x0F766E);
    public static final Color PRINCIPAL_ESCURO = new Color(0x115E59);
    public static final Color PRINCIPAL_CLARO = new Color(0xCCFBF1);
    public static final Color VERDE = new Color(0x16A34A);
    public static final Color VERMELHO = new Color(0xDC2626);
    public static final Color CINZA = new Color(0xCBD5E1);

    public static Font fonte(int estilo, float tamanho) {
        return new JLabel().getFont().deriveFont(estilo, tamanho);
    }

    public static JLabel rotulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(fonte(Font.BOLD, 12f));
        lbl.setForeground(TEXTO_SECUNDARIO);
        return lbl;
    }

    // Cabeçalho com degradê, título e subtítulo (igual ao do Painel de Controle)
    public static JPanel cabecalho(String titulo, String subtitulo) {
        JPanel cabecalho = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, PRINCIPAL, getWidth(), getHeight(), PRINCIPAL_ESCURO));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        cabecalho.setBorder(BorderFactory.createEmptyBorder(18, 20, 16, 20));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(fonte(Font.BOLD, 20f));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(fonte(Font.PLAIN, 12f));
        lblSubtitulo.setForeground(PRINCIPAL_CLARO);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(lblSubtitulo);
        cabecalho.add(textos, BorderLayout.CENTER);
        return cabecalho;
    }

    // Painel branco com cantos arredondados e borda clara
    public static class Cartao extends JPanel {
        public Cartao() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(16, 18, 18, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(FUNDO_CARTAO);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.setColor(BORDA);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Botão arredondado desenhado à mão (o visual padrão do macOS ignora cores de fundo)
    public static class Botao extends JButton {
        private Color cor;
        private Color corTexto = Color.WHITE;
        private boolean mouseEmCima = false;

        public Botao(String texto, Color cor) {
            super(texto);
            this.cor = cor;
            setPreferredSize(new Dimension(118, 34));
            setFont(fonte(Font.BOLD, 13f));
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

        public void setCor(Color cor) {
            this.cor = cor;
            repaint();
        }

        public void setCorTexto(Color corTexto) {
            this.corTexto = corTexto;
            repaint();
        }

        @Override
        public void setEnabled(boolean habilitado) {
            super.setEnabled(habilitado);
            setCursor(Cursor.getPredefinedCursor(habilitado ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Color fundo = isEnabled() ? cor : CINZA;
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

    // Campo de texto arredondado, com texto de exemplo (placeholder) e borda que muda de cor
    public static class CampoTexto extends JTextField {
        private final String placeholder;
        private boolean erro = false;

        public CampoTexto(String placeholder) {
            this.placeholder = placeholder;
            setFont(fonte(Font.PLAIN, 14f));
            setForeground(TEXTO);
            setCaretColor(PRINCIPAL);
            setOpaque(false);
            setBorder(new BordaArredondada(this));
            setPreferredSize(new Dimension(100, 38));
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    repaint();
                }
            });
        }

        public void setErro(boolean erro) {
            this.erro = erro;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.dispose();
            super.paintComponent(g);
            if (getText().isEmpty() && !placeholder.isEmpty()) {
                Graphics2D g3 = (Graphics2D) g.create();
                g3.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g3.setColor(TEXTO_DESABILITADO);
                g3.setFont(getFont());
                Insets in = getInsets();
                FontMetrics fm = g3.getFontMetrics();
                g3.drawString(placeholder, in.left, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g3.dispose();
            }
        }

        private static class BordaArredondada extends AbstractBorder {
            private final CampoTexto campo;

            BordaArredondada(CampoTexto campo) {
                this.campo = campo;
            }

            @Override
            public void paintBorder(java.awt.Component c, Graphics g, int x, int y, int w, int h) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(campo.erro ? VERMELHO : (campo.hasFocus() ? PRINCIPAL : CINZA));
                g2.setStroke(new java.awt.BasicStroke(campo.hasFocus() || campo.erro ? 2f : 1f));
                g2.drawRoundRect(x + 1, y + 1, w - 3, h - 3, 10, 10);
                g2.dispose();
            }

            @Override
            public Insets getBorderInsets(java.awt.Component c) {
                return new Insets(8, 12, 8, 12);
            }
        }
    }

    // Opção (toggle) com bolinha colorida + nome; selecionada ganha borda e fundo na cor.
    // Usada para a cor do nome (login) e o modo da mensagem (chat).
    public static class Opcao extends JToggleButton {
        private final Color cor;

        public Opcao(String nome, Color cor) {
            super(nome);
            this.cor = cor;
            setPreferredSize(new Dimension(100, 36));
            setFont(fonte(Font.BOLD, 13f));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setColor(isSelected() ? new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 22) : Color.WHITE);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 10, 10);
            g2.setColor(isSelected() ? cor : CINZA);
            g2.setStroke(new java.awt.BasicStroke(isSelected() ? 2f : 1f));
            g2.drawRoundRect(1, 1, w - 3, h - 3, 10, 10);
            // bolinha + texto centralizados
            g2.setFont(getFont());
            var fm = g2.getFontMetrics();
            int bolinha = 12, espaco = 6;
            int total = bolinha + espaco + fm.stringWidth(getText());
            int x = (w - total) / 2;
            g2.setColor(cor);
            g2.fillOval(x, (h - bolinha) / 2, bolinha, bolinha);
            g2.drawString(getText(), x + bolinha + espaco, (h - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
}
