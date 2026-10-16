# Observer no Pagamento

> Sistema em Java que modela o subsistema de **Pagamento** do G1_ProjetoComercioEletronico usando o padrão de projeto comportamental **Observer**: quando o status de um pagamento muda, todas as partes interessadas são avisadas e cada uma decide sozinha como reagir. Acompanha uma interface gráfica local em **Swing**.

## Sumário

1. [Introdução](#1-introdução)
2. [Visão geral do sistema](#2-visão-geral-do-sistema)
3. [O modelo de notificação](#3-o-modelo-de-notificação)
4. [Fundamentação: o padrão Observer](#4-fundamentação-o-padrão-observer)
5. [Implementação, classe por classe](#5-implementação-classe-por-classe)
6. [A interface gráfica](#6-a-interface-gráfica)
7. [Demonstração](#7-demonstração)
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

Um pagamento passa por vários status: nasce **pendente**, entra **em análise** e termina **aprovado** ou **recusado**. Um pagamento aprovado ainda pode ser **estornado**. O ponto central é que **cada mudança de status precisa provocar reações em partes diferentes do sistema**:

* O comprador precisa ser avisado de qualquer mudança.
* Quando o pagamento é aprovado, o pedido deve ser liberado para o vendedor e a nota fiscal deve ser emitida.
* Quando o pagamento é recusado ou estornado, o item reservado deve voltar ao estoque.

Se a classe `Pagamento` chamasse cada um desses módulos diretamente, ela ficaria acoplada a todos eles, e cada nova reação (um SMS, uma atualização de relatório) obrigaria a editar o `Pagamento`.

### 1.2 Abordagem

O sistema usa o padrão **Observer** (Gamma, Helm, Johnson e Vlissides, 1995): o `Pagamento` mantém uma lista de observadores e, a cada mudança de status, avisa todos eles. O `Pagamento` não sabe quem são nem o que fazem; cada observador decide, no próprio método `atualizar`, se aquele status interessa a ele.

### 1.3 Objetivos deste documento

* Descrever a arquitetura e o comportamento do sistema.
* Mostrar como a interface gráfica funciona e como usá-la.
* Registrar as decisões tomadas e as alternativas consideradas.
* Relacionar o código com a literatura de padrões de projeto.
* Avaliar criticamente os pontos fortes e as fragilidades da solução.

### 1.4 Escopo

O sistema cobre **somente o mecanismo de notificação do pagamento e uma interface para exercitá-lo**. Não há gateway de pagamento real, emissão fiscal real, estoque, persistência nem usuários. As reações dos observadores são simuladas com mensagens.

### 1.5 Enquadramento na disciplina

Este trabalho segue o material da aula *Arquitetura e Desenho de Software: GoFs Comportamentais* (Profa. Milene Serrano, UnB):

* **Padrão GoF:** solução consolidada para um problema recorrente no desenvolvimento e na manutenção de software orientado a objetos.
* **Categoria comportamental:** padrões voltados a alterações no nível do comportamento dos objetos.
* **Objetivo do Observer, segundo a aula:** definir um mecanismo eficiente para reagir às alterações realizadas em determinados objetos.

**Correspondência com a estrutura usada na aula**

| Estrutura da aula | Onde está nesta documentação |
|---|---|
| Objetivo | Seções 1.1 a 1.3 e 4.1 |
| Modelagem genérica | Seção 2.4 |
| Participantes | Seções 2.3 e 2.4 |
| Modelagem aplicada/instanciada | Seção 2.2 |
| Motivação | Seção 1.1 |
| Solução | Seções 4 e 5 |
| Possível implementação | Seções 5 e 6 (código e interface gráfica) |
| Teste e saída | Seção 7 (demonstração) |
| Considerações finais | Seções 11 e 13 |
| Referências | Seção 14 |

As seções 9 (decisões), 10 (literatura), 11 (visão crítica) e 12 (melhorias) **vão além** da estrutura da aula e estão identificadas como acréscimos.

---

## 2. Visão geral do sistema

### 2.1 Estrutura de pastas

Em Java, o nome do pacote (`package`) deve corresponder à estrutura de diretórios. O projeto fica assim:

```
observer-pagamento/
├── README.md
└── src/
    ├── Main.java                         # demonstração no terminal
    ├── InterfaceGrafica.java             # demonstração com janela (Swing)
    ├── modelo/
    │   ├── PagamentoObservavel.java      # Subject (interface)
    │   ├── Pagamento.java                # ConcreteSubject
    │   └── StatusPagamento.java          # enum com os 5 status
    ├── observadores/
    │   ├── ObservadorDePagamento.java    # Observer (interface)
    │   ├── NotificacaoComprador.java     # ConcreteObserver
    │   ├── LiberacaoPedido.java          # ConcreteObserver
    │   ├── EmissaoNotaFiscal.java        # ConcreteObserver
    │   └── LiberacaoEstoque.java         # ConcreteObserver
    └── cliente/
        └── GatewayPagamento.java         # Client
```

### 2.2 Diagrama de classes (modelagem aplicada/instanciada)

<div style="text-align:center;">

![Diagrama de classes do Observer no Pagamento](../Assets/ObserverPagamento/DiagramaDeClasses.png)

<p><strong>Observer no Pagamento</strong>: diagrama de classes da modelagem aplicada. <em>Autores: João Paulo Barbosa Pereira Nunes e Júlia Santana Campos</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/ObserverPagamento/DiagramaDeClasses.png ':ignore')

</div>

**Notação seguida:** seta tracejada com triângulo vazio para **realização** (classe implementa interface); **agregação** (losango vazio) entre `Pagamento` e `ObservadorDePagamento`, com multiplicidade `0..*`; associação simples entre `Pagamento` e `StatusPagamento`; dependência tracejada do `GatewayPagamento` para o `Pagamento`. Cada classe leva um selo com seu papel no padrão: interfaces do padrão em vermelho, classes concretas em amarelo e o Client em azul.

### 2.3 Papéis no padrão

| Papel (GoF) | Classe | Responsabilidade |
|---|---|---|
| **Subject** | `PagamentoObservavel` | Declara como registrar, remover e avisar observadores. |
| **ConcreteSubject** | `Pagamento` | Guarda o status e a lista de observadores; avisa todos a cada mudança de status. |
| **Observer** | `ObservadorDePagamento` | Declara o método `atualizar`, chamado a cada aviso. |
| **ConcreteObserver** | `NotificacaoComprador`, `LiberacaoPedido`, `EmissaoNotaFiscal`, `LiberacaoEstoque` | Decidem, cada um, se reagem ao novo status e como. |
| **Client** | `GatewayPagamento` | Informa o resultado da transação, mudando o status do pagamento sem saber quem será avisado. |

### 2.4 Modelagem genérica

<div style="text-align:center;">

![Modelagem genérica do Observer](../Assets/ObserverPagamento/Generico.png)

<p><strong>Modelagem genérica</strong>: o Observer conforme o catálogo GoF. <em>Autores: João Paulo Barbosa Pereira Nunes e Júlia Santana Campos</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/ObserverPagamento/Generico.png ':ignore')

</div>

| Modelagem genérica | Modelagem aplicada (este sistema) |
|---|---|
| `Client` | `GatewayPagamento` |
| `Subject` | `PagamentoObservavel` |
| `attach(Observer)` | `adicionarObservador(ObservadorDePagamento)` |
| `detach(Observer)` | `removerObservador(ObservadorDePagamento)` |
| `notify()` | `notificarObservadores()` |
| `ConcreteSubject` | `Pagamento` |
| `getState()` / `setState()` | `getStatus()` / `setStatus(StatusPagamento)` |
| `subjectState` | atributo `status` |
| `Observer` | `ObservadorDePagamento` |
| `update()` | `atualizar(Pagamento)` |
| `ConcreteObserver` | Os quatro observadores concretos |
| (sem equivalente) | `StatusPagamento`, elemento adicional que não aparece na modelagem genérica |

---

## 3. O modelo de notificação

### 3.1 Quem reage a quê

Esta tabela foi montada a partir da leitura do método `atualizar` de cada `ConcreteObserver`. "Reage" significa que o observador executa sua ação; "Ignora" significa que ele **recebeu o aviso**, mas aquele status não interessa a ele.

| Novo status | `NotificacaoComprador` | `LiberacaoPedido` | `EmissaoNotaFiscal` | `LiberacaoEstoque` |
|---|---|---|---|---|
| `EM_ANALISE` | Reage | Ignora | Ignora | Ignora |
| `APROVADO` | Reage | Reage | Reage | Ignora |
| `RECUSADO` | Reage | Ignora | Ignora | Reage |
| `ESTORNADO` | Reage | Ignora | Ignora | Reage |

São 4 status alcançáveis × 4 observadores = **16 decisões**, e cada uma está implementada em exatamente um método de exatamente uma classe. O `Pagamento` não participa de nenhuma delas.

### 3.2 Observações sobre o modelo

* `PENDENTE` é o status inicial, definido no construtor. Como ele não é resultado de uma mudança, **não gera notificação**.
* **Todos os observadores registrados são avisados em toda mudança**, inclusive os que vão ignorar. Quem filtra é o observador, não o `Pagamento`.
* Se `setStatus` for chamado com o mesmo status atual, **ninguém é avisado**: não houve mudança.
* O sistema **não valida as transições**. Nada impede, por exemplo, ir de `ESTORNADO` para `APROVADO`. Validar o ciclo de vida é a responsabilidade de outro padrão, o **State**, aplicado pela subequipe no documento [Fluxo de Compra (State)](docs/Base/Relatórios/Subequipe03/GofsComportamentais/FluxoDeCompra(State).md). Ver seção 4.5.

---

## 4. Fundamentação: o padrão Observer

### 4.1 Intenção

Segundo o catálogo do GoF, o Observer define uma **dependência um para muitos** entre objetos, de modo que, quando um objeto muda de estado, todos os seus dependentes são notificados e atualizados automaticamente.

### 4.2 Quando usar

O próprio catálogo indica o padrão quando:

1. a mudança em um objeto exige mudanças em outros, e não se sabe de antemão quantos objetos precisam mudar; ou
2. um objeto deve avisar outros sem fazer suposições sobre quem eles são, ou seja, sem ficar fortemente acoplado a eles.

Os dois casos aparecem aqui: a lista de reações a um pagamento cresce com o tempo, e o `Pagamento` não deveria conhecer estoque, nota fiscal ou notificações.

### 4.3 Consequências descritas pelo GoF

| Consequência | Presente neste sistema? |
|---|---|
| Acoplamento abstrato entre Subject e Observer | Sim: o `Pagamento` só conhece a interface `ObservadorDePagamento` |
| Suporte a comunicação em difusão (*broadcast*) | Sim: um único `notificarObservadores()` avisa todos os registrados |
| Atualizações inesperadas: uma mudança simples pode disparar uma cascata de reações custosas | **Sim, como risco**: o `Pagamento` não sabe o custo das reações que provoca (ver seção 11) |

### 4.4 Modelo *pull* e quem dispara a notificação

O catálogo discute duas formas de passar a informação ao observador:

* **Push:** o Subject envia os detalhes da mudança no próprio aviso.
* **Pull:** o Subject envia o mínimo, e o observador consulta o que precisa.

Este projeto adota o **pull**: `atualizar(Pagamento)` recebe o próprio pagamento, e cada observador lê o `getStatus()` ou o `getValor()` conforme sua necessidade.

O catálogo também pergunta **quem dispara** a notificação. Aqui, é o próprio `setStatus` que chama `notificarObservadores()`, então o cliente nunca esquece de avisar. O custo é que duas mudanças seguidas geram duas rodadas de notificação.

### 4.5 Diferença para o Strategy e para o State

Os três padrões da aula têm estruturas parecidas, mas intenções diferentes:

| Padrão | Pergunta que responde | Exemplo no comércio eletrônico |
|---|---|---|
| **Strategy** | *Como* fazer algo, escolhido de fora pelo cliente | Escolher a forma de pagamento: cartão, Pix ou boleto (exemplo da aula) |
| **State** | *O que* o objeto pode fazer agora, conforme seu estado | Impedir que um pedido aguardando pagamento seja enviado |
| **Observer** | *Quem* precisa saber que algo mudou | Avisar comprador, pedido, fiscal e estoque quando o pagamento muda |

Eles se complementam: o State poderia controlar **quais** mudanças de status do pagamento são válidas, e o Observer continuaria responsável por **avisar** sobre as mudanças que aconteceram.

---

## 5. Implementação, classe por classe

### 5.1 `StatusPagamento` (enum)

```java
public enum StatusPagamento {
    PENDENTE("Pendente"),
    EM_ANALISE("Em analise"),
    APROVADO("Aprovado"),
    RECUSADO("Recusado"),
    ESTORNADO("Estornado");
    ...
    public String getDescricao() { return descricao; }
}
```

Representa o conjunto fixo de status possíveis, cada um com uma descrição para exibição. Um `enum` é a forma idiomática em Java de representar um conjunto fixo de constantes.

### 5.2 `PagamentoObservavel` (interface Subject)

```java
public interface PagamentoObservavel {
    void adicionarObservador(ObservadorDePagamento observador);
    void removerObservador(ObservadorDePagamento observador);
    void notificarObservadores();
}
```

É o contrato de quem pode ser observado. Corresponde ao `attach`, `detach` e `notify` da modelagem genérica.

### 5.3 `Pagamento` (ConcreteSubject)

```java
public class Pagamento implements PagamentoObservavel {
    private final String id;
    private final double valor;
    private StatusPagamento status;
    private final List<ObservadorDePagamento> observadores = new ArrayList<>();

    public Pagamento(String id, double valor) {
        this.id = id;
        this.valor = valor;
        this.status = StatusPagamento.PENDENTE;      // todo pagamento nasce pendente
    }

    public void setStatus(StatusPagamento novo) {
        if (novo == this.status) {
            return;                                   // sem mudança, sem aviso
        }
        this.status = novo;
        notificarObservadores();
    }

    @Override
    public void adicionarObservador(ObservadorDePagamento observador) {
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void notificarObservadores() {
        for (ObservadorDePagamento observador : new ArrayList<>(observadores)) {
            try {
                observador.atualizar(this);
            } catch (RuntimeException e) {
                System.out.println("  [ERRO] " + observador.getClass().getSimpleName()
                        + " falhou: " + e.getMessage());
            }
        }
    }
    ...
}
```

Pontos importantes:

* **Nenhum conhecimento dos observadores concretos**: o `Pagamento` só usa a interface `ObservadorDePagamento`.
* **O estado é atualizado antes do aviso**: quando o observador consulta `getStatus()`, já encontra o valor novo. O GoF destaca isso como requisito para o Subject estar consistente ao notificar.
* **Cópia da lista**: a notificação percorre uma cópia, para que um observador possa se remover durante o aviso sem quebrar o laço.
* **Falhas isoladas**: se um observador lançar exceção, os demais continuam sendo avisados.
* **Sem duplicidade**: o mesmo observador não é registrado duas vezes.
* `id` e `valor` são `final`: a identidade e o valor do pagamento não mudam.

### 5.4 `ObservadorDePagamento` (interface Observer)

```java
public interface ObservadorDePagamento {
    void atualizar(Pagamento pagamento);
}
```

Tem um único método, o que a torna uma **interface funcional** em Java: um observador pode ser criado com uma expressão lambda, como o observador de SMS do `Main` (seção 5.7).

### 5.5 Observadores concretos

Todos têm a mesma forma: o método `atualizar` **verifica o status e decide se age**.

Exemplo, `LiberacaoEstoque`:

```java
public class LiberacaoEstoque implements ObservadorDePagamento {
    @Override
    public void atualizar(Pagamento pagamento) {
        StatusPagamento status = pagamento.getStatus();
        if (status == StatusPagamento.RECUSADO || status == StatusPagamento.ESTORNADO) {
            System.out.println("  [Estoque] Item do pagamento " + pagamento.getId()
                    + " devolvido ao estoque.");
        }
    }
}
```

| Observador | Reage a | Ação simulada |
|---|---|---|
| `NotificacaoComprador` | Qualquer status | Avisa o comprador do novo status |
| `LiberacaoPedido` | `APROVADO` | Libera o pedido para o vendedor preparar |
| `EmissaoNotaFiscal` | `APROVADO` | Emite a nota fiscal no valor pago |
| `LiberacaoEstoque` | `RECUSADO` ou `ESTORNADO` | Devolve o item reservado ao estoque |

### 5.6 `GatewayPagamento` (Client)

```java
public class GatewayPagamento {
    public void informarResultado(Pagamento pagamento, StatusPagamento novo) {
        System.out.println("\nGateway: pagamento " + pagamento.getId()
                + " mudou para " + novo.getDescricao());
        pagamento.setStatus(novo);
    }
}
```

Simula o serviço externo que processa a transação. Ele **só muda o status**: não sabe quem será avisado nem o que cada um fará.

### 5.7 `Main` (demonstração no terminal)

Executa cinco cenários:

| Cenário | O que demonstra |
|---|---|
| 1. Pagamento aprovado | Comprador avisado, pedido liberado, nota emitida |
| 2. Pagamento recusado | Comprador avisado, item devolvido ao estoque |
| 3. Aprovado e depois estornado | O mesmo pagamento gera duas rodadas de notificação |
| 4. Comprador desativa as notificações | `removerObservador` em ação |
| 5. Novo observador de SMS | Uma nova reação criada com lambda, **sem alterar o `Pagamento`** |

O cenário 5 é o argumento central do padrão:

```java
p5.adicionarObservador(pag -> System.out.println(
        "  [SMS] Seu pagamento " + pag.getId() + " foi " + pag.getStatus().getDescricao()));
```

---

## 6. A interface gráfica

### 6.1 Tecnologia

A interface usa o **Swing**, biblioteca gráfica que já faz parte da plataforma Java. Assim, o projeto continua sem dependências externas e compila apenas com o `javac`.

### 6.2 Organização da tela

| Área | Conteúdo | Papel no padrão |
|---|---|---|
| **1. Novo pagamento** | Campo de valor e botão para criar um pagamento | Cria o ConcreteSubject e registra os observadores marcados |
| **2. Gateway muda o status** | Botões Em análise, Aprovar, Recusar e Estornar | Fazem o papel do Client (`GatewayPagamento`) |
| **3. Observers registrados** | Caixas de marcação para cada observador | Marcar chama `adicionarObservador`; desmarcar chama `removerObservador` |
| **Cartões** | Um cartão por observador | Mostram o resultado da última notificação |
| **Saída do programa** | Histórico textual | O mesmo conteúdo que apareceria no terminal |

Cada cartão assume uma de três situações:

| Situação | Cor | Significado |
|---|---|---|
| **Reagiu** | Verde | O observador foi avisado e executou sua ação |
| **Notificado, sem ação** | Cinza | O observador foi avisado, mas o status não interessa a ele |
| **Não registrado** | Apagado | O observador não está na lista do pagamento |

A situação **"Notificado, sem ação"** é o que a interface acrescenta em relação ao terminal: ela torna visível que **todos os observadores registrados recebem o aviso**, e que a decisão de reagir é de cada um.

### 6.3 Como a interface sabe se o observador reagiu

A interface usa as **classes reais** do projeto, sem alterá-las. Para descobrir o que aconteceu em cada observador, ela registra no `Pagamento` um `ObservadorMonitorado`, que:

1. também implementa `ObservadorDePagamento`, então o `Pagamento` o trata como qualquer outro observador;
2. repassa o aviso ao observador real;
3. captura o que o observador real imprimiu: se imprimiu algo, ele **reagiu**; se não, ele **ignorou** o status.

Essa ideia de envolver um objeto com outro de mesma interface lembra o padrão **Decorator** (GoF, categoria estrutural). A limitação dessa escolha está na seção 11.

---

## 7. Demonstração

### 7.1 Pagamento criado

<div style="text-align:center;">

![Pagamento criado](../Assets/ObserverPagamento/01.png)

<p><strong>Interface Gráfica</strong>: pagamento criado, com quatro observadores registrados e o SMS fora da lista. <em>Autor: João Paulo Barbosa Pereira Nunes</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/ObserverPagamento/01.png ':ignore')

</div>

### 7.2 Pagamento em análise

<div style="text-align:center;">

![Pagamento em análise](../Assets/ObserverPagamento/02.png)

<p><strong>Interface Gráfica</strong>: em análise. Só o comprador reage; os outros três são notificados e ignoram. <em>Autor: João Paulo Barbosa Pereira Nunes</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/ObserverPagamento/02.png ':ignore')

</div>

### 7.3 Pagamento aprovado

<div style="text-align:center;">

![Pagamento aprovado](../Assets/ObserverPagamento/03.png)

<p><strong>Interface Gráfica</strong>: aprovado. Comprador, pedido e nota fiscal reagem; o estoque ignora. <em>Autor: João Paulo Barbosa Pereira Nunes</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/ObserverPagamento/03.png ':ignore')

</div>

### 7.4 Pagamento recusado

<div style="text-align:center;">

![Pagamento recusado](../Assets/ObserverPagamento/04.png)

<p><strong>Interface Gráfica</strong>: recusado. O estoque reage, e pedido e nota fiscal ignoram: o oposto do aprovado. <em>Autor: João Paulo Barbosa Pereira Nunes</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/ObserverPagamento/04.png ':ignore')

</div>

### 7.5 Estorno com a lista de observadores alterada

<div style="text-align:center;">

![Estorno com SMS](../Assets/ObserverPagamento/05.png)

<p><strong>Interface Gráfica</strong>: estorno após remover o comprador e adicionar o SMS. O SMS reage sem nenhuma alteração no Pagamento. <em>Autor: João Paulo Barbosa Pereira Nunes</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/ObserverPagamento/05.png ':ignore')

</div>

### 7.6 Vídeo da demonstração

O vídeo não tem áudio. O passo a passo abaixo descreve cada ação feita na tela e o que observar depois dela, na mesma ordem do vídeo.

<div style="text-align:center;">

[ObserverPagamento.mp4](../Assets/ObserverPagamento/ObserverPagamento.mp4 ':include :type=video controls width=80%')

<p><strong>Interface Gráfica</strong>: demonstração do Observer no pagamento. <em>Autor: João Paulo Barbosa Pereira Nunes</em></p>

</div>

**Passo a passo do vídeo**

| Passo | Ação na tela | O que observar | O que isso mostra do padrão |
|---|---|---|---|
| 1 | Clique em **Criar pagamento** com os quatro observadores marcados e o SMS desmarcado | Os quatro cartões passam para **Registrado**; o SMS continua **Não registrado**. A saída mostra quatro chamadas a `adicionarObservador` | O Subject guarda uma lista de observadores (*attach*) |
| 2 | Clique em **Em análise** | Só o **NotificacaoComprador** fica verde; os outros três ficam cinza | Todos os registrados são avisados, mas cada observador decide se reage |
| 3 | Clique em **Aprovar** | **Comprador**, **Pedido** e **Fiscal** ficam verdes; o **Estoque** fica cinza | Um único aviso do Subject provoca reações diferentes em cada observador |
| 4 | Clique em **Criar pagamento** de novo e depois em **Recusar** | O **Estoque** fica verde; **Pedido** e **Fiscal** ficam cinza, o oposto do passo 3 | O comportamento é definido nos observadores; o `Pagamento` é o mesmo nos dois casos |
| 5 | Desmarque **NotificacaoComprador** e marque **SMS** | A saída mostra `removerObservador` e `adicionarObservador`; o cartão do comprador fica apagado | A lista de observadores pode mudar em tempo de execução (*attach* e *detach*) |
| 6 | Clique em **Criar pagamento**, depois em **Aprovar** e em **Estornar** | No estorno, **Estoque** e **SMS** ficam verdes; o comprador não aparece | Uma nova reação (SMS) foi incluída sem alterar uma linha da classe `Pagamento` |
| 7 | Clique de novo no status atual (por exemplo, **Estornar** outra vez) | A saída informa que o status já é o mesmo e nenhum cartão muda | Sem mudança de estado, não há notificação |

