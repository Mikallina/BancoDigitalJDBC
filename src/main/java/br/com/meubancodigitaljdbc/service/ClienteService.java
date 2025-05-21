package br.com.meubancodigitaljdbc.service;

import br.com.meubancodigitaljdbc.dao.ClienteDAO;
import br.com.meubancodigitaljdbc.execptions.ClienteInvalidoException;
import br.com.meubancodigitaljdbc.model.Cliente;
import br.com.meubancodigitaljdbc.model.Endereco;
import br.com.meubancodigitaljdbc.utils.ValidaCpfUtils;
import br.com.meubancodigitaljdbc.utils.ValidarClienteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteDAO clienteDAO;
    private final CepService cepService;

    @Autowired
    public ClienteService(ClienteDAO clienteDAO, CepService cepService) {
        this.clienteDAO = clienteDAO;
        this.cepService = cepService;
    }

    public boolean salvarCliente(Cliente cliente, boolean isAtualizar) throws ClienteInvalidoException, SQLException {
        LOGGER.info("Recebido cliente: nome={}, cpf={}, nascimento={}, endereco={}",
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getDataNascimento(),
                cliente.getEndereco()
        );


        LOGGER.info("Iniciando processo de {} do cliente com CPF: {}", isAtualizar ? "atualização" : "criação", cliente.getCpf());

        validarCliente(cliente, isAtualizar);
        clienteDAO.save(cliente);

        LOGGER.info("Cliente com CPF {} {} com sucesso.", cliente.getCpf(), isAtualizar ? "atualizado" : "cadastrado");
        return isAtualizar;
    }


    public Cliente buscarClientePorCpf(String cpf) {
        LOGGER.info("Buscando cliente por CPF: {}", cpf);
        return clienteDAO.findByCpf(cpf);
    }

    public List<Cliente> listarClientes() {
        LOGGER.info("Listando todos os clientes");
        return clienteDAO.findAll();
    }

    public Optional<Cliente> findById(Long clienteId) {
        LOGGER.info("Buscando cliente por ID: {}", clienteId);
        return clienteDAO.findById(clienteId);
    }

    public void deletarCliente(Long clienteId) throws ClienteInvalidoException {
        LOGGER.info("Tentando deletar cliente com ID: {}", clienteId);
        Optional<Cliente> clienteExistente = clienteDAO.findById(clienteId);

        if (clienteExistente.isEmpty()) {
            LOGGER.warn("Cliente com ID {} não encontrado para deleção.", clienteId);
            throw new ClienteInvalidoException("Cliente com ID " + clienteId + " não encontrado.");
        }

        clienteDAO.deleteById(clienteId);
        LOGGER.info("Cliente com ID {} deletado com sucesso", clienteId);
    }

    private void validarCliente(Cliente cliente, boolean isAtualizar) throws ClienteInvalidoException {
        if (!validarCpf(cliente.getCpf(), isAtualizar, cliente.getIdCliente())) {
            LOGGER.error("Validação de CPF falhou: {}", cliente.getCpf());
            throw new ClienteInvalidoException("CPF inválido ou já cadastrado.");
        }
        if (!ValidarClienteUtils.validarNome(cliente.getNome())) {
            LOGGER.error("Validação de nome falhou: {}", cliente.getNome());
            throw new ClienteInvalidoException("Nome inválido.");
        }
        if (!ValidarClienteUtils.validarEndereco(cliente.getEndereco())) {
            LOGGER.error("Validação de endereço falhou: {}", cliente.getEndereco());
            throw new ClienteInvalidoException("Endereço inválido.");
        }
        if (!ValidarClienteUtils.validarDataNascimento(cliente.getDataNascimento())) {
            LOGGER.error("Validação de data de nascimento falhou: {}", cliente.getDataNascimento());
            throw new ClienteInvalidoException("Data de nascimento inválida.");
        }

        LOGGER.debug("Validação do cliente concluída com sucesso.");

    }


    public Cliente atualizarCliente(Long id, Cliente cliente) throws Exception {
        Cliente existente = clienteDAO.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Cliente com ID " + id + " não encontrado."));

        cliente.setIdCliente(id);

        clienteDAO.update(cliente);
        return cliente;
    }



    public boolean validarCpf(String cpf, boolean isAtualizar, Long clienteId) {
        if (!ValidaCpfUtils.isCPF(cpf)) {
            return false;
        }
        Cliente clienteExistente = clienteDAO.findByCpf(cpf);

        if (isAtualizar) {
            return clienteExistente == null || clienteExistente.getIdCliente().equals(clienteId);
        } else {
            return clienteExistente == null;
        }
    }

}