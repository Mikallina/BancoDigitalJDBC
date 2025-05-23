package br.com.meubancodigitaljdbc.service;

import br.com.meubancodigitaljdbc.model.Cliente;
import br.com.meubancodigitaljdbc.model.ContaCorrente;
import br.com.meubancodigitaljdbc.model.ContaPoupanca;
import org.springframework.stereotype.Service;

@Service
public class TaxaService {

    public double taxaManutencaoCC(Cliente cliente, ContaCorrente contaC) {
        double taxaManutencao = 0;

        if (cliente.getCategoria().getDescricao().equals("Comum")) {

            taxaManutencao = 12;

        } else if (cliente.getCategoria().getDescricao().equals("Super")) {

            taxaManutencao = 8;

        } else {
            taxaManutencao = 2;
        }

        if (contaC.getSaldo() < taxaManutencao) {
            throw new IllegalArgumentException("Saldo insuficiente para aplicar a taxa de manutenção");
        }

        contaC.setTaxaManutencao(taxaManutencao);
        return taxaManutencao;
    }

    public double taxaManutencaoCP(Cliente cliente, ContaPoupanca contaP) {
        double taxaRendimento = 0;
        double saldoAtual = contaP.getSaldo();

        if (cliente.getCategoria().getDescricao().equals("Comum")) {

            taxaRendimento = 0.5;

        } else if (cliente.getCategoria().getDescricao().equals("Super")) {

            taxaRendimento = 0.7;

        } else {
            taxaRendimento = 0.9;
        }

        double taxaMensal = taxaRendimento / 12;
        double saldoRendimento = saldoAtual * Math.pow(1 + (taxaMensal / 100), 1);
        double rendimentoMensal = saldoRendimento - saldoAtual;

        contaP.setTaxaRendimento(taxaRendimento);

        return rendimentoMensal;
    }

}
