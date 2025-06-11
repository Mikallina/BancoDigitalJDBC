package br.com.meubancodigitaljdbc.application.ports.output.repository;

import br.com.meubancodigitaljdbc.application.domain.model.Cartao;
import br.com.meubancodigitaljdbc.application.domain.model.Cliente;
import br.com.meubancodigitaljdbc.application.domain.model.Conta;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CartaoRepositoryPort {
    Cartao save(Cartao cartao) throws SQLException;

    List<Cartao> buscarPorConta(Conta conta) throws Exception;

    Cartao buscarPorNumeroCartao(String numCartao) throws Exception;

    boolean alterarStatusCartao(String numCartao, boolean status) throws Exception;

    boolean alterarSenhaCartao(String numCartao, int senhaAntiga, int novaSenha) throws Exception;

    boolean alterarLimiteCartao(String numCartao, double novoLimite) throws Exception;

    void deleteById(Long idCartao) throws Exception;

    Optional<Cartao> findById(Long idCartao) throws Exception;


}
