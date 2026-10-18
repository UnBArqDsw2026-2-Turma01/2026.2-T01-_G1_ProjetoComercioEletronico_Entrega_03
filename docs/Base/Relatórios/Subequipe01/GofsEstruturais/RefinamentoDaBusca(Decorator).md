# GoF Estrutural — Decorator: refinamento da busca

Conforme a divisão registrada nas reuniões de [16/10](/ReunioesAtas/Subequipe1/Ata16-10.md) e [18/10/2026](/ReunioesAtas/Subequipe1/Ata18_10.md), este é o artefato estrutural da Subequipe 01. O **dono do artefato** é **Pedro Luciano de Azevedo** (modelagem, componente base, demonstração e vídeo), e cada um dos outros quatro integrantes implementa e documenta um refinamento concreto.

---

## 1. Contexto: o problema

A busca do G1_ProjetoComercioEletronico não é uma operação única. O Recorte A da [Engenharia Reversa](https://unbarqdsw2026-2-turma01.github.io/2026.2-T01-_G1_ProjetoComercioEletronico_Entrega_01/#/Base/Relat%C3%B3rios/SubEquipe01/EngenhariaReversa) da Entrega 1 registrou quatro comportamentos que se acumulam sobre o mesmo resultado:

| Regra | O que a observação registrou |
| -- | -- |
| RN-A02 / RF-A03 | A ordenação padrão é por relevância, e o comprador pode trocá-la |
| RN-A03 / RF-A02 | As facetas são cumulativas e cada opção exibe antecipadamente quantos resultados restarão |
| RN-A04 | Frete e prazo são calculados a partir do CEP, antes do carrinho |
| RN-A05 | A reputação do vendedor aparece já no cartão da listagem |

Esses refinamentos são **opcionais, cumulativos e combináveis em ordem variável** — o comprador aplica uma faceta, reordena, aplica outra. Nenhum deles é obrigatório, e nenhum deles exclui os demais.

A implementação ingênua seria uma classe `CatalogoCompleto` que fizesse tudo, com um parâmetro booleano por refinamento. Ela tem dois defeitos conhecidos: cresce a cada novo refinamento, e obriga a mexer no código da busca — que funciona — para acrescentar qualquer coisa nova. A alternativa por herança é pior: quatro refinamentos combináveis dariam dezesseis subclasses.

> **Elo com a Entrega 2.** No Diagrama de Casos de Uso, *Filtrar por facetas* e *Ordenar listagem* são `«extend»` de *Buscar produto* — comportamento opcional, acrescentado em condições específicas, sem que o caso base conheça a extensão. `«extend»` na UML e Decorator no código são a mesma afirmação sobre o sistema, em notações diferentes. Foi esse elo que decidiu a escolha do padrão na reunião de 18/10.

---

## 2. Fundamentação: o padrão Decorator

**Intenção.** "Dinamicamente, agregar responsabilidades adicionais a um objeto. Os *Decorators* fornecem uma alternativa flexível ao uso de subclasses para extensão de funcionalidades" (GAMMA et al., 2000).

**A estrutura.** O decorador **é um** componente (implementa a mesma interface) e **tem um** componente (guarda a referência para quem ele envolve). É essa dupla relação que permite empilhar decoradores indefinidamente sem que o cliente perceba a diferença — para quem chama, uma pilha de quatro refinamentos e o catálogo cru são o mesmo tipo.

**Quando o GoF recomenda usá-lo.** Três das quatro situações descritas pelos autores se aplicam aqui:

1. acrescentar responsabilidades a objetos individuais de forma dinâmica e transparente;
2. quando as responsabilidades podem ser retiradas;
3. quando a extensão por subclasses é impraticável pela explosão combinatória.

**Por que ele, e não o Strategy.** O Strategy troca *um* algoritmo por outro; aqui os refinamentos não se substituem, eles se **empilham**. É a distinção que o próprio GoF faz: o Decorator muda a pele do objeto, o Strategy muda as suas entranhas.

**Por que ele, e não o Chain of Responsibility.** Os dois montam cadeias, mas com propósitos opostos: na cadeia de responsabilidade, **um** elo trata a requisição e a cadeia para; aqui **todos** os elos contribuem, e o resultado final é a soma de todos eles.

---

## 3. Modelagem (Notação UML)

### 3.1. Modelagem genérica

[![Estrutura genérica do padrão Decorator](../Assets/DecoratorGenerico.svg)](../Assets/DecoratorGenerico.svg ":ignore")

<sub>Clique na imagem para abrir em tela cheia, com zoom.</sub>

> _Figura 1 — Estrutura genérica do padrão Decorator. `Decorator` implementa `Component` e agrega um `Component`: é um e tem um. A nota ao pé mostra o corpo típico de `operacao()` em um decorador concreto — delega e acrescenta. Fonte: Subequipe 01, 2026, a partir de GAMMA et al. (2000)._

### 3.2. Modelagem aplicada

[![Decorator aplicado ao refinamento da busca](../Assets/DecoratorAplicado.svg)](../Assets/DecoratorAplicado.svg ":ignore")

<sub>Clique na imagem para abrir em tela cheia, com zoom.</sub>

> _Figura 2 — Decorator aplicado ao refinamento da busca. Três pacotes espelhando as pastas do código: `componente`, `decorador` e `dominio`. Estereótipos `«Component»`, `«ConcreteComponent»`, `«Decorator»` e `«ConcreteDecorator»` marcam o papel de cada classe no padrão. A legenda embutida rastreia cada elemento ao achado da Entrega 1 que o sustenta e identifica o autor de cada fatia. Fonte: Subequipe 01, 2026._

**Decisões de modelagem.**

- **Os pacotes espelham as pastas do código**, e não os conceitos do domínio. A escolha é deliberada: o diagrama serve para verificar a implementação, e um leitor que abra o repositório encontra `componente/`, `decorador/` e `dominio/` com exatamente essas classes.
- **A agregação, e não a composição, entre `RefinamentoDecorator` e `FonteDeResultados`.** O decorador não é dono da fonte que envolve: a mesma instância de `CatalogoEmMemoria` pode ser envolvida por cadeias diferentes, e sobrevive a elas.
- **Os quatro refinamentos concretos aparecem sem atributos.** O que distingue um do outro é o corpo de `buscar()`, e não a estrutura — exibir campos inexistentes só para preencher a caixa tornaria o diagrama menos fiel.
- **A legenda identifica o autor de cada fatia.** É a forma de o próprio artefato mostrar o rastro individual que as Diretrizes pedem, sem depender do quadro de participações.

---

## 4. Papéis no padrão

| Papel GoF | Classe | Autor da fatia |
| -- | -- | -- |
| Component | `FonteDeResultados` | Pedro Luciano de Azevedo |
| ConcreteComponent | `CatalogoEmMemoria` | Pedro Luciano de Azevedo |
| Decorator | `RefinamentoDecorator` | Pedro Luciano de Azevedo |
| ConcreteDecorator | `ComFiltroDeFacetas` | Maria Clara Sena de Lima |
| ConcreteDecorator | `ComOrdenacao` | Guilherme Davila Rodrigues Carneiro Sampaio |
| ConcreteDecorator | `ComFreteEPrazo` | Patrick Anderson Carvalho dos Santos |
| ConcreteDecorator | `ComReputacaoDoVendedor` | Guilherme Costa Zanella |
| Client | `cliente.Main` | Pedro Luciano de Azevedo |

Código no repositório: [`src/estruturais/RefinamentoDaBusca(Decorator)`](https://github.com/UnBArqDsw2026-2-Turma01/2026.2-T01-_G1_ProjetoComercioEletronico_Entrega_03/tree/main/docs/Base/Relat%C3%B3rios/Subequipe01/src/estruturais)

---

## 5. Implementação, fatia por fatia

### 5.1. Componente, decorador abstrato e domínio — Pedro Luciano de Azevedo

#### O componente

`FonteDeResultados` é a interface que torna o padrão possível: o catálogo cru e uma pilha de quatro refinamentos têm exatamente o mesmo tipo, e quem chama não distingue um do outro.

```java
public interface FonteDeResultados {
    Listagem buscar(ConsultaCatalogo consulta);
    String descricao();
}
```

O segundo método não faz parte do padrão: `descricao()` existe para a demonstração, devolvendo a cadeia montada como texto. Sem ele, o vídeo mostraria resultados diferentes sem deixar visível **por que** são diferentes. É um acréscimo ao padrão, e fica registrado como tal.

#### O componente concreto

`CatalogoEmMemoria` faz o mínimo que uma busca precisa fazer — casar o termo com o título do anúncio — e nada mais:

```java
@Override
public Listagem buscar(ConsultaCatalogo consulta) {
    List<ItemListagem> encontrados = new ArrayList<ItemListagem>();
    String termo = consulta.termo().toLowerCase();
    for (Anuncio anuncio : acervo) {
        if (termo.isEmpty() || anuncio.titulo().toLowerCase().contains(termo)) {
            encontrados.add(new ItemListagem(anuncio));
        }
    }
    return new Listagem(encontrados);
}
```

Ele é **deliberadamente pobre**: não filtra por faceta, não ordena, não calcula frete e não exibe reputação. Essa pobreza é o que torna visível, na demonstração, o que cada refinamento acrescenta. Um componente concreto que já fizesse metade do trabalho esconderia o efeito do padrão.

O acervo é fixo e em memória porque a engenharia reversa da Entrega 1 foi feita de caixa-preta: não há acesso ao catálogo real. Os oito anúncios reproduzem o que a observação registrou — várias ofertas para o mesmo item (RN-B09), lojas oficiais e vendedores autônomos com notas diferentes (Rich Picture), novos e usados.

#### O decorador abstrato

```java
public abstract class RefinamentoDecorator implements FonteDeResultados {

    protected final FonteDeResultados origem;

    protected RefinamentoDecorator(FonteDeResultados origem) {
        if (origem == null) {
            throw new IllegalArgumentException("Um decorador precisa envolver uma fonte de resultados");
        }
        this.origem = origem;
    }

    @Override
    public Listagem buscar(ConsultaCatalogo consulta) {
        return origem.buscar(consulta);
    }

    @Override
    public String descricao() {
        return origem.descricao() + " + " + getClass().getSimpleName();
    }
}
```

Três decisões neste trecho:

1. **`origem` é `protected final`.** Os refinamentos precisam alcançá-la por `super.buscar()`; ninguém precisa trocá-la depois de montada a cadeia.
2. **O construtor recusa `null`.** Um decorador sem fonte não é um decorador — falha no momento da montagem, e não na primeira busca.
3. **`buscar()` tem implementação padrão que repassa.** O GoF permite que o decorador abstrato seja `abstract` puro; aqui ele delega, para que um refinamento que só precise mexer na descrição não seja obrigado a reimplementar a busca.

#### O domínio herdado da Entrega 2

| Classe | De onde veio |
| -- | -- |
| `ConsultaCatalogo` | Decisão 2 do Diagrama de Classes — value object serializável; `paraURL`/`deURL` implementam RN-A06 |
| `Faceta` | Decisão 3 — `contagemPrevia` é atributo, não cálculo sob demanda |
| `Listagem` | `termosAlternativos` vem de RN-A01 / RF-A06 |
| `Dinheiro` | Decisão 10 — valor em centavos, padrão *Money* |
| `Anuncio` | Decisão 1 (Produto × Anuncio) e decisão 8 (`TipoVendedor` como enumeração) |
| `Ordenacao` | Enumeração do Diagrama de Classes; `RELEVANCIA` como padrão por RN-A02 |

**Imutabilidade como regra combinada.** `ConsultaCatalogo`, `Listagem` e `ItemListagem` são imutáveis, e todas as operações devolvem cópias (`comFiltro`, `comItens`, `comSelo`). A regra foi fixada na reunião de 18/10 e é o que garante que um refinamento nunca corrompa o resultado que recebeu do anterior — sem ela, a ordem da cadeia produziria efeitos colaterais difíceis de rastrear.

**Uma classe que não veio da Entrega 2: `ItemListagem`.** Os refinamentos de enriquecimento (frete, reputação) precisam acrescentar informação a cada resultado, e fazer isso em `Anuncio` obrigaria a mexer na classe de domínio a cada novo selo. `ItemListagem` separa o que o catálogo guarda do que a busca devolve, e é o que permite que `ComFreteEPrazo` e `ComReputacaoDoVendedor` existam sem tocar em `Anuncio`.

### 5.2. `ComFiltroDeFacetas` — Maria Clara Sena de Lima

> _A preencher: como o refinamento filtra os itens pelos filtros da consulta e monta as facetas com a contagem prévia (RN-A03 / RF-A02); trecho de código; por que a contagem é calculada antes de o comprador filtrar e o que isso custa, conforme o SIG da Entrega 1._

### 5.3. `ComOrdenacao` — Guilherme Davila Rodrigues Carneiro Sampaio

> _A preencher: como o refinamento reordena conforme `consulta.ordenacao()` (RN-A02 / RF-A03); trecho de código; por que `RELEVANCIA` preserva a ordem do catálogo e por que isso é inferência declarada._

### 5.4. `ComFreteEPrazo` — Patrick Anderson Carvalho dos Santos

> _A preencher: como o refinamento acrescenta os selos de frete e prazo a cada item a partir do CEP (RN-A04); trecho de código; elo com o `Serviço de Entrega` e a interface `IFrete` do Diagrama de Componentes da Entrega 2._

### 5.5. `ComReputacaoDoVendedor` — Guilherme Costa Zanella

> _A preencher: como o refinamento acrescenta nota, número de avaliações e tipo de vendedor a cada item (RN-A05); trecho de código; elo com o Rich Picture — a tensão entre loja oficial e vendedor autônomo e a concern de confiança da compradora._

---

## 6. Execução (vídeo com o padrão em execução)

> _A preencher por **Pedro Luciano de Azevedo**, depois que as quatro fatias subirem: vídeo mostrando a mesma consulta respondida pelo catálogo cru e pela cadeia completa, com a composição exibida por `descricao()`._

**Como compilar e executar:**

```bash
cd "docs/Base/Relatórios/Subequipe01/src/estruturais/RefinamentoDaBusca(Decorator)"
make run
```

Pré-requisito: JDK 8 ou superior. Instruções completas no [README do artefato](https://github.com/UnBArqDsw2026-2-Turma01/2026.2-T01-_G1_ProjetoComercioEletronico_Entrega_03/tree/main/docs/Base/Relat%C3%B3rios/Subequipe01/src/estruturais).

---

## 7. Rastreabilidade

| Elemento do código | Vem de | Entrega |
| -- | -- | -- |
| `ConsultaCatalogo` com `paraURL` / `deURL` | RN-A06; decisão 2 do Diagrama de Classes | 1 e 2 |
| `Faceta.contagemPrevia` | RN-A03 / RF-A02; decisão 3 do Diagrama de Classes | 1 e 2 |
| `Ordenacao.RELEVANCIA` como padrão | RN-A02 | 1 |
| Selos de frete e prazo | RN-A04; `IFrete` no Diagrama de Componentes | 1 e 2 |
| Selo de reputação | RN-A05; Rich Picture | 1 |
| `Listagem.termosAlternativos` | RN-A01 / RF-A06 | 1 |
| `Anuncio.TipoVendedor` | Rich Picture; decisão 8 do Diagrama de Classes | 1 e 2 |
| A cadeia de refinamentos | Diagrama de Sequência, mensagens 5–25 | 2 |
| A escolha do padrão | `«extend»` de *Filtrar por facetas* e *Ordenar listagem* no Diagrama de Casos de Uso | 2 |

---

## 8. Visão crítica

**A ordem da cadeia é semanticamente relevante, e o padrão não protege contra a montagem errada.** Ordenar antes de filtrar e filtrar antes de ordenar dão o mesmo conjunto, mas calcular a contagem prévia das facetas depois de um filtro já aplicado dá números diferentes de calculá-la antes. O Decorator torna a composição livre — e essa liberdade inclui compor errado. A cadeia correta está fixada em `Main` e documentada, mas nada no código impede outra montagem. É o preço da flexibilidade, e o GoF o reconhece ao listar "muitos objetos pequenos parecidos" entre as consequências do padrão.

**Dois tipos de refinamento convivem na mesma interface.** `ComFiltroDeFacetas` e `ComOrdenacao` mudam o **conjunto** de resultados; `ComFreteEPrazo` e `ComReputacaoDoVendedor` mudam **cada item**. Os quatro implementam `Listagem buscar(ConsultaCatalogo)` e empilham-se igual, mas são operações de naturezas diferentes. Um modelo mais rigoroso teria duas interfaces — uma de filtragem e outra de enriquecimento — ao custo de duas hierarquias e de perder a composição uniforme. A escolha foi pela uniformidade, e fica registrada como escolha, não como descuido.

**`descricao()` não é do padrão.** Ele existe para que a demonstração mostre a cadeia montada. É acréscimo didático: em produção, seria ruído na interface.

**O acervo é fixo.** A engenharia reversa foi de caixa-preta, então não há catálogo real para consultar. Os oito anúncios são suficientes para demonstrar os quatro refinamentos, mas não exercitam o que importaria em escala — o custo que o SIG da Entrega 1 atribuiu aos filtros facetados sobre *Tempo de Resposta* não aparece com oito itens.

**O que o artefato não cobre.** A rolagem infinita (e o RNF-A02 não satisfeito, que o Diagrama de Sequência da Entrega 2 explicou) não virou refinamento, embora coubesse como um. Ficou de fora por decisão de escopo: são cinco fatias para cinco pessoas, e um quinto refinamento desequilibraria a divisão. Está anotado como a evolução natural deste artefato.

> _Cada integrante acrescenta aqui o limite que enxerga na própria fatia._

---

## 9. Referências

GAMMA, Erich; HELM, Richard; JOHNSON, Ralph; VLISSIDES, John. **Padrões de Projeto: soluções reutilizáveis de software orientado a objetos**. Porto Alegre: Bookman, 2000.

FREEMAN, Eric; ROBSON, Elisabeth. **Use a Cabeça! Padrões de Projetos**. 2. ed. Rio de Janeiro: Alta Books, 2007.

FOWLER, Martin. **Patterns of Enterprise Application Architecture**. Boston: Addison-Wesley, 2002.

---

## Histórico de Versões

| Versão | Data | Descrição | Autor(es) | Revisor(es) |
| -- | -- | -- | -- | -- |
| 1.0 | 18/10/2026 | Criação da página: contexto, fundamentação, papéis no padrão e estrutura das seções por integrante | Pedro Luciano de Azevedo | -- |
| 1.1 | 18/10/2026 | Modelagem UML genérica e aplicada; documentação do componente, do decorador abstrato e do domínio; rastreabilidade e visão crítica | Pedro Luciano de Azevedo | -- |
