# 07 · Chat v3.0: Atividade (5 pontos)

Continuação da `06_chat_v2.0`. Abra a pasta raiz do repositório no IntelliJ.
Rode `Chat_Middleware_Service_v3 > pacote.Run` e depois `Chat_Cliente_Desktop_v3 > pacote.Chat_Cliente_Desktop`.

## 1. Popup menu de emojis (substitui o combobox)
- `PopupEmojis.java`: `JPopupMenu` → `JTabbedPane` (Guia 1 **Carinhas**, Guia 2 **GIFs** animados) → `JPanel` + `GridLayout` de botões.
- Clicar num emoji insere o **código** no campo de mensagem (ex.: `:-)`, `:gif_fogo:`).
- `Emojis.java`: ao enviar, `substituir()` faz o *replace* do código pelo HTML:
  - Carinhas → entidade Unicode (`:-)` → `&#128513;`)
  - GIFs → `<img src='gifs/fogo.gif'>` (o caminho é relativo; cada cliente resolve pela própria pasta `images`)

| Código | Emoji | Código | Emoji | Código | Emoji |
|---|---|---|---|---|---|
| `:-)` | 😁 | `:-D` | 😂 | `;-)` | 😉 |
| `:-P` | 😋 | `B-)` | 😎 | `:-*` | 😘 |
| `:-\|` | 😐 | `:-O` | 😮 | `:-(` | 😞 |
| `:'(` | 😢 | `>:)` | 😈 | `:pensando:` | 🤔 |
| `<3` | 💖 | `(y)` | 👍 | `(n)` | 👎 |
| `:ok:` | 👌 | `:palmas:` | 👏 | `:fogo:` | 🔥 |
| `:festa:` | 🎉 | `:cafe:` | ☕ | | |

GIFs animados (64×64, em `src/images/gifs`):
`:gif_risada:` `:gif_rolando:` `:gif_apaixonado:` `:gif_chorando:` `:gif_pensando:` `:gif_comemorando:`
`:gif_coracao:` `:gif_fogo:` `:gif_festa:` `:gif_palmas:` `:gif_tchau:` `:gif_cem:`

> GIFs: [Noto Emoji Animation](https://googlefonts.github.io/noto-emoji-animation/) © Google, licença
> [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/). Reduzidos de 512×512 para 64×64.

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
| Figuras | o `Beijo.png` do colega é o desenho de uma **arma** | removido; as figuras viraram GIFs animados |

## Visual (telas reestruturadas)
Painel de Controle, Login e Chat usam a mesma paleta (`Estilo.java` no cliente, `CartaoServico.java` no middleware).
- **Painel:** um cartão por serviço. Serviços não implementados ficam cinza: `Util.SERVICO_..._HABILITADO = false`.
- **Login:** cor do nome, avatar (os 6 da `05_chat_v1.0`, padronizados em 64×64 e redondos) e uma prévia ao vivo.
- **Chat:** conversa em **balões** (`PainelConversa.java`): os seus à direita, os dos outros à esquerda com avatar;
  "Grita" com texto grande em tomate e "Xinga" em balão escuro com texto amarelo.

### Como os balões sabem quem mandou (sem quebrar a interoperabilidade)
Cada mensagem continua sendo o **mesmo HTML** de antes, mas começa com um **comentário HTML** com os dados:
```
<!--chat3|12:19|avatar-mulher-2.png|#FF0000|Fala|duda|texto com emojis-->  <font ...>12:19</font> <img ...> <b>duda</b> Fala: ...<br>
```
- Comentários são **invisíveis** em HTML: a v2.0 e clientes de colegas continuam mostrando a mensagem normalmente.
- O cliente v3 lê o comentário e desenha o balão. Mensagens sem ele (antigas ou de outros clientes) aparecem num balão neutro.
- O middleware v3 separa as mensagens com `\n` (também invisível em HTML). Com outro middleware, o cliente separa pelo `<br>`.
