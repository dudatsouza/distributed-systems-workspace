# Sistemas Distribuídos: atividades

Atividades da disciplina de Sistemas Distribuídos, em **Java** (Swing + Sockets), feitas no **IntelliJ IDEA**.

## Organização

O repositório inteiro é **um projeto do IntelliJ**: abra a pasta `distributed-systems-workspace` com **File > Open**.
Cada pasta `NN_tema` é uma atividade (a numeração segue a ordem das aulas) e tem um ou mais **módulos**.
Os módulos dos chats levam a versão no nome (`_v2`, `_v3`), porque o IntelliJ exige nomes únicos.

| Pasta | Conteúdo | Módulos | Como rodar |
|---|---|---|---|
| [`01_sockets_tcp`](01_sockets_tcp) | Cliente/servidor TCP com `ObjectInput/OutputStream` (porta 3322) | `Sockets01_Server`, `Sockets01_Client` | rode o `Main` do servidor e depois o do cliente |
| [`02_sockets_udp`](02_sockets_udp) | UDP ponto a ponto e UDP Multicast (grupo `239.0.0.10`) | `Sockets02_UDP` | `Sockets02_Receiver_UDP` + `Sockets02_Sender_UDP`; `Sockets03_Receiver_UDP_Multicast` + `Sockets03_Sender_UDP_Multicast` |
| [`03_urna_eletronica`](03_urna_eletronica) | Urna eletrônica distribuída (TCP + UDP multicast) | `Atividade_Urna` | `CentralVotacao` e depois `UrnaEletronica` |
| [`04_threads`](04_threads) | Threads, virtual threads (Java 21+) e primeira thread de recepção do chat | `Threads` | `threads` (exemplos) ou `PainelControle` |
| [`05_chat_v1.0`](05_chat_v1.0) | Primeiras versões do chat (**substituídas** pela v2.0) | `ChatClienteDesktop`, `Chat_Client_Desktop` | `chat.servidor.Servidor` + `chat.cliente.LoginFrame` |
| [`06_chat_v2.0`](06_chat_v2.0) | Chat com middleware: repositório, threads de envio/recepção, PULL a cada 1 s | `Chat_Middleware_Service_v2`, `Chat_Cliente_Desktop_v2` | `pacote.Run` → Ativar "Serviço Desktop" → `pacote.Chat_Cliente_Desktop` |
| [`07_chat_v3.0`](07_chat_v3.0) | **Versão atual.** Popup de emojis, aviso "Em manutenção..." e revisão das mensagens ([detalhes](07_chat_v3.0/README.md)) | `Chat_Middleware_Service_v3`, `Chat_Cliente_Desktop_v3` | igual à v2.0 |
| [`entregas`](entregas) | `.zip` entregues da urna e de threads | | |

## Requisitos

- JDK **26** ou mais recente (os projetos usam `void main()` e virtual threads)
- IntelliJ IDEA. Se ele pedir o SDK, escolha o JDK instalado em **File > Project Structure > SDK**.

## Estrutura

```
distributed-systems-workspace/
├── .idea/                          ← projeto IntelliJ (único)
├── 01_sockets_tcp/
│   ├── Sockets01_Client/           ← módulo (Sockets01_Client.iml + src/)
│   └── Sockets01_Server/
├── 02_sockets_udp/Sockets02_UDP/
├── 03_urna_eletronica/Atividade_Urna/
├── 04_threads/Threads/
├── 05_chat_v1.0/{ChatClienteDesktop, Chat_Client_Desktop}/
├── 06_chat_v2.0/{Chat_Cliente_Desktop_v2, Chat_Middleware_Service_v2}/
├── 07_chat_v3.0/{Chat_Cliente_Desktop_v3, Chat_Middleware_Service_v3}/
└── entregas/
```

> As versões 2.0 e 3.0 do chat usam as mesmas portas (6661/6662): rode **uma versão por vez**.
