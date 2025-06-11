package br.com.meubancodigitaljdbc.application.ports.input.usecases;

import br.com.meubancodigitaljdbc.application.domain.enuns.TipoConta;
import br.com.meubancodigitaljdbc.application.domain.exceptions.ClienteInvalidoException;
import br.com.meubancodigitaljdbc.application.domain.exceptions.ContaNaoValidaException;
import br.com.meubancodigitaljdbc.application.domain.exceptions.OperacoesException;
import br.com.meubancodigitaljdbc.application.domain.model.Cliente;
import br.com.meubancodigitaljdbc.application.domain.model.Conta;

import java.sql.SQLException;
import java.util.List;

public interface ContaUserCase {

    void salvarConta(Conta conta, boolean isAtualizar) throws ContaNaoValidaException;

    List<Conta> buscarContasPorCliente(Cliente cliente) throws SQLException;

    Conta criarConta(Cliente cliente, int agencia, TipoConta tipoConta) throws SQLException;

    boolean aplicarTaxaOuRendimento(Long idConta, TipoConta tipoConta, boolean aplicarTaxa) throws SQLException;

    boolean realizarDeposito(String numContaDestino, double valor) throws OperacoesException, SQLException, ContaNaoValidaException;

    boolean realizarTransferencia(double valor, String numContaOrigem, String numContaDestino,
                                  boolean transferenciaPoupança, boolean transferenciaPix, boolean transferenciaOutrasContas) throws SQLException;

    Conta buscarContas(String conta) throws SQLException;

    Conta buscarContaPorClienteEConta(String cpf, String numConta) throws SQLException;

    void deletarConta(Long contaId) throws ClienteInvalidoException;

    boolean realizarTransferenciaPoupanca(double valor, String numContaOrigem, String numContaDestino) throws SQLException;

    boolean realizarTransferenciaPIX(double valor, String numContaOrigem, String chaveDestino) throws SQLException;

    boolean realizarTransferenciaOutrasContas(double valor, String numContaOrigem, String numContaDestino) throws SQLException;


}
