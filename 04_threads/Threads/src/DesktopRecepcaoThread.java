import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DesktopRecepcaoThread implements Runnable {

    @Override
    public void run() {
        while (true) {
            try {
                ServerSocket receptor = new ServerSocket(Util.PortaRecepcaoDesktop);
                Socket cliente = receptor.accept();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(cliente.getInputStream())
                );
                String msg = reader.readLine();

                reader.close();
                cliente.close();
                receptor.close();

                JOptionPane.showMessageDialog(null, "Mensagem: " + msg);

                FileWriter fWriter = new FileWriter(Util.PathRepDesktop, true);
                fWriter.write(msg);

                fWriter.close();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro na Thread Recepção Desktop: " + e.getMessage());
            }
        }
    }
}