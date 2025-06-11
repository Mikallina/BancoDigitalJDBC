package br.com.meubancodigitaljdbc.application.ports.input.usecases;

import br.com.meubancodigitaljdbc.application.domain.dto.CompraCartaoDTO;
import br.com.meubancodigitaljdbc.application.domain.enuns.TipoCartao;
import br.com.meubancodigitaljdbc.application.domain.exceptions.*;
import br.com.meubancodigitaljdbc.application.domain.model.Cartao;
import br.com.meubancodigitaljdbc.application.domain.model.Conta;

import java.sql.SQLException;
import java.util.List;

public interface CartaoUserCase {

    void salvarCartao(Cartao cartao)throws Exception;
    Cartao criarCartao(String contaC, TipoCartao tipoCartao, int senha, String diaVencimento) throws SQLException;

    Cartao criarCartaoCredito (Conta conta, int senha, String numCartao, String diaVencimento);

    Cartao criarCartaoDebito(Conta conta, int senha, String numCartao);

    boolean alterarLimiteCartao(String numCartao, double novoLimite)throws CartaoStatusException, SQLException, CartaoNuloException, RegraNegocioException;

    boolean alterarStatus(String numCartao, boolean novoStatus)  throws SQLException, CartaoStatusException, CartaoNuloException;

    boolean alterarSenhaCartao(String numCartao, int senhaAntiga, int novaSenha)  throws SQLException, CartaoStatusException, CartaoNuloException;

    Cartao validarCartao(String numCartao, boolean checarStatusAtivo)
            throws CartaoNuloException, CartaoStatusException, SQLException;

    boolean realizarCompra(CompraCartaoDTO dto) throws CartaoStatusException, SQLException, CartaoNuloException;
    void realizarPagamentoFatura(String numCartao, double valorPagamento) throws CartaoFaturaException, SQLException, CartaoStatusException, CartaoNuloException;

    Cartao buscarCartaoPorCliente(String numCartao) throws SQLException;

    List<Cartao> buscarCartaoPorConta(String numeroConta) throws SQLException, ContaNaoEncontradaException, CartaoNaoEncontradoException;

    void consultarFatura(String numCartao) throws SQLException;
    void deletarCartao(Long cartaoId) throws ClienteInvalidoException, SQLException;

}
