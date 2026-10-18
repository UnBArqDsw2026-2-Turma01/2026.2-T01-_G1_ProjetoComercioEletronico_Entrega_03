# IA Generativa — lições aprendidas e senso crítico

Conforme o Foco 03 das Diretrizes, cada integrante registra aqui, individualmente, o que aprendeu com os padrões GoF desta entrega e como utilizou — e onde desconfiou de — IA generativa. A página segue o formato adotado pela subequipe desde a Entrega 1: relato próprio, fundamentado, com exemplo concreto de onde a ferramenta ajudou e de onde ela falhou.

> **Combinação da [reunião de 18/10/2026](/ReunioesAtas/Subequipe1/Ata18_10.md):** cada um escreve e commita a própria seção. O relato é individual e obrigatório.

---

## Guilherme Costa Zanella

### Lições aprendidas

--

### Uso da IA generativa (senso crítico)

--

---

## Guilherme Davila Rodrigues Carneiro Sampaio

### Lições aprendidas

--

### Uso da IA generativa (senso crítico)

--

---

## Maria Clara Sena de Lima

### Lições aprendidas

--

### Uso da IA generativa (senso crítico)

--

---

## Patrick Anderson Carvalho dos Santos

### Lições aprendidas

--

### Uso da IA generativa (senso crítico)

--

---

## Pedro Luciano de Azevedo

### Lições aprendidas

**A escolha de um padrão tem um critério que o livro não traz.** O GoF explica quando cada padrão se aplica, e nós aplicamos isso: percorremos o catálogo procurando, para cada padrão, um problema que a nossa evidência registrasse. Mas o critério que de fato decidiu foi outro, e nasceu da nossa situação — o padrão precisava se dividir em cinco fatias independentes, porque somos cinco commitando em paralelo com um dia de prazo. Foi ele que eliminou o **Builder** da `ConsultaCatalogo`, que é bem sustentado por RN-A06, e o **Facade** do pós-venda, que é o achado mais forte do Rich Picture do Guilherme. Os dois resolvem problemas reais nossos; nenhum dos dois se divide entre cinco pessoas. Aprendi que a escolha de projeto depende tanto do problema quanto de quem vai implementá-lo, e que isso não é concessão — é parte da decisão.

**Os contratos são o artefato de coordenação, não só de projeto.** Definir `FonteDeResultados`, `RefinamentoDecorator` e as quatro interfaces do Abstract Factory antes de qualquer implementação foi o que permitiu que quatro pessoas começassem ao mesmo tempo sem esbarrar umas nas outras. É a mesma lição da Entrega 2, em que o Diagrama de Classes veio primeiro para fixar nomes e tipos — mas lá o efeito era consistência entre diagramas, e aqui é paralelismo entre pessoas. Uma interface bem definida vale, em equipe, mais pelo que ela destrava do que pelo que ela abstrai.

**Modelar para um padrão muda o domínio, e isso não é necessariamente ruim.** `ItemListagem` não existe no Diagrama de Classes da Entrega 2 — ela nasceu aqui, porque os refinamentos de enriquecimento precisavam acrescentar informação a cada resultado, e fazer isso em `Anuncio` obrigaria a mexer na classe de domínio a cada novo selo. A separação entre "o que o catálogo guarda" e "o que a busca devolve" é uma distinção que o padrão me obrigou a enxergar e que melhora o modelo independentemente dele. O risco do movimento oposto existe e é real: deformar o domínio para caber no padrão. A diferença é se a distinção faz sentido sem o padrão — e esta faz.

**Um componente concreto pobre é uma decisão de projeto.** `CatalogoEmMemoria` só casa o termo com o título; não ordena, não filtra, não calcula frete. Foi deliberado: um componente concreto que já fizesse metade do trabalho esconderia o efeito dos refinamentos na demonstração. Aprendi que, em código que também serve para explicar, o que a classe **não** faz comunica tanto quanto o que ela faz.

### Uso da IA generativa (senso crítico)

**Onde usei.** Como tradutora de notação — transcrever um achado codificado da Engenharia Reversa para a gramática do Decorator ou do Abstract Factory — e como conferidora de consistência entre o código, os diagramas e as páginas. Não usei para escolher os padrões: isso saiu das duas reuniões, com o catálogo GoF aberto e a nossa evidência na mesa.

**Onde ajudou.** Na regra formal, que é onde ela é forte. Foi útil para checar a semântica do padrão — a distinção entre Decorator, Strategy e Chain of Responsibility, que parecem próximos e não são: na cadeia de responsabilidade **um** elo trata e a cadeia para; no Decorator **todos** contribuem. Essa distinção virou a seção 2 da página, e eu não a tinha clara antes de precisar escrevê-la.

**Onde ela falha, e é o mesmo lugar da Entrega 1 e da Entrega 2.** Preencher lacuna com o plausível sem sinalizar que está preenchendo. Ao pedir ideias de refinamentos para a cadeia, ela produziu desconto por cupom, frete grátis acima de um valor e taxa de serviço — todos perfeitamente verossímeis para um marketplace, nenhum deles observado por nós na Entrega 1. Se eu não tivesse a tabela de RN-A ao lado, teriam entrado no diagrama com a mesma naturalidade de RN-A03. Os quatro refinamentos que ficaram são os quatro que a observação sustenta, e nenhum a mais.

Há uma variante dessa falha específica de padrões de projeto: a IA é **entusiasta de padrão**. Pedida a opinar, ela quase nunca responde "aqui não cabe padrão nenhum" — sugere Singleton para o catálogo, Facade para qualquer serviço, Factory para qualquer construtor. É o oposto do critério que a subequipe adotou, e é o tipo de sugestão que produz um diagrama correto sobre um problema inventado.

**Um caso concreto de verificação.** Gerei a modelagem genérica do Decorator em PlantUML e ela saiu *correta* — todas as relações certas — e *ilegível*: o layout tinha posto `Decorator` acima de `Component`, invertendo a leitura da herança. Ler o código-fonte do diagrama não denunciava nada; só abrir a imagem denunciou. Corrigi com uma âncora de direção na agregação e reexportei. É exatamente a lição que eu já tinha registrado na Entrega 2 — **um diagrama correto e um diagrama legível são coisas diferentes** —, e a repetição do erro me diz que ela não é uma lição aprendida de uma vez: é um passo que precisa estar no processo, e não na memória.

**O que levo.** Conferir contra o artefato, e não contra o texto. Os `RN-` citados nas minhas páginas foram conferidos um a um contra a tabela da Entrega 1; os nomes das classes no diagrama, contra os arquivos `.java`; os diagramas, contra a imagem renderizada. Enquanto a verificação for feita em cima da própria resposta da IA, ela vai concordar consigo mesma.

---

## Histórico de Versões

| Versão | Data | Descrição | Autor(es) | Revisor(es) |
| -- | -- | -- | -- | -- |
| 1.0 | 18/10/2026 | Criação da página com a estrutura dos cinco relatos individuais | Pedro Luciano de Azevedo | -- |
| 1.1 | 18/10/2026 | Redação do ponto de vista de Pedro Luciano de Azevedo sobre lições aprendidas e uso de IA generativa | Pedro Luciano de Azevedo | -- |
