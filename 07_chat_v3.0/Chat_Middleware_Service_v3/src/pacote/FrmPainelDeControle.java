package pacote;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Painel de Controle do middleware: um cartão por serviço.
// Quais serviços estão disponíveis é definido em Util (SERVICO_..._HABILITADO).
public class FrmPainelDeControle extends javax.swing.JFrame {

    private static final Color FUNDO = new Color(0xF1F5F9);
    private static final Color CABECALHO_1 = new Color(0x0F766E);
    private static final Color CABECALHO_2 = new Color(0x115E59);

    private static final int LARGURA = 460;

    private final CartaoServico[] cartoes;
    private final JLabel lblResumo = new JLabel();

    public FrmPainelDeControle() {
        setTitle("Painel de Controle");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        cartoes = new CartaoServico[] {
            new CartaoServico("Serviço Desktop", "Inspirado no WhatsApp Desktop",
                    Util.SERVICO_DESKTOP_HABILITADO, this::alternarDesktop),
            new CartaoServico("Serviço Web", "Inspirado no WhatsApp Web",
                    Util.SERVICO_WEB_HABILITADO, ativar -> true),
            new CartaoServico("Serviço Terceiros", "Integração com software de outras linguagens",
                    Util.SERVICO_TERCEIROS_HABILITADO, ativar -> true),
            new CartaoServico("Serviço Publicidade", "Envio de propaganda em multicast",
                    Util.SERVICO_PUBLICIDADE_HABILITADO, ativar -> true),
        };

        JPanel conteudo = new JPanel(new BorderLayout());
        conteudo.setBackground(FUNDO);
        conteudo.add(criarCabecalho(), BorderLayout.NORTH);

        JPanel lista = new JPanel(new GridLayout(0, 1, 0, 10));
        lista.setOpaque(false);
        lista.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        for (CartaoServico cartao : cartoes) {
            cartao.setAoMudarStatus(this::atualizarResumo);
            lista.add(cartao);
        }
        conteudo.add(lista, BorderLayout.CENTER);

        String caminho = Util.PathRepDesktop.replace(System.getProperty("user.home"), "~");
        JLabel lblRodape = new JLabel("Repositório: " + caminho);
        lblRodape.setToolTipText(Util.PathRepDesktop);
        lblRodape.setFont(lblRodape.getFont().deriveFont(Font.PLAIN, 11f));
        lblRodape.setForeground(CartaoServico.TEXTO_SECUNDARIO);
        lblRodape.setBorder(BorderFactory.createEmptyBorder(0, 18, 12, 18));
        conteudo.add(lblRodape, BorderLayout.SOUTH);

        setContentPane(conteudo);
        atualizarResumo();
        pack();
        setSize(LARGURA, getHeight());
        setLocationRelativeTo(null);
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, CABECALHO_1, getWidth(), getHeight(), CABECALHO_2));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        cabecalho.setBorder(BorderFactory.createEmptyBorder(18, 20, 16, 20));

        JLabel lblTitulo = new JLabel("Chat Middleware Service");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 20f));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Painel de Controle dos serviços do servidor de chat");
        lblSubtitulo.setFont(lblSubtitulo.getFont().deriveFont(Font.PLAIN, 12f));
        lblSubtitulo.setForeground(new Color(0xCCFBF1));

        lblResumo.setFont(lblResumo.getFont().deriveFont(Font.BOLD, 11f));
        lblResumo.setForeground(Color.WHITE);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(lblSubtitulo);
        textos.add(Box.createVerticalStrut(10));
        textos.add(lblResumo);
        cabecalho.add(textos, BorderLayout.CENTER);
        return cabecalho;
    }

    private void atualizarResumo() {
        int ativos = 0;
        for (CartaoServico cartao : cartoes) {
            if (cartao.isAtivo()) {
                ativos++;
            }
        }
        lblResumo.setText(ativos + " de " + cartoes.length + " serviços ativos");
    }

    // Liga/desliga o Serviço Desktop. Devolve true se a mudança aconteceu.
    private boolean alternarDesktop(boolean ativar) {
        if (ativar) {
            try {
                Util.criarRepositorio();
            } catch (Exception e) {
                Dialogo.erro(this, "Não foi possível ativar", "Erro ao criar o repositório " + Util.PathRepDesktop + ":\n" + e.getMessage());
                return false;
            }
            Util.desktopRecepcaoThread = new DesktopRecepcaoThread();
            Thread.ofVirtual().start(Util.desktopRecepcaoThread); //[JAVA 21]

            Util.emManutencao = false;
            // A thread de envio só é criada uma vez: ao desativar ela continua rodando para avisar a manutenção
            if (Util.desktopEnvioThread == null) {
                Util.desktopEnvioThread = new DesktopEnvioThread();
                Thread.ofVirtual().start(Util.desktopEnvioThread);
            }
            return true;
        }
        boolean confirmou = Dialogo.confirmar(this, "Desativar o Serviço Desktop?",
                "Os clientes desktop deixam de enviar mensagens e passam a ver o aviso \"Em manutenção...\" até o serviço ser reativado.",
                "Desativar");
        if (!confirmou) {
            return false;
        }
        // Para de receber mensagens e passa a responder "em manutenção" para os clientes
        Util.desktopRecepcaoThread.fecharServidor();
        Util.emManutencao = true;
        return true;
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmPainelDeControle().setVisible(true));
    }
}
