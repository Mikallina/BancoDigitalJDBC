package br.com.meubancodigitaljdbc.application.domain.model;

import br.com.meubancodigitaljdbc.application.domain.enuns.TipoCartao;

public abstract class Cartao {

    private Long idCartao;

    private String numCartao;

    private Conta conta;

    private boolean status;

    protected TipoCartao tipoCartao;

    private int senha;

    private double fatura;

    protected Cartao() {

    }
    protected Cartao(String numCartao, Conta conta, TipoCartao tipoCartao, int senha, boolean status) {
        super();
        this.numCartao = numCartao;
        this.conta = conta;
        this.tipoCartao = tipoCartao;
        this.senha = senha;
        this.status = status;
    }

    public double getFatura() {
        return fatura;
    }

    public void setFatura(double fatura) {
        this.fatura = fatura;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public TipoCartao getTipoCartao() {
        return tipoCartao;
    }

    public void setTipoCartao(TipoCartao tipoCartao) {
        this.tipoCartao = tipoCartao;
    }

    public Long getIdCartao() {
        return idCartao;
    }

    public void setIdCartao(Long idCartao) {
        this.idCartao = idCartao;
    }

    public int getSenha() {
        return senha;
    }

    public void setSenha(int senha) {
        this.senha = senha;
    }

    public String getNumCartao() {
        return numCartao;
    }

    public void setNumCartao(String numCartao) {
        this.numCartao = numCartao;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

      public boolean verificarStatus() {
        return this.status;
    }


}
