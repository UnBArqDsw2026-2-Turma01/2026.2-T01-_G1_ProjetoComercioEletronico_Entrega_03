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

A família Pix existe na demonstração para fazer **contraste com o boleto**. Os dois meios respondem às mesmas três perguntas da `FabricaMeioPagamento` com respostas opostas. Esse contraste mostra que a fábrica abstrata separa famílias diferentes de fato, e não variações cosméticas de uma mesma família.

| Produto | Classe | Resposta do Pix | Contraste com o boleto |
| -- | -- | -- | -- |
| Fábrica concreta | `FabricaPix` | `meio()` devolve `MeioPagamento.PIX` | `FabricaBoleto` |
| Autorizador | `AutorizadorPix` | Síncrono: `ehAssincrono()` é `false` e o desfecho volta na mesma chamada | O boleto fica em `Autorizando` até a compensação |
| Comprovante | `ComprovantePix` | `formato()` é `"QR Code"` e o texto cita o identificador da transação gerado pelo `AutorizadorPix` | Linha digitável |
| Política de estorno | `EstornoPix` | Devolução para a conta de origem, no mesmo dia (`prazoDeDevolucaoEmDias()` é `0`) | Depósito em conta informada pelo comprador |

**A fábrica.** É a única classe da família que o `ServicoDeCheckout` chega a ver, e mesmo assim só como `FabricaMeioPagamento`. As classes do pacote `familias.pix` ficam invisíveis para o cliente:

```java
public final class FabricaPix implements FabricaMeioPagamento {

    @Override public MeioPagamento meio()                   { return MeioPagamento.PIX; }
    @Override public Autorizador criarAutorizador()         { return new AutorizadorPix(); }
    @Override public Comprovante criarComprovante()         { return new ComprovantePix(); }
    @Override public PoliticaEstorno criarPoliticaEstorno() { return new EstornoPix(); }
}
```

**De onde vem o Pix como família.** O Pix não aparece pela primeira vez nesta entrega. Na Entrega 2, ele é uma das quatro constantes da enumeração `MeioPagamento` no Diagrama de Classes da Subequipe 01. No [Diagrama de Classes da Subequipe 03](https://unbarqdsw2026-2-turma01.github.io/2026.2-T01-_G1_ProjetoComercioEletronico_Entrega_02/#/Base/Relat%C3%B3rios/Subequipe03/ModelagemEstatica/DiagramaDeClasses), ele já tinha comportamento próprio: um `ProcessadorPix` separado do `ProcessadorCartao`, para que `Pagamento` não precisasse de `if metodo == PIX ... else if metodo == CARTAO`. O `AutorizadorPix` é esse processador. A diferença é que agora ele não vem sozinho: nasce na mesma fábrica que o comprovante e o estorno que precisam combinar com ele.

**Autorização imediata.** No Pix o desfecho volta na mesma chamada. Por isso o pedido passa direto de `Autorizando` para `Pago`, ou para `Recusado`, na [Máquina de Estados do Pedido](https://unbarqdsw2026-2-turma01.github.io/2026.2-T01-_G1_ProjetoComercioEletronico_Entrega_02/#/Base/Relat%C3%B3rios/Subequipe01/ModelagemDinamica/DiagramaDeMaquinaDeEstados) (Entrega 2), sem ficar esperando compensação:

```java
@Override
public Autorizacao autorizar(String pedido, Dinheiro valor) {
    if (valor.centavos() == 0) {
        return Autorizacao.recusada("Pix sem valor nao pode ser gerado");
    }
    return Autorizacao.aprovada("PIX-" + pedido,
            "Pix confirmado na hora; sem espera por compensacao");
}

@Override public int prazoLimiteEmMinutos() { return PRAZO_DE_DEMONSTRACAO_EM_MINUTOS; }
@Override public boolean ehAssincrono()     { return false; }
```

**Por que o prazo é "de demonstração".** A Máquina de Estados do Pedido deixou `after(tempo limite)` **sem valor numérico de propósito**: "escrever `after(30min)` seria inventar um número" (Entrega 2, decisão 2). O prazo nunca foi observado, porque o Recorte B da Entrega 1 parou antes da tela de pagamento. Como o contrato `Autorizador` exige um `int`, a família devolve uma constante cujo nome diz o que ela é: um parâmetro para a demonstração rodar, e não uma afirmação sobre a plataforma. O mesmo vale para `prazoDeDevolucaoEmDias()` no `EstornoPix`.

**Por que comprovante e estorno precisam ser da mesma família.** O `ComprovantePix` imprime o identificador que o `AutorizadorPix` gerou. O `EstornoPix` devolve o dinheiro para a conta de origem e só aceita estornar quando o Pix original foi confirmado:

```java
@Override
public boolean podeEstornar(Autorizacao autorizacao) {
    // So existe o que devolver se o Pix original foi confirmado.
    return autorizacao.aprovada();
}
```

Um `ComprovantePix` aplicado a uma autorização de boleto apontaria para uma transação Pix que não existe. Um `EstornoPix` aplicado a um cartão devolveria o dinheiro por um caminho diferente do de ida. São exatamente as combinações que a `FabricaPix` impede ao criar os três produtos juntos.

**Saída da família em execução**: chamada isolada dos três produtos, antes de o `Main` integrar as quatro famílias, para o pedido `PED-1001` de R$ 159,90:

```text
Pix | assincrono=false | prazo=30min | QR Code
APROVADO (PIX-PED-1001) - Pix confirmado na hora; sem espera por compensacao
Comprovante Pix
  Pedido ........: PED-1001
  Valor .........: R$ 159,90
  Transacao .....: PIX-PED-1001
  QR Code .......: [PIX-PED-1001|15990]
  Situacao ......: pago na hora
Devolucao Pix de R$ 159,90 do pedido PED-1001 enviada para a conta de origem
```

**De onde vem cada decisão da família.** Cada elemento tem uma de três fontes: um modelo das Entregas 1 e 2, uma decisão da reunião de 18/10 ou um parâmetro de demonstração. Os parâmetros de demonstração existem só porque o contrato exige um valor que nenhuma entrega observou.

| Elemento | Fonte |
| -- | -- |
| Pix como família própria | Enumeração `MeioPagamento`, Diagrama de Classes da Subequipe 01 (Entrega 2); `ProcessadorPix`, Diagrama de Classes da Subequipe 03 (Entrega 2) |
| Desfechos `APROVADO` e `RECUSADO` | Estados `Pago` e `Recusado` da Máquina de Estados do Pedido (Entrega 2); evento de borda de erro do BPMN 1 (Entrega 1) |
| Existência de um prazo para pagar | `after(tempo limite) → Expirado` na Máquina de Estados do Pedido; evento de borda de temporizador do BPMN 1 |
| Estorno pelo caminho de ida | Estorno via instituição financeira no Rich Picture (Entrega 1); *Estornar pagamento via operadora* por `IPagamento` no Diagrama de Atividades e no de Componentes; `Em disputa → Reembolsado / estornar pagamento` (Entrega 2) |
| `ehAssincrono() == false` e formato `"QR Code"` | Contratos fixados na [reunião de 18/10/2026](/ReunioesAtas/Subequipe1/Ata18_10.md): o Pix é o contraste com o boleto |
| 30 minutos de prazo e devolução em 0 dias | Parâmetros de demonstração. O valor real não foi observado, porque o Recorte B parou antes da tela de pagamento, e a Entrega 2 deixou `after(tempo limite)` sem número pelo mesmo motivo |
| Conteúdo do QR Code e identificador `PIX-<pedido>` | Simulação, para que a demonstração seja reproduzível |

Código: [`fabrica/FabricaPix.java`](https://github.com/UnBArqDsw2026-2-Turma01/2026.2-T01-_G1_ProjetoComercioEletronico_Entrega_03/tree/main/docs/Base/Relat%C3%B3rios/Subequipe01/src/criacionais/MeiosDePagamento(AbstractFactory)/fabrica/FabricaPix.java) e [`familias/pix/`](https://github.com/UnBArqDsw2026-2-Turma01/2026.2-T01-_G1_ProjetoComercioEletronico_Entrega_03/tree/main/docs/Base/Relat%C3%B3rios/Subequipe01/src/criacionais/MeiosDePagamento(AbstractFactory)/familias/pix).

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
| 1.2 | 18/10/2026 | Seção 5.3: família Pix (fábrica, autorizador síncrono, comprovante em QR Code e devolução), com trechos de código e rastreabilidade de cada decisão às Entregas 1 e 2 (`ProcessadorPix`, Máquina de Estados do Pedido) | Maria Clara Sena de Lima | -- |
