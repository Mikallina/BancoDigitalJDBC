package br.com.meubancodigitaljdbc.application.ports.output.repository;

import br.com.meubancodigitaljdbc.application.domain.model.ContaPoupanca;

import java.sql.SQLException;

public interface ContaPoupancaRepository {

    void atualizarConta(ContaPoupanca contaPoupanca) throws SQLException;
}
