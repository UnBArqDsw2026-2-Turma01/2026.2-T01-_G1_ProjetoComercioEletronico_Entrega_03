package decorador;

import componente.FonteDeResultados;
import dominio.Anuncio;
import dominio.ConsultaCatalogo;
import dominio.ItemListagem;
import dominio.Listagem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Decorador concreto (ConcreteDecorator, GAMMA et al., 1995): reputacao do
 * vendedor no cartao da listagem.
 *
 * Origem: RN-A05 e RF-A05 da Entrega 1 — todo produto de marketplace exibe a
 * reputacao do vendedor junto ao preco, ja na listagem, com nota e numero de
 * avaliacoes por oferta (observado). O tipo de vendedor vem do Rich Picture
 * (Entrega 1): a tensao entre loja oficial e vendedor autonomo, e a concern da
 * Jenny, "o produto e confiavel?", que e a pergunta que este selo responde
 * antes do clique.
 *
 * E um refinamento de ENRIQUECIMENTO: acrescenta o selo "reputação" a cada item
 * e nao muda o conjunto nem a ordem. Isso e deliberado: a reputacao e exibida,
 * e nao usada para reordenar ou filtrar. Se este decorador promovesse quem tem
 * nota maior, ele tomaria partido na tensao do Rich Picture — as lojas oficiais
 * do acervo tem muito mais avaliacoes — sem que nenhuma regra observada
 * sustentasse isso.
 *
 * Nao altera a listagem recebida: cada item ganha o selo por comSelo, que
 * devolve uma copia, e a listagem nova sai por comItens.
 *
 * Autor: Guilherme Costa Zanella (refinamento ComReputacaoDoVendedor).
 */
public final class ComReputacaoDoVendedor extends RefinamentoDecorator {

    public static final String SELO = "reputação";

    /** Nota com uma casa decimal e virgula, como o comprador le na tela. */
    private static final Locale PT_BR = new Locale("pt", "BR");

    public ComReputacaoDoVendedor(FonteDeResultados origem) {
        super(origem);
    }

    @Override
    public Listagem buscar(ConsultaCatalogo consulta) {
        Listagem recebida = super.buscar(consulta);

        List<ItemListagem> enriquecidos = new ArrayList<ItemListagem>();
        for (ItemListagem item : recebida.itens()) {
            enriquecidos.add(item.comSelo(SELO, reputacao(item.anuncio())));
        }

        return recebida.comItens(enriquecidos);
    }

    /** "4,8 de 5 (1532 avaliacoes), loja oficial" — nota, volume e tipo (RF-A05). */
    private static String reputacao(Anuncio anuncio) {
        return String.format(PT_BR, "%.1f de 5 (%d avaliacoes), %s",
                anuncio.notaVendedor(), anuncio.quantidadeAvaliacoes(),
                rotulo(anuncio.tipoVendedor()));
    }

    private static String rotulo(Anuncio.TipoVendedor tipo) {
        return tipo == Anuncio.TipoVendedor.LOJA_OFICIAL ? "loja oficial" : "vendedor autonomo";
    }
}
