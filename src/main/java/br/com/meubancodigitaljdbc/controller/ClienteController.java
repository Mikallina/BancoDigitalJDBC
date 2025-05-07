package br.com.meubancodigitaljdbc.controller;

import br.com.meubancodigitaljdbc.dao.ClienteDAO;
import br.com.meubancodigitaljdbc.execptions.ClienteInvalidoException;
import br.com.meubancodigitaljdbc.model.Cliente;
import br.com.meubancodigitaljdbc.service.CepService;
import br.com.meubancodigitaljdbc.service.ClienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteDAO clienteDAO;

    @Autowired
    private CepService cepService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClienteController.class);
    @PostMapping("/adicionar-cliente")
    public boolean addCliente(@RequestBody Cliente cliente) throws Exception {
        long tempoInicio = System.currentTimeMillis();

        boolean sucesso = clienteService.salvarCliente(cliente, false);

        LOGGER.info("Adicionar cliente" + cliente);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info("Tempo Decorrido: " + tempototal + " millisegundos: " + request.getRequestURI());

        return sucesso;
    }

    @GetMapping("/buscarCpf/{cpf}")
    public ResponseEntity<Cliente> buscarClientePorCpf(@PathVariable String cpf) {
        long tempoInicio = System.currentTimeMillis();

        Cliente cliente = clienteService.buscarClientePorCpf(cpf);

        LOGGER.info("Buscar cliente" + cliente);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info("Tempo Decorrido: " + tempototal + " millisegundos: " + request.getRequestURI());

        return ResponseEntity.ok(cliente);


    }

    @GetMapping("/listAllCliente")
    public ResponseEntity<List<Cliente>> getAllClientes() {
        long tempoInicio = System.currentTimeMillis();

        List<Cliente> clientes = clienteService.listarClientes();

        LOGGER.info("Listar clientes" + clientes);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info("Tempo Decorrido: " + tempototal + " millisegundos: " + request.getRequestURI());

        return new ResponseEntity<List<Cliente>>(clientes, HttpStatus.OK);

    }

    @GetMapping("/cadastro-cliente/{clienteId}")
    public ResponseEntity<Cliente> buscarClientePorID(@PathVariable Long clienteId) {
        long tempoInicio = System.currentTimeMillis();

        Optional<Cliente> clienteOptional = clienteService.findById(clienteId);

        LOGGER.info("Buscar cliente por ID" + clienteOptional);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info("Tempo Decorrido: " + tempototal + " millisegundos: " + request.getRequestURI());

        return clienteOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PutMapping("/atualizar-cliente/{clienteId}")
    public Optional<Cliente> atualizarClientePorID(@PathVariable Long clienteId,
                                                   @RequestBody Cliente clienteAtualizado) {
        long tempoInicio = System.currentTimeMillis();
        Optional<Cliente> clienteExistente = clienteService.findById(clienteId);

        LOGGER.info("Atualizar cliente por ID" + clienteId);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info("Tempo Decorrido: " + tempototal + " millisegundos: " + request.getRequestURI());

        return clienteExistente;
    }


    @DeleteMapping("/deletar-cliente/{clienteId}")
    public ResponseEntity<String> deletarClientePorID(@PathVariable Long clienteId) throws ClienteInvalidoException {
        long tempoInicio = System.currentTimeMillis();

        clienteService.deletarCliente(clienteId);

        LOGGER.info("Deletar Cliente" + clienteId);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info("Tempo Decorrido: " + tempototal + " millisegundos: " + request.getRequestURI());

        return ResponseEntity.ok("Cliente deletado com sucesso.");
    }
}
