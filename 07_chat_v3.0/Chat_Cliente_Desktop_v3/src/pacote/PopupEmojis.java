package pacote;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

// Popup menu de emojis (desenho do quadro):
// JPopupMenu -> JTabbedPane (Guia 1 = Carinhas, Guia 2 = Figuras) -> JPanel com GridLayout de botões
public class PopupEmojis extends JPopupMenu {

    private final Consumer<String> aoEscolher;

    // aoEscolher recebe o código do emoji clicado (ex.: ":-)" ou ":coracao:")
    public PopupEmojis(Consumer<String> aoEscolher) {
        this.aoEscolher = aoEscolher;

        JTabbedPane guias = new JTabbedPane();

        JPanel pnlCarinhas = new JPanel(new GridLayout(0, 5, 4, 4));
        pnlCarinhas.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        for (Object[] c : Emojis.CARINHAS) {
            JButton btn = criarBotao((String) c[0]);
            btn.setText(Emojis.texto((Integer) c[1]));
            btn.setFont(btn.getFont().deriveFont(Font.PLAIN, 22f));
            pnlCarinhas.add(btn);
        }
        guias.addTab("Carinhas", pnlCarinhas);

        JPanel pnlFiguras = new JPanel(new GridLayout(0, 3, 4, 4));
        pnlFiguras.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        for (String[] f : Emojis.FIGURAS) {
            JButton btn = criarBotao(f[0]);
            Image img = new ImageIcon(PopupEmojis.class.getResource("/images/" + f[1])).getImage();
            btn.setIcon(new ImageIcon(img.getScaledInstance(28, 28, Image.SCALE_SMOOTH)));
            pnlFiguras.add(btn);
        }
        guias.addTab("Figuras", pnlFiguras);

        add(guias);
    }

    private JButton criarBotao(String codigo) {
        JButton btn = new JButton();
        btn.setToolTipText(codigo);
        btn.setMargin(new Insets(2, 2, 2, 2));
        btn.setFocusable(false);
        btn.addActionListener(e -> {
            setVisible(false);
            aoEscolher.accept(codigo);
        });
        return btn;
    }
}
