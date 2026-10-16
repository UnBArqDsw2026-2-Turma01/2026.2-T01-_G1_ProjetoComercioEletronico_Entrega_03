# Fluxo de Compra (State)

> Sistema em Java que modela o ciclo de vida de um **Pedido** (da espera pelo pagamento até a entrega ou o cancelamento) usando o padrão de projeto comportamental **State**, com uma interface gráfica local em **Swing**.

## Sumário

1. [Introdução](#1-introdução)
2. [Visão geral do sistema](#2-visão-geral-do-sistema)
3. [O modelo de estados](#3-o-modelo-de-estados)
4. [Fundamentação: o padrão State](#4-fundamentação-o-padrão-state)
5. [Implementação, classe por classe](#5-implementação-classe-por-classe)
6. [A interface gráfica](#6-a-interface-gráfica)
7. [Demonstração da interface](#7-demonstração-da-interface)
8. [Como compilar e executar](#8-como-compilar-e-executar)
9. [Decisões de projeto](#9-decisões-de-projeto)
10. [Relação com a literatura](#10-relação-com-a-literatura)
11. [Visão crítica](#11-visão-crítica)
12. [Melhorias propostas](#12-melhorias-propostas)
13. [Limitações desta documentação](#13-limitações-desta-documentação)
14. [Referências](#14-referências)

---

## 1. Introdução

### 1.1 Problema

Um pedido de compra passa por etapas bem definidas: ele é criado, aguarda o pagamento, é pago, é separado no estoque, é enviado e é entregue. Em qualquer momento antes do fim ele também pode ser cancelado. O ponto central é que **a mesma ação do usuário significa coisas diferentes (ou é inválida) dependendo da etapa em que o pedido está**:

- "Confirmar pagamento" faz sentido apenas para um pedido que aguarda pagamento.
- "Avançar" leva o pedido à próxima etapa, mas não deve funcionar enquanto o pagamento não foi confirmado.
- "Cancelar" não deve funcionar em um pedido que já foi cancelado.

### 1.2 Abordagem

Em vez de uma classe `Pedido` cheia de `if`/`switch` sobre o status, o sistema usa o padrão **State** (Gamma, Helm, Johnson e Vlissides, 1995): cada estado vira uma classe que sabe responder, por conta própria, o que cada operação faz naquela etapa. O `Pedido` apenas delega.

### 1.3 Objetivos deste documento

- Descrever a arquitetura e o comportamento do sistema.
- Mostrar como a interface gráfica funciona e como usá-la.
- Registrar as decisões tomadas e as alternativas consideradas.
- Relacionar o código com a literatura de padrões de projeto.
- Avaliar criticamente os pontos fortes e as fragilidades da solução.

### 1.4 Escopo

O sistema cobre **somente a máquina de estados do pedido e uma interface para exercitá-la**. Não há persistência, itens de pedido, valores, pagamento real, estoque, logística nem usuários.

### 1.5 Enquadramento na disciplina

Este trabalho segue o material da aula *Arquitetura e Desenho de Software — GoFs Comportamentais* (Profa. Milene Serrano, UnB Gama):

- **Padrão GoF:** solução consolidada para um problema recorrente no desenvolvimento e na manutenção de software orientado a objetos.
- **Categoria comportamental:** padrões voltados a alterações no nível do comportamento dos objetos.
- **Objetivo do State, segundo a aula:** alterar o comportamento de um objeto de acordo com o estado em que ele se encontra.

**Correspondência com a estrutura usada na aula**

| Estrutura da aula | Onde está nesta documentação |
|---|---|
| Objetivo | Seções 1.1 a 1.3 e 4.1 |
| Modelagem genérica | Seção 2.4 e `state-generico.puml` |
| Participantes | Seções 2.3 e 2.4 |
| Modelagem aplicada/instanciada | Seção 2.2 e `state-pedido.puml` |
| Motivação | Seção 1.1 |
| Solução | Seções 4 e 5 |
| Possível implementação | Seções 5 e 6 (código e interface gráfica) |
| Teste e saída | Seção 7 (demonstração) |
| Considerações finais | Seções 11.11 e 13 |
| Referências | Seção 14 |

As seções 9 (decisões), 10 (literatura), 11 (visão crítica) e 12 (melhorias) **vão além** da estrutura da aula e estão identificadas como acréscimos.

---

## 2. Visão geral do sistema

### 2.1 Estrutura de pastas

Em Java, o nome do pacote (`package`) deve corresponder à estrutura de diretórios. O projeto fica assim:

```
projeto/
├── Makefile
├── context/
│   ├── Pedido.java              # Context
│   └── StatusPedido.java        # enum com os 6 status
├── state/
│   ├── EstadoPedido.java        # State (interface)
│   ├── EstadoAguardandoPagamento.java   # ConcreteState
│   ├── EstadoPago.java                  # ConcreteState
│   ├── EstadoEmSeparacao.java           # ConcreteState
│   ├── EstadoEnviado.java               # ConcreteState
│   ├── EstadoEntregue.java              # ConcreteState
│   └── EstadoCancelado.java             # ConcreteState
└── ui/
    └── PedidoGUI.java           # interface gráfica (Swing)
```

### 2.2 Diagrama de classes

<div style="text-align:center;">

![Pedido Pago](../Assets/FluxoDeCompra(State)/DiagramaDeClasses.png)

<p><strong>Sistema de Compras</strong> — Diagrama de Classes do sistema <em>Autor: José Joaquim da Silva Neto</em></p>

</div>

### 2.3 Papéis no padrão

| Papel (GoF) | Classe | Responsabilidade |
|---|---|---|
| **Context** | `Pedido` | Guarda o estado atual e delega a ele toda operação. |
| **State** | `EstadoPedido` | Declara as operações que dependem do estado. |
| **ConcreteState** | `EstadoAguardandoPagamento`, `EstadoPago`, `EstadoEmSeparacao`, `EstadoEnviado`, `EstadoEntregue`, `EstadoCancelado` | Implementam o comportamento de cada estado e definem as transições. |
| **Cliente** | `PedidoGUI` | Chama as operações do `Pedido` sem conhecer as regras. |

### 2.4 Modelagem genérica

<div style="text-align:center;">


![Pedido criado aguardando pagamento](../Assets/FluxoDeCompra(State)/Generico.png)

<p><strong>Modelagem genérica</strong> — Modelagem genérica do sistema <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/FluxoDeCompraState/Generico.png ':ignore')

</div>

| Modelagem genérica | Modelagem aplicada (este sistema) |
|---|---|
| `Client` | `PedidoGUI` |
| `Context` | `Pedido` |
| `request()` | `confirmarPagamento()`, `cancelar()`, `avancar()` |
| `State` | `EstadoPedido` |
| `handle(context)` | `confirmarPagamento(pedido)`, `cancelar(pedido)`, `avancar(pedido)` |
| `ConcreteStateA/B` | Os seis `Estado*` |
| `setState(State)` | `mudarEstado(EstadoPedido)` |
| — | `StatusPedido` (elemento adicional, sem equivalente na aula) |

**Notação seguida (a mesma dos slides):** seta tracejada `<<usa>>` do Client para o Context; **agregação** (losango) entre Context e State; `<<cria>>` para criação de objetos; as transições aparecem como **notas** nos ConcreteStates (como `context.setState(StateTwo);` na aula); cores: Context azul, State vermelho, ConcreteState amarelo.

O diagrama aplicado, com a legenda de cores e as notas das transições, é o da seção 2.2 (arquivo `state-pedido.puml`); a explicação detalhada está em `SECAO_DIAGRAMA.md`.

---

## 3. O modelo de estados

### 3.1 Diagrama de estados

<div style="text-align:center;">


![Diagrama de estados](../Assets/FluxoDeCompra(State)/DiagramaDeEstados.png)

<p><strong>Diagrama de estados</strong> — Diagrama de estados do sistema <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/FluxoDeCompraState/DiagramaDeEstados.png ':ignore')

</div>

### 3.2 Tabela de transições

Esta tabela foi montada a partir da leitura do código de cada `ConcreteState`. "Erro" significa que o estado lança `IllegalStateException`.

| Estado atual | `confirmarPagamento` | `avancar` | `cancelar` |
|---|---|---|---|
| `AGUARDANDO_PAGAMENTO` | → `PAGO` | Erro | → `CANCELADO` |
| `PAGO` | Erro | → `EM_SEPARACAO` | → `CANCELADO` |
| `EM_SEPARACAO` | Erro | → `ENVIADO` | → `CANCELADO` |
| `ENVIADO` | Erro | → `ENTREGUE` | → `CANCELADO` |
| `ENTREGUE` | Erro | Erro | → `CANCELADO` |
| `CANCELADO` | Erro | Erro | Erro |

São 6 estados × 3 operações = **18 células**, e cada uma está implementada em exatamente um método de exatamente uma classe.

### 3.3 Observações sobre o modelo

- `CANCELADO` é o único **estado terminal** de fato: nenhuma operação é válida a partir dele.
- `ENTREGUE` encerra o fluxo *normal*, mas aceita `cancelar`. O comentário no código explica que esse cancelamento representa o fluxo de **devolução e estorno** (citado como "Diagrama de Atividades", que não faz parte dos arquivos analisados), e não um cancelamento espontâneo do cliente.
- Cancelar um pedido `ENVIADO` é permitido "por simplicidade do exemplo", conforme o comentário no código. Num sistema real, isso exigiria logística reversa.

---

## 4. Fundamentação: o padrão State

### 4.1 Intenção

Segundo o catálogo do GoF, o State permite que um objeto **altere seu comportamento quando seu estado interno muda**, de modo que, visto de fora, ele parece ter mudado de classe.

### 4.2 Quando usar

O próprio catálogo indica o padrão quando:

1. o comportamento de um objeto depende do seu estado e precisa mudar em tempo de execução; ou
2. as operações têm comandos condicionais grandes e com várias partes que dependem do estado, muitas vezes repetidos em várias operações.

O segundo caso é exatamente o que apareceria aqui. Sem o padrão, `confirmarPagamento()`, `cancelar()` e `avancar()` teriam, cada uma, um `switch` sobre o status.

### 4.3 Consequências descritas pelo GoF

| Consequência | Presente neste sistema? |
|---|---|
| Localiza o comportamento específico de cada estado e o separa por estado | Sim: cada estado é uma classe |
| Torna explícitas as transições entre estados | Sim: cada `mudarEstado(new ...)` mostra o destino |
| Objetos de estado podem ser compartilhados quando não têm atributos de instância | **Não aproveitado**: cada transição cria um objeto novo (ver seção 11) |
| Aumenta o número de classes | Sim: 7 classes/interfaces para um modelo de 6 estados |

### 4.4 Quem define as transições

O catálogo discute duas opções: o **Context** definir as transições (uma tabela ou lógica central) ou os **próprios estados** definirem o próximo. Este projeto adota a segunda, que é mais flexível e mantém o `Pedido` simples, ao custo de **cada estado conhecer seus sucessores** (acoplamento entre `ConcreteStates`).

### 4.5 Diferença para o Strategy

O State e o Strategy têm estrutura quase idêntica (um contexto delegando a um objeto de comportamento intercambiável). A diferença está na intenção: no **Strategy**, o cliente escolhe o algoritmo de fora; no **State**, o objeto muda de comportamento **por conta própria**, conforme seu ciclo de vida. Aqui, o cliente nunca escolhe o estado: ele só pede uma operação, e o estado decide o próximo.

---

## 5. Implementação, classe por classe

### 5.1 `StatusPedido` (enum)

```java
public enum StatusPedido {
    AGUARDANDO_PAGAMENTO, PAGO, EM_SEPARACAO, ENVIADO, ENTREGUE, CANCELADO
}
```

É uma **identificação** do estado, usada para exibição, log e testes. Um `enum` é a forma idiomática em Java de representar um conjunto fixo de constantes (ver o tutorial oficial *Enum Types*, da Oracle).

### 5.2 `EstadoPedido` (interface State)

```java
public interface EstadoPedido {
    void confirmarPagamento(Pedido pedido);
    void cancelar(Pedido pedido);
    void avancar(Pedido pedido);
    StatusPedido getStatus();
}
```

Cada operação recebe o `Pedido` como parâmetro. É assim que o estado consegue **acionar a troca** (`pedido.mudarEstado(...)`) sem guardar uma referência permanente ao contexto, o que também o torna, em princípio, **sem estado interno** e portanto compartilhável.

### 5.3 `Pedido` (Context)

```java
public class Pedido {
    private final String id;
    private EstadoPedido estado;

    public Pedido(String id) {
        this.id = id;
        this.estado = new EstadoAguardandoPagamento();   // todo pedido nasce aqui
    }

    public void confirmarPagamento() { estado.confirmarPagamento(this); }
    public void cancelar()           { estado.cancelar(this); }
    public void avancar()            { estado.avancar(this); }

    public void mudarEstado(EstadoPedido novoEstado) {
        System.out.println("[Pedido " + id + "] "
            + estado.getStatus() + " -> " + novoEstado.getStatus());
        this.estado = novoEstado;
    }
    ...
}
```

Pontos importantes:

- **Delegação pura**: nenhuma das três operações públicas tem condicional.
- **`this` como argumento**: o contexto se oferece ao estado para que ele possa pedir a troca.
- **`mudarEstado`** é o único ponto de mutação do atributo `estado`, e também o único lugar onde a transição é registrada no console.
- O campo `id` é `final`: a identidade do pedido não muda.

### 5.3.1 Fluxo de uma chamada

<div style="text-align:center;">


![Diagrama de Chamada](../Assets/FluxoDeCompra(State)/FluxoChamada.png)

<p><strong>Diagrama de estados</strong> — Diagrama de estados do sistema <em>Autor: José Joaquim da Silva Neto</em></p>

</div>

### 5.4 Estados concretos

Cada um tem a mesma forma: três métodos que **ou trocam de estado, ou lançam `IllegalStateException`**, mais o `getStatus()`.

Exemplo, `EstadoPago`:

```java
public class EstadoPago implements EstadoPedido {
    public void confirmarPagamento(Pedido p) {
        throw new IllegalStateException("Pagamento já confirmado para este pedido.");
    }
    public void cancelar(Pedido p) { p.mudarEstado(new EstadoCancelado()); }
    public void avancar(Pedido p)  { p.mudarEstado(new EstadoEmSeparacao()); }
    public StatusPedido getStatus() { return StatusPedido.PAGO; }
}
```

A [documentação do Java](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/IllegalStateException.html) define `IllegalStateException` como o sinal de que um método foi chamado **em um momento ilegal ou inadequado**, o que descreve bem "tentar avançar um pedido que aguarda pagamento". Por ser uma exceção não verificada (*unchecked*), ela não precisa ser declarada nas assinaturas.

**Mensagens de erro por estado** (úteis na demonstração):

| Estado | Operação inválida | Mensagem |
|---|---|---|
| `AGUARDANDO_PAGAMENTO` | `avancar` | "Não é possível avançar um pedido que ainda aguarda pagamento." |
| `PAGO` | `confirmarPagamento` | "Pagamento já confirmado para este pedido." |
| `EM_SEPARACAO` | `confirmarPagamento` | "Pagamento já confirmado — pedido já está em separação." |
| `ENVIADO` | `confirmarPagamento` | "Pagamento já confirmado — pedido já foi enviado." |
| `ENTREGUE` | `confirmarPagamento` | "Pagamento já confirmado — pedido já foi entregue." |
| `ENTREGUE` | `avancar` | "Pedido entregue é o fim do ciclo normal de vida — nada para avançar." |
| `CANCELADO` | `confirmarPagamento` / `avancar` | "Pedido cancelado — operação inválida." |
| `CANCELADO` | `cancelar` | "Pedido já está cancelado." |

---


## Demonstração da interface (Imagens em cada estado)

### Pedido criado aguardando pagamento

<div style="text-align:center;">


![Pedido criado aguardando pagamento](../Assets/FluxoDeCompra(State)/01.png)

<p><strong>Interface Gráfica</strong> — Pedido criado aguardando pagamento. <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/FluxoDeCompraState/01.png ':ignore')

</div>

### Pedido Pago 

<div style="text-align:center;">

![Pedido Pago](../Assets/FluxoDeCompra(State)/02.png)

<p><strong>Interface Gráfica</strong> — Pedido pago. <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/FluxoDeCompraState/02.png ':ignore')

</div>

### Pedido em separação

<div style="text-align:center;">

![Pedido em separação](../Assets/FluxoDeCompra(State)/03.png)

<p><strong>Interface Gráfica</strong> — Pedido em separação. <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/FluxoDeCompraState/03.png ':ignore')

</div>

### Pedido enviado

<div style="text-align:center;">

![Pedido enviado](../Assets/FluxoDeCompra(State)/04.png)

<p><strong>Interface Gráfica</strong> — Pedido enviado. <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/FluxoDeCompraState/04.png ':ignore')

</div>

### Pedido entregue

<div style="text-align:center;">

![Pedido entregue](../Assets/FluxoDeCompra(State)/05.png)

<p><strong>Interface Gráfica</strong> — Pedido entregue. <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/FluxoDeCompraState/05.png ':ignore')

</div>

### Fim do ciclo de vida da compra

<div style="text-align:center;">

![Fim do ciclo de vida da compra](../Assets/FluxoDeCompra(State)/06.png)

<p><strong>Interface Gráfica</strong> — Fim do ciclo de vida da compra. <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/FluxoDeCompraState/06.png ':ignore')

</div>

## Demonstração da interface e fluxos (vídeos)

### Fluxo de compra padrão

Esse fluxo trata do ciclo de vida padrão de um pedido, partimos de um pedido que foi criado, logo em seguida o pagamento desse pedido é realizado, em seguida é testado que é exibida uma mesagem de operação inválida ao clicar em confirmar pagamento de um pedido cujo o pagamento já foi realizado, após isso o estado do pedido é atualizado para "em separação", ou seja, sendo separado e aguardando o envio ser realizado, em seguida o estado do pedido é atualizado para enviado e logo em seguida para entregue, assim finalizando o clico de vida padrão da compra de um pedido.

<div style="text-align:center;">

[06.mp4](../Assets/FluxoDeCompra(State)/01.mp4 ':include :type=video controls width=80%')

<p><strong>Interface Gráfica</strong> — Fluxo de compra padrão. <em>Autor: José Joaquim da Silva Neto</em></p>

</div>


## Histórico de Versões

| Versão | Data | Descrição | Autor(es) | Revisor(es) |
| -- | -- | -- | -- | -- |
| 1.0 | 05/10/2026 | Criação da página | José Joaquim da Silva Neto | -- |
| 1.1 | 15/10/2026 | Adiciona justificativa da tecnologia utilizada para a interfacce gráfica | José Joaquim da Silva Neto | -- |
| 1.2 | 15/10/2026 | Adiciona imagens e vídeo da demonstração da interface gráfica | José Joaquim da Silva Neto | -- |
| 1.3 | 15/10/2026 | Adiciona documentação | José Joaquim da Silva Neto | -- |