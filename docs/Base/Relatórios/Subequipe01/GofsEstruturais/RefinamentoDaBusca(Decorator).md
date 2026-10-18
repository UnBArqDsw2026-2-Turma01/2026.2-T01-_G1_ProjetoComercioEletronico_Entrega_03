# GoF Estrutural — Decorator: refinamento da busca

Conforme a divisão registrada na [reunião de 18/10/2026](/ReunioesAtas/Subequipe1/Ata18_10.md), este é o artefato estrutural da Subequipe 01. O **dono do artefato** é **Pedro Luciano de Azevedo** (modelagem, componente base, demonstração e vídeo), e cada um dos outros quatro integrantes implementa e documenta um refinamento concreto.

---

## 1. Contexto: o problema

A busca do G1_ProjetoComercioEletronico não é uma operação única. O Recorte A da [Engenharia Reversa](https://unbarqdsw2026-2-turma01.github.io/2026.2-T01-_G1_ProjetoComercioEletronico_Entrega_01/#/Base/Relat%C3%B3rios/SubEquipe01/EngenhariaReversa) da Entrega 1 registrou quatro comportamentos que se acumulam sobre o mesmo resultado:

| Regra | O que a observação registrou |
| -- | -- |
| RN-A02 / RF-A03 | A ordenação padrão é por relevância, e o comprador pode trocá-la |
| RN-A03 / RF-A02 | As facetas são cumulativas e cada opção exibe antecipadamente quantos resultados restarão |
| RN-A04 | Frete e prazo são calculados a partir do CEP, antes do carrinho |
| RN-A05 | A reputação do vendedor aparece já no cartão da listagem |

Esses refinamentos são **opcionais, cumulativos e combináveis em ordem variável** — o comprador aplica uma faceta, reordena, aplica outra. A implementação ingênua seria uma classe `CatalogoCompleto` com todos eles embutidos e um punhado de parâmetros booleanos, que cresce a cada novo refinamento e obriga a mexer no código da busca para acrescentar qualquer coisa.

> **Elo com a Entrega 2.** No [Diagrama de Casos de Uso](https://unbarqdsw2026-2-turma01.github.io/2026.2-T01-_G1_ProjetoComercioEletronico_Entrega_02/), *Filtrar por facetas* e *Ordenar listagem* são `«extend»` de *Buscar produto* — comportamento opcional, acrescentado em condições específicas, sem que o caso base conheça a extensão. `«extend»` na UML e Decorator no código são a mesma afirmação sobre o sistema, em notações diferentes.

---

## 2. Fundamentação: o padrão Decorator

**Intenção.** "Dinamicamente, agregar responsabilidades adicionais a um objeto. Os Decorators fornecem uma alternativa flexível ao uso de subclasses para extensão de funcionalidades" (GAMMA et al., 2000).

**Por que ele, e não herança.** Quatro refinamentos combináveis dariam, por subclasses, dezesseis combinações possíveis. O Decorator transforma combinação em composição: cada refinamento é uma classe, e a combinação é montada em tempo de execução.

**Por que ele, e não Strategy.** O Strategy troca *um* algoritmo por outro; aqui os refinamentos não se substituem, eles se **empilham**. É a distinção que o próprio GoF faz: o Decorator muda a pele do objeto, o Strategy muda as suas entranhas.

---

## 3. Modelagem (Notação UML)

> _A preencher por **Pedro Luciano de Azevedo**: modelagem genérica do padrão (Component, ConcreteComponent, Decorator, ConcreteDecorator) e modelagem aplicada ao G1, com a legenda de rastreabilidade embutida, no padrão adotado desde a Entrega 2._

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

> _A preencher: `FonteDeResultados`, `CatalogoEmMemoria`, `RefinamentoDecorator` e os tipos de domínio herdados do Diagrama de Classes da Entrega 2, com o trecho de código de cada um e a razão de o componente concreto ser deliberadamente pobre._

### 5.2. `ComFiltroDeFacetas` — Maria Clara Sena de Lima

> _A preencher: como o refinamento filtra os itens pelos filtros da consulta e monta as facetas com a contagem prévia (RN-A03 / RF-A02); trecho de código; por que a contagem é calculada antes de o comprador filtrar e o que isso custa, conforme o SIG da Entrega 1._

### 5.3. `ComOrdenacao` — Guilherme Davila Rodrigues Carneiro Sampaio

> _A preencher: como o refinamento reordena conforme `consulta.ordenacao()` (RN-A02 / RF-A03); trecho de código; por que RELEVANCIA é o padrão e o que muda quando a ordenação é aplicada antes ou depois do filtro._

### 5.4. `ComFreteEPrazo` — Patrick Anderson Carvalho dos Santos

> _A preencher: como o refinamento acrescenta os selos de frete e prazo a cada item a partir do CEP (RN-A04); trecho de código; elo com o `Serviço de Entrega` e a interface `IFrete` do Diagrama de Componentes da Entrega 2._

### 5.5. `ComReputacaoDoVendedor` — Guilherme Costa Zanella

> _A preencher: como o refinamento acrescenta nota, número de avaliações e tipo de vendedor a cada item (RN-A05); trecho de código; elo com o Rich Picture — a tensão entre loja oficial e vendedor autônomo e a concern de confiança da compradora._

---

## 6. Execução (vídeo com o padrão em execução)

> _A preencher por **Pedro Luciano de Azevedo**: vídeo mostrando a busca com o catálogo cru e depois com a cadeia completa, com a composição exibida em `descricao()`. Instruções de compilação em [README do artefato](https://github.com/UnBArqDsw2026-2-Turma01/2026.2-T01-_G1_ProjetoComercioEletronico_Entrega_03/tree/main/docs/Base/Relat%C3%B3rios/Subequipe01/src/estruturais)._

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

---

## 8. Visão crítica

> _A preencher coletivamente — cada integrante acrescenta o limite que enxerga na própria fatia. Pontos já levantados na reunião de 18/10:_
>
> - _a ordem da cadeia é semanticamente relevante (ordenar antes ou depois de filtrar não dá o mesmo resultado), e o padrão não impede a montagem errada;_
> - _o acervo é fixo e em memória, porque a engenharia reversa foi de caixa-preta;_
> - _decoradores que filtram e decoradores que enriquecem têm naturezas diferentes convivendo na mesma interface._

---

## 9. Referências

GAMMA, Erich; HELM, Richard; JOHNSON, Ralph; VLISSIDES, John. **Padrões de Projeto: soluções reutilizáveis de software orientado a objetos**. Porto Alegre: Bookman, 2000.

FREEMAN, Eric; ROBSON, Elisabeth. **Use a Cabeça! Padrões de Projetos**. 2. ed. Rio de Janeiro: Alta Books, 2007.

---

## Histórico de Versões

| Versão | Data | Descrição | Autor(es) | Revisor(es) |
| -- | -- | -- | -- | -- |
| 1.0 | 18/10/2026 | Criação da página: contexto, fundamentação, papéis no padrão e estrutura das seções por integrante | Pedro Luciano de Azevedo | -- |
