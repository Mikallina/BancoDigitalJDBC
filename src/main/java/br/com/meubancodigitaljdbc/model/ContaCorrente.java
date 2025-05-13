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

    public double getTaxaManutencao() {
        return taxaManutencao;
    }

    public void setTaxaManutencao(double taxaManutencao) {
        this.taxaManutencao = taxaManutencao;
    }


    @Override
    public String toString() {
        return "ContaCorrente [cliente=" + cliente + ", agencia=" + agencia + ", numConta=" + numConta + "]";
    }


    public ContaCorrente(double taxaManutencao) {
        super();
        this.taxaManutencao = taxaManutencao;
    }

}
