import javax.swing.*;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorTCP {

    public void execute(){
        try{
            ServerSocket Server = new ServerSocket(3322); // cria um socket para a porta 332, porém ainda fechada
            JOptionPane.showMessageDialog(null, "Servidor iniciado");

            Socket client = Server.accept(); // abre a porta 3322 para aceitar conexões

            ObjectOutputStream writer = new ObjectOutputStream(client.getOutputStream());
            writer.flush(); // limpa lixo da conexão (opcional)

            writer.writeUTF("Você conectou-se com sucesso ao servidor - Bem vindo!");

            writer.close(); // fecha a conexão
            client.close(); // fecha a conexão


        } catch(Exception error) {
            JOptionPane.showMessageDialog(null,"Erro no servidor: " + error.getMessage());
        }
    }
}
