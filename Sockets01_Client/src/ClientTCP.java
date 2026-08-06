import javax.swing.*;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientTCP {

    public void execute(){
        try{
            Socket client = new Socket("127.0.0.1", 3322);
            ObjectInputStream reader = new ObjectInputStream(client.getInputStream());
            JOptionPane.showMessageDialog(null, "Mensagem recebida no cliente: " + reader.readUTF());

            reader.close();
            client.close();

        } catch(Exception error) {
            JOptionPane.showMessageDialog(null,"Erro no cliente: " + error.getMessage());
        }
    }
}
