package pacote;

import java.awt.CheckboxMenuItem;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class MenuServidor {
    SystemTray Tray;
    TrayIcon Icon;
    PopupMenu PopUp;
    MenuItem mnuPainelControle, mnuSair;
    CheckboxMenuItem mnuItDesktop, mnuItWeb, mnuItTerceiros, mnuItPublicidade;
    Menu mnuAcoes;
    FrmPainelDeControle frmpainelcontrole;

    public MenuServidor() {
        try {
            frmpainelcontrole = new FrmPainelDeControle();
            if (!SystemTray.isSupported()) {
                // Sem bandeja (ícone na barra de menus), abre o painel direto
                System.out.println("Sem suporte a SystemTray");
                frmpainelcontrole.setVisible(true);
                return;
            }
            Tray = SystemTray.getSystemTray();
            ImageIcon imgIcone = new ImageIcon(MenuServidor.class.getResource("/images/icone.jpeg"), "Servidor Chat");
            Icon = new TrayIcon(imgIcone.getImage(), "Servidor Chat");
            Icon.setImageAutoSize(true);
            PopUp = new PopupMenu();
            mnuPainelControle = new MenuItem("Abrir Painel de Controle");
            mnuPainelControle.addActionListener(ae -> frmpainelcontrole.setVisible(true));
            mnuAcoes = new Menu("Ações");
            mnuItDesktop = new CheckboxMenuItem("Servidor Desktop");
            mnuItWeb = new CheckboxMenuItem("Servidor Web");
            mnuItTerceiros = new CheckboxMenuItem("Servidor Terceiros");
            mnuItPublicidade = new CheckboxMenuItem("Servidor Publicidade");
            mnuSair = new MenuItem("Sair");
            mnuSair.addActionListener(ae -> System.exit(0));
            PopUp.add(mnuPainelControle);
            PopUp.addSeparator();
            mnuAcoes.add(mnuItDesktop);
            mnuAcoes.add(mnuItWeb);
            mnuAcoes.add(mnuItTerceiros);
            mnuAcoes.addSeparator();
            mnuAcoes.add(mnuItPublicidade);
            PopUp.add(mnuAcoes);
            PopUp.addSeparator();
            PopUp.add(mnuSair);
            Icon.setPopupMenu(PopUp);
            Tray.add(Icon);
            frmpainelcontrole.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao criar o menu do servidor de chat:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
