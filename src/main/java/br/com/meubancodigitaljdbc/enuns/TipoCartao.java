package br.com.meubancodigitaljdbc.enuns;

public enum TipoCartao {
    DEBITO(1),
    CREDITO(2);

    private Integer codigoCartao;

    private TipoCartao(int codigoCartao) {
        this.codigoCartao = codigoCartao;
    }

    public Integer getCodigoCartao() {
        return codigoCartao;
    }

    public void setCodigoCartao(Integer codigoCartao) {
        this.codigoCartao = codigoCartao;
    }

    public String getTipoAbreviado() {
        return this == DEBITO ? "DB" : "CR";
    }

}
