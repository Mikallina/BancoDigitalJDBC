package br.com.meubancodigitaljdbc.application.service;

import br.com.meubancodigitaljdbc.adapters.output.producers.ClienteProducer;
import br.com.meubancodigitaljdbc.application.domain.exceptions.ClienteInvalidoException;
import br.com.meubancodigitaljdbc.application.domain.model.Cliente;
import br.com.meubancodigitaljdbc.application.domain.model.Endereco;
import br.com.meubancodigitaljdbc.application.ports.input.usecases.ClienteUseCase;
import br.com.meubancodigitaljdbc.application.ports.output.repository.ClienteRepositoryPort;
import br.com.meubancodigitaljdbc.utils.ValidaCpfUtils;
import br.com.meubancodigitaljdbc.utils.ValidarClienteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService implements ClienteUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final CepService cepService;

    private final ClienteProducer clienteProducer;

    @Autowired
    public ClienteService(ClienteRepositoryPort clienteRepositoryPort, CepService cepService, ClienteProducer clienteProducer) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.cepService = cepService;
        this.clienteProducer = clienteProducer;
    }

    @Transactional
    public Cliente salvarCliente(Cliente cliente, boolean isAtualizar) throws ClienteInvalidoException, SQLException {
        int numero = cliente.getEndereco().getNumero();
        String complemento = cliente.getEndereco().getComplemento();

        Endereco enderecoViaCep = cepService.buscarEnderecoPorCep(cliente.getEndereco().getCep());

        if (enderecoViaCep != null) {
            enderecoViaCep.setNumero(numero);
            enderecoViaCep.setComplemento(complemento);
            cliente.setEndereco(enderecoViaCep);
        }

        LOGGER.info("Iniciando processo de {} do cliente com CPF: {}", isAtualizar ? "atualização" : "criação", cliente.getCpf());

        if (!validarCpf(cliente.getCpf(), isAtualizar, cliente.getIdCliente())) {
            throw new ClienteInvalidoException("CPF inválido ou já cadastrado.");
        }
        if (!ValidarClienteUtils.validarNome(cliente.getNome())) {
            throw new ClienteInvalidoException("Nome inválido.");
        }
        if (!ValidarClienteUtils.validarDataNascimento(cliente.getDataNascimento())) {
            throw new ClienteInvalidoException("Data de nascimento inválida.");
        }

        // Salva o cliente
        cliente = clienteRepositoryPort.save(cliente);

        LOGGER.info("Cliente com CPF {} {} com sucesso.", cliente.getCpf(), isAtualizar ? "atualizado" : "cadastrado");
        return cliente;


    }


    public Cliente buscarClientePorCpf(String cpf) {
        LOGGER.info("Buscando cliente por CPF: {}", cpf);
        return clienteRepositoryPort.findByCpf(cpf);
    }

    public List<Cliente> listarClientes() {
        LOGGER.info("Listando todos os clientes");
        return clienteRepositoryPort.findAll();
    }


    @Override
    public Optional<Cliente> findById(Long clienteId) {
        LOGGER.info("Buscando cliente por ID: {}", clienteId);
        return clienteRepositoryPort.findById(clienteId);
    }

    public void deletarCliente(Long clienteId) throws ClienteInvalidoException {
        LOGGER.info("Tentando deletar cliente com ID: {}", clienteId);
        Optional<Cliente> clienteExistente = clienteRepositoryPort.findById(clienteId);

        if (clienteExistente.isEmpty()) {
            LOGGER.warn("Cliente com ID {} não encontrado para deleção.", clienteId);
            throw new ClienteInvalidoException("Cliente com ID " + clienteId + " não encontrado.");
        }

        clienteRepositoryPort.deleteById(clienteId);
        LOGGER.info("Cliente com ID {} deletado com sucesso", clienteId);
    }


    public Cliente atualizarCliente(Long id, Cliente cliente) throws Exception {
        Cliente existente = clienteRepositoryPort.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Cliente com ID " + id + " não encontrado."));

        cliente.setIdCliente(id);

        clienteRepositoryPort.update(cliente);
        return existente;
    }


    public boolean validarCpf(String cpf, boolean isAtualizar, Long clienteId) {
        if (!ValidaCpfUtils.isCPF(cpf)) {
            return false;
        }
        Cliente clienteExistente = clienteRepositoryPort.findByCpf(cpf);
        if (isAtualizar) {

            return clienteExistente == null || clienteExistente.getIdCliente().equals(clienteId);
        } else {
            return clienteExistente == null;
        }
    }

}