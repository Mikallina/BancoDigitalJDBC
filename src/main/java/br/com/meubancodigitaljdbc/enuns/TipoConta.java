package br.com.meubancodigitaljdbc.enuns;

public enum TipoConta {

    CORRENTE(1), POUPANCA(2);

    private final Integer codigo;

    private TipoConta(Integer codigo) {
        this.codigo = codigo;

    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getTipoAbreviado() {
        return this == CORRENTE ? "CC" : "CP";
    }
}
