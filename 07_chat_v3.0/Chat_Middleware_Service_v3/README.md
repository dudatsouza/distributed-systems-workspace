# Chat_Middleware_Service_v3 (servidor)

Migrado do projeto NetBeans `SD-v4/Chat_Middleware_Service` para IntelliJ.

## Como rodar
1. IntelliJ: **File > Open** e escolha a pasta raiz `distributed-systems-workspace` (um projeto com todas as atividades).
2. No módulo `Chat_Middleware_Service_v3`, rode `pacote.Run`. Aparece um ícone na barra de menus do Mac e o **Painel de Controle** abre.
3. Clique em **Ativar** no "Serviço Desktop".

## Como funciona
| Classe | Papel |
|---|---|
| `DesktopRecepcaoThread` | porta **6662**: recebe as mensagens dos clientes e grava no repositório |
| `DesktopEnvioThread` | porta **6661**: devolve o histórico para o cliente (PULL) |
| `Util` | portas e caminho do repositório: `~/Dados/RepositorioDesktop.txt` |
| `MenuServidor` / `FrmPainelDeControle` | ícone da bandeja e painel com os botões dos serviços |

Os botões Web, Terceiros e Publicidade ainda não fazem nada (próximas atividades).