**Como ler as cores durante o vídeo:** verde significa que o observador reagiu; cinza, que recebeu o aviso mas o status não interessa a ele; apagado, que ele não está registrado no pagamento (seção 6.2).

---

## 8. Como compilar e executar

Requer **Java 17 ou superior**. A partir da pasta do projeto (a que contém `src`):

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out Main              # versão no terminal
java -cp out InterfaceGrafica  # versão com janela
```

**Observação para macOS:** o sistema bloqueia o Terminal de ler as pastas Downloads, Mesa e Documentos até que a permissão seja concedida. Se aparecer `Operation not permitted`, mova o projeto para a pasta do usuário ou libere o Terminal em *Ajustes do Sistema, Privacidade e Segurança, Arquivos e Pastas*.

---

## 9. Decisões de projeto

*(Acréscimo em relação à estrutura da aula.)*

| Decisão | Alternativa considerada | Motivo da escolha |
|---|---|---|
| Interfaces próprias (`PagamentoObservavel`, `ObservadorDePagamento`) | `java.util.Observable` e `java.util.Observer` | As classes da biblioteca estão **depreciadas desde o Java 9**, e `Observable` é uma classe, não uma interface, o que obrigaria `Pagamento` a herdar dela |
| Modelo *pull* (`atualizar(Pagamento)`) | Modelo *push* com os detalhes da mudança | Cada observador precisa de dados diferentes (status, valor, id); o pull evita uma assinatura que tente agradar a todos |
| `setStatus` dispara a notificação | O cliente chamar `notificarObservadores()` | O cliente não pode esquecer de avisar |
| Filtro no observador | `Pagamento` avisar só os interessados em cada status | Mantém o `Pagamento` sem nenhuma regra de negócio dos observadores |
| `try/catch` por observador | Deixar a exceção interromper o laço | A falha da nota fiscal, por exemplo, não deve impedir o aviso ao comprador |
| Pacotes `modelo`, `observadores` e `cliente` | Tudo no pacote padrão | Separa visualmente os dois lados do padrão e facilitou a divisão do trabalho na dupla |

---

## 10. Relação com a literatura

*(Acréscimo em relação à estrutura da aula.)*

O catálogo do GoF lista questões de implementação do Observer. A tabela mostra como cada uma foi tratada:

| Questão do GoF | Tratamento neste sistema |
|---|---|
| Mapear subjects aos seus observers | Cada `Pagamento` guarda sua própria lista |
| Observar mais de um subject | Possível, porque `atualizar` recebe o pagamento que mudou; a demonstração usa o mesmo observador em vários pagamentos |
| Quem dispara a atualização | O próprio Subject, em `setStatus` (seção 4.4) |
| Subject consistente antes de notificar | O status é atualizado antes do aviso (seção 5.3) |
| Modelos push e pull | Pull (seção 4.4) |
| Especificar explicitamente os eventos de interesse | **Não implementado**: todos recebem tudo e filtram (ver seção 12) |
| Encapsular atualizações complexas em um gerenciador (*ChangeManager*) | Não necessário para o tamanho do sistema |

No ecossistema Java, além das classes depreciadas citadas na seção 9, o mesmo mecanismo aparece em `java.beans.PropertyChangeListener` e, de forma assíncrona e com controle de fluxo, na API `java.util.concurrent.Flow`, introduzida no Java 9.

---

## 11. Visão crítica

*(Acréscimo em relação à estrutura da aula.)*

**Pontos fortes**

* **Extensibilidade real**: o cenário do SMS mostra uma nova reação adicionada sem tocar no `Pagamento`.
* **Responsabilidades separadas**: cada regra de reação vive em uma única classe.
* **Robustez básica**: falhas isoladas, sem notificação duplicada e sem aviso quando o status não muda.

**Fragilidades**

1. **Ordem de notificação não garantida pelo padrão.** Aqui os observadores são avisados na ordem de registro, por causa da `ArrayList`, mas o Observer não promete isso. Se a nota fiscal dependesse do pedido já liberado, essa dependência ficaria implícita e frágil.
2. **Notificação síncrona.** O `Gateway` espera todas as reações terminarem. Uma emissão fiscal real, lenta, travaria o processamento do pagamento.
3. **Avisos desnecessários.** Todo observador recebe todo aviso e descarta o que não interessa. Com muitos observadores, isso desperdiça chamadas.
4. **Falhas apenas registradas.** O `try/catch` evita que uma falha derrube as outras, mas a reação que falhou não é refeita nem sinalizada a ninguém.
5. **Risco de cascata.** Como o observador recebe o próprio `Pagamento`, ele poderia chamar `setStatus` dentro do `atualizar`, disparando uma nova rodada de avisos no meio da atual. O GoF chama isso de atualizações inesperadas.
6. **Sem validação de transições.** É possível ir de `ESTORNADO` para `APROVADO` (seção 3.2).
7. **Sem segurança para concorrência.** A lista de observadores não é protegida para uso por várias threads.
8. **Interface acoplada ao console.** A interface gráfica descobre se um observador reagiu pelo que ele imprime. Um observador que reagisse sem imprimir nada apareceria como "Notificado, sem ação".
9. **O observador não sabe o status anterior.** No modelo pull adotado, um estorno é visto apenas como "status atual ESTORNADO", sem saber de onde veio.

---

## 12. Melhorias propostas

*(Acréscimo em relação à estrutura da aula.)*

| Fragilidade (seção 11) | Melhoria |
|---|---|
| 3. Avisos desnecessários | Registrar observadores por status de interesse, por exemplo um `Map<StatusPagamento, List<ObservadorDePagamento>>` |
| 2. Notificação síncrona | Notificar de forma assíncrona com um `ExecutorService` ou com a API `Flow` |
| 4. Falhas apenas registradas | Fila de reprocessamento para reações que falharam |
| 6. Sem validação de transições | Combinar com o padrão **State**, aplicado pela subequipe no [Fluxo de Compra](docs/Base/Relatórios/Subequipe03/GofsComportamentais/FluxoDeCompra(State).md) |
| 7. Concorrência | Usar `CopyOnWriteArrayList`, que já resolve também a cópia feita no laço |
| 8. Interface acoplada ao console | Observadores devolverem ou publicarem um resultado, e o uso de um logger no lugar de `System.out` |
| 9. Status anterior desconhecido | Passar um objeto de evento, como `EventoPagamento(statusAnterior, statusNovo)`, migrando para o modelo push |
| Geral | Testes automatizados com JUnit para cada linha da tabela da seção 3.1 |

---

## 13. Limitações desta documentação

* As reações dos observadores são **simuladas** com mensagens; não há integração com estoque, emissão fiscal ou envio de notificações reais.
* O `GatewayPagamento` é uma simulação; não há comunicação com um gateway de pagamento real.
* A tabela da seção 3.1 foi derivada da leitura do código, não de testes automatizados.
* As imagens da seção 7 foram geradas a partir da interface em execução e podem variar levemente conforme o sistema operacional, por exemplo no formato do valor (`249,90` ou `249.90`) de acordo com o idioma do sistema.

---

## 14. Referências

GAMMA, Erich; HELM, Richard; JOHNSON, Ralph; VLISSIDES, John. **Padrões de projeto: soluções reutilizáveis de software orientado a objetos**. Porto Alegre: Bookman, 2000.

GAMMA, Erich; HELM, Richard; JOHNSON, Ralph; VLISSIDES, John. **Design patterns: elements of reusable object-oriented software**. Reading: Addison-Wesley, 1995.

SERRANO, Milene. **Arquitetura e Desenho de Software: aula GoFs comportamentais**. Brasília: Universidade de Brasília, 2026. Material de aula.

SOURCEMAKING. **Observer design pattern in Java**. Disponível em: <https://sourcemaking.com/design_patterns/observer/java/1>. Acesso em: 15 out. 2026.

TUTORIALSPOINT. **Design patterns: observer pattern**. Disponível em: <https://www.tutorialspoint.com/design_pattern/observer_pattern.htm>. Acesso em: 15 out. 2026.

ORACLE. **Class Observable (Java SE 21 & JDK 21)**. Disponível em: <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Observable.html>. Acesso em: 15 out. 2026.

ORACLE. **Class Flow (Java SE 21 & JDK 21)**. Disponível em: <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Flow.html>. Acesso em: 15 out. 2026.

ORACLE. **Creating a GUI with Swing**. The Java Tutorials. Disponível em: <https://docs.oracle.com/javase/tutorial/uiswing/>. Acesso em: 15 out. 2026.

---

## Histórico de Versões

| Versão | Data | Descrição | Autor(es) | Revisor(es) |
| -- | -- | -- | -- | -- |
| 1.0 | 15/10/2026 | Criação da página com a documentação do Observer no pagamento | João Paulo Barbosa Pereira Nunes | Júlia Santana Campos |
| 1.1 | 15/10/2026 | Adiciona imagens da demonstração da interface gráfica | João Paulo Barbosa Pereira Nunes | Júlia Santana Campos |
