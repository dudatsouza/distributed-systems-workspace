void main() {
    System.out.println("Iniciando o sistema...");

    // 1. Cria a instância da sua classe que implementa Runnable
    DesktopRecepcaoThread tarefaRecepcao = new DesktopRecepcaoThread();

    // 2. Cria uma Thread do Java e passa a sua tarefa para ela
    Thread threadRecepcao = new Thread(tarefaRecepcao);

    // 3. Inicia a execução da Thread (isso faz o método run() começar a rodar)
    threadRecepcao.start();

    System.out.println("Servidor de recepção rodando em segundo plano aguardando mensagens!");
}