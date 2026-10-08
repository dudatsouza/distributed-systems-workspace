# 07 · Chat v3.0: Atividade (5 pontos)

Continuação da `06_chat_v2.0`. Abra a pasta raiz do repositório no IntelliJ.
Rode `Chat_Middleware_Service_v3 > pacote.Run` e depois `Chat_Cliente_Desktop_v3 > pacote.Chat_Cliente_Desktop`.

## 1. Popup menu de emojis (substitui o combobox)
- `PopupEmojis.java`: `JPopupMenu` → `JTabbedPane` (Guia 1 **Carinhas**, Guia 2 **Figuras**) → `JPanel` + `GridLayout` de botões.
- Clicar num emoji insere o **código** no campo de mensagem (ex.: `:-)`, `:coracao:`).
- `Emojis.java`: ao enviar, `substituir()` faz o *replace* do código pelo HTML:
  - Carinhas → entidade Unicode (`:-)` → `&#128513;`)
  - Figuras → `<img src='Coracao.png'>` (o caminho é relativo; cada cliente resolve pela própria pasta `images`)

| Código | Emoji | Código | Emoji | Código | Emoji |
|---|---|---|---|---|---|
| `:-)` | 😁 | `:-D` | 😂 | `;-)` | 😉 |
| `:-P` | 😋 | `B-)` | 😎 | `:-*` | 😘 |
| `:-\|` | 😐 | `:-O` | 😮 | `:-(` | 😞 |
| `:'(` | 😢 | `>:)` | 😈 | `:pensando:` | 🤔 |
| `<3` | 💖 | `(y)` | 👍 | `(n)` | 👎 |
| `:ok:` | 👌 | `:palmas:` | 👏 | `:fogo:` | 🔥 |
| `:festa:` | 🎉 | `:cafe:` | ☕ | | |

Figuras: `:coracao:` `:dinheiro:` `:lanterna:` `:flash:` `:batman:`

## 2. "Em manutenção..." ao parar o servidor (interoperabilidade)
Middleware e cliente combinam uma mensagem de protocolo: `Util.MSG_MANUTENCAO = "#MANUTENCAO#"`.
- **Desativar** no Painel: `DesktopRecepcaoThread` para de receber e `Util.emManutencao = true`.
  A `DesktopEnvioThread` continua no ar respondendo `#MANUTENCAO#` em vez do histórico.
- O cliente, ao receber `#MANUTENCAO#`, troca o painel de conversa pelo aviso **"Em manutenção..."**
  e desabilita Enviar/Emojis.
- Se o middleware for **fechado de vez** (sem conexão), o cliente mostra o mesmo aviso.
- **Ativar** de novo: a conversa volta sozinha, com o histórico.

## 3. Análise das mensagens (cores, avatares, nicknames, modos)
Problemas encontrados na versão anterior e corrigidos:
| Item | Problema | Correção |
|---|---|---|
| Cores | `Tomato` não existe no HTML do Swing; o "Grita" saía **preto** | cores em hexadecimal (`#FF6347`) |
| Cores | "Xinga" amarelo no fundo branco ficava ilegível | amarelo sobre faixa preta |
| Cores | o "Azul" da mensagem era diferente do azul do botão no login | mesma cor (`#0000CC`) nos dois |
| Avatares | o 2º avatar mostrava o Flash e enviava o Batman | corrigido |
| Avatares | o caminho da imagem era do PC do colega | caminho relativo + `setBase` |
| Nickname | vazio/só espaços passava; podia ter HTML (`<b>`) | `trim`, máx. 20 caracteres, HTML escapado |
| Texto | dava para injetar HTML (`<font size=7>`) | texto escapado antes do replace dos emojis |
| Modos | no "Grita", `:fogo:` virava `:FOGO:` e o emoji não era trocado | replace sem diferenciar maiúsculas |
| Modos | as tags `<b><i><u>` fechavam fora de ordem | fechadas na ordem certa |
| Emojis | `:-\|` mostrava 🗿 (moai) | 😐 |
| Mensagem | mensagem vazia era enviada; sem limite de tamanho | ignorada; máx. 500 caracteres |
| Mensagem | sem horário | `[HH:mm]` no início |
| Figuras | o `Beijo.png` do colega é o desenho de uma **arma** | removido (o beijo é o 😘 `:-*`) |
