package br.com.meubancodigitaljdbc.controller;

import br.com.meubancodigitaljdbc.dto.ContaResponseDTO;
import br.com.meubancodigitaljdbc.dto.DepositoDTO;
import br.com.meubancodigitaljdbc.dto.TransferenciaDTO;
import br.com.meubancodigitaljdbc.enuns.TipoConta;
import br.com.meubancodigitaljdbc.execptions.ContaNaoEncontradaException;
import br.com.meubancodigitaljdbc.execptions.ContaNaoValidaException;
import br.com.meubancodigitaljdbc.execptions.OperacoesException;
import br.com.meubancodigitaljdbc.model.Cliente;
import br.com.meubancodigitaljdbc.model.Conta;
import br.com.meubancodigitaljdbc.service.ClienteService;
import br.com.meubancodigitaljdbc.service.ContaService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;


@RestController
@RequestMapping("/conta")
public class ContaController {


    private static final Logger LOGGER = LoggerFactory.getLogger(ContaController.class);

    private static final String LOG_TEMPO_DECORRIDO = "Tempo Decorrido: {} milissegundos: {}";
    private final ContaService contaService;
    private final ClienteService clienteService;

    @Autowired
    public ContaController(ContaService contaService, ClienteService clienteService) {
        this.contaService = contaService;
        this.clienteService = clienteService;
    }

    @PostMapping("/criarConta")
    public ResponseEntity<Conta> criarConta(@RequestParam String cpf, @RequestParam int agencia,
                                            @RequestParam TipoConta tipoConta, HttpServletRequest request)
            throws SQLException, ContaNaoEncontradaException {

        long tempoInicio = System.currentTimeMillis();
        Cliente cliente = clienteService.buscarClientePorCpf(cpf);
        Conta conta = contaService.criarConta(cliente, agencia, tipoConta);

        LOGGER.info("Conta criada com sucesso para CPF {}", cpf);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok(conta);
    }


    @GetMapping("/buscarConta/{cpf}")
    public ResponseEntity<Cliente> buscarClienteComContas(@PathVariable String cpf, HttpServletRequest request) throws SQLException {
        long tempoInicio = System.currentTimeMillis();
        Cliente cliente = clienteService.buscarClientePorCpf(cpf);

        List<Conta> contas = contaService.buscarContasPorCliente(cliente);
        cliente.setContas(contas);

        LOGGER.info("Cliente encontrado: {} ", cpf);

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());
        return ResponseEntity.ok(cliente);


    }

    @PostMapping("/depositar")
    public ResponseEntity<String> depositar(@RequestBody DepositoDTO depositoDTO,HttpServletRequest request) throws SQLException, OperacoesException, ContaNaoValidaException {
        long tempoInicio = System.currentTimeMillis();

        contaService.realizarDeposito(depositoDTO.getNumContaDestino(), depositoDTO.getValor());

        LOGGER.info("Depósito realizado na conta {} no valor de R$ {}", depositoDTO.getNumContaDestino(), depositoDTO.getValor());
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok("Depósito realizado com sucesso.");
    }

    @PostMapping("/efetuarPIX")
    public ResponseEntity<String> efetuarPIX(@RequestBody TransferenciaDTO transferenciaDTO, HttpServletRequest request) throws SQLException {
        long tempoInicio = System.currentTimeMillis();

        contaService.realizarTransferenciaPIX(transferenciaDTO.getValor(),
                transferenciaDTO.getNumContaOrigem(), transferenciaDTO.getChave());

        LOGGER.info("PIX realizado com sucesso");

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());
        return null;
    }

    @PostMapping("/transferirPoupanca")
    public ResponseEntity<String> transferirPoupanca(@RequestBody TransferenciaDTO transferenciaDTO, HttpServletRequest request) throws SQLException {
        long tempoInicio = System.currentTimeMillis();

        contaService.realizarTransferenciaPoupanca(transferenciaDTO.getValor(),
                transferenciaDTO.getNumContaOrigem(), transferenciaDTO.getNumContaDestino());

        LOGGER.info("Transferencia realizada com sucesso R$ {}", transferenciaDTO.getValor());

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok("Transferência para conta poupança realizada com sucesso!");

    }

    @PostMapping("/transferirOutrasContas")
    public ResponseEntity<String> transferirOutrasContas(@RequestBody TransferenciaDTO transferenciaDTO,HttpServletRequest request) throws SQLException {
        long tempoInicio = System.currentTimeMillis();

        contaService.realizarTransferenciaOutrasContas(transferenciaDTO.getValor(),
                transferenciaDTO.getNumContaDestino(), transferenciaDTO.getNumContaOrigem());

        LOGGER.info("Transferencia realizada com sucesso.");

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok("Transferência realizada com sucesso!");

    }

    @PutMapping("/{idConta}/manutencao")
    public ResponseEntity<String> aplicarTaxaManutencao(@PathVariable Long idConta, TipoConta tipoConta, HttpServletRequest request) throws ContaNaoEncontradaException, SQLException {
        long tempoInicio = System.currentTimeMillis();

        contaService.aplicarTaxaOuRendimento(idConta, TipoConta.CORRENTE, true);

        LOGGER.info("Taxa de Manutenção Aplicada");

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok("Taxa de Manutenção aplicada com sucesso");

    }


    @PutMapping("/{idConta}/rendimentos")
    public ResponseEntity<String> aplicarRendimentos(@PathVariable Long idConta, TipoConta tipoConta,HttpServletRequest request) throws ContaNaoEncontradaException, SQLException {
        long tempoInicio = System.currentTimeMillis();

        contaService.aplicarTaxaOuRendimento(idConta, tipoConta, false);

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        LOGGER.info("Taxa de Rendimentos aplicado com Sucesso");
        return ResponseEntity.ok("Rendimento aplicado com sucesso");

    }

    @GetMapping("/exibirSaldoDetalhado")
    public ResponseEntity<ContaResponseDTO> exibirSaldoDetalhado(@RequestParam String cpf, @RequestParam String numConta, HttpServletRequest request) throws SQLException {
        long tempoInicio = System.currentTimeMillis();

        Conta conta = contaService.buscarContaPorClienteEConta(cpf, numConta);

        ContaResponseDTO contaResponseDTO = new ContaResponseDTO(conta.getCliente().getNome(),
                conta.getCliente().getCpf(), conta);

        LOGGER.info("Exibir detalhes da conta...");

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok(contaResponseDTO);

    }

}
