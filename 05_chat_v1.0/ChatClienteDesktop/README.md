# Chat Cliente Desktop (Sistemas Distribuídos): versão 1.0, substituída pela 06_chat_v2.0

Chat em Java (Swing + Sockets TCP), sem bibliotecas externas.

## Estrutura

```
src/chat/
├── comum/Mensagem.java          -> protocolo: TIPO|NICK|COR|AVATAR|MODO|TEXTO
├── servidor/
│   ├── Servidor.java            -> middleware: aceita conexões e faz PUSH (broadcast)
│   ├── ClienteHandler.java      -> 1 thread por cliente conectado
│   └── Repositorio.java         -> guarda o histórico em historico.txt
└── cliente/
    ├── LoginFrame.java          -> tela de login (nickname, cor, avatar)  [main]
    ├── ChatFrame.java           -> tela do chat (MSG, Modo, Emoji, Envia)
    └── Conexao.java             -> thread de ENVIO + thread de RECEPÇÃO
```

## Como rodar no IntelliJ

1. **File > Open** e escolha a pasta raiz `distributed-systems-workspace`.
2. Rode **`chat.servidor.Servidor`**.
3. Rode **`chat.cliente.LoginFrame`**. Para abrir vários clientes:
   Run > Edit Configurations > LoginFrame > **Modify options > Allow multiple instances**.
4. Para testar entre computadores do laboratório, digite o IP da máquina do servidor
   no campo "Servidor" do login (porta 5000 liberada no firewall).

## Relação com o quadro

| Quadro                    | Código                                                        |
|---------------------------|---------------------------------------------------------------|
| Repositório               | `Repositorio` (arquivo `historico.txt`)                       |
| Middleware                | `Servidor` + `ClienteHandler`                                 |
| Thread envio / recepção   | as duas threads de `Conexao`                                   |
| PUSH                      | servidor empurra cada mensagem para todos (`broadcast`)        |
| PULL                      | ao conectar, o cliente recebe o histórico do repositório       |
| Cliente gordo/magro       | cliente é "magro": o servidor só repassa; a formatação (cor, modo) é feita no cliente |
