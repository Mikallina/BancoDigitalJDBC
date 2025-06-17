package br.com.meubancodigitaljdbc.application.ports.output.repository;

import br.com.meubancodigitaljdbc.application.domain.exceptions.*;
import br.com.meubancodigitaljdbc.application.domain.model.Cartao;
import br.com.meubancodigitaljdbc.application.domain.model.Cliente;
import br.com.meubancodigitaljdbc.application.domain.model.Conta;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CartaoRepositoryPort {
    Cartao save(Cartao cartao) throws SQLException;

    List<Cartao> buscarPorConta(Conta conta) throws  SQLException, ContaNaoEncontradaException, CartaoNaoEncontradoException;

    Cartao buscarPorNumeroCartao(String numCartao) throws CartaoFaturaException, SQLException, CartaoStatusException, CartaoNuloException;

    boolean alterarStatusCartao(String numCartao, boolean status) throws SQLException, CartaoStatusException, CartaoNuloException;

    boolean alterarSenhaCartao(String numCartao, int senhaAntiga, int novaSenha) throws SQLException, CartaoStatusException, CartaoNuloException;

    boolean alterarLimiteCartao(String numCartao, double novoLimite) throws CartaoStatusException, SQLException, CartaoNuloException, RegraNegocioException;

    void deleteById(Long idCartao) throws ClienteInvalidoException, SQLException;

    Optional<Cartao> findById(Long idCartao) throws ClienteInvalidoException;


}
