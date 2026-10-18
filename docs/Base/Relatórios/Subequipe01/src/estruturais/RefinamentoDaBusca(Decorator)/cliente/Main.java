package cliente;

import componente.CatalogoEmMemoria;
import componente.FonteDeResultados;
import dominio.ConsultaCatalogo;
import dominio.Faceta;
import dominio.ItemListagem;
import dominio.Listagem;

/**
 * Demonstracao do Decorator no refinamento da busca.
 *
 * Esta e a versao 1, que roda com o catalogo cru. Assim que as quatro fatias de
 * refinamento subirem, a cadeia completa e montada no metodo cadeiaCompleta(),
 * e esta classe passa a exibir a busca antes e depois de cada refinamento — que
 * e o que vai para o video.
 *
 * Autor: Pedro Luciano de Azevedo (cliente e demonstracao).
 */
public final class Main {

    public static void main(String[] args) {
        System.out.println("=== G1_ProjetoComercioEletronico | GoF Estrutural: Decorator ===");
        System.out.println("Refinamento da busca - Subequipe 01, Entrega 3");
        System.out.println();

        ConsultaCatalogo consulta = new ConsultaCatalogo("fone de ouvido").comCep("72230-130");

        FonteDeResultados catalogo = new CatalogoEmMemoria();
        exibir("1) Catalogo cru, sem nenhum refinamento", catalogo, consulta);

        // Assim que as quatro fatias subirem, descomentar a linha abaixo:
        // exibir("2) Cadeia completa de refinamentos", cadeiaCompleta(catalogo), consulta);

        System.out.println("URL da consulta (RN-A06): " + consulta.paraURL());
        ConsultaCatalogo reconstruida = ConsultaCatalogo.deURL(consulta.paraURL());
        System.out.println("Consulta reconstruida da URL: " + reconstruida.paraURL());
    }

    /**
     * Monta a cadeia de refinamentos na ordem combinada na reuniao de
     * 18/10/2026: primeiro o que muda o conjunto (filtrar, ordenar), depois o
     * que enriquece cada item (frete, reputacao).
     *
     * Entra em uso quando as quatro fatias estiverem no repositorio:
     *
     * return new ComReputacaoDoVendedor(
     *            new ComFreteEPrazo(
     *                new ComOrdenacao(
     *                    new ComFiltroDeFacetas(catalogo))));
     */
    private static FonteDeResultados cadeiaCompleta(FonteDeResultados catalogo) {
        return catalogo;
    }

    private static void exibir(String titulo, FonteDeResultados fonte, ConsultaCatalogo consulta) {
        System.out.println(titulo);
        System.out.println("   cadeia: " + fonte.descricao());

        Listagem listagem = fonte.buscar(consulta);
        System.out.println("   " + listagem.total() + " resultado(s):");
        for (ItemListagem item : listagem.itens()) {
            System.out.println("     - " + item);
        }
        if (!listagem.facetas().isEmpty()) {
            System.out.println("   facetas (contagem previa - RN-A03):");
            for (Faceta faceta : listagem.facetas()) {
                System.out.println("     - " + faceta);
            }
        }
        if (!listagem.termosAlternativos().isEmpty()) {
            System.out.println("   termos alternativos (RN-A01): " + listagem.termosAlternativos());
        }
        System.out.println();
    }
}
