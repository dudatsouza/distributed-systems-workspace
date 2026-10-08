package pacote;

import java.awt.event.KeyEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class FrmChat extends javax.swing.JFrame {
    public String msg = "";
    public void gerarEenviarMensagem(){
        if (TxtMensagem.getText().isBlank() && CBEmoji.getSelectedIndex() == 0) {
            return;
        }
        // As imagens vão com caminho relativo; cada cliente resolve pela própria pasta images (ver setBase no construtor)
        this.msg = "";
        this.msg += "<img src='" + Util.avatar + "' width='20' height='20'>";
        this.msg += "<font color='" + Util.cor + "'> " + Util.nickname + " </font>";
        if(CBModo.getSelectedItem().toString().equals("Fala")){
            this.msg += "<b> Fala: </b>";
            this.msg += TxtMensagem.getText();
        } else if(CBModo.getSelectedItem().toString().equals("Grita")){
            this.msg += "<b><i><u> Grita: </u></i></b>";
            this.msg += "<font color='Tomato' size='+1'>" + TxtMensagem.getText().toUpperCase() + "</font>";
        } else if(CBModo.getSelectedItem().toString().equals("Xinga")){
            this.msg += "<b> Xinga: </b>";
            this.msg += "<font color='Yellow' size='+3'>" + TxtMensagem.getText().toUpperCase() + "</font>";
        }
        if (CBEmoji.getSelectedItem().toString().equals("Coração")) {
            this.msg += " <img src='Coracao.png' width='20' height='20'>";
        } else if (CBEmoji.getSelectedItem().toString().equals("Dinheiro")) {
            this.msg += " <img src='Dinheiro.png' width='20' height='20'>";
        } else if (CBEmoji.getSelectedItem().toString().equals("Beijo")) {
            this.msg += " <img src='Beijo.png' width='20' height='20'>";
        }
        this.msg += "<br>";
        ArrayList<String> codigos = new ArrayList<String>();
        ArrayList<String> simbolos = new ArrayList<String>();
        codigos.add(":-)");
        simbolos.add("&#128513;");
        codigos.add(";-)");
        simbolos.add("&#128521;");
        codigos.add(":-|");
        simbolos.add("&#128511;");
        codigos.add(":-P");
        simbolos.add("&#128523;");
        codigos.add(">:)");
        simbolos.add("&#128520;");
        codigos.add("B-)");
        simbolos.add("&#128526;");
        codigos.add(":-(");
        simbolos.add("&#128531;");
        for(int i=0;i<codigos.size();i++){
            this.msg = this.msg.replace(codigos.get(i),simbolos.get(i));
        }
        try{
            Socket cliente = new Socket(Util.ServidorIP,Util.PortaRecepcaoDesktop);
            ObjectOutputStream output = new ObjectOutputStream(cliente.getOutputStream());
            output.writeUTF(this.msg);
            output.close();
            cliente.close();
            TxtMensagem.setText("");
            CBEmoji.setSelectedIndex(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Erro no Cliente ao enviar:" + e.getMessage());
            e.printStackTrace();
        }
    }
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmChat.class.getName());
    private String ultimoHistorico = "";
    public FrmChat() {
        initComponents();
        setTitle("Chat - " + Util.nickname);
        ((HTMLDocument) EdtConversa.getDocument()).setBase(FrmChat.class.getResource("/images/"));
        // Thread de recepção (PULL): a cada 1s pede o histórico ao middleware
        Thread.ofVirtual().start(()->{
            boolean avisouErro = false;
            while (true){
                try{
                    Socket cliente = new Socket(Util.ServidorIP,Util.PortaEnvioDesktop);
                    ObjectInputStream input = new ObjectInputStream(cliente.getInputStream());
                    String msgs = input.readUTF();
                    input.close();
                    cliente.close();
                    avisouErro = false;
                    if (!msgs.equals(ultimoHistorico)) {
                        ultimoHistorico = msgs;
                        SwingUtilities.invokeLater(() -> mostrarConversa(msgs));
                    }
                } catch (Exception e){
                    // Mostra o erro só uma vez e continua tentando (ex.: middleware ainda não foi ativado)
                    if (!avisouErro) {
                        avisouErro = true;
                        JOptionPane.showMessageDialog(null,"Erro ao receber mensagens do servidor " + Util.ServidorIP + ":\n" + e.getMessage() + "\nO cliente vai continuar tentando.");
                        e.printStackTrace();
                    }
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
            HTMLDocument doc = (HTMLDocument) EdtConversa.getDocument();
            HTMLEditorKit kit = (HTMLEditorKit) EdtConversa.getEditorKit();
            doc.remove(0, doc.getLength());
            kit.insertHTML(doc,doc.getLength(),msgs,0,0,null); // 0,0,null = ponto inicial de inserção, niveis de tags a serem ignorados, tag para iniciar edição
            EdtConversa.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        CBEmoji = new javax.swing.JComboBox<>();
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

        CBEmoji.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nenhum", "Coração", "Dinheiro", "Beijo" }));

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
                                    .addComponent(CBEmoji, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(CBEmoji, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BtnEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEnviarActionPerformed
        this.gerarEenviarMensagem();
    }//GEN-LAST:event_BtnEnviarActionPerformed

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
    private javax.swing.JButton BtnEnviar;
    private javax.swing.JComboBox<String> CBEmoji;
    private javax.swing.JComboBox<String> CBModo;
    private javax.swing.JEditorPane EdtConversa;
    private javax.swing.JLabel LblEmoji;
    private javax.swing.JLabel LblMensagem;
    private javax.swing.JLabel LblModo;
    private javax.swing.JScrollPane ScrConversa;
    private javax.swing.JTextField TxtMensagem;
    // End of variables declaration//GEN-END:variables
}
