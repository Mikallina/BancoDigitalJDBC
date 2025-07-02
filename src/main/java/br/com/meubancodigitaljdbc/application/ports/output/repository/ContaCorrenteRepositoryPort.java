package br.com.meubancodigitaljdbc.application.ports.output.repository;

import br.com.meubancodigitaljdbc.application.domain.model.ContaCorrente;

public interface ContaCorrenteRepositoryPort {

    void atualizarConta(ContaCorrente contaCorrente) throws Exception;
}
