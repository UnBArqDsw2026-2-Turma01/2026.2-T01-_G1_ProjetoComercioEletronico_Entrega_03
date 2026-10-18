package decorador;

import java.util.ArrayList;
import java.util.List;

import componente.FonteDeResultados;
import dominio.ConsultaCatalogo;
import dominio.ItemListagem;
import dominio.Listagem;

/**
 * Decorador concreto (ConcreteDecorator, GAMMA et al., 1995): acrescenta a cada
 * item os selos de frete e de prazo de entrega.
 *
 * Origem: RN-A04 da Entrega 1 — frete e prazo dependem do CEP e sao calculados
 * ainda na listagem, antes do carrinho. Foi esse achado que, no Diagrama de
 * Componentes da Entrega 2, obrigou o Servico de Catalogo a requerer IFrete do
 * Servico de Entrega: mostrar o frete cedo reduz o abandono no fim do funil,
 * mas empurra uma dependencia para o componente mais consultado do sistema.
 *
 * No Diagrama de Sequencia da Entrega 2, o calculo esta no fragmento opt das
 * mensagens 30 a 35, condicionado a "informa CEP". Aqui a condicao e a mesma:
 * sem CEP na consulta, nenhum selo e acrescentado, e a listagem atravessa este
 * decorador sem alteracao. E a traducao direta do «extend» para codigo.
 *
 * Este e um decorador de *enriquecimento*: nao remove nem reordena item nenhum,
 * so acrescenta informacao a cada um. Por isso a sua posicao na cadeia e
 * indiferente para o conjunto de resultados, mas nao para o custo: colocado
 * depois de ComFiltroDeFacetas, calcula frete apenas dos itens que sobraram.
 *
 * INFERIDO: a tabela de frete abaixo nao foi observada na Entrega 1. O Recorte A
 * registrou que o frete e calculado por CEP na ficha e na listagem, e nao os
 * valores nem a formula da transportadora. Os numeros servem a demonstracao.
 *
 * Nao altera a listagem recebida: devolve uma copia nova via comItens.
 *
 * Autor: Patrick Anderson Carvalho dos Santos (refinamento ComFreteEPrazo).
 */
public final class ComFreteEPrazo extends RefinamentoDecorator {

    public static final String SELO_FRETE = "frete";
    public static final String SELO_PRAZO = "prazo";

    /** Faixa de peso ate a qual vale a primeira tarifa, em gramas. INFERIDO. */
    private static final int PESO_LEVE_EM_GRAMAS = 500;
    private static final int PESO_MEDIO_EM_GRAMAS = 2000;

    /** Tarifas de demonstracao, em centavos. INFERIDAS. */
    private static final long TARIFA_LEVE_EM_CENTAVOS = 1490L;
    private static final long TARIFA_MEDIA_EM_CENTAVOS = 2490L;
    private static final long TARIFA_PESADA_EM_CENTAVOS = 3990L;

    /** Acrescimo por faixa de distancia, derivado do primeiro digito do CEP. INFERIDO. */
    private static final long ACRESCIMO_POR_REGIAO_EM_CENTAVOS = 350L;

    public ComFreteEPrazo(FonteDeResultados origem) {
        super(origem);
    }

    @Override
    public Listagem buscar(ConsultaCatalogo consulta) {
        Listagem recebida = super.buscar(consulta);

        // RN-A04 so se aplica quando ha CEP. Sem ele, nada a acrescentar: o
        // fragmento opt do Diagrama de Sequencia simplesmente nao executa.
        String cep = consulta.cep();
        if (cep == null || cep.trim().isEmpty()) {
            return recebida;
        }

        int regiao = regiaoDoCep(cep);
        List<ItemListagem> enriquecidos = new ArrayList<ItemListagem>(recebida.total());
        for (ItemListagem item : recebida.itens()) {
            int peso = item.anuncio().pesoEmGramas();
            enriquecidos.add(item
                    .comSelo(SELO_FRETE, formatarCentavos(freteEmCentavos(peso, regiao)))
                    .comSelo(SELO_PRAZO, prazoEmDias(regiao) + " dia(s) util(eis)"));
        }
        return recebida.comItens(enriquecidos);
    }

    /**
     * Primeiro digito do CEP como proxy de regiao. E a unica parte do CEP que a
     * Entrega 1 permite usar com alguma base: RN-B04 registrou que o CEP e
     * armazenado como oito digitos sem separador, e nada alem disso.
     */
    private static int regiaoDoCep(String cep) {
        String digitos = cep.replaceAll("[^0-9]", "");
        if (digitos.isEmpty()) {
            return 0;
        }
        return Character.getNumericValue(digitos.charAt(0));
    }

    private static long freteEmCentavos(int pesoEmGramas, int regiao) {
        long tarifa;
        if (pesoEmGramas <= PESO_LEVE_EM_GRAMAS) {
            tarifa = TARIFA_LEVE_EM_CENTAVOS;
        } else if (pesoEmGramas <= PESO_MEDIO_EM_GRAMAS) {
            tarifa = TARIFA_MEDIA_EM_CENTAVOS;
        } else {
            tarifa = TARIFA_PESADA_EM_CENTAVOS;
        }
        return tarifa + (regiao * ACRESCIMO_POR_REGIAO_EM_CENTAVOS);
    }

    private static int prazoEmDias(int regiao) {
        return 2 + regiao;
    }

    /** Mesma apresentacao de Dinheiro.toString, sem depender do value object. */
    private static String formatarCentavos(long centavos) {
        return String.format("R$ %d,%02d", centavos / 100, centavos % 100);
    }
}
