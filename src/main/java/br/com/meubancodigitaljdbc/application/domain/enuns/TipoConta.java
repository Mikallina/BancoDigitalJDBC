package br.com.meubancodigitaljdbc.application.domain.enuns;

public enum TipoConta {

    CORRENTE(1), POUPANCA(2);

    private final Integer codigo;

    TipoConta(Integer codigo) {
        this.codigo = codigo;

    }


    public String getTipoAbreviado() {
        return this == CORRENTE ? "CC" : "CP";
    }
}
