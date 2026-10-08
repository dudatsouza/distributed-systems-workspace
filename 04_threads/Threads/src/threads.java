import java.nio.channels.MulticastChannel;

public class threads {

    public static void main(String[] args){

        // Execução na thread principal, vamos executar após o trecho de código;

        for(int i = 0 ; i < 10000; i++) {
            System.out.println("Thread Principal");
        }

        // Thread principal instancia outras threads e faz execução simultânea
        Multiexecucao Exec1 = new Multiexecucao(1);
        Thread T1 = new Thread(Exec1);

        Multiexecucao Exec2 = new Multiexecucao(2);
        Thread T2 = new Thread(Exec2);
        T1.start();
        T2.start();

        Thread T3 = new Thread(new Multiexecucao(3));
        T3.start();

        // Usando virtual threads
        Multiexecucao Exec4 = new Multiexecucao(4);
        Thread T4 = Thread.ofVirtual().unstarted(Exec4);
        T4.start();

        // Outra forma
        Thread.ofVirtual().start(new Multiexecucao(5));

        // Outra forma
        Multiexecucao Exec6 = new Multiexecucao(6);
        Thread.ofVirtual().start(Exec6);

        // Classes abstratas com apenas 1 metodo a ser implementado, podem fazê-lo via lambda. Runnable é um exemplo
        Thread T7 = new Thread(() -> {
            for(int i = 0; i < 100000; i++) {
                System.out.println("Sintaxe Lambda");
            }
        });

        T7.start();
    }
}
