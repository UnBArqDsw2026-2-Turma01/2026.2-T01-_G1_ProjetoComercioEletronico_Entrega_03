# GoF Observer: Subsistema de Pagamento

G1_ProjetoComercioEletronico, SubEquipe_03, Entrega 3 (GoFs Comportamentais).

## Estrutura

| Pasta | Classe | Papel GoF |
|---|---|---|
| `src/modelo` | `PagamentoObservavel` | Subject |
| `src/modelo` | `Pagamento` | ConcreteSubject |
| `src/modelo` | `StatusPagamento` | Apoio (enum) |
| `src/observadores` | `ObservadorDePagamento` | Observer |
| `src/observadores` | `NotificacaoComprador`, `LiberacaoPedido`, `EmissaoNotaFiscal`, `LiberacaoEstoque` | ConcreteObserver |
| `src/cliente` | `GatewayPagamento` | Client |
| `src` | `Main` | Demonstração no terminal |
| `src` | `InterfaceGrafica` | Demonstração com janela (Swing) |

## Como executar

### Passo 1: conferir o Java

É necessário o **Java 17 ou superior** (JDK). No terminal:

```bash
java -version
```

Se o comando não for reconhecido ou a versão for menor que 17, instale um JDK, por exemplo o [Eclipse Temurin](https://adoptium.net/).

### Passo 2: baixar o código

O GitHub não permite baixar uma pasta isolada, então é preciso baixar o repositório inteiro, de uma destas formas:

* **Pelo navegador:** na [página do repositório](https://github.com/UnBArqDsw2026-2-Turma01/2026.2-T01-_G1_ProjetoComercioEletronico_Entrega_03), clique em **Code → Download ZIP** e descompacte o arquivo. A pasta descompactada recebe o sufixo `-main` no nome.
* **Pelo Git:**
  ```bash
  git clone https://github.com/UnBArqDsw2026-2-Turma01/2026.2-T01-_G1_ProjetoComercioEletronico_Entrega_03.git
  ```

### Passo 3: entrar na pasta do projeto

Dentro do repositório baixado, o projeto fica em:

```
docs/Base/Relatórios/Subequipe03/src/comportamentais/FluxoDeCompra(Observer)
```

No terminal (as aspas são necessárias por causa dos parênteses no nome da pasta):

```bash
cd "2026.2-T01-_G1_ProjetoComercioEletronico_Entrega_03/docs/Base/Relatórios/Subequipe03/src/comportamentais/FluxoDeCompra(Observer)"
```

Se o repositório foi baixado pelo ZIP, use `2026.2-T01-_G1_ProjetoComercioEletronico_Entrega_03-main` no início do caminho.

### Passo 4: compilar e executar

**macOS e Linux:**

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out Main              # versão no terminal
java -cp out InterfaceGrafica  # versão com janela
```

**Windows (PowerShell):**

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Path src -Recurse -Filter *.java).FullName
java -cp out Main
java -cp out InterfaceGrafica
```

**Pela IDE:** também é possível abrir a pasta do projeto no IntelliJ IDEA ou no VS Code (com a extensão *Extension Pack for Java*) e executar diretamente as classes `Main` ou `InterfaceGrafica`.

Na janela: crie um pagamento, mude o status pelos botões do gateway e marque ou desmarque observers.
Cada cartão mostra se o observer reagiu, se foi notificado mas ignorou o status, ou se não está registrado.

## Cenários da demonstração

1. Pagamento aprovado: comprador avisado, pedido liberado, nota fiscal emitida.
2. Pagamento recusado: comprador avisado, item devolvido ao estoque.
3. Aprovado e depois estornado: reações de aprovação e, em seguida, devolução ao estoque.
4. Comprador remove a notificação: `removerObservador` em ação.
5. Novo observador (SMS) adicionado sem alterar a classe `Pagamento`.
