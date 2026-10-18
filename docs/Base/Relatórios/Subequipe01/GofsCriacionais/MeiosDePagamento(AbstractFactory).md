# GoF Criacional — Abstract Factory: meios de pagamento

Conforme a divisão registrada na [reunião de 18/10/2026](/ReunioesAtas/Subequipe1/Ata18_10.md), este é o artefato criacional da Subequipe 01. O **dono do artefato** é **Patrick Anderson Carvalho dos Santos** (modelagem, cliente, demonstração e vídeo), e cada um dos outros quatro integrantes implementa e documenta uma família de produtos.

---

## 1. Contexto: o problema

Quatro achados independentes das entregas anteriores convergem para o mesmo ponto:

| Achado | Origem |
| -- | -- |
| Os meios de pagamento são quatro: cartão, Pix, boleto e saldo em conta | Enumeração `MeioPagamento`, Diagrama de Classes (Entrega 2) |
| A autorização pode ser aprovada, recusada ou expirar por tempo | Eventos de borda do BPMN 1 (Entrega 1); estados `Autorizando`, `Pago`, `Recusado` e `Expirado` (Entrega 2) |
| O comprovante muda de formato conforme o meio | Recorte B da Engenharia Reversa (Entrega 1) |
| A reclamação pode terminar em estorno, pelo caminho por onde o dinheiro veio | Rich Picture e Diagrama de Atividades; transição `Em disputa → Reembolsado` |

Juntando-os, aparece o problema: escolhido um meio de pagamento, o checkout precisa de **três objetos que têm obrigatoriamente de ser do mesmo meio** — quem autoriza, o comprovante que o comprador recebe e a política de estorno. Um comprovante de boleto para uma autorização de cartão é uma combinação inválida que o código tem de tornar impossível, e não apenas improvável.

---

## 2. Fundamentação: o padrão Abstract Factory

**Intenção.** "Fornecer uma interface para criação de famílias de objetos relacionados ou dependentes sem especificar suas classes concretas" (GAMMA et al., 2000).

**Por que ele, e não o Factory Method.** O Factory Method criaria *um* produto por meio de pagamento, e não daria conta da coerência entre os três. O que torna o Abstract Factory necessário aqui é a `PoliticaEstorno`: o caminho de volta do dinheiro depende do caminho de ida, e os dois objetos precisam nascer juntos.

> _Patrick: completar com as consequências descritas pelo GoF e com o contraste em relação ao Builder e ao Prototype._

---

## 3. Modelagem (Notação UML)

> _A preencher por **Patrick Anderson Carvalho dos Santos**: modelagem genérica do padrão (AbstractFactory, ConcreteFactory, AbstractProduct, ConcreteProduct, Client) e modelagem aplicada ao G1, com a legenda de rastreabilidade embutida._

---

## 4. Papéis no padrão

| Papel GoF | Classe | Autor da fatia |
| -- | -- | -- |
| AbstractFactory | `FabricaMeioPagamento` | Pedro Luciano de Azevedo |
| AbstractProduct | `Autorizador`, `Comprovante`, `PoliticaEstorno` | Pedro Luciano de Azevedo |
| ConcreteFactory + ConcreteProduct | Família **Cartão de crédito** | Patrick Anderson Carvalho dos Santos |
| ConcreteFactory + ConcreteProduct | Família **Pix** | Maria Clara Sena de Lima |
| ConcreteFactory + ConcreteProduct | Família **Boleto** | Guilherme Davila Rodrigues Carneiro Sampaio |
| ConcreteFactory + ConcreteProduct | Família **Saldo em conta** | Guilherme Costa Zanella |
| Client | `ServicoDeCheckout`, `Main` | Patrick Anderson Carvalho dos Santos |

