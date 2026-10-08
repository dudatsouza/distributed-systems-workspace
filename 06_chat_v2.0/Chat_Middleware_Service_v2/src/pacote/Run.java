package pacote;

// Ponto de entrada do middleware: cria o ícone na barra de menus e abre o Painel de Controle
public class Run {
    static MenuServidor menuservidor;

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> menuservidor = new MenuServidor());
    }
}
