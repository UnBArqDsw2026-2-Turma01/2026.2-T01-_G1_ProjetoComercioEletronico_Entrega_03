package componente;

import dominio.Anuncio;
import dominio.ConsultaCatalogo;
import dominio.Dinheiro;
import dominio.ItemListagem;
import dominio.Listagem;

import java.util.ArrayList;
import java.util.List;

/**
 * Componente concreto (ConcreteComponent): o catalogo cru.
 *
 * Faz o minimo que a busca precisa fazer — casar o termo com o titulo do
 * anuncio — e nada mais: nao filtra por faceta, nao ordena, nao calcula frete e
 * nao exibe reputacao. Tudo isso e responsabilidade dos decoradores, e deixar o
 * componente concreto pobre de proposito e o que torna visivel o que cada
 * refinamento acrescenta.
 *
 * O acervo e fixo, em memoria: a Engenharia Reversa da Entrega 1 foi feita de
 * caixa-preta, entao nao ha acesso ao catalogo real. Os anuncios abaixo
 * reproduzem o que a observacao registrou — varias ofertas para o mesmo item
 * (RN-B09), lojas oficiais e vendedores autonomos (Rich Picture), novos e
 * usados — em quantidade suficiente para a demonstracao.
 *
 * Autor: Pedro Luciano de Azevedo (contratos e componente base).
 */
public final class CatalogoEmMemoria implements FonteDeResultados {

    private final List<Anuncio> acervo;

    public CatalogoEmMemoria() {
        this(acervoPadrao());
    }

    public CatalogoEmMemoria(List<Anuncio> acervo) {
        this.acervo = acervo;
    }

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

    @Override
    public String descricao() {
        return "CatalogoEmMemoria(" + acervo.size() + " anuncios)";
    }

    private static List<Anuncio> acervoPadrao() {
        List<Anuncio> anuncios = new ArrayList<Anuncio>();
        anuncios.add(new Anuncio("Fone de ouvido bluetooth XZ-200", "AudioStore", Anuncio.TipoVendedor.LOJA_OFICIAL,
                Dinheiro.emReais(24990), "Eletronicos", Anuncio.Condicao.NOVO, 4.8, 1532, 250));
        anuncios.add(new Anuncio("Fone de ouvido bluetooth XZ-200", "Carlos Eletro", Anuncio.TipoVendedor.AUTONOMO,
                Dinheiro.emReais(21990), "Eletronicos", Anuncio.Condicao.NOVO, 4.1, 87, 250));
        anuncios.add(new Anuncio("Fone de ouvido bluetooth XZ-200 (usado)", "Marcia Usados", Anuncio.TipoVendedor.AUTONOMO,
                Dinheiro.emReais(13500), "Eletronicos", Anuncio.Condicao.USADO, 3.6, 41, 250));
        anuncios.add(new Anuncio("Fone de ouvido com fio P2", "AudioStore", Anuncio.TipoVendedor.LOJA_OFICIAL,
                Dinheiro.emReais(4990), "Eletronicos", Anuncio.Condicao.NOVO, 4.8, 1532, 120));
        anuncios.add(new Anuncio("Fone de ouvido gamer RGB", "GameHouse", Anuncio.TipoVendedor.LOJA_OFICIAL,
                Dinheiro.emReais(31990), "Games", Anuncio.Condicao.NOVO, 4.6, 903, 480));
        anuncios.add(new Anuncio("Fone de ouvido esportivo a prova dagua", "FitSound", Anuncio.TipoVendedor.AUTONOMO,
                Dinheiro.emReais(17900), "Esportes", Anuncio.Condicao.NOVO, 4.3, 210, 180));
        anuncios.add(new Anuncio("Capa para fone de ouvido", "AcessoriosBR", Anuncio.TipoVendedor.AUTONOMO,
                Dinheiro.emReais(2990), "Acessorios", Anuncio.Condicao.NOVO, 3.9, 64, 60));
        anuncios.add(new Anuncio("Teclado mecanico ABNT2", "GameHouse", Anuncio.TipoVendedor.LOJA_OFICIAL,
                Dinheiro.emReais(45900), "Games", Anuncio.Condicao.NOVO, 4.6, 903, 900));
        return anuncios;
    }
}
