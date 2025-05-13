package br.com.meubancodigitaljdbc.enuns;

public enum TipoCartao {
    DEBITO(1),
    CREDITO(2);

    private Integer codigoCartao;

    TipoCartao(int codigoCartao) {
        this.codigoCartao = codigoCartao;
    }


}
