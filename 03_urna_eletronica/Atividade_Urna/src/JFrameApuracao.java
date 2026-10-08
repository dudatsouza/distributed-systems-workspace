import javax.swing.*;
import java.awt.*;

public class JFrameApuracao extends JFrame {

    public JFrameApuracao(String resultadoFinal) {
        setTitle("Resultado da Apuração");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Área de texto para mostrar o resultado formatado
        JTextArea txtResultado = new JTextArea(resultadoFinal);
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtResultado.setMargin(new Insets(10, 10, 10, 10));

        add(new JScrollPane(txtResultado), BorderLayout.CENTER);

        // Botão Fechar pedido no quadro
        JButton btnFechar = new JButton("FECHAR");
        btnFechar.addActionListener(e -> {
            System.exit(0); // Encerra a aplicação
        });

        add(btnFechar, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null); // Centraliza a janela
    }
}