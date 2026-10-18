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

**O padrão não impede a combinação errada: ele faz com que ela não exista.** Eu entrei achando que o Abstract Factory *protegia* contra misturar um comprovante de boleto com uma autorização de cartão — como se houvesse uma checagem em algum lugar. Não há. O `ServicoDeCheckout` cria os três produtos no construtor, da mesma fábrica, e simplesmente não existe no código uma expressão que produza um produto isolado. A combinação inválida não é rejeitada; ela é **inalcançável**. É uma diferença que só ficou clara ao escrever o cliente e procurar onde colocar a validação — e descobrir que não havia onde.

**A família do cartão foi a que mais ensinou porque é a única com três desfechos.** Pix, boleto e saldo respondem sim ou não. O cartão percorre os dois eventos de borda que eu mesmo tinha modelado no BPMN 1 da Entrega 1 — erro e temporizador — e que viraram `Recusado` e `Expirado` na Máquina de Estados. Implementar obrigou a ordenar as verificações: a janela é checada **antes** da operadora, porque um pedido expirado nunca chega a ser recusado. Essa ordem estava implícita no diagrama e só virou decisão explícita no código.

**Um artefato de demonstração tem um problema que um sistema real não tem: como exercitar um caminho sem quebrar o padrão.** O desfecho expirado depende do tempo, e o contrato não tem parâmetro para isso. Fazer o `Main` instanciar um autorizador configurado resolveria — e destruiria a demonstração, porque o cliente passaria a conhecer uma classe concreta. A saída foi tornar a *fábrica* configurável: escolher a família é o que o padrão permite ao cliente. Aprendi que, num artefato que também serve para explicar, a forma de testar faz parte do que está sendo explicado.

**Escrever para cinco pessoas em paralelo é um requisito de projeto, não um detalhe de processo.** Metade das famílias não existia quando escrevi o cliente. Uma referência direta a `FabricaBoleto` impediria o artefato de compilar, e registrar a família nova exigiria editar o meu arquivo — o que a ata proíbe. A solução, resolver as fábricas pelo nome da classe, é tecnicamente pior que um `switch`: troca uma verificação do compilador por uma falha em tempo de execução. Foi a decisão certa aqui e seria a errada em produção, e o que aprendi foi a registrar isso na página em vez de deixar parecer descuido.

**O meu diagrama recusou três versões antes de sair.** Escrevi o gerador com três verificações — caixa contra caixa, nota contra caixa e **aresta contra caixa** — e as três acusaram erro que eu não tinha visto olhando a imagem: setas de realização atravessando classificadores, uma nota cobrindo uma fábrica. Na Entrega 2 eu tinha aprendido a conferir o texto contra o artefato; aqui aprendi que o artefato também pode se conferir, e que uma verificação automática encontra o que o olho perde em desenho denso.

### Uso da IA generativa (senso crítico)

**O que pedi.** Tradução de notação e conferência, nunca escolha de padrão nem conteúdo de domínio. A evidência era nossa: os achados codificados da Entrega 1, os diagramas da Entrega 2 e os contratos que o Pedro fixou na reunião. A IA ajudou a manter o código no estilo dos contratos já publicados e a redigir no mesmo registro das outras seções da página.

**Onde foi boa.** Em regra formal. Apontou que um `Class.forName(...).newInstance()` está depreciado desde o Java 9 e sugeriu a forma que funciona no JDK 8 sem avisos — o tipo de detalhe que não muda o desenho e faz o artefato compilar limpo. E foi útil como contraditora: pedir o argumento contrário a uma decisão minha produziu objeções melhores do que eu teria levantado sozinho.

**Onde falhou, e é sempre o mesmo lugar.** Ao pedir a família do cartão, ela devolveu antifraude, tokenização e retentativa automática — plausíveis, convincentes e ausentes de qualquer coisa que a gente tenha observado. Nenhuma veio marcada como invenção. É a terceira entrega seguida em que registro exatamente isso, e o padrão não mudou: a fluência é idêntica quando ela cita RN-A04 e quando inventa um fluxo inteiro.

**O erro que me pegou de novo foi de verificação, não de conteúdo.** Na Entrega 2, uma conferência que eu mesmo formulei deu "tudo certo" e havia três divergências, porque eu só olhava numa direção. Aqui aconteceu o equivalente em outro meio: olhei a imagem do diagrama, achei que estava boa, e só o verificador que eu escrevi depois mostrou arestas cruzando caixas. A lição que consolido é que o problema raramente é a ferramenta errar — é a pergunta ser estreita demais. A IA faz exatamente o que se pede, e uma pergunta mal formulada devolve um "tudo certo" indistinguível do verdadeiro.

**A conclusão.** A IA foi útil na proporção direta da evidência que eu tinha para dar a ela. Sobre os meus recortes e os meus diagramas, acertou quase tudo e me corrigiu duas vezes. Sobre o que ninguém observou — tarifa de frete, prazo de fatura, limite de cartão —, produziu números igualmente fluentes e inteiramente inventados. Por isso os três estão no código com `DE_DEMONSTRACAO` no nome: não para enfeitar, mas porque a diferença entre observado e inventado não sobrevive sozinha ao texto.

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
| 1.3 | 18/10/2026 | Redação do ponto de vista de Patrick Anderson Carvalho dos Santos sobre lições aprendidas e uso de IA generativa | Patrick Anderson Carvalho dos Santos | -- |
