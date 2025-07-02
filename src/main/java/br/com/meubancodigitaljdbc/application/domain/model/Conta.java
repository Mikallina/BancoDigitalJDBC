package br.com.meubancodigitaljdbc.application.domain.model;

import br.com.meubancodigitaljdbc.application.domain.enuns.TipoConta;

public abstract class Conta {


    protected Cliente cliente;
    protected Long idConta;
    protected int agencia;
    protected String numConta;
    protected double saldo;
    protected TipoConta tipoConta;


    public Conta() {

    }

    public Conta(Cliente cliente, int agencia, String numConta) {
        this.cliente = cliente;
        this.agencia = agencia;
        this.numConta = numConta;
        this.saldo = 0;
    }

    public Conta(Cliente cliente, Long idConta, int agencia, String numConta, double saldo, TipoConta tipoConta) {
        super();
        this.cliente = cliente;
        this.idConta = idConta;
        this.agencia = agencia;
        this.numConta = numConta;
        this.saldo = saldo;
        this.tipoConta = tipoConta;

    }


    public Long getIdConta() {
        return idConta;
    }

    public void setIdConta(Long idConta) {
        this.idConta = idConta;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public int getAgencia() {
        return agencia;
    }

    public void setAgencia(int agencia) {
        this.agencia = agencia;
    }

    public String getNumConta() {
        return numConta;
    }

    public void setNumConta(String numConta) {
        this.numConta = numConta;
    }

    public TipoConta getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(TipoConta tipoConta) {
        this.tipoConta = tipoConta;
    }


}
