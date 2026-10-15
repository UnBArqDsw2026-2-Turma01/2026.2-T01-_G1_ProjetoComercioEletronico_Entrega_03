# Fluxo de Compra (State)

# Interface Gráfica

## Tecnologia utilizada

Para a implementação da camada de interface gráfica (Graphical User Interface - GUI) do sistema, adotou-se o **Java Swing** como tecnologia base. A seleção deste arcabouço fundamenta-se em premissas arquiteturais voltadas à manutenibilidade, portabilidade e independência de ecossistemas externos de terceiros.

A escolha do Java Swing justifica-se pelos seguintes vetores técnicos:

* **Ecossistema Nativo e Autossuficiência (`Batteries-Included`):** 
  Por fazer parte integrante e nativa do **JDK (Java Development Kit)**, o Swing elimina a necessidade de inclusão de dependências externas de terceiros para a renderização de elementos visuais. Em termos de engenharia de software, isso resulta em:
  * Redução da complexidade na árvore de dependências do projeto.
  * Mitigação de vulnerabilidades e conflitos de versionamento associados a bibliotecas externas.
  * Simplificação drástica dos artefatos de *build* e dos processos de empacotamento da aplicação.

* **Portabilidade e Independência de Plataforma:** 
  Aderindo ao paradigma de write-once, run-anywhere do Java, a camada de apresentação executa sobre a Máquina Virtual Java (**JVM**). Isso garante que o comportamento e o layout da aplicação mantenham-se consistentes e determinísticos em diferentes ambientes operacionais (tais como GNU/Linux, Microsoft Windows e macOS), sem a exigência de compilação cruzada ou refatoração de código específico de plataforma.

* **Maturidade e Robustez Arquitetural:** 
  O Swing implementa o padrão de arquitetura *Model-View-Controller* (MVC) desacoplado em seus componentes visuais (julgados leves ou *lightweight components*), o que facilita a modularização da interface e desacopla a lógica de negócio dos elementos de renderização gráfica, assegurando previsibilidade e longevidade à manutenção do código-fonte.



## Demonstração da interface (Imagens em cada estado)

### Pedido criado aguardando pagamento

<div style="text-align:center;">


![Pedido criado aguardando pagamento](../Assets/FluxoDeCompra(State)/01.png)

<p><strong>Interface Gráfica</strong> — Pedido criado aguardando pagamento. <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/FluxoDeCompraState/01.png ':ignore')

</div>

### Pedido Pago 

<div style="text-align:center;">

![Pedido Pago](../Assets/FluxoDeCompra(State)/02.png)

<p><strong>Interface Gráfica</strong> — Pedido pago. <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/FluxoDeCompraState/02.png ':ignore')

</div>

### Pedido em separação

<div style="text-align:center;">

![Pedido em separação](../Assets/FluxoDeCompra(State)/03.png)

<p><strong>Interface Gráfica</strong> — Pedido em separação. <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/FluxoDeCompraState/03.png ':ignore')

</div>

### Pedido enviado

<div style="text-align:center;">

![Pedido enviado](../Assets/FluxoDeCompra(State)/04.png)

<p><strong>Interface Gráfica</strong> — Pedido enviado. <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/FluxoDeCompraState/04.png ':ignore')

</div>

### Pedido entregue

<div style="text-align:center;">

![Pedido entregue](../Assets/FluxoDeCompra(State)/05.png)

<p><strong>Interface Gráfica</strong> — Pedido entregue. <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/FluxoDeCompraState/05.png ':ignore')

</div>

### Fim do ciclo de vida da compra

<div style="text-align:center;">

![Fim do ciclo de vida da compra](../Assets/FluxoDeCompra(State)/06.png)

<p><strong>Interface Gráfica</strong> — Fim do ciclo de vida da compra. <em>Autor: José Joaquim da Silva Neto</em></p>

[Clique aqui para baixar a imagem!](/Base/Subequipe03/Assets/FluxoDeCompraState/06.png ':ignore')

</div>

## Demonstração da interface e fluxos (vídeos)

### Fluxo de compra padrão

Esse fluxo trata do ciclo de vida padrão de um pedido, partimos de um pedido que foi criado, logo em seguida o pagamento desse pedido é realizado, em seguida é testado que é exibida uma mesagem de operação inválida ao clicar em confirmar pagamento de um pedido cujo o pagamento já foi realizado, após isso o estado do pedido é atualizado para "em separação", ou seja, sendo separado e aguardando o envio ser realizado, em seguida o estado do pedido é atualizado para enviado e logo em seguida para entregue, assim finalizando o clico de vida padrão da compra de um pedido.

<div style="text-align:center;">

[06.mp4](../Assets/FluxoDeCompra(State)/01.mp4 ':include :type=video controls width=80%')

<p><strong>Interface Gráfica</strong> — Fluxo de compra padrão. <em>Autor: José Joaquim da Silva Neto</em></p>

</div>


## Histórico de Versões

| Versão | Data | Descrição | Autor(es) | Revisor(es) |
| -- | -- | -- | -- | -- |
| 1.0 | 05/10/2026 | Criação da página | José Joaquim da Silva Neto | -- |
| 1.1 | 15/10/2026 | Adiciona justificativa da tecnologia utilizada para a interfacce gráfica | José Joaquim da Silva Neto | -- |
| 1.2 | 15/10/2026 | Adiciona imagens e vídeo da demonstração da interface gráfica | José Joaquim da Silva Neto | -- |