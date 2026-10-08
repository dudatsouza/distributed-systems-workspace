public class Multiexecucao implements Runnable{
    int valor = 0;
    public Multiexecucao(int dado) {
        this.valor = dado;
    }

    @Override
    public void run() {
        // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        for (int i = 0; i < 100000; i++) {
            System.out.println("Impresso pela Thread: " + this.valor);
        }
    }
}