# GoF Criacional — Abstract Factory: meios de pagamento

G1_ProjetoComercioEletronico, SubEquipe_01, Entrega 3 (GoFs Criacionais).

O checkout escolhe **um** meio de pagamento e, a partir dali, precisa de três
coisas que têm de ser do mesmo meio: quem autoriza, o comprovante que o
comprador recebe e a política de estorno que devolve o dinheiro se a reclamação
terminar em reembolso. Um comprovante de boleto para uma autorização de cartão é
uma combinação inválida — e é exatamente isso que o Abstract Factory impede.

## Papéis no padrão

| Pasta | Classe | Papel GoF |
| -- | -- | -- |
| `fabrica` | `FabricaMeioPagamento` | AbstractFactory |
| `fabrica` | `FabricaCartao`, `FabricaPix`, `FabricaBoleto`, `FabricaSaldo` | ConcreteFactory |
| `produto` | `Autorizador`, `Comprovante`, `PoliticaEstorno` | AbstractProduct |
| `familias/*` | implementações concretas dos três produtos | ConcreteProduct |
| `cliente` | `ServicoDeCheckout` | Client |
| `cliente` | `Main` | Demonstração no terminal |
| `dominio` | `Dinheiro`, `MeioPagamento` | Apoio (Entrega 2) |
| `produto` | `Autorizacao` | Apoio (desfecho da tentativa) |

## Divisão por integrante

Cada fatia é um conjunto de arquivos próprio: ninguém edita o arquivo de
ninguém, e cada um faz o seu commit sem conflito.

| Integrante | Arquivos que cria | Situação |
| -- | -- | -- |
| **Pedro Luciano de Azevedo** | `dominio/Dinheiro.java`, `dominio/MeioPagamento.java`, `produto/Autorizacao.java`, `produto/Autorizador.java`, `produto/Comprovante.java`, `produto/PoliticaEstorno.java`, `fabrica/FabricaMeioPagamento.java` | ✅ no repositório |
| **Patrick Anderson Carvalho dos Santos** | `fabrica/FabricaCartao.java`, `familias/cartao/AutorizadorCartao.java`, `familias/cartao/ComprovanteCartao.java`, `familias/cartao/EstornoCartao.java`, `cliente/ServicoDeCheckout.java`, `cliente/Main.java` | a fazer |
| **Maria Clara Sena de Lima** | `fabrica/FabricaPix.java`, `familias/pix/AutorizadorPix.java`, `familias/pix/ComprovantePix.java`, `familias/pix/EstornoPix.java` | a fazer |
| **Guilherme Davila Rodrigues Carneiro Sampaio** | `fabrica/FabricaBoleto.java`, `familias/boleto/AutorizadorBoleto.java`, `familias/boleto/ComprovanteBoleto.java`, `familias/boleto/EstornoBoleto.java` | a fazer |
| **Guilherme Costa Zanella** | `fabrica/FabricaSaldo.java`, `familias/saldo/AutorizadorSaldo.java`, `familias/saldo/ComprovanteSaldo.java`, `familias/saldo/EstornoSaldo.java` | a fazer |

> As fábricas concretas ficam em `fabrica/` e os produtos de cada família em
> `familias/<meio>/`, com o pacote `familias.<meio>`.

## O que cada família precisa responder

Os contratos estão em `produto/` e `fabrica/`. Ao implementar, decida com base
no que a Entrega 1 observou do seu meio de pagamento — e marque como **inferido**
o que não foi observado, como a subequipe faz desde a Entrega 1.

| Contrato | Pergunta que a família responde |
| -- | -- |
| `Autorizador.autorizar` | A autorização sai na hora? Pode ser recusada? |
| `Autorizador.prazoLimiteEmMinutos` | Quanto tempo o comprador tem antes de o pedido expirar (`after(tempo limite)` da Máquina de Estados) |
| `Autorizador.ehAssincrono` | O desfecho chega depois (boleto) ou na hora (Pix, saldo)? |
| `Comprovante.gerar` / `formato` | O que o comprador recebe: QR Code, linha digitável, recibo de parcelamento |
| `PoliticaEstorno.podeEstornar` | O estorno volta pelo mesmo caminho? |
| `PoliticaEstorno.prazoDeDevolucaoEmDias` | Em quantos dias o dinheiro reaparece |

## Como compilar e executar

Pré-requisito: **JDK 8 ou superior** (`javac -version`).

```bash
# a partir desta pasta
make run
```

Sem `make`:

```bash
javac -encoding UTF-8 -d out $(find dominio produto fabrica familias cliente -name "*.java")
java -cp out cliente.Main
```

> Enquanto as quatro famílias não estiverem no repositório, só os contratos
> compilam — `cliente.Main` entra junto com a fatia do Patrick.

## Rastreabilidade

| Elemento | Origem na Entrega 1 / Entrega 2 |
| -- | -- |
| `MeioPagamento` (4 constantes) | Enumeração `MeioPagamento` do Diagrama de Classes (Entrega 2) |
| `Dinheiro` em centavos | Decisão 10 do Diagrama de Classes; padrão *Money* (FOWLER, 2002) |
| `Autorizacao.Status` | Estados `Pago`, `Recusado` e `Expirado` da Máquina de Estados do Pedido |
| `Autorizador.prazoLimiteEmMinutos` | Evento de borda de temporizador do BPMN 1; `after(tempo limite)` |
| `Autorizador.autorizar` | Tarefa *Autorizar pagamento* do BPMN 1; estado `Autorizando` |
| `PoliticaEstorno` | Fluxo de reclamação do Rich Picture; *Estornar pagamento via operadora* no Diagrama de Atividades; transição `Em disputa → Reembolsado` |
| `ServicoDeCheckout` | Componente `Serviço de Checkout e Pedidos` do Diagrama de Componentes |

## Referências

GAMMA, Erich; HELM, Richard; JOHNSON, Ralph; VLISSIDES, John. **Padrões de Projeto: soluções reutilizáveis de software orientado a objetos**. Porto Alegre: Bookman, 2000.

FOWLER, Martin. **Patterns of Enterprise Application Architecture**. Boston: Addison-Wesley, 2002.
