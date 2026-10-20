package dominio;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Estado da busca: termo, filtros aplicados, ordenacao, pagina e CEP informado.
 *
 * Origem: decisao 2 do Diagrama de Classes da Entrega 2. RN-A06 registrou que o
 * estado da consulta viaja na URL, o que torna a busca compartilhavel e
 * reproduzivel — por isso a classe e imutavel e sabe se serializar (paraURL) e
 * se reconstruir (deURL).
 *
 * Imutavel de proposito: cada refinamento devolve uma consulta nova, e e isso
 * que permite que os decoradores sejam encadeados sem efeito colateral.
 *
 * Autor: Pedro Luciano de Azevedo (dominio compartilhado e contratos).
 */
public final class ConsultaCatalogo {

    private final String termo;
    private final Map<String, String> filtros;
    private final Ordenacao ordenacao;
    private final int pagina;
    private final String cep;

    public ConsultaCatalogo(String termo) {
        this(termo, new LinkedHashMap<String, String>(), Ordenacao.RELEVANCIA, 1, null);
    }

    private ConsultaCatalogo(String termo, Map<String, String> filtros, Ordenacao ordenacao,
                             int pagina, String cep) {
        this.termo = termo == null ? "" : termo;
        this.filtros = filtros;
        this.ordenacao = ordenacao;
        this.pagina = pagina;
        this.cep = cep;
    }

    public ConsultaCatalogo comFiltro(String nome, String opcao) {
        Map<String, String> novos = new LinkedHashMap<String, String>(filtros);
        novos.put(nome, opcao);
        return new ConsultaCatalogo(termo, novos, ordenacao, pagina, cep);
    }

    public ConsultaCatalogo comOrdenacao(Ordenacao novaOrdenacao) {
        return new ConsultaCatalogo(termo, filtros, novaOrdenacao, pagina, cep);
    }

    public ConsultaCatalogo comCep(String novoCep) {
        return new ConsultaCatalogo(termo, filtros, ordenacao, pagina, novoCep);
    }

    public ConsultaCatalogo naPagina(int novaPagina) {
        return new ConsultaCatalogo(termo, filtros, ordenacao, novaPagina, cep);
    }

    public String termo() {
        return termo;
    }

    public Map<String, String> filtros() {
        return Collections.unmodifiableMap(filtros);
    }

    public String filtro(String nome) {
        return filtros.get(nome);
    }

    public Ordenacao ordenacao() {
        return ordenacao;
    }

    public int pagina() {
        return pagina;
    }

    public String cep() {
        return cep;
    }

    /** RN-A06: o estado da consulta viaja na URL. */
    public String paraURL() {
        StringBuilder url = new StringBuilder("/busca?termo=").append(termo.replace(' ', '+'));
        for (Map.Entry<String, String> filtro : filtros.entrySet()) {
            url.append('&').append(filtro.getKey()).append('=').append(filtro.getValue().replace(' ', '+'));
        }
        url.append("&ordem=").append(ordenacao.name());
        url.append("&pagina=").append(pagina);
        if (cep != null) {
            url.append("&cep=").append(cep);
        }
        return url.toString();
    }

    /** Reconstroi a consulta a partir da URL — o "voltar" do navegador. */
    public static ConsultaCatalogo deURL(String url) {
        String consulta = url.contains("?") ? url.substring(url.indexOf('?') + 1) : url;

        String termo = "";
        Map<String, String> filtros = new LinkedHashMap<String, String>();
        Ordenacao ordenacao = Ordenacao.RELEVANCIA;
        int pagina = 1;
        String cep = null;

        for (String par : consulta.split("&")) {
            String[] partes = par.split("=", 2);
            if (partes.length < 2) {
                continue;
            }
            String chave = partes[0];
            String valor = partes[1].replace('+', ' ');
            if ("termo".equals(chave)) {
                termo = valor;
            } else if ("ordem".equals(chave)) {
                ordenacao = Ordenacao.valueOf(valor);
            } else if ("pagina".equals(chave)) {
                pagina = Integer.parseInt(valor);
            } else if ("cep".equals(chave)) {
                cep = valor;
            } else {
                filtros.put(chave, valor);
            }
        }
        return new ConsultaCatalogo(termo, filtros, ordenacao, pagina, cep);
    }

    @Override
    public String toString() {
        return paraURL();
    }
}
