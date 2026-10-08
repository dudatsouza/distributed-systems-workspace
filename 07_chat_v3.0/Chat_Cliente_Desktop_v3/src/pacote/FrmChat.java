package pacote;

import java.awt.event.KeyEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class FrmChat extends javax.swing.JFrame {
    public String msg = "";
    public void gerarEenviarMensagem(){
        if (emManutencao) {
            return;
        }
        String texto = TxtMensagem.getText().trim();
        if (texto.isEmpty()) {
            return;
        }
        if (texto.length() > Util.TAMANHO_MAX_MENSAGEM) {
            JOptionPane.showMessageDialog(null,"A mensagem pode ter no máximo " + Util.TAMANHO_MAX_MENSAGEM + " caracteres.");
            return;
        }
        String modo = CBModo.getSelectedItem().toString();
        if (!modo.equals("Fala")) {
            texto = texto.toUpperCase();
        }
        // Escapa o HTML digitado e depois troca os códigos (":-)", ":coracao:"...) pelos emojis
        texto = Emojis.substituir(Util.escaparHtml(texto));

        // As imagens vão com caminho relativo; cada cliente resolve pela própria pasta images (ver setBase no construtor)
        this.msg = "";
        this.msg += "<font color='#808080' size='-1'>[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "]</font> ";
        this.msg += "<img src='" + Util.avatar + "' width='20' height='20'>";
        this.msg += "<font color='" + Util.cor + "'><b> " + Util.escaparHtml(Util.nickname) + " </b></font>";
        if(modo.equals("Fala")){
            this.msg += "<b> Fala: </b>";
            this.msg += texto;
        } else if(modo.equals("Grita")){
            this.msg += "<b><i><u> Grita: </u></i></b>";
            // "Tomato" por nome não existe no Swing (saía preto), por isso o hexadecimal
            this.msg += "<font color='#FF6347' size='+1'>" + texto + "</font>";
        } else if(modo.equals("Xinga")){
            this.msg += "<b> Xinga: </b>";
            // Amarelo puro some no fundo branco: amarelo sobre faixa preta
            this.msg += "<span style='background-color:#000000'><font color='#FFD700' size='+3'>" + texto + "</font></span>";
        }
        this.msg += "<br>";
        try{
            Socket cliente = new Socket(Util.ServidorIP,Util.PortaRecepcaoDesktop);
            ObjectOutputStream output = new ObjectOutputStream(cliente.getOutputStream());
            output.writeUTF(this.msg);
            output.close();
            cliente.close();
            TxtMensagem.setText("");
        } catch (Exception e) {
            // Não conseguiu falar com o middleware: servidor parado
            mostrarManutencao();
        }
    }
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmChat.class.getName());
    private volatile String ultimoHistorico = "";
    private volatile boolean emManutencao = false;
    private PopupEmojis popupEmojis;
    public FrmChat() {
        initComponents();
        setTitle("Chat - " + Util.nickname);
        ((HTMLDocument) EdtConversa.getDocument()).setBase(FrmChat.class.getResource("/images/"));
        popupEmojis = new PopupEmojis(this::inserirEmoji);
        // Thread de recepção (PULL): a cada 1s pede o histórico ao middleware
        Thread.ofVirtual().start(()->{
            while (true){
                try{
                    Socket cliente = new Socket(Util.ServidorIP,Util.PortaEnvioDesktop);
                    ObjectInputStream input = new ObjectInputStream(cliente.getInputStream());
                    String msgs = input.readUTF();
                    input.close();
                    cliente.close();
                    if (msgs.equals(Util.MSG_MANUTENCAO)) {
                        // O middleware avisou que o Serviço Desktop foi desativado
                        SwingUtilities.invokeLater(this::mostrarManutencao);
                    } else if (emManutencao || !msgs.equals(ultimoHistorico)) {
                        SwingUtilities.invokeLater(() -> mostrarConversa(msgs));
                    }
                } catch (Exception e){
                    // Middleware fechado ou fora da rede: também é manutenção, e o cliente segue tentando
                    SwingUtilities.invokeLater(this::mostrarManutencao);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
    }

    private void mostrarConversa(String msgs) {
        try {
            ultimoHistorico = msgs;
            setManutencao(false);
            HTMLDocument doc = (HTMLDocument) EdtConversa.getDocument();
            doc.setBase(FrmChat.class.getResource("/images/"));
            HTMLEditorKit kit = (HTMLEditorKit) EdtConversa.getEditorKit();
            doc.remove(0, doc.getLength());
            kit.insertHTML(doc,doc.getLength(),msgs,0,0,null); // 0,0,null = ponto inicial de inserção, niveis de tags a serem ignorados, tag para iniciar edição
            EdtConversa.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarManutencao() {
        if (emManutencao) {
            return;
        }
        setManutencao(true);
        EdtConversa.setText("<html><body><center><br><br>"
                + "<font size='+3'>&#128736;</font><br>"
                + "<font size='+2' color='#CC6600'><b>Em manutenção...</b></font><br><br>"
                + "<font color='#808080'>O servidor de chat está temporariamente fora do ar.<br>"
                + "A conversa volta sozinha assim que ele for reativado.</font>"
                + "</center></body></html>");
    }

    private void setManutencao(boolean manutencao) {
        emManutencao = manutencao;
        BtnEnviar.setEnabled(!manutencao);
        BtnEmoji.setEnabled(!manutencao);
        setTitle("Chat - " + Util.nickname + (manutencao ? " (em manutenção)" : ""));
    }

    // Chamado pelo popup: coloca o código do emoji onde está o cursor
    private void inserirEmoji(String codigo) {
        TxtMensagem.replaceSelection(codigo + " ");
        TxtMensagem.requestFocusInWindow();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ScrConversa = new javax.swing.JScrollPane();
        EdtConversa = new javax.swing.JEditorPane();
        LblMensagem = new javax.swing.JLabel();
        TxtMensagem = new javax.swing.JTextField();
        LblModo = new javax.swing.JLabel();
        CBModo = new javax.swing.JComboBox<>();
        LblEmoji = new javax.swing.JLabel();
        BtnEmoji = new javax.swing.JButton();
        BtnEnviar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat");

        EdtConversa.setEditable(false);
        EdtConversa.setContentType("text/html"); // NOI18N
        ScrConversa.setViewportView(EdtConversa);

        LblMensagem.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        LblMensagem.setText("Mensagem");

        TxtMensagem.setToolTipText("");
        TxtMensagem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TxtMensagemKeyPressed(evt);
            }
        });

        LblModo.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        LblModo.setText("Modo:");

        CBModo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Fala", "Grita", "Xinga" }));

        LblEmoji.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        LblEmoji.setText("Emoji");

        BtnEmoji.setFont(new java.awt.Font("Liberation Sans", 0, 16)); // NOI18N
        BtnEmoji.setText("\uD83D\uDE00 Emojis");
        BtnEmoji.setToolTipText("Abre o menu de emojis");
        BtnEmoji.addActionListener(this::BtnEmojiActionPerformed);

        BtnEnviar.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        BtnEnviar.setText("ENVIAR");
        BtnEnviar.addActionListener(this::BtnEnviarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ScrConversa)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(LblModo)
                            .addComponent(LblMensagem)
                            .addComponent(LblEmoji))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TxtMensagem)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(CBModo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(BtnEmoji, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(BtnEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 351, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ScrConversa, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LblMensagem)
                    .addComponent(TxtMensagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LblModo)
                    .addComponent(CBModo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LblEmoji)
                    .addComponent(BtnEmoji, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BtnEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEnviarActionPerformed
        this.gerarEenviarMensagem();
    }//GEN-LAST:event_BtnEnviarActionPerformed

    private void BtnEmojiActionPerformed(java.awt.event.ActionEvent evt) {
        popupEmojis.show(BtnEmoji, 0, BtnEmoji.getHeight());
    }

    private void TxtMensagemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TxtMensagemKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            this.gerarEenviarMensagem();
        }
    }//GEN-LAST:event_TxtMensagemKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FrmChat().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnEmoji;
    private javax.swing.JButton BtnEnviar;
    private javax.swing.JComboBox<String> CBModo;
    private javax.swing.JEditorPane EdtConversa;
    private javax.swing.JLabel LblEmoji;
    private javax.swing.JLabel LblMensagem;
    private javax.swing.JLabel LblModo;
    private javax.swing.JScrollPane ScrConversa;
    private javax.swing.JTextField TxtMensagem;
    // End of variables declaration//GEN-END:variables
}
