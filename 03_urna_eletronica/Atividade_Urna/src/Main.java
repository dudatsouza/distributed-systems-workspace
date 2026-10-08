import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Configura a preferência por IPv4 conforme código original
        System.setProperty("java.net.preferIPv4Stack", "true");

        // Inicia a interface gráfica na thread correta do Swing
        SwingUtilities.invokeLater(() -> {
            JFrameUrna urna = new JFrameUrna();
            urna.setVisible(true);
        });
    }
}