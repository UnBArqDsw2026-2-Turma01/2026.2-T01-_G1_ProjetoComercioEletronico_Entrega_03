# Fluxo de Compra (State)

> Sistema em Java que modela o ciclo de vida de um **Pedido** (da espera pelo pagamento até a entrega ou o cancelamento) usando o padrão de projeto comportamental **State**, com uma interface gráfica local em **Swing**.

## Sumário

1. [Introdução](#1-introdução)
2. [Visão geral do sistema](#2-visão-geral-do-sistema)
3. [O modelo de estados](#3-o-modelo-de-estados)
4. [Fundamentação: o padrão State](#4-fundamentação-o-padrão-state)
5. [Implementação, classe por classe](#5-implementação-classe-por-classe)
6. [A interface gráfica](#6-a-interface-gráfica)
7. [Diagramas de Classes do Padrão State (PlantUML)](#7-diagramas-de-classes-do-padrão-state-plantuml)
8. [Como compilar e executar](#8-como-compilar-e-executar)
9. [Decisões de projeto](#9-decisões-de-projeto)
10. [Relação com a literatura](#10-relação-com-a-literatura)
11. [Visão crítica](#11-visão-crítica)
12. [Melhorias propostas](#12-melhorias-propostas)
13. [Limitações desta documentação](#13-limitações-desta-documentação)
14. [Referências](#14-referências)
15. [Demonstração da interface (imagens em cada estado)](#15-demonstração-da-interface-imagens-em-cada-estado)
16. [Demonstração da interface e fluxos (vídeos)](#16-demonstração-da-interface-e-fluxos-vídeos)

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

![Diagrama de classes do sistema de compras](<../assets/FluxoDeCompra(State)/DiagramaDeClasses.png>)

<p><strong>Sistema de Compras</strong> — Diagrama de Classes do sistema <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/assets/FluxoDeCompraState/DiagramaDeClasses.png ":ignore")

</div>

### 2.3 Papéis no padrão

| Papel (GoF)       | Classe                                                                                                               | Responsabilidade                                                    |
| ----------------- | -------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------- |
| **Context**       | `Pedido`                                                                                                             | Guarda o estado atual e delega a ele toda operação.                 |
| **State**         | `EstadoPedido`                                                                                                       | Declara as operações que dependem do estado.                        |
| **ConcreteState** | `EstadoAguardandoPagamento`, `EstadoPago`, `EstadoEmSeparacao`, `EstadoEnviado`, `EstadoEntregue`, `EstadoCancelado` | Implementam o comportamento de cada estado e definem as transições. |
| **Cliente**       | `PedidoGUI`                                                                                                          | Chama as operações do `Pedido` sem conhecer as regras.              |

### 2.4 Modelagem genérica

<div style="text-align:center;">

![Modelagem genérica do padrão State](<../assets/FluxoDeCompra(State)/Generico.png>)

<p><strong>Modelagem genérica</strong> — Modelagem genérica do sistema <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/assets/FluxoDeCompraState\Generico.png ":ignore")

</div>

| Modelagem genérica | Modelagem aplicada (este sistema)                                   |
| ------------------ | ------------------------------------------------------------------- |
| `Client`           | `PedidoGUI`                                                         |
| `Context`          | `Pedido`                                                            |
| `request()`        | `confirmarPagamento()`, `cancelar()`, `avancar()`                   |
| `State`            | `EstadoPedido`                                                      |
| `handle(context)`  | `confirmarPagamento(pedido)`, `cancelar(pedido)`, `avancar(pedido)` |
| `ConcreteStateA/B` | Os seis `Estado*`                                                   |
| `setState(State)`  | `mudarEstado(EstadoPedido)`                                         |
| —                  | `StatusPedido` (elemento adicional, sem equivalente na aula)        |

**Notação seguida (a mesma dos slides):** seta tracejada `<<usa>>` do Client para o Context; **agregação** (losango) entre Context e State; `<<cria>>` para criação de objetos (slides de Command e Iterator); as transições aparecem como **notas** nos ConcreteStates (como `context.setState(StateTwo);` na aula); cores: Context azul, State vermelho e ConcreteState amarelo, como nos slides de Strategy e Template Method.

O diagrama aplicado, com a legenda de cores e as notas das transições, é o da seção 2.2 (arquivo `state-pedido.puml`); a explicação detalhada está na seção 7 (**Diagramas de Classes do Padrão State (PlantUML)**).

---

## 3. O modelo de estados

### 3.1 Diagrama de estados

<div style="text-align:center;">

![Diagrama de estados](<../assets/FluxoDeCompra(State)/DiagramaDeEstados.png>)

<p><strong>Diagrama de estados</strong> — Diagrama de estados do sistema <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/assets/FluxoDeCompraState/DiagramaDeEstados.png ":ignore")

</div>

### 3.2 Tabela de transições

Esta tabela foi montada a partir da leitura do código de cada `ConcreteState`. "Erro" significa que o estado lança `IllegalStateException`.

| Estado atual           | `confirmarPagamento` | `avancar`        | `cancelar`    |
| ---------------------- | -------------------- | ---------------- | ------------- |
| `AGUARDANDO_PAGAMENTO` | → `PAGO`             | Erro             | → `CANCELADO` |
| `PAGO`                 | Erro                 | → `EM_SEPARACAO` | → `CANCELADO` |
| `EM_SEPARACAO`         | Erro                 | → `ENVIADO`      | → `CANCELADO` |
| `ENVIADO`              | Erro                 | → `ENTREGUE`     | → `CANCELADO` |
| `ENTREGUE`             | Erro                 | Erro             | → `CANCELADO` |
| `CANCELADO`            | Erro                 | Erro             | Erro          |

São 6 estados × 3 operações = **18 células**, e cada uma está implementada em exatamente um método de exatamente uma classe.

### 3.3 Observações sobre o modelo

- `CANCELADO` é o único **estado terminal** de fato: nenhuma operação é válida a partir dele.
- `ENTREGUE` encerra o fluxo _normal_, mas aceita `cancelar`. O comentário no código explica que esse cancelamento representa o fluxo de **devolução e estorno** (citado como "Diagrama de Atividades", que não faz parte dos arquivos analisados), e não um cancelamento espontâneo do cliente.
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

| Consequência                                                                     | Presente neste sistema?                                                |
| -------------------------------------------------------------------------------- | ---------------------------------------------------------------------- |
| Localiza o comportamento específico de cada estado e o separa por estado         | Sim: cada estado é uma classe                                          |
| Torna explícitas as transições entre estados                                     | Sim: cada `mudarEstado(new ...)` mostra o destino                      |
| Objetos de estado podem ser compartilhados quando não têm atributos de instância | **Não aproveitado**: cada transição cria um objeto novo (ver seção 11) |
| Aumenta o número de classes                                                      | Sim: 7 classes/interfaces para um modelo de 6 estados                  |

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

É uma **identificação** do estado, usada para exibição, log e testes. Um `enum` é a forma idiomática em Java de representar um conjunto fixo de constantes (ver o tutorial oficial _Enum Types_, da Oracle).

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

![Diagrama de sequência do fluxo de uma chamada](<../assets/FluxoDeCompra(State)/FluxoChamada.png>)

<p><strong>Fluxo de uma chamada</strong> — Diagrama de sequência da chamada de <code>avancar()</code> <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/assets/FluxoDeCompraState/FluxoChamada.png ":ignore")

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

A [documentação do Java](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/IllegalStateException.html) define `IllegalStateException` como o sinal de que um método foi chamado **em um momento ilegal ou inadequado**, o que descreve bem "tentar avançar um pedido que aguarda pagamento". Por ser uma exceção não verificada (_unchecked_), ela não precisa ser declarada nas assinaturas.

**Mensagens de erro por estado** (úteis na demonstração):

| Estado                 | Operação inválida                | Mensagem                                                               |
| ---------------------- | -------------------------------- | ---------------------------------------------------------------------- |
| `AGUARDANDO_PAGAMENTO` | `avancar`                        | "Não é possível avançar um pedido que ainda aguarda pagamento."        |
| `PAGO`                 | `confirmarPagamento`             | "Pagamento já confirmado para este pedido."                            |
| `EM_SEPARACAO`         | `confirmarPagamento`             | "Pagamento já confirmado — pedido já está em separação."               |
| `ENVIADO`              | `confirmarPagamento`             | "Pagamento já confirmado — pedido já foi enviado."                     |
| `ENTREGUE`             | `confirmarPagamento`             | "Pagamento já confirmado — pedido já foi entregue."                    |
| `ENTREGUE`             | `avancar`                        | "Pedido entregue é o fim do ciclo normal de vida — nada para avançar." |
| `CANCELADO`            | `confirmarPagamento` / `avancar` | "Pedido cancelado — operação inválida."                                |
| `CANCELADO`            | `cancelar`                       | "Pedido já está cancelado."                                            |

---

## 6. A interface gráfica

### 6.1 Tecnologia escolhida

**Swing**, que faz parte do JDK: não exige dependências externas e roda em qualquer máquina com Java instalado.

### 6.2 Princípio de projeto

A `PedidoGUI` **não conhece nenhuma regra de transição**. Ela só chama `confirmarPagamento()`, `avancar()` e `cancelar()` e trata o resultado. Se amanhã uma transição mudar, nenhuma linha da interface precisa mudar.

### 6.3 Componentes

| Componente          | Classe Swing                 | Função                                                         |
| ------------------- | ---------------------------- | -------------------------------------------------------------- |
| Janela              | `JFrame`                     | Janela principal; `EXIT_ON_CLOSE` encerra o programa ao fechar |
| Título do pedido    | `JLabel`                     | Mostra "Pedido PED-001 — PAGO"                                 |
| Botão "Novo pedido" | `JButton`                    | Cria um `Pedido` com id sequencial (`PED-001`, `PED-002`…)     |
| Fileira de etapas   | 6 × `JLabel` em `GridLayout` | Uma caixa por status; a atual fica colorida                    |
| Botões de ação      | 3 × `JButton`                | `Confirmar pagamento`, `Avançar`, `Cancelar`                   |
| Histórico           | `JTextArea` em `JScrollPane` | Registro de cada operação e de cada erro                       |
| Aviso de erro       | `JOptionPane`                | Diálogo com a mensagem da exceção                              |

### 6.4 Layout

Combinação de _layout managers_ padrão do Swing (ver o tutorial _Laying Out Components Within a Container_, da Oracle):

- `BorderLayout` na janela: topo (título + botão), centro (etapas + ações), sul (histórico);
- `GridLayout(1, 0)` para as 6 etapas lado a lado;
- `FlowLayout` para centralizar os 3 botões.

### 6.5 O método central: `executar`

```java
private void executar(String nome, Runnable operacao) {
    StatusPedido antes = pedido.getStatus();
    try {
        operacao.run();
        log.append(nome + ": " + antes + " -> " + pedido.getStatus() + "\n");
    } catch (IllegalStateException ex) {
        log.append("ERRO em '" + nome + "' (" + antes + "): " + ex.getMessage() + "\n");
        JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Operação inválida", JOptionPane.WARNING_MESSAGE);
    }
    atualizar();
}
```

1. Guarda o status **antes**.
2. Executa a operação (uma _lambda_ que chama o método do `Pedido`).
3. Se funcionar, registra `antes -> depois`.
4. Se o estado rejeitar, captura a exceção e mostra a mensagem.
5. Em qualquer caso, redesenha a tela.

Como a GUI compara o status antes e depois, **não foi necessário alterar a classe `Pedido`** para exibir o histórico.

### 6.6 Os botões nunca são desabilitados

É uma decisão deliberada (ver seção 9): se a GUI desabilitasse botões conforme o estado, ela passaria a **duplicar as regras** que hoje existem só nos estados. Deixando os botões sempre ativos, o usuário consegue **provocar** as transições inválidas e ver o padrão rejeitando-as, o que é o objetivo didático do sistema.

### 6.7 Thread de eventos

O `main` cria a janela dentro de `SwingUtilities.invokeLater`. O Swing não é _thread-safe_, e a documentação oficial (_Concurrency in Swing_) orienta que a interface seja construída e alterada na **Event Dispatch Thread**.

---

## 7. Diagramas de Classes do Padrão State (PlantUML)

> Esta seção explica os diagramas de classes do sistema: a **modelagem genérica** (seção 2.4) e a **modelagem aplicada** ao `Pedido` (seção 2.2). Ela segue a estrutura e a notação do material da aula _Arquitetura e Desenho de Software — GoFs Comportamentais_ (Profa. Milene Serrano, UnB Gama) e, ao final, registra separadamente onde a visão crítica vai além desse material.

### Objetivo dos diagramas

Segundo a aula, o State tem o objetivo de **alterar o comportamento de um objeto de acordo com o estado em que ele se encontra**, e pertence à categoria dos GoFs comportamentais (padrões voltados a alterações no nível do comportamento dos objetos).

Os diagramas de classes respondem a uma pergunta de **estrutura**: _quais classes participam do padrão e como se relacionam?_ Eles **não** mostram, sozinhos, a ordem do fluxo do pedido; isso é papel do diagrama de estados da seção 3. Como na aula, usamos dois níveis:

1. **Modelagem Genérica:** o padrão em abstrato (Client, Context, State, ConcreteStates), na seção 2.4.
2. **Modelagem Aplicada/Instanciada:** o mesmo padrão com os nomes do domínio (`Pedido`, `EstadoPedido`, `EstadoPago`…), na seção 2.2.

Os diagramas de classes foram escritos em PlantUML (arquivos `state-generico.puml` e `state-pedido.puml`); os diagramas de estados e de sequência (seções 3.1 e 5.3.1) também (`state-estados.puml` e `state-sequencia.puml`). Por ser texto, o código-fonte pode ser versionado no Git junto com o código e revisado em _pull requests_.

### Modelagem genérica

**Participantes**

| Participante      | Papel                                                                                           | Exemplo na aula          |
| ----------------- | ----------------------------------------------------------------------------------------------- | ------------------------ |
| **Client**        | Usa o Context (`<<usa>>`)                                                                       | —                        |
| **Context**       | Mantém uma referência para um `State`, que define o estado atual, e delega a ele as requisições | `Taxímetro`              |
| **State**         | Interface que padroniza os estados do Context                                                   | `Bandeira`               |
| **ConcreteState** | Implementações particulares de um `State`                                                       | `Bandeira1`, `Bandeira2` |

**Mecanismo.** O diagrama genérico combina os dois modelos apresentados na aula: o `Context` guarda o estado atual (`current`), delega a requisição ao estado passando a si mesmo (`current.handle(this)`) e **o próprio ConcreteState realiza a transição** chamando `context.setState(...)` com o próximo estado (na aula, a nota `context.setState(StateTwo);`).

### Modelagem aplicada

**Correspondência entre o genérico e o aplicado**

| Modelagem genérica     | Modelagem aplicada                                                                                                   |
| ---------------------- | -------------------------------------------------------------------------------------------------------------------- |
| `Client`               | `PedidoGUI`                                                                                                          |
| `Context`              | `Pedido`                                                                                                             |
| `request()`            | `confirmarPagamento()`, `cancelar()`, `avancar()`                                                                    |
| `State`                | `EstadoPedido`                                                                                                       |
| `handle(context)`      | `confirmarPagamento(pedido)`, `cancelar(pedido)`, `avancar(pedido)`                                                  |
| `ConcreteStateA/B`     | `EstadoAguardandoPagamento`, `EstadoPago`, `EstadoEmSeparacao`, `EstadoEnviado`, `EstadoEntregue`, `EstadoCancelado` |
| `setState(State)`      | `mudarEstado(EstadoPedido)`                                                                                          |
| — (elemento adicional) | `StatusPedido` (enum que identifica cada estado)                                                                     |

**Os relacionamentos, um por um**

| Notação                                           | Significado                               | O que expressa aqui                                                                                         |
| ------------------------------------------------- | ----------------------------------------- | ----------------------------------------------------------------------------------------------------------- |
| `PedidoGUI ..> Pedido : <<usa>>`                  | Dependência                               | A interface usa as operações do `Pedido`                                                                    |
| `Pedido o--> "1" EstadoPedido`                    | Agregação com navegação, multiplicidade 1 | O `Pedido` mantém exatamente um estado atual                                                                |
| `EstadoPedido <\|.. EstadoX`                      | Realização                                | Cada estado concreto cumpre o contrato do State                                                             |
| `EstadoPedido ..> Pedido : <<usa>>`               | Dependência                               | O contexto é passado como parâmetro às operações                                                            |
| `Pedido ..> EstadoAguardandoPagamento : <<cria>>` | Dependência de criação                    | Todo pedido nasce nesse estado                                                                              |
| `... ..> StatusPedido : <<usa>>`                  | Dependência                               | `PedidoGUI`, `Pedido` e `EstadoPedido` usam o enum                                                          |
| Notas nos estados                                 | Código da transição                       | Mostram o `pedido.mudarEstado(new ...)` de cada operação, como a nota `context.setState(StateTwo);` da aula |

**Roteiro de leitura**

1. Compare com o diagrama genérico: `Pedido` ↔ `Context`, `EstadoPedido` ↔ `State`, estados ↔ `ConcreteState`.
2. Observe que `Pedido` conhece só a interface, exceto na criação do estado inicial (`<<cria>>`).
3. Leia as **notas** de cada estado para reconstruir o fluxo: `AguardandoPagamento → Pago → EmSeparacao → Enviado → Entregue`, com todos podendo ir para `Cancelado`.
4. Consulte a legenda de cores (azul = Context, vermelho = State, amarelo = ConcreteState, cinza = Client), a mesma convenção de cores dos slides de Strategy e Template Method.

### Convenções alinhadas ao material da aula

| Elemento                | Notação da aula                                                                        | Adotada nos diagramas                                 |
| ----------------------- | -------------------------------------------------------------------------------------- | ----------------------------------------------------- |
| Client → Context        | Seta tracejada com `<<usa>>`                                                           | Idem                                                  |
| Context → State         | Agregação (losango no Context)                                                         | Idem (`o-->`)                                         |
| Criação de objeto       | `<<cria>>` (slides de Command e Iterator)                                              | Idem                                                  |
| Transição entre estados | Nota com `context.setState(...)`                                                       | Notas nos ConcreteStates                              |
| Papéis (cores)          | Context azul, State vermelho, concretos amarelo (slides de Strategy e Template Method) | Idem, em tons mais claros para manter o texto legível |
| Generalização de State  | Triângulo vazio                                                                        | Realização (`<\|..`), por ser uma `interface` Java    |
| Nomes                   | Modelagem genérica × aplicada                                                          | Dois diagramas: seções 2.4 e 2.2                      |

A única diferença de notação é o tipo da seta de herança: a aula desenha a generalização de uma classe abstrata (linha cheia); como `EstadoPedido` é uma **interface** em Java, o correto em UML é a **realização** (linha tracejada).

### Decisões de modelagem e visão crítica

Formato: **o que foi feito → por quê → alternativa → crítica**.

#### Diagramas de classes (e não só um diagrama de estados)

- **Por quê:** o State é reconhecido pela relação entre Context, State e ConcreteStates, e a aula o apresenta em diagramas de classes.
- **Alternativa:** diagrama de máquina de estados UML.
- **Crítica:** o diagrama de classes mostra **o mecanismo**, mas esconde **a regra de negócio**. Quem olha só para ele não descobre, por exemplo, que `ENTREGUE` ainda aceita `cancelar`. As notas ajudam, mas o diagrama de estados da seção 3 continua necessário.

#### Estereótipos, cores e legenda

- **Por quê:** seguir a convenção de cores dos slides de Strategy e Template Method (Context, State e ConcreteState em cores distintas) e tornar o diagrama um **mapa de papéis**.
- **Crítica:** a cor sozinha não deve carregar informação (daltonismo, impressão em preto e branco). Por isso há **estereótipos textuais** e uma **legenda**. Os estereótipos são informais (não pertencem a um perfil UML padronizado), o que é aceitável em um esboço didático.

#### Pacotes que espelham o código

- **Por quê:** ligar o diagrama à organização real do projeto (`ui`, `context`, `state`).
- **Crítica:** isso **expõe uma dependência circular entre pacotes**. `context` depende de `state` (o `Pedido` usa `EstadoPedido` e cria o estado inicial) e `state` depende de `context` (os estados usam `Pedido` e `StatusPedido`). Em Java isso compila, mas viola o **Princípio das Dependências Acíclicas** (Martin). É um ponto a favor do diagrama: ele revela um problema que a leitura de arquivos isolados esconde.

#### Agregação entre `Pedido` e `EstadoPedido`

- **Decisão:** manter o **losango**, como no modelo da aula (a agregação aparece em vários dos GoFs apresentados, como Strategy, Command e State).
- **Crítica (observação além do material):** na UML, a agregação tem semântica fraca, e Fowler (_UML Distilled_) recomenda ignorá-la por ser, na prática, indistinguível de uma associação comum. Uma associação simples (`-->`) seria a escolha mais segura em termos de UML estrita; **nesta documentação seguimos a notação da disciplina**.

#### Transições como notas, e não como setas entre estados

- **Decisão:** representar as transições em **notas** presas a cada ConcreteState, como a aula faz (`context.setState(StateTwo);`).
- **Alternativa:** setas pontilhadas entre estados concretos (`<<cria>>`), que mostram o acoplamento diretamente.
- **Crítica:**
  - **A favor das notas:** o diagrama fica mais limpo e fiel ao material, e cada estado mostra _exatamente_ o código da sua transição.
  - **Contra:** o **acoplamento entre estados** (cada estado conhece seus sucessores) deixa de ser visível na estrutura do desenho; ele só aparece lendo as notas. O diagrama de estados da seção 3 compensa, mostrando o fluxo completo.

#### `EstadoPedido ..> Pedido`: o contexto como parâmetro

- **Por quê:** permite que o estado acione a troca (`pedido.mudarEstado(...)`) sem guardar uma referência permanente ao contexto, mantendo os estados **sem atributos**.
- **Crítica:** junto com `Pedido o--> EstadoPedido`, forma um **ciclo visível** entre os dois elementos centrais. O ciclo é inerente à solução do GoF (o contexto delega, o estado pede a troca), mas é um acoplamento bidirecional que vale ter identificado.

#### `StatusPedido` como elemento adicional

- **Por quê:** identifica cada estado para exibição, log e testes. Não existe no modelo da aula, então foi **justificado** e destacado na tabela de correspondência.
- **Crítica:** cria uma **hierarquia paralela** (6 classes + 6 constantes) que precisa ser mantida em sincronia. As dependências de `PedidoGUI` e `Pedido` com o enum estão desenhadas.

#### Estados concretos sem membros, e `PedidoGUI` sem detalhes

- **Estados:** todos implementam as mesmas operações da interface; as **notas** substituem a lista de métodos e mostram o que os diferencia (o valor do padrão).
- **PedidoGUI:** aparece só como **Client**, sem métodos, como no modelo da aula; detalhes de interface são irrelevantes para o padrão.

### Balanço: o que os diagramas mostram bem e mal

| Mostram bem                                                    | Mostram mal ou omitem                                                                      |
| -------------------------------------------------------------- | ------------------------------------------------------------------------------------------ |
| Os papéis do GoF e a delegação (genérico × aplicado)           | O **fluxo** completo de uma só vez (só nas notas)                                          |
| Que `Pedido` conhece só a interface (exceto no estado inicial) | Que `mudarEstado` é público e pode ser chamado por qualquer classe                         |
| O código da transição de cada estado (notas)                   | A relação com `IllegalStateException` como mecanismo de rejeição (apenas citada nas notas) |
| A dependência circular entre os pacotes                        | O acoplamento entre estados **como estrutura** (só nas notas)                              |
| A hierarquia paralela enum × classes                           | —                                                                                          |

### Relação dos diagramas com a literatura

**Material da disciplina.** A aula _Arquitetura e Desenho de Software — GoFs Comportamentais_ (Profa. Milene Serrano, UnB Gama) é a **fonte de notação** destes diagramas. Dela vêm:

- o enquadramento do State como GoF **comportamental** (alterações no nível do comportamento dos objetos);
- o **objetivo** do padrão (alterar o comportamento conforme o estado);
- a separação em **modelagem genérica** e **aplicada/instanciada**;
- a notação `<<usa>>` e o losango (slides de State), `<<cria>>` (slides de Command e Iterator) e o código de cores (slides de Strategy e Template Method);
- as referências indicadas, entre elas o livro do GoF e o artigo do SourceMaking sobre State em Java.

**O catálogo GoF.** O catálogo _Design Patterns_ (Gamma, Helm, Johnson e Vlissides, 1995, como citado na aula) descreve o State com os participantes **Context**, **State** e **ConcreteState**, e discute os pontos que o diagrama explicita:

- **Quem define as transições** (Context ou ConcreteStates): este projeto adota a segunda opção, vista nas notas;
- **Criação dos objetos de estado**: aqui, criados a cada transição (`new`), e não compartilhados.

**UML como linguagem de modelagem.**

- A **especificação UML** (OMG) define realização, dependência, associação, agregação, estereótipos, pacotes e notas.
- Fowler (_UML Distilled_) distingue UML como **esboço**, **projeto detalhado** e **linguagem de programação**. Estes diagramas são **esboços didáticos**, o que justifica simplificações.
- Booch, Rumbaugh e Jacobson (_The Unified Modeling Language User Guide_) descrevem estereótipos e dependências como mecanismos de extensão, base das convenções `<<usa>>` e `<<cria>>`.

**Estrutura × comportamento.** O diagrama de estados deriva de **Statecharts** (Harel, 1987). A ideia de que classes (estrutura) e estados (comportamento) pedem visões distintas justifica o par de diagramas desta documentação.

**Princípios de projeto evidenciados**

| Princípio                   | Fonte                       | O que os diagramas revelam                                                                      |
| --------------------------- | --------------------------- | ----------------------------------------------------------------------------------------------- |
| **Aberto/Fechado**          | Meyer (1988); Martin (2002) | Novo estado = nova classe, sem alterar `Pedido`; mas os estados vizinhos precisam ser alterados |
| **Dependências Acíclicas**  | Martin (2002)               | O ciclo `context` ↔ `state`                                                                     |
| **Ocultação de informação** | Parnas (1972)               | `Pedido` esconde o estado atrás da interface, mas `mudarEstado` público enfraquece isso         |

**O mesmo padrão em outras fontes.** _Head First Design Patterns_ (Freeman e Robson) e o site Refactoring Guru apresentam a mesma estrutura de participantes. Eles destacam a semelhança com o **Strategy**: o diagrama é quase idêntico e a diferença está na **intenção**, algo que o diagrama de classes não mostra por si só. Na aula, o contraste é visível nos objetivos: o Strategy permite variar **algoritmos**, o State altera o comportamento conforme o **estado**.

### Onde a visão crítica vai além do material da aula

Para evitar confundir "o que a disciplina pede" com "o que a crítica acrescenta", estes são os pontos em que os diagramas **seguem a aula** apesar de existir uma objeção:

| Ponto                             | Material da aula                                                                  | Crítica / alternativa                                                                                                        |
| --------------------------------- | --------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------- |
| Agregação (losango)               | Usada em vários dos padrões apresentados (por exemplo, Strategy, Command e State) | Semântica fraca na UML; associação simples seria mais segura                                                                 |
| `<<cria>>`                        | Estereótipo em português                                                          | Na UML, o estereótipo padrão para dependência de instanciação é `«instantiate»` (confira na versão da especificação adotada) |
| Método público de troca de estado | `+setState(in State)` público                                                     | Quebra de encapsulamento; seria melhor _package-private_ (exige `Pedido` e estados no mesmo pacote)                          |
| Transições em notas               | Nota `context.setState(...)`                                                      | O acoplamento entre estados fica menos visível na estrutura                                                                  |

### Como validar os diagramas contra o código

1. **Cada classe do diagrama existe no código** e vice-versa (`Pedido`, `StatusPedido`, `EstadoPedido`, seis estados e `PedidoGUI`).
2. **Cada `<|..`** corresponde a um `implements EstadoPedido`.
3. **Cada nota de estado** corresponde ao `new EstadoX()` realmente usado no método indicado.
4. **`Pedido ..> EstadoAguardandoPagamento`** corresponde ao `new` do construtor.
5. **Assinaturas** de `Pedido` e `EstadoPedido` iguais às do código.
6. Ao **adicionar um estado**, atualize: a classe, o enum, as notas, o `.puml` e o diagrama de estados.

### Limitações desta seção

- A explicação foi feita a partir da **leitura do código e dos slides**. Confira se as figuras das seções 2.2 e 2.4 correspondem ao que esta seção descreve (cores, legenda, notas das transições, rótulos `<<usa>>` e `<<cria>>`); se divergirem, ajuste o texto à figura ou a figura ao `.puml`.
- O artigo do **SourceMaking** indicado na aula **não foi consultado**.
- Termos de notação UML (por exemplo, estereótipos padrão) devem ser conferidos na **versão da especificação** adotada pela disciplina.
- As referências da seção 14 estão **sem números de página**; confirme edição e ano antes de citar.

### Referências desta seção

As referências citadas nesta seção estão reunidas na seção 14.

---

## 8. Como compilar e executar

### 8.1 Pré-requisito

JDK instalado (a documentação do `javac` e do `java` está nas páginas de referência da Oracle). Confira com `javac -version`.

### 8.2 Compilação manual

Na raiz do projeto (a pasta que contém `context/`, `state/` e `ui/`):

```bash
javac -d out context/*.java state/*.java ui/*.java
java -cp out ui.PedidoGUI
```

- `-d out` define a pasta de saída dos `.class`.
- `-cp out` (_classpath_) diz ao `java` onde procurá-los.
- `ui.PedidoGUI` é o nome **completo** da classe (pacote + nome).

### 8.3 Com o Makefile

```bash
make        # compila (só recompila se algum .java mudou)
make run    # compila e abre a interface
make clean  # apaga a pasta out
```

O `Makefile` deve ficar na **raiz do projeto**. No Windows, o `make` exige WSL ou Git Bash (ou use um `.bat` com os dois comandos da seção 8.2).

### 8.4 Erros comuns

| Mensagem                               | Causa provável                                 | Solução                                          |
| -------------------------------------- | ---------------------------------------------- | ------------------------------------------------ |
| `error: file not found: ui/*.java`     | A pasta `ui/` não existe ou está vazia         | Criar `ui/` e colocar `PedidoGUI.java` dentro    |
| `ClassNotFoundException: ui.PedidoGUI` | A compilação falhou ou `-cp` está errado       | Corrigir a compilação e rodar na raiz do projeto |
| `missing separator` (make)             | Receitas do Makefile com espaços em vez de TAB | Usar TAB nas linhas de comando                   |

---

## 9. Decisões de projeto

| #   | Decisão                                            | Alternativa considerada                                         | Justificativa                                                                    | Custo / trade-off                                           |
| --- | -------------------------------------------------- | --------------------------------------------------------------- | -------------------------------------------------------------------------------- | ----------------------------------------------------------- |
| 1   | Usar o padrão **State**                            | `switch` sobre o `StatusPedido` dentro de `Pedido`              | Evita condicionais repetidas em 3 operações; cada estado fica isolado e legível  | 7 tipos em vez de 1 classe; transições espalhadas           |
| 2   | **Transições definidas pelos estados**             | Tabela central de transições no `Pedido`                        | O `Pedido` não precisa saber das regras; adicionar um estado altera pouco código | Cada estado conhece seus sucessores (`new EstadoX()`)       |
| 3   | `StatusPedido` como **enum separado**              | Derivar o status da classe do estado (`instanceof`)             | Identificação estável para GUI, log e testes                                     | Duas hierarquias paralelas a manter em sincronia            |
| 4   | Estados recebem o `Pedido` como **parâmetro**      | Estado guarda referência ao contexto                            | Estados sem atributos de instância                                               | `mudarEstado` precisa ser público (ver seção 11)            |
| 5   | **`IllegalStateException`** para operação inválida | Retornar `boolean`; exceção própria; consultar ações permitidas | Simples, padrão da linguagem, não polui as assinaturas                           | Usa exceção para uma situação esperada (ver seção 11)       |
| 6   | **Swing**                                          | JavaFX, interface web                                           | Já vem no JDK; zero dependências                                                 | Visual datado; Swing está em manutenção, não em evolução    |
| 7   | **GUI sem regras de negócio**                      | Habilitar/desabilitar botões por estado                         | Uma única fonte de verdade: os estados                                           | O usuário clica em ações que sempre falham                  |
| 8   | **Log da GUI por comparação antes/depois**         | Alterar `Pedido` para notificar a GUI                           | Nenhuma alteração no código do domínio                                           | A GUI só vê mudanças causadas por ela mesma                 |
| 9   | Um **único pedido por vez** na GUI                 | Lista de pedidos em paralelo                                    | Simplicidade; foco no padrão                                                     | Não demonstra vários pedidos simultâneos                    |
| 10  | **Makefile** para automatizar                      | Maven/Gradle; script `.bat`                                     | Poucas linhas, sem dependências, recompilação incremental                        | Não é portável para Windows puro; não gerencia dependências |

---

## 10. Relação com a literatura

### 10.1 Material da disciplina

A aula _GoFs Comportamentais_ (Profa. Milene Serrano, UnB Gama) é a **fonte de notação e de estrutura** desta documentação: define o State como padrão comportamental, apresenta seu objetivo, a modelagem genérica e a aplicada, e indica como referências o catálogo do GoF (citado como 1995) e o artigo do SourceMaking sobre State em Java.

### 10.2 Padrões de projeto (GoF)

- **State** (_Design Patterns: Elements of Reusable Object-Oriented Software_, 1995): fonte primária. A estrutura Context/State/ConcreteState, a delegação e a discussão sobre quem define as transições aparecem diretamente neste sistema.
- **Singleton / Flyweight**: o catálogo cita que objetos de estado sem atributos podem ser compartilhados (muitas vezes como singletons). **Este projeto não aplica essa otimização** (seção 11.3).
- **Strategy**: estrutura semelhante, intenção diferente (seção 4.5).
- **Observer**: não usado, mas seria a solução natural para a GUI reagir a mudanças feitas fora dela (seção 12).

### 10.3 Material didático

- **Head First Design Patterns** (Freeman e Robson) apresenta o State com o exemplo de uma máquina de venda de chicletes, que tem a mesma estrutura deste sistema e mostra bem o problema dos `if` aninhados que o padrão elimina.
- **Refactoring** (Fowler) descreve a técnica _Replace Conditional with Polymorphism_, que é a mecânica por trás do State: trocar condicionais sobre um tipo/estado por chamadas polimórficas.
- **Refactoring to Patterns** (Kerievsky) cataloga a refatoração _Replace State-Altering Conditionals with State_, que parte justamente de um código com `switch` sobre status, como seria a versão "ingênua" deste sistema.

### 10.4 Modelagem de máquinas de estados

- **Statecharts** (Harel, 1987) é a base teórica dos diagramas de estados da UML, usados na seção 3.
- A **especificação UML** (OMG) define o diagrama de máquina de estados, com estados, transições e estado inicial/final.

### 10.5 Princípios de projeto

- **Princípio Aberto/Fechado** (Meyer; popularizado por Martin): adicionar um novo estado exige criar uma classe e ajustar apenas os estados vizinhos, sem reescrever condicionais em `Pedido`. A adesão é **parcial**: adicionar uma nova _operação_ ainda exige alterar a interface e todos os estados.
- **Responsabilidade Única**: cada estado concentra as regras de uma etapa do ciclo de vida.
- **Encapsulamento**: o `Pedido` esconde o estado atual; contudo `mudarEstado` público enfraquece isso (seção 11.2).

### 10.6 Boas práticas em Java

- _Effective Java_ (Bloch): uso de `enum` para constantes, enum como singleton, e a recomendação de usar exceções para condições excepcionais. Esta última é diretamente relevante para a crítica da seção 11.5.
- **Documentação oficial da Oracle** para Swing (_Creating a GUI with Swing_, _Concurrency in Swing_) e para `IllegalStateException`.

---

## 11. Visão crítica

### 11.1 O que funciona bem

- **Aderência ao padrão**: a estrutura segue o GoF de forma reconhecível, o que torna o código um bom material de estudo.
- **Ausência de condicionais sobre o status** em `Pedido` e na GUI.
- **Regras localizadas**: para saber "o que acontece se cancelar um pedido pago", basta abrir `EstadoPago`.
- **Mensagens de erro específicas** por estado, que ajudam o usuário a entender _por que_ a operação falhou.
- **GUI desacoplada do domínio**: nenhuma regra de transição vazou para a interface.
- **Extensibilidade de estados**: um novo estado (por exemplo, `EM_DEVOLUCAO`) é uma nova classe, mais ajustes pontuais nos vizinhos.

### 11.2 `mudarEstado` é público

O comentário do código diz que "só os ConcreteState devem chamar este método", mas **nada na linguagem impõe isso**: qualquer classe pode fazer `pedido.mudarEstado(new EstadoEntregue())` e pular o fluxo, inclusive a GUI. Como `Pedido` e os estados estão em pacotes diferentes (`context` e `state`), não é possível usar visibilidade de pacote. Isso é uma **violação de encapsulamento** que o padrão em si não resolve. Vale notar que o próprio modelo apresentado na aula também expõe um método público de troca de estado (`+setState(in State)`); a crítica é, portanto, uma melhoria **além** do material, e não um desvio dele.

### 11.3 Um objeto novo a cada transição

Os estados não têm atributos de instância, então poderiam ser **compartilhados** (uma instância por estado). Hoje, cada `new EstadoPago()` cria um objeto descartável. Em escala de exemplo isso é irrelevante, mas é uma decisão que o GoF discute e que aqui ficou implícita.

### 11.4 Duplicação e acoplamento entre estados

- Todos os estados repetem blocos `throw new IllegalStateException(...)`. Uma classe abstrata com o comportamento padrão "operação inválida" reduziria o código; cada estado sobrescreveria só o que permite.
- Cada estado referencia **diretamente** classes concretas de outros estados. O custo do padrão (mais classes, transições espalhadas) aparece aqui: para entender o fluxo completo é preciso abrir 6 arquivos, o que o diagrama da seção 3 compensa.
- Há **dependência circular entre pacotes**: `context` importa `state` (o `Pedido` cria `EstadoAguardandoPagamento`) e `state` importa `context` (usa `Pedido` e `StatusPedido`). Em Java funciona, mas é um cheiro de acoplamento.

### 11.5 Exceções como fluxo normal

Tentar "avançar sem pagar" **não é uma situação excepcional**: é algo que um usuário faz com frequência. Usar exceção para isso contraria a recomendação de reservá-las para condições excepcionais, e a escolha de `IllegalStateException` (genérica) impede que quem chama diferencie "operação inválida neste estado" de um erro de programação. Alternativas: uma exceção própria (`TransicaoInvalidaException`), um resultado explícito (`boolean` ou tipo `Resultado`), ou um método `acoesPermitidas()` que a GUI consulte para habilitar botões.

Nota: na GUI isso é **intencional** (seção 6.6), mas em um sistema real a interface bloquearia ações impossíveis em vez de provocar o erro.

### 11.6 A operação `avancar()` é genérica demais

"Avançar" esconde significados de domínio diferentes: _iniciar separação_, _enviar_, _marcar como entregue_. Na prática, cada uma dessas ações teria **pré-condições, dados e efeitos próprios** (código de rastreio, data de entrega…). Com uma única operação genérica, o modelo não consegue expressar essas diferenças.

### 11.7 O comportamento que varia é pequeno

Quase todo método dos estados faz uma de duas coisas: troca de estado ou lança exceção. O State brilha quando o estado muda **o que a operação faz** (calcular frete de forma diferente, gerar documentos distintos, acionar sistemas externos). Aqui, o benefício real do padrão é **organizacional**, e uma alternativa mais compacta seria um `enum` com uma tabela de transições. Para um modelo com 6 estados e 3 operações, essa alternativa seria defensável; o State se justifica mais pelo valor didático e pela expectativa de crescimento do comportamento por estado.

### 11.8 Regras de negócio simplificadas

- Cancelar após `ENVIADO` é permitido "por simplicidade" (comentário do próprio código).
- `ENTREGUE → CANCELADO` mistura duas ideias (cancelamento e devolução) em uma só operação e um só estado final.
- Não há distinção entre **cancelado antes do pagamento** (sem estorno) e **cancelado depois** (com estorno).
- `confirmarPagamento` assume sucesso: não existe pagamento recusado, expirado ou estornado.

### 11.9 Observações sobre a interface

- **Sem sincronização com o domínio**: a GUI só atualiza quando o próprio usuário clica. Se o `Pedido` mudasse por outra origem (outra thread, outro módulo), a tela ficaria desatualizada. O padrão **Observer** resolveria isso.
- **Histórico duplicado**: o log do console (em `Pedido`) e o log da janela (em `PedidoGUI`) registram a mesma informação por caminhos diferentes.
- **Um pedido por vez**: criar um novo pedido descarta a referência ao anterior, que não pode mais ser consultado.
- **Texto fixo no código**: mensagens e rótulos estão embutidos nas classes, sem internacionalização.
- **Tela pouco adaptável**: a fileira de seis etapas em `GridLayout` pode ficar apertada em janelas estreitas.

### 11.10 Robustez e qualidade

- **Sem testes automatizados**: a tabela de 18 transições (seção 3.2) é um convite natural a testes parametrizados.
- **Sem proteção contra concorrência**: `Pedido` é mutável e não sincronizado. Duas threads chamando `avancar()` ao mesmo tempo poderiam gerar um estado inconsistente. Para a GUI (uma thread de eventos) não é problema; para um serviço web seria.
- **Sem persistência**: ao fechar a janela, todo o histórico e os pedidos se perdem. Persistir exigiria mapear `StatusPedido` para o banco e reconstruir o objeto de estado a partir dele (o que justificaria uma _factory_ de estados).
- **Idioma misturado** no código (nomes em português, termos do padrão em inglês); é uma escolha válida, mas deve ser consistente.

### 11.11 Síntese crítica

O sistema é um **bom exemplo didático** do State: pequeno o bastante para ser lido inteiro e completo o bastante para mostrar delegação, transições e rejeição de operações inválidas. As fragilidades relevantes (`mudarEstado` público, exceções como fluxo, `avancar` genérico, ausência de testes) são, em sua maioria, **simplificações aceitáveis para um exemplo**, mas precisariam ser resolvidas antes de o modelo ser usado como base de um sistema real.

---

## 12. Melhorias propostas

### 12.1 Estado base abstrato (reduz duplicação)

```java
public abstract class EstadoBase implements EstadoPedido {
    public void confirmarPagamento(Pedido p) { negar("confirmar pagamento"); }
    public void cancelar(Pedido p)           { negar("cancelar"); }
    public void avancar(Pedido p)            { negar("avançar"); }

    private void negar(String operacao) {
        throw new IllegalStateException(
            "Não é possível " + operacao + " em " + getStatus() + ".");
    }
}
```

Cada estado concreto sobrescreve **apenas** o que é permitido.

### 12.2 Instâncias compartilhadas

```java
public final class Estados {
    public static final EstadoPedido PAGO = new EstadoPago();
    public static final EstadoPedido CANCELADO = new EstadoCancelado();
    // ...
}
```

Os estados passam a fazer `p.mudarEstado(Estados.PAGO)`. Só é seguro enquanto os estados **não tiverem atributos de instância**.

### 12.3 Encapsular `mudarEstado`

Colocar `Pedido` e os estados no **mesmo pacote** e tornar `mudarEstado` _package-private_ (sem modificador). A GUI, em outro pacote, deixa de poder chamá-lo.

### 12.4 Expor as ações permitidas

Adicionar, por exemplo, `Set<Acao> acoesPermitidas()` em `EstadoPedido`. A GUI pode então habilitar só os botões válidos, **sem duplicar regras**, porque a informação vem do próprio estado.

### 12.5 Notificar a interface

Fazer o `Pedido` aceitar _listeners_ (Observer) e notificá-los em `mudarEstado`. A GUI se registra e atualiza a tela e o histórico a partir do evento, eliminando o log duplicado.

### 12.6 Testes

Um teste parametrizado (JUnit) percorrendo as 18 combinações da seção 3.2, verificando o status resultante ou a exceção esperada.

### 12.7 Evolução do domínio

Operações específicas (`iniciarSeparacao()`, `enviar(codigoRastreio)`, `entregar()`) no lugar de `avancar()`, e um estado/fluxo próprio para **devolução**.

---

## 13. Limitações desta documentação

- As **transições e as mensagens de erro** foram derivadas da **leitura do código**; o comportamento observado na interface está nas seções 15 e 16 (imagens e vídeo).
- A referência ao **Diagrama de Atividades** (fluxo de Devolução e Estorno) vem de um comentário no código; o diagrama em si não foi analisado.
- As referências bibliográficas (seção 14) estão citadas **sem números de página**. Se for usar em trabalho acadêmico, confirme edição, ano e páginas na sua cópia.
- As figuras das seções 2.2, 2.4, 3.1 e 5.3.1 devem corresponder aos arquivos `.puml` do projeto (`state-pedido`, `state-generico`, `state-estados` e `state-sequencia`). Esses arquivos foram escritos sem renderização no momento da elaboração do texto; confira a legibilidade e a coerência das figuras com a explicação da seção 7.
- O artigo do **SourceMaking** indicado na aula **não foi consultado**.
- O ano do livro do GoF (1995) segue a citação da aula; a primeira tiragem é de 1994. Use a data exigida pela sua norma de citação e mantenha-a em todo o texto.
- A estrutura deste documento **não repete a ordem exata** dos slides da aula.

---

## 14. Referências

**Material da disciplina**

- SERRANO, M. _Arquitetura e Desenho de Software — Aula: GoFs Comportamentais_. Slides. UnB Gama.
- SourceMaking. _State Design Pattern in Java_ (indicado nos slides). <https://sourcemaking.com/design_patterns/state/java/5>

**Padrões de projeto e refatoração**

- GAMMA, E.; HELM, R.; JOHNSON, R.; VLISSIDES, J. _Design Patterns: Elements of Reusable Object-Oriented Software_. Addison-Wesley, 1995. (Padrão State; menções a Singleton, Flyweight e Strategy.)
- FREEMAN, E.; ROBSON, E. _Head First Design Patterns_, 2ª ed. O'Reilly. (Capítulo sobre o padrão State.)
- FOWLER, M. _Refactoring: Improving the Design of Existing Code_. Addison-Wesley. (_Replace Conditional with Polymorphism_.)
- KERIEVSKY, J. _Refactoring to Patterns_. Addison-Wesley, 2004. (_Replace State-Altering Conditionals with State_.)
- Refactoring Guru. _State_. <https://refactoring.guru/design-patterns/state>

**Modelagem e princípios**

- HAREL, D. Statecharts: A visual formalism for complex systems. _Science of Computer Programming_, 8(3), 1987.
- OMG. _Unified Modeling Language (UML) Specification_ — diagramas de classes e de máquina de estados.
- FOWLER, M. _UML Distilled_. Addison-Wesley. (Usos da UML; crítica à agregação.)
- BOOCH, G.; RUMBAUGH, J.; JACOBSON, I. _The Unified Modeling Language User Guide_. Addison-Wesley.
- MEYER, B. _Object-Oriented Software Construction_. Prentice Hall, 1988. (Princípio Aberto/Fechado.)
- MARTIN, R. C. _Agile Software Development: Principles, Patterns, and Practices_. Prentice Hall, 2002. (Princípios SOLID e de pacotes.)
- PARNAS, D. L. On the criteria to be used in decomposing systems into modules. _Communications of the ACM_, 15(12), 1972. (Ocultação de informação.)

**Java**

- BLOCH, J. _Effective Java_, 3ª ed. Addison-Wesley, 2018. (Enums, enum como singleton, uso adequado de exceções.)
- ORACLE. _The Java Tutorials_: _Enum Types_; _Creating a GUI with Swing_; _Laying Out Components Within a Container_; _Concurrency in Swing_; _Managing Source and Class Files_ (pacotes e estrutura de diretórios). <https://docs.oracle.com/javase/tutorial/>
- ORACLE. _Java Platform API Specification_: `java.lang.IllegalStateException`. <https://docs.oracle.com/en/java/javase/21/docs/api/>
- ORACLE. Documentação das ferramentas `javac` e `java`. <https://docs.oracle.com/en/java/javase/21/docs/specs/man/>

**Ferramentas**

- GNU Make Manual. <https://www.gnu.org/software/make/manual/make.html>
- PlantUML. _Class Diagram_ e _Language Reference Guide_. <https://plantuml.com/class-diagram> e <https://plantuml.com/guide>

---

## 15. Demonstração da interface (Imagens em cada estado)

### Pedido criado aguardando pagamento

<div style="text-align:center;">

![Pedido criado aguardando pagamento](<../assets/FluxoDeCompra(State)/01.png>)

<p><strong>Interface Gráfica</strong> — Pedido criado aguardando pagamento. <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/assets/FluxoDeCompraState/01.png ":ignore")

</div>

### Pedido Pago

<div style="text-align:center;">

![Pedido Pago](<../assets/FluxoDeCompra(State)/02.png>)

<p><strong>Interface Gráfica</strong> — Pedido pago. <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/assets/FluxoDeCompraState/02.png ":ignore")

</div>

### Pedido em separação

<div style="text-align:center;">

![Pedido em separação](<../assets/FluxoDeCompra(State)/03.png>)

<p><strong>Interface Gráfica</strong> — Pedido em separação. <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/assets/FluxoDeCompraState/03.png ":ignore")

</div>

### Pedido enviado

<div style="text-align:center;">

![Pedido enviado](<../assets/FluxoDeCompra(State)/04.png>)

<p><strong>Interface Gráfica</strong> — Pedido enviado. <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/assets/FluxoDeCompraState/04.png ":ignore")

</div>

### Pedido entregue

<div style="text-align:center;">

![Pedido entregue](<../assets/FluxoDeCompra(State)/05.png>)

<p><strong>Interface Gráfica</strong> — Pedido entregue. <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/assets/FluxoDeCompraState/05.png ":ignore")

</div>

### Fim do ciclo de vida da compra

<div style="text-align:center;">

![Fim do ciclo de vida da compra](<../assets/FluxoDeCompra(State)/06.png>)

<p><strong>Interface Gráfica</strong> — Fim do ciclo de vida da compra. <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/assets/FluxoDeCompraState/06.png ":ignore")

</div>

## 16. Demonstração da interface e fluxos (vídeos)

### Fluxo de compra padrão

Esse fluxo trata do ciclo de vida padrão de um pedido, partimos de um pedido que foi criado, logo em seguida o pagamento desse pedido é realizado, em seguida é testado que é exibida uma mensagem de operação inválida ao clicar em confirmar pagamento de um pedido cujo pagamento já foi realizado, após isso o estado do pedido é atualizado para "em separação", ou seja, sendo separado e aguardando o envio ser realizado, em seguida o estado do pedido é atualizado para enviado e logo em seguida para entregue, assim finalizando o ciclo de vida padrão da compra de um pedido.

<div style="text-align:center;">

[01.mp4](<../assets/FluxoDeCompra(State)/01.mp4> ":include :type=video controls width=80%")

<p><strong>Interface Gráfica</strong> — Fluxo de compra padrão. <em>Autor: José Joaquim da Silva Neto</em></p>

</div>

### Fluxo de compra com cancelamentos

<div style="text-align:center;">

[02.mp4](<../assets/FluxoDeCompra(State)/02.mp4> ":include :type=video controls width=80%")

<p><strong>Interface Gráfica</strong> — Fluxo de compra com cancelamentos. <em>Autor: José Joaquim da Silva Neto</em></p>

</div>

## Histórico de Versões

| Versão | Data       | Descrição                                                                                                                           | Autor(es)                  | Revisor(es) |
| ------ | ---------- | ----------------------------------------------------------------------------------------------------------------------------------- | -------------------------- | ----------- |
| 1.0    | 05/10/2026 | Criação da página                                                                                                                   | José Joaquim da Silva Neto | --          |
| 1.1    | 15/10/2026 | Adiciona justificativa da tecnologia utilizada para a interface gráfica                                                             | José Joaquim da Silva Neto | --          |
| 1.2    | 15/10/2026 | Adiciona imagens e vídeo da demonstração da interface gráfica                                                                       | José Joaquim da Silva Neto | --          |
| 1.3    | 15/10/2026 | Adiciona documentação                                                                                                               | José Joaquim da Silva Neto | --          |
| 1.4    | 15/10/2026 | Adiciona a explicação dos diagramas de classes, decisões de projeto, literatura, visão crítica, melhorias, limitações e referências | José Joaquim da Silva Neto | --          |
| 1.5    | 15/10/2026 | Revisão geral: corrige referências cruzadas, legendas, textos alternativos, links de download e digitação                           | José Joaquim da Silva Neto | --          |
| 1.6    | 15/10/2026 | Adiciona segundo vídeo de apresentação da interface gráfica                                                                         | José Joaquim da Silva Neto | --          |

