package br.com.meubancodigitaljdbc.model;

import br.com.meubancodigitaljdbc.enuns.TipoConta;

import java.util.Optional;

public class ContaCorrente extends Conta {

    private double taxaManutencao;

    public ContaCorrente() {}

    public ContaCorrente(Cliente cliente, int agencia, String numConta, TipoConta tipoConta) {
        super(cliente, agencia, numConta);
        this.tipoConta = tipoConta;

    }

    public ContaCorrente(Cliente cliente, int agencia, String numConta, double taxaManutencao) {
        super(cliente, agencia, numConta);
        this.taxaManutencao = taxaManutencao;
    }

    public ContaCorrente(Cliente cliente, int agencia, String numConta) {
        super(cliente, agencia, numConta);
    }

    public ContaCorrente(Optional<Cliente> cliente, int agencia, String numConta, TipoConta tipo) {
    }

    public double getTaxaManutencao() {
        return taxaManutencao;
    }

    public void setTaxaManutencao(double taxaManutencao) {
        this.taxaManutencao = taxaManutencao;
    }

    public void aplicarTaxa() {
        saldo -= taxaManutencao;
    }

    @Override
    public void exibirSaldo() {
        System.out.println("Saldo conta corrente: " + saldo);

    }

    @Override
    protected double getSaldoTotal() {

        return saldo;
    }

    @Override
    public String toString() {
        return "ContaCorrente [cliente=" + cliente + ", agencia=" + agencia + ", numConta=" + numConta + "]";
    }

    @Override
    protected Conta[] values() {
        // TODO Auto-generated method stub
        return null;
    }

    public ContaCorrente(double taxaManutencao) {
        super();
        this.taxaManutencao = taxaManutencao;
    }

}
