package br.com.meubancodigitaljdbc.model;

import br.com.meubancodigitaljdbc.enuns.TipoConta;

public class ContaPoupanca extends Conta {
    private double taxaRendimento;
    public ContaPoupanca() {
    }
    public ContaPoupanca(Cliente cliente, int agencia, String numConta, TipoConta tipoConta) {
        super(cliente, agencia, numConta);
        this.tipoConta = tipoConta;

    }

    public void setTaxaRendimento(double taxaRendimento) {
        this.taxaRendimento = taxaRendimento;
    }

    public double getTaxaRendimento() {
        return taxaRendimento;
    }
}
