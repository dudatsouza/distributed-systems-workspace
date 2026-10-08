import javax.swing.*;
import java.awt.*;

public class PainelControle extends JFrame {

    private boolean ativado = false;
    private JButton botaoAtivar;

    public PainelControle() {
        super("Painel de Controle");

        botaoAtivar = new JButton("Ativar");

        botaoAtivar.addActionListener(e -> {
            if (!ativado) {
                // Liga o servidor de recepção
                DesktopRecepcaoThread t1 = new DesktopRecepcaoThread();
                Thread.ofVirtual().start(t1);

                ativado = true;
                botaoAtivar.setText("Desativar");
            } else {
                ativado = false;
                botaoAtivar.setText("Ativar");
            }
        });

        setLayout(new FlowLayout());
        add(botaoAtivar);

        setSize(300, 70);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PainelControle painel = new PainelControle();
            painel.setVisible(true);
        });
    }
}