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

Requer Java 17 ou superior.

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out Main              # versão no terminal
java -cp out InterfaceGrafica  # versão com janela
```

Na janela: crie um pagamento, mude o status pelos botões do gateway e marque ou desmarque observers.
Cada cartão mostra se o observer reagiu, se foi notificado mas ignorou o status, ou se não está registrado.

## Cenários da demonstração

1. Pagamento aprovado: comprador avisado, pedido liberado, nota fiscal emitida.
2. Pagamento recusado: comprador avisado, item devolvido ao estoque.
3. Aprovado e depois estornado: reações de aprovação e, em seguida, devolução ao estoque.
4. Comprador remove a notificação: `removerObservador` em ação.
5. Novo observador (SMS) adicionado sem alterar a classe `Pagamento`.
