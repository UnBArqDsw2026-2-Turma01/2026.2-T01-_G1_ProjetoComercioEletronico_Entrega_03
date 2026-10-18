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

**O Abstract Factory protege a coerência, e não a criação.** Instanciar um `AutorizadorPix` é trivial; qualquer `new` resolve. O que a `FabricaPix` garante é outra coisa: que esse autorizador nunca chegue ao checkout acompanhado de um comprovante ou de uma política de estorno de outro meio. Ficou claro quando o `ComprovantePix` passou a citar o identificador gerado pelo `AutorizadorPix` — os dois só fazem sentido juntos, e a fábrica é o único lugar que os cria. É por isso que o Factory Method não bastava: ele resolveria a criação de cada produto, mas não o vínculo entre eles.

**Uma família só mostra o padrão em contraste com outra.** Sozinha, a família Pix parece apenas uma implementação de três interfaces. É ao lado do boleto que ela mostra o padrão funcionando: as mesmas três perguntas do contrato — a autorização sai na hora? o que o comprador recebe? em quanto tempo o dinheiro volta? — recebem respostas opostas. Aprendi que, num padrão de famílias, a diferença entre as famílias é o que justifica a abstração; quatro famílias quase iguais seriam só variações cosméticas.

**O `«extend»` que eu modelei virou código.** No Diagrama de Casos de Uso da Entrega 2, *Filtrar Resultados* é `«extend»` de *Buscar Produtos*: opcional, e só faz sentido depois que a busca trouxe resultados. O `ComFiltroDeFacetas` é exatamente isso — envolve uma fonte que já devolve resultados e acrescenta o filtro sobre ela, sem que o catálogo saiba que ele existe. A implementação também deu razão a uma decisão da minha Máquina de Estados da busca: reutilizar o estado `Buscando` para filtros, ordenação e paginação. No código, todos os refinamentos rodam dentro de uma única chamada a `buscar()`; não haveria por que ter um estado de espera para cada um.

**Uma regra simples pode esconder um detalhe que só aparece ao implementar.** "A contagem prévia é calculada antes de filtrar" parece suficiente até haver um filtro ativo. Com `categoria=Eletronicos` aplicado, a opção "USADO" precisa dizer quantos *eletrônicos* usados restam, e não quantos usados existem no total. A solução foi contar cada dimensão aplicando os filtros das outras. A regra RN-A03 estava certa; o que faltava era descobrir o que ela exige quando os filtros se acumulam — e isso só apareceu testando com consultas reais.

**A imutabilidade é o que torna a pilha segura.** Como cada decorador devolve uma `Listagem` nova, o meu refinamento não precisa saber quem está embaixo nem em cima dele. Foi a regra combinada na reunião que mais simplificou o meu trabalho: pude implementar e testar a minha fatia sem esperar a dos outros.

### Uso da IA generativa (senso crítico)

**Onde usei.** Como apoio na implementação e na redação: para ler os contratos já publicados, implementar a família Pix e o `ComFiltroDeFacetas`, testar o código e estruturar as minhas seções nas páginas dos padrões.

**Onde ajudou.** Em apontar casos que eu não tinha considerado. Foi a IA que levantou que a contagem prévia ficaria errada com filtros ativos e propôs contar cada dimensão separadamente — e essa proposta eu consegui conferir de forma objetiva, rodando três consultas e somando as contagens à mão. Também foi útil para manter o código no estilo dos contratos que o Pedro já tinha publicado.

**Onde ela falha.** Ao preencher lacunas com o que é plausível, sem avisar que está preenchendo. O caso mais claro foi no meu próprio artefato: ao citar a Máquina de Estados da busca, a IA escreveu nomes de estados que soavam certos e não existem no diagrama que eu fiz na Entrega 2. Em outro ponto, descreveu uma "primeira versão" do decorador que nunca foi implementada, como se fosse histórico do trabalho. Nos dois casos, o texto estava bem escrito e coerente, e era exatamente isso que tornava o erro difícil de ver.

**O que levo.** A IA escreve com a mesma segurança quando acerta e quando inventa, então a revisão não pode ser feita em cima da própria resposta dela. O que funcionou foi conferir cada afirmação contra a fonte — o diagrama da Entrega 2, a ata, a saída do programa. 

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
| 1.2 | 18/10/2026 | Adiciona lições aprendidas e uso de IA generativa de Maria Clara | Maria Clara Sena de Lima | -- |
