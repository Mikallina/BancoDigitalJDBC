package br.com.meubancodigitaljdbc.controller;

import br.com.meubancodigitaljdbc.dto.ContaResponseDTO;
import br.com.meubancodigitaljdbc.dto.DepositoDTO;
import br.com.meubancodigitaljdbc.dto.TransferenciaDTO;
import br.com.meubancodigitaljdbc.enuns.TipoConta;
import br.com.meubancodigitaljdbc.execptions.ContaNaoValidaException;
import br.com.meubancodigitaljdbc.execptions.OperacoesException;
import br.com.meubancodigitaljdbc.model.Cliente;
import br.com.meubancodigitaljdbc.model.Conta;
import br.com.meubancodigitaljdbc.service.ClienteService;
import br.com.meubancodigitaljdbc.service.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Tag(name = "Conta", description = "Operações bancárias relacionadas a contas")
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

    @Operation(summary = "Criar uma nova Conta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")

    })

    @PostMapping("/criarConta")
    public ResponseEntity<Conta> criarConta(@Parameter(description = "CPF do Cliente") @RequestParam String cpf, @Parameter(description = "Agência do Cliente") @RequestParam int agencia,
                                            @Parameter(description = "Tipo de Conta do Cliente") @RequestParam TipoConta tipoConta, HttpServletRequest request)
            throws SQLException {

        long tempoInicio = System.currentTimeMillis();
        Cliente cliente = clienteService.buscarClientePorCpf(cpf);
        Conta conta = contaService.criarConta(cliente, agencia, tipoConta);

        LOGGER.info("Conta criada com sucesso para CPF {}", cpf);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok(conta);
    }

    @Operation(summary = "Buscar Conta por CPF")
    @GetMapping("/buscarConta/{cpf}")
    public ResponseEntity<Cliente> buscarClienteComContas(@Parameter(description = "CPF do Cliente") @PathVariable String cpf, HttpServletRequest request) throws SQLException {
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

    @Operation(summary = "Realiza depósito em uma conta")
    @ApiResponse(responseCode = "200", description = "Depósito realizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PostMapping("/depositar")
    public ResponseEntity<String> depositar(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do depósito", required = true, content = @Content(schema = @Schema(implementation = DepositoDTO.class))
    )
                                            DepositoDTO depositoDTO, HttpServletRequest request) throws SQLException, OperacoesException, ContaNaoValidaException {

        long tempoInicio = System.currentTimeMillis();

        contaService.realizarDeposito(depositoDTO.getNumContaDestino(), depositoDTO.getValor());

        LOGGER.info("Depósito realizado na conta {} no valor de R$ {}", depositoDTO.getNumContaDestino(), depositoDTO.getValor());
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok("Depósito realizado com sucesso.");
    }

    @Operation(
            summary = "Efetuar uma transferência via PIX",
            description = "Realiza uma transferência instantânea entre contas utilizando chave PIX."
    )
    @ApiResponse(responseCode = "200", description = "PIX realizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou chave PIX incorreta")
    @PostMapping("/efetuarPIX")
    public ResponseEntity<String> efetuarPIX(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados necessários para o PIX",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TransferenciaDTO.class))
            )
            TransferenciaDTO transferenciaDTO,
            HttpServletRequest request
    ) throws SQLException {
        long tempoInicio = System.currentTimeMillis();

        contaService.realizarTransferenciaPIX(
                transferenciaDTO.getValor(),
                transferenciaDTO.getNumContaOrigem(),
                transferenciaDTO.getChave()
        );

        LOGGER.info("PIX realizado com sucesso");

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok("PIX realizado com sucesso");
    }


    @Operation(
            summary = "Transferir para conta poupança",
            description = "Realiza uma transferência de uma conta corrente para uma conta poupança."
    )
    @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou conta não encontrada")
    @PostMapping("/transferirPoupanca")
    public ResponseEntity<String> transferirPoupanca(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da transferência para poupança",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TransferenciaDTO.class))
            )
            TransferenciaDTO transferenciaDTO,
            HttpServletRequest request
    ) throws SQLException {
        long tempoInicio = System.currentTimeMillis();

        contaService.realizarTransferenciaPoupanca(
                transferenciaDTO.getValor(),
                transferenciaDTO.getNumContaOrigem(),
                transferenciaDTO.getNumContaDestino()
        );

        LOGGER.info("Transferência realizada com sucesso R$ {}", transferenciaDTO.getValor());

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok("Transferência para conta poupança realizada com sucesso!");
    }


    @Operation(
            summary = "Transferir para outras contas cadastradas",
            description = "Realiza a transferência entre contas correntes/poupança já registradas no banco."
    )
    @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou conta não encontrada")
    @PostMapping("/transferirOutrasContas")
    public ResponseEntity<String> transferirOutrasContas(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da transferência entre contas",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TransferenciaDTO.class))
            )
            TransferenciaDTO transferenciaDTO,
            HttpServletRequest request
    ) throws SQLException {
        long tempoInicio = System.currentTimeMillis();

        contaService.realizarTransferenciaOutrasContas(
                transferenciaDTO.getValor(),
                transferenciaDTO.getNumContaDestino(),
                transferenciaDTO.getNumContaOrigem()
        );

        LOGGER.info("Transferência realizada com sucesso.");

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok("Transferência realizada com sucesso!");
    }


    @Operation(summary = "Aplicar Manutenção de Conta")
    @ApiResponse(responseCode = "200", description = "Manutenção realizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro ao aplicar a taxa")
    @PutMapping("/{idConta}/manutencao")
    public ResponseEntity<String> aplicarTaxaManutencao(
            @PathVariable Long idConta,
            HttpServletRequest request
    ) throws SQLException {
        long tempoInicio = System.currentTimeMillis();

        contaService.aplicarTaxaOuRendimento(idConta, TipoConta.CORRENTE, true);

        LOGGER.info("Taxa de Manutenção Aplicada");

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok("Taxa de Manutenção aplicada com sucesso");
    }


    @Operation(summary = "Aplicar Rendimentos")
    @ApiResponse(responseCode = "200", description = "Rendimento aplicado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro ao aplicar rendimentos")
    @PutMapping("/{idConta}/rendimentos")
    public ResponseEntity<String> aplicarRendimentos(@PathVariable Long idConta, TipoConta tipoConta, HttpServletRequest request) throws SQLException {
        long tempoInicio = System.currentTimeMillis();

        contaService.aplicarTaxaOuRendimento(idConta, tipoConta, false);

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        LOGGER.info("Taxa de Rendimentos aplicado com Sucesso");
        return ResponseEntity.ok("Rendimento aplicado com sucesso");

    }

    @Operation(summary = "Exibir Saldo detalhado")
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
