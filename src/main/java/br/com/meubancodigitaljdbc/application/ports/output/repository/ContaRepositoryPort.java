package br.com.meubancodigitaljdbc.application.ports.output.repository;

import br.com.meubancodigitaljdbc.application.domain.model.Cliente;
import br.com.meubancodigitaljdbc.application.domain.model.Conta;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ContaRepositoryPort {

    Conta salvarConta(Conta conta) throws SQLException;

    Conta buscarPorNumero(String numeroConta)throws SQLException;

    Cliente buscarClientePorCpf(String cpf);
    Conta buscarContaPorId(Long idConta) throws SQLException;

    Optional<Conta> findById(Long idConta);

    List<Conta> buscarPorClienteId(Long clienteId) throws SQLException;
    void atualizarSaldo(Long idConta, double novoSaldo) throws SQLException;
    Conta atualizarConta(Conta conta) throws SQLException;
    void deleteById(Long id);

}
