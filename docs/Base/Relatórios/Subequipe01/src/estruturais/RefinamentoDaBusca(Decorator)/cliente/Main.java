package cliente;

import componente.CatalogoEmMemoria;
import componente.FonteDeResultados;
import decorador.ComFiltroDeFacetas;
import decorador.ComFreteEPrazo;
import decorador.ComOrdenacao;
import decorador.ComReputacaoDoVendedor;
import dominio.ConsultaCatalogo;
import dominio.Faceta;
import dominio.ItemListagem;
import dominio.Listagem;
import dominio.Ordenacao;

/**
 * Demonstracao do Decorator no refinamento da busca.
 *
 * O roteiro reproduz o percurso que a Engenharia Reversa da Entrega 1 registrou
 * no Recorte A: o comprador busca um termo, ve a listagem com frete e reputacao,
 * aplica uma faceta e reordena — e a URL acompanha cada passo.
 *
 * Autor: Pedro Luciano de Azevedo (cliente e demonstracao).
 */
public final class Main {

    public static void main(String[] args) {
        cabecalho("G1_ProjetoComercioEletronico | GoF Estrutural: Decorator");
        System.out.println("Refinamento da busca - Subequipe 01, Entrega 3");
        System.out.println();

        FonteDeResultados catalogo = new CatalogoEmMemoria();
        FonteDeResultados busca = cadeiaCompleta(catalogo);

        ConsultaCatalogo consulta = new ConsultaCatalogo("fone de ouvido").comCep("72230-130");

        // ------------------------------------------------------------------
        titulo("1. O catalogo cru, sem nenhum refinamento");
        System.out.println("O componente concreto so casa o termo com o titulo do anuncio.");
        System.out.println("Nao filtra, nao ordena, nao calcula frete e nao exibe reputacao.");
        System.out.println();
        exibir(catalogo, consulta);

        // ------------------------------------------------------------------
        titulo("2. A mesma consulta, agora com a cadeia de refinamentos");
        System.out.println("Quatro decoradores empilhados sobre o mesmo catalogo. Repare que");
        System.out.println("surgiram as facetas com contagem previa (RN-A03) e que cada cartao");
        System.out.println("ganhou frete e prazo (RN-A04) e reputacao do vendedor (RN-A05).");
        System.out.println();
        exibir(busca, consulta);

        // ------------------------------------------------------------------
        titulo("3. O comprador aplica uma faceta: categoria = Eletronicos");
        System.out.println("A contagem previa da listagem anterior ja antecipava quantos");
        System.out.println("resultados restariam. E o que RN-A03 registrou: contar antes de filtrar.");
        System.out.println();
        consulta = consulta.comFiltro(ComFiltroDeFacetas.CATEGORIA, "Eletronicos");
        exibir(busca, consulta);

        // ------------------------------------------------------------------
        titulo("4. E reordena por menor preco");
        System.out.println("RN-A02: a ordenacao padrao e por relevancia; trocar e opcional.");
        System.out.println("O filtro continua aplicado — os refinamentos sao cumulativos.");
        System.out.println();
        consulta = consulta.comOrdenacao(Ordenacao.MENOR_PRECO);
        exibir(busca, consulta);

        // ------------------------------------------------------------------
        titulo("5. O estado da consulta viaja na URL (RN-A06)");
        String url = consulta.paraURL();
        System.out.println("  URL ...........: " + url);
        ConsultaCatalogo reconstruida = ConsultaCatalogo.deURL(url);
        System.out.println("  Reconstruida ..: " + reconstruida.paraURL());
        System.out.println("  Identicas? ....: " + url.equals(reconstruida.paraURL()));
        System.out.println();
        System.out.println("A busca e compartilhavel e reproduzivel: a URL reconstroi a mesma");
        System.out.println("consulta, com o mesmo filtro e a mesma ordenacao.");
        System.out.println();
    }

    /**
     * Monta a cadeia na ordem combinada na reuniao de 18/10/2026: primeiro o que
     * muda o conjunto de resultados, depois o que enriquece cada item.
     *
     * A ordem importa. Enriquecer antes de filtrar calcularia frete e reputacao
     * para itens que seriam descartados em seguida — correto no resultado, e
     * desperdicio no caminho. E a contagem previa das facetas precisa ser
     * calculada sobre o conjunto que chega ao filtro, e nao depois dele.
     */
    private static FonteDeResultados cadeiaCompleta(FonteDeResultados catalogo) {
        return new ComReputacaoDoVendedor(   // 4. RN-A05 — enriquece cada item
                new ComFreteEPrazo(           // 3. RN-A04 — enriquece cada item
                        new ComOrdenacao(     // 2. RN-A02 — reordena o conjunto
                                new ComFiltroDeFacetas(catalogo))));  // 1. RN-A03 — filtra e conta
    }

    private static void exibir(FonteDeResultados fonte, ConsultaCatalogo consulta) {
        System.out.println("  cadeia ........: " + fonte.descricao());
        System.out.println("  consulta ......: " + consulta.paraURL());

        Listagem listagem = fonte.buscar(consulta);
        System.out.println("  " + listagem.total() + " resultado(s):");
        for (ItemListagem item : listagem.itens()) {
            System.out.println("    - " + item);
        }

        if (!listagem.facetas().isEmpty()) {
            System.out.println("  facetas (contagem previa - RN-A03):");
            for (Faceta faceta : listagem.facetas()) {
                System.out.println("    - " + faceta);
            }
        }
        if (!listagem.termosAlternativos().isEmpty()) {
            System.out.println("  termos alternativos (RN-A01): " + listagem.termosAlternativos());
        }
        System.out.println();
    }

    private static void cabecalho(String texto) {
        System.out.println("=======================================================================");
        System.out.println(" " + texto);
        System.out.println("=======================================================================");
    }

    private static void titulo(String texto) {
        System.out.println("-----------------------------------------------------------------------");
        System.out.println(" " + texto);
        System.out.println("-----------------------------------------------------------------------");
    }
}
