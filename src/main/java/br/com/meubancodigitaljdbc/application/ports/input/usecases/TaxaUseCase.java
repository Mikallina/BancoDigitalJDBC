package br.com.meubancodigitaljdbc.application.ports.input.usecases;

import br.com.meubancodigitaljdbc.application.domain.model.Cliente;
import br.com.meubancodigitaljdbc.application.domain.model.ContaCorrente;
import br.com.meubancodigitaljdbc.application.domain.model.ContaPoupanca;

public interface TaxaUseCase {

    double taxaManutencaoCC(Cliente cliente, ContaCorrente contaC);

    double taxaManutencaoCP(Cliente cliente, ContaPoupanca contaP);
}