Código no repositório: [`src/criacionais/MeiosDePagamento(AbstractFactory)`](https://github.com/UnBArqDsw2026-2-Turma01/2026.2-T01-_G1_ProjetoComercioEletronico_Entrega_03/tree/main/docs/Base/Relat%C3%B3rios/Subequipe01/src/criacionais)

---

## 5. Implementação, fatia por fatia

### 5.1. Contratos: fábrica e produtos abstratos — Pedro Luciano de Azevedo

#### A fábrica abstrata

```java
public interface FabricaMeioPagamento {
    MeioPagamento meio();
    Autorizador criarAutorizador();
    Comprovante criarComprovante();
    PoliticaEstorno criarPoliticaEstorno();
}
```

Três métodos de criação, um por produto da família. O cliente escolhe a fábrica uma única vez, no início do checkout, e dali em diante **nunca mais decide** qual autorizador, comprovante ou política de estorno usar — é isso que torna impossível, e não apenas improvável, combinar produtos de famílias diferentes.

O método `meio()` não cria nada: devolve a constante de `MeioPagamento` que a família atende. Ele existe para o cliente registrar no pedido qual caminho foi seguido, sem precisar de `instanceof`.

**A seleção da fábrica fica no cliente, não na interface.** Seria cômodo pôr aqui um `static FabricaMeioPagamento para(MeioPagamento meio)`, mas isso faria a fábrica abstrata conhecer todas as suas implementações concretas — exatamente a dependência que o padrão existe para eliminar. A escolha vive em `ServicoDeCheckout`, e está documentada na seção 5.6.

#### Os três produtos abstratos

```java
public interface Autorizador {
    Autorizacao autorizar(String pedido, Dinheiro valor);
    int prazoLimiteEmMinutos();
    boolean ehAssincrono();
}
```

Os dois métodos além de `autorizar` vêm diretamente da Entrega 1, e não de conveniência de projeto:

- **`prazoLimiteEmMinutos()`** é o evento de borda de temporizador que o BPMN 1 colocou sobre *Informar meio de pagamento*, e que a Máquina de Estados do Pedido traduziu como `after(tempo limite) → Expirado`. O prazo varia com o meio — um boleto não expira no mesmo tempo que um Pix —, então pertence à família, e não ao checkout.
- **`ehAssincrono()`** distingue o meio cujo desfecho chega depois (boleto) daquele que responde na hora (Pix, saldo). É a diferença que decide se o pedido vai direto a `Pago` ou fica aguardando confirmação externa.

```java
public interface Comprovante {
    String gerar(String pedido, Dinheiro valor, Autorizacao autorizacao);
    String formato();
}
```

`formato()` devolve o tipo do comprovante — "QR Code", "linha digitável", "recibo de parcelamento". É o método que torna visível, na demonstração, que o comprovante acompanha o meio: trocada a fábrica, o formato muda sem que o cliente peça.

```java
public interface PoliticaEstorno {
    boolean podeEstornar(Autorizacao autorizacao);
    String estornar(String pedido, Dinheiro valor);
    int prazoDeDevolucaoEmDias();
}
```

**Este é o produto que torna o Abstract Factory necessário.** Com apenas autorizador e comprovante, um Factory Method por meio resolveria. O estorno muda o quadro porque o caminho de volta do dinheiro **depende do caminho de ida**: não existe "estornar um boleto" — a devolução é por depósito; já o saldo em conta volta na hora. Autorizador e política de estorno precisam nascer juntos, e é exatamente isso que uma fábrica de famílias garante.

A origem está no fluxo de reclamação do Rich Picture (Entrega 1), na ação *Estornar pagamento via operadora* do Diagrama de Atividades e na transição `Em disputa → Reembolsado` da Máquina de Estados (Entrega 2) — três artefatos, de três autores diferentes, apontando para a mesma operação.

#### A classe de apoio `Autorizacao`

```java
public final class Autorizacao {
    public enum Status { APROVADO, RECUSADO, EXPIRADO }
    public static Autorizacao aprovada(String identificador, String detalhe) { ... }
    public static Autorizacao recusada(String detalhe) { ... }
    public static Autorizacao expirada(String detalhe) { ... }
}
```

Os três estados não foram inventados: são os desfechos que a Máquina de Estados do Pedido já registrava. `APROVADO` leva a `Pago`, `RECUSADO` ao estado `Recusado` (evento de borda de erro do BPMN 1) e `EXPIRADO` ao estado `Expirado` (evento de borda de temporizador). As fábricas estáticas existem para que cada família não precise repetir a montagem do objeto.

#### Domínio reaproveitado da Entrega 2

`Dinheiro` (valor em centavos, com `parcelar(n)` sem perda de arredondamento) e `MeioPagamento` (as quatro constantes) vêm do Diagrama de Classes da Entrega 2, decisões 10 e 8 respectivamente. Nenhum dos dois foi criado para esta entrega — e é esse reaproveitamento que mantém o elo entre os três módulos do projeto.

### 5.2. Família Cartão de crédito — Patrick Anderson Carvalho dos Santos

> _A preencher: autorização com os três desfechos, comprovante com parcelamento (`Dinheiro.parcelar`) e estorno pela operadora; trecho de código; elo com o BPMN 1 e com a Máquina de Estados do Pedido._

### 5.3. Família Pix — Maria Clara Sena de Lima

> _A preencher: autorização imediata, comprovante em QR Code e estorno por devolução; trecho de código; o que foi observado e o que é inferido._

### 5.4. Família Boleto — Guilherme Davila Rodrigues Carneiro Sampaio

> _A preencher: autorização assíncrona (compensação), comprovante com linha digitável e estorno por depósito; trecho de código; elo com o componente externo `Operadora de Pagamento` do Diagrama de Componentes._

### 5.5. Família Saldo em conta — Guilherme Costa Zanella

> _A preencher: autorização contra o saldo, comprovante de débito em conta e estorno imediato; trecho de código; elo com o fluxo de reclamação e mediação do Diagrama de Atividades, em que o estorno é um dos dois desfechos._

### 5.6. Cliente: `ServicoDeCheckout` — Patrick Anderson Carvalho dos Santos

> _A preencher: como o cliente escolhe a família uma única vez e nunca mais decide qual produto usar; trecho de código; por que a seleção da fábrica fica no cliente e não na interface da fábrica._

---

## 6. Execução (vídeo com o padrão em execução)

> _A preencher por **Patrick Anderson Carvalho dos Santos**: vídeo mostrando o mesmo pedido pago pelos quatro meios, com autorização, comprovante e estorno coerentes em cada família._

---

## 7. Rastreabilidade

| Elemento do código | Vem de | Entrega |
| -- | -- | -- |
| `MeioPagamento` com quatro constantes | Enumeração do Diagrama de Classes | 2 |
| `Dinheiro` em centavos e `parcelar` | Decisão 10 do Diagrama de Classes; padrão *Money* | 2 |
| `Autorizacao.Status` | Estados `Pago`, `Recusado` e `Expirado` da Máquina de Estados | 2 |
| `Autorizador.prazoLimiteEmMinutos` | Evento de borda de temporizador do BPMN 1; `after(tempo limite)` | 1 e 2 |
| `Autorizador.ehAssincrono` | Recorte B — o checkout foi interrompido antes da tela de pagamento (limite declarado) | 1 |
| `PoliticaEstorno` | Rich Picture; *Estornar pagamento via operadora* no Diagrama de Atividades | 1 e 2 |
| `ServicoDeCheckout` | Componente `Serviço de Checkout e Pedidos` | 2 |

---

## 8. Visão crítica

> _A preencher coletivamente. Ponto de partida registrado na reunião de 18/10: o Recorte B da Engenharia Reversa **parou antes da tela de pagamento**, porque avançar exigiria uma compra real. Tudo o que este artefato afirma sobre o comportamento interno de cada meio é, portanto, inferido — e precisa estar marcado como tal, como a subequipe faz desde a Entrega 1._

---

## 9. Referências

GAMMA, Erich; HELM, Richard; JOHNSON, Ralph; VLISSIDES, John. **Padrões de Projeto: soluções reutilizáveis de software orientado a objetos**. Porto Alegre: Bookman, 2000.

FOWLER, Martin. **Patterns of Enterprise Application Architecture**. Boston: Addison-Wesley, 2002.

---

## Histórico de Versões

| Versão | Data | Descrição | Autor(es) | Revisor(es) |
| -- | -- | -- | -- | -- |
| 1.0 | 18/10/2026 | Criação da página: contexto, fundamentação, papéis no padrão e estrutura das seções por integrante | Pedro Luciano de Azevedo | -- |
| 1.1 | 18/10/2026 | Documentação dos contratos: fábrica abstrata, os três produtos abstratos, a classe de apoio `Autorizacao` e o domínio reaproveitado da Entrega 2 | Pedro Luciano de Azevedo | -- |
