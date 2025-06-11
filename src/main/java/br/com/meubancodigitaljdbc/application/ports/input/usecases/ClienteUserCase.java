package br.com.meubancodigitaljdbc.application.ports.input.usecases;

import br.com.meubancodigitaljdbc.application.domain.exceptions.ClienteInvalidoException;
import br.com.meubancodigitaljdbc.application.domain.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteUserCase {
    Cliente salvarCliente(Cliente cliente, boolean isAtualizar) throws Exception;
    Cliente buscarClientePorCpf(String cpf);
    List<Cliente> listarClientes();
    Optional<Cliente> findById(Long id);
    void deletarCliente(Long id) throws ClienteInvalidoException;
    Cliente atualizarCliente(Long id, Cliente cliente) throws Exception;
}
