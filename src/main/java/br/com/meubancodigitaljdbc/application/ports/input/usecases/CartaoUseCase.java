package br.com.meubancodigitaljdbc.application.ports.input.usecases;

import br.com.meubancodigitaljdbc.adapters.input.controllers.request.CompraCartaoRequest;
import br.com.meubancodigitaljdbc.application.domain.enuns.TipoCartao;
import br.com.meubancodigitaljdbc.application.domain.exceptions.*;
import br.com.meubancodigitaljdbc.application.domain.model.Cartao;
import br.com.meubancodigitaljdbc.application.domain.model.Conta;

import java.sql.SQLException;
import java.util.List;

public interface CartaoUseCase {

    void salvarCartao(Cartao cartao)throws Exception;
    Cartao criarCartao(String contaC, TipoCartao tipoCartao, int senha, String diaVencimento) throws SQLException;

    Cartao criarCartaoCredito (Conta conta, int senha, String numCartao, String diaVencimento);

    Cartao criarCartaoDebito(Conta conta, int senha, String numCartao);

    boolean alterarLimiteCartao(String numCartao, double novoLimite) throws CartaoStatusException, SQLException, CartaoNuloException, RegraNegocioException, CartaoFaturaException;

    boolean alterarStatus(String numCartao, boolean novoStatus) throws SQLException, CartaoStatusException, CartaoNuloException, CartaoFaturaException;

    boolean alterarSenhaCartao(String numCartao, int senhaAntiga, int novaSenha) throws SQLException, CartaoStatusException, CartaoNuloException, CartaoFaturaException;

    Cartao validarCartao(String numCartao, boolean checarStatusAtivo)
            throws CartaoNuloException, CartaoStatusException, SQLException, CartaoFaturaException;

    boolean realizarCompra(CompraCartaoRequest dto) throws CartaoStatusException, SQLException, CartaoNuloException, CartaoFaturaException;
    void realizarPagamentoFatura(String numCartao, double valorPagamento) throws CartaoFaturaException, SQLException, CartaoStatusException, CartaoNuloException;

    Cartao buscarCartaoPorCliente(String numCartao) throws SQLException, CartaoFaturaException, CartaoStatusException, CartaoNuloException;

    List<Cartao> buscarCartaoPorConta(String numeroConta) throws SQLException, ContaNaoEncontradaException, CartaoNaoEncontradoException;

    void consultarFatura(String numCartao) throws SQLException, CartaoFaturaException, CartaoStatusException, CartaoNuloException;
    void deletarCartao(Long cartaoId) throws ClienteInvalidoException, SQLException;

}
