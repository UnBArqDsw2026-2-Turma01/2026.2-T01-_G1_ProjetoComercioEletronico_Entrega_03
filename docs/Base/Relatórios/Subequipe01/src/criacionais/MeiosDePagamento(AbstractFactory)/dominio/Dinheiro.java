package dominio;

import java.util.ArrayList;
import java.util.List;

/**
 * Value object de dinheiro, guardado em centavos.
 *
 * Origem: decisao 10 do Diagrama de Classes da Entrega 2 — operacoes financeiras
 * sucessivas sobre ponto flutuante acumulam erro de arredondamento, entao o valor
 * e representado como inteiro na menor unidade (FOWLER, 2002, padrao Money).
 *
 * Autor: Pedro Luciano de Azevedo (dominio compartilhado e contratos).
 */
public final class Dinheiro {

    private final long centavos;
    private final String moeda;

    public Dinheiro(long centavos, String moeda) {
        if (centavos < 0) {
            throw new IllegalArgumentException("Valor negativo nao e suportado: " + centavos);
        }
        if (moeda == null || moeda.trim().isEmpty()) {
            throw new IllegalArgumentException("Moeda obrigatoria");
        }
        this.centavos = centavos;
        this.moeda = moeda;
    }

    public static Dinheiro emReais(long centavos) {
        return new Dinheiro(centavos, "BRL");
    }

    public long centavos() {
        return centavos;
    }

    public String moeda() {
        return moeda;
    }

    public Dinheiro somar(Dinheiro outro) {
        exigirMesmaMoeda(outro);
        return new Dinheiro(this.centavos + outro.centavos, this.moeda);
    }

    public Dinheiro multiplicar(int fator) {
        return new Dinheiro(this.centavos * fator, this.moeda);
    }

    /**
     * Divide o valor em n parcelas sem perder centavos: o resto da divisao
     * inteira e distribuido uma unidade por vez nas primeiras parcelas.
     */
    public List<Dinheiro> parcelar(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Numero de parcelas deve ser maior que zero");
        }
        long base = centavos / n;
        long resto = centavos % n;
        List<Dinheiro> parcelas = new ArrayList<Dinheiro>(n);
        for (int i = 0; i < n; i++) {
            parcelas.add(new Dinheiro(base + (i < resto ? 1 : 0), moeda));
        }
        return parcelas;
    }

    private void exigirMesmaMoeda(Dinheiro outro) {
        if (!this.moeda.equals(outro.moeda)) {
            throw new IllegalArgumentException("Moedas diferentes: " + this.moeda + " e " + outro.moeda);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dinheiro)) {
            return false;
        }
        Dinheiro outro = (Dinheiro) o;
        return centavos == outro.centavos && moeda.equals(outro.moeda);
    }

    @Override
    public int hashCode() {
        return (int) (centavos ^ (centavos >>> 32)) * 31 + moeda.hashCode();
    }

    @Override
    public String toString() {
        return String.format("R$ %d,%02d", centavos / 100, centavos % 100);
    }
}
