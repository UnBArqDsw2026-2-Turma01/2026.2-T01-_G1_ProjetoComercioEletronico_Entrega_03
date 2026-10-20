# GoF Estrutural — Decorator: refinamento da busca

G1_ProjetoComercioEletronico, SubEquipe_01, Entrega 3 (GoFs Estruturais).

A busca do G1 não é uma operação só: o comprador digita um termo, aplica facetas
cumulativas, reordena, e cada cartão da listagem ainda exibe frete, prazo e
reputação do vendedor. Tudo isso **se acumula sobre o mesmo resultado**, em
ordem variável e em combinações que o comprador escolhe — que é a situação que o
Decorator resolve: acrescentar responsabilidades a um objeto dinamicamente, sem
inchar a classe que faz a busca.

A alternativa seria um `CatalogoCompleto` com todos os refinamentos embutidos e
um punhado de parâmetros booleanos. O Decorator troca essa classe por uma pilha
em que cada refinamento é uma classe pequena, testável e removível.

## Papéis no padrão

| Pasta | Classe | Papel GoF |
| -- | -- | -- |
| `componente` | `FonteDeResultados` | Component |
| `componente` | `CatalogoEmMemoria` | ConcreteComponent |
| `decorador` | `RefinamentoDecorator` | Decorator |
| `decorador` | `ComFiltroDeFacetas`, `ComOrdenacao`, `ComFreteEPrazo`, `ComReputacaoDoVendedor` | ConcreteDecorator |
| `cliente` | `Main` | Client / demonstração |
| `dominio` | `Anuncio`, `ConsultaCatalogo`, `Listagem`, `ItemListagem`, `Faceta`, `Ordenacao`, `Dinheiro` | Apoio (Entrega 2) |

## Divisão por integrante

| Integrante | Arquivos que cria | Situação |
| -- | -- | -- |
| **Pedro Luciano de Azevedo** | todo o `dominio/`, `componente/FonteDeResultados.java`, `componente/CatalogoEmMemoria.java`, `decorador/RefinamentoDecorator.java`, `cliente/Main.java` | ✅ no repositório |
| **Maria Clara Sena de Lima** | `decorador/ComFiltroDeFacetas.java` | a fazer |
| **Guilherme Davila Rodrigues Carneiro Sampaio** | `decorador/ComOrdenacao.java` | a fazer |
| **Patrick Anderson Carvalho dos Santos** | `decorador/ComFreteEPrazo.java` | a fazer |
| **Guilherme Costa Zanella** | `decorador/ComReputacaoDoVendedor.java` | a fazer |

## O que cada refinamento faz

Todos estendem `RefinamentoDecorator`, chamam `super.buscar(consulta)` para
pegar o resultado de quem está embaixo e devolvem uma **listagem nova** — nunca
alteram a que receberam.

| Refinamento | O que acrescenta | Regra da Entrega 1 |
| -- | -- | -- |
| `ComFiltroDeFacetas` | Filtra os itens pelos filtros da consulta e preenche as facetas com a contagem prévia de cada opção | RN-A03, RF-A02 — cada opção de filtro exibe antecipadamente quantos resultados restarão |
| `ComOrdenacao` | Reordena conforme `consulta.ordenacao()` | RN-A02, RF-A03 — a ordenação padrão é por relevância, não por preço |
| `ComFreteEPrazo` | Acrescenta a cada item os selos `frete` e `prazo`, calculados a partir do CEP e do peso | RN-A04 — frete e prazo são calculados na ficha, antes do carrinho |
| `ComReputacaoDoVendedor` | Acrescenta a cada item o selo `reputação` (nota, nº de avaliações e tipo de vendedor) | RN-A05 — a reputação aparece já na listagem; Rich Picture: loja oficial × vendedor autônomo |

Exemplo do formato esperado (fatia da Maria Clara):

```java
package decorador;

public final class ComFiltroDeFacetas extends RefinamentoDecorator {

    public ComFiltroDeFacetas(FonteDeResultados origem) {
        super(origem);
    }

    @Override
    public Listagem buscar(ConsultaCatalogo consulta) {
        Listagem recebida = super.buscar(consulta);   // o resultado de quem está embaixo
        // ... filtra os itens e monta as facetas com contagem prévia
        return recebida.comItens(filtrados).comFacetas(facetas);
    }
}
```

## Como compilar e executar

Pré-requisito: **JDK 8 ou superior** (`javac -version`).

```bash
# a partir desta pasta
make run
```

Sem `make`:

```bash
javac -encoding UTF-8 -d out dominio/*.java componente/*.java decorador/*.java cliente/*.java
java -cp out cliente.Main
```

O esqueleto já roda hoje, exibindo o catálogo cru e a URL da consulta. Quando as
quatro fatias subirem, Pedro descomenta a montagem da cadeia em `Main` e a
demonstração passa a mostrar a busca antes e depois de cada refinamento.

## Rastreabilidade

| Elemento | Origem na Entrega 1 / Entrega 2 |
| -- | -- |
| `ConsultaCatalogo` com `paraURL`/`deURL` | Decisão 2 do Diagrama de Classes; RN-A06 — o estado da consulta viaja na URL |
| `Faceta.contagemPrevia` | Decisão 3 do Diagrama de Classes; RN-A03 |
| `Listagem.termosAlternativos` | RN-A01 / RF-A06 — estado vazio com termos alternativos |
| `Ordenacao.RELEVANCIA` como padrão | RN-A02 |
| `Anuncio.TipoVendedor` | Rich Picture — loja oficial × vendedor autônomo; decisão 8 do Diagrama de Classes |
| Separação `Anuncio` / `ItemListagem` | Decisão 1 do Diagrama de Classes (Produto × Anuncio), adaptada à listagem |
| Cadeia de refinamentos | Diagrama de Sequência (Entrega 2), mensagens 5–25: buscar, aplicar faceta, reordenar |

## Referências

GAMMA, Erich; HELM, Richard; JOHNSON, Ralph; VLISSIDES, John. **Padrões de Projeto: soluções reutilizáveis de software orientado a objetos**. Porto Alegre: Bookman, 2000.

FREEMAN, Eric; ROBSON, Elisabeth. **Use a Cabeça! Padrões de Projetos**. 2. ed. Rio de Janeiro: Alta Books, 2007.
