package br.com.meubancodigitaljdbc.adapters.input.controllers;

import br.com.meubancodigitaljdbc.application.domain.dto.*;
import br.com.meubancodigitaljdbc.application.domain.enuns.TipoCartao;
import br.com.meubancodigitaljdbc.application.domain.exceptions.*;
import br.com.meubancodigitaljdbc.application.domain.model.Cartao;
import br.com.meubancodigitaljdbc.application.service.CartaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/cartao")
public class CartaoController {


    private final CartaoService cartaoService;

    private static final String LOG_TEMPO_DECORRIDO = "Tempo Decorrido: {} milissegundos: {}";

    private static final Logger LOGGER = LoggerFactory.getLogger(CartaoController.class);

    @Autowired
    public CartaoController(CartaoService cartaoService) {

        this.cartaoService = cartaoService;

    }

    @Operation(
            summary = "Emitir Cartão de Crédito/Débito",
            description = "Emite um cartão de crédito/débito para uma conta específica com base no tipo de cartão e, opcionalmente, o dia de vencimento."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão emitido com sucesso",
                    content = @Content(schema = @Schema(implementation = Cartao.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @PostMapping("/emitir-cartao")
    public ResponseEntity<Cartao> emitirCartao(@RequestParam String contaC, @RequestParam TipoCartao tipoCartao,
                                               @RequestParam(required = false) String diaVencimento, HttpServletRequest request) throws SQLException {
        long tempoInicio = System.currentTimeMillis();

        Cartao cartao = cartaoService.criarCartao(contaC, tipoCartao, 1234, diaVencimento);

        LOGGER.info("Emitir cartão: {} ", tipoCartao);

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;

        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok(cartao);
    }


    @Operation(
            summary = "Alterar Senha do Cartão",
            description = "Permite alterar a senha de um cartão de crédito/débito com base no número do cartão e nas senhas fornecidas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso", content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar a solicitação")
    })
    @PutMapping("/alterar-senha/{numCartao}")
    public Boolean alterarSenha(@PathVariable String numCartao, @RequestBody AlterarSenhaDTO dto, HttpServletRequest request) throws CartaoStatusException, SQLException, CartaoNuloException {
        long tempoInicio = System.currentTimeMillis();

        // Chama o serviço para alterar a senha do cartão
        boolean sucesso = cartaoService.alterarSenhaCartao(numCartao, dto.getSenhaAntiga(), dto.getSenhaNova());

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;

        LOGGER.info("Alterando senha para o cartão: {} ", numCartao);
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return sucesso;
    }


    @Operation(
            summary = "Buscar Cartão por Número",
            description = "Retorna os detalhes de um cartão baseado no número informado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = Cartao.class))),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao buscar o cartão")
    })
    @GetMapping("/dados/{numCartao}")
    public ResponseEntity<Cartao> buscarCartao(@PathVariable String numCartao, HttpServletRequest request) throws SQLException {
        long tempoInicio = System.currentTimeMillis();

        // Chama o serviço para buscar o cartão pelo número
        Cartao cartao = cartaoService.buscarCartaoPorCliente(numCartao);

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;

        LOGGER.info("Buscando cartão: {} ", numCartao);
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok(cartao);
    }


    @Operation(
            summary = "Alterar status do cartão",
            description = "Altera o status de um cartão, ativando ou desativando-o com base no número do cartão informado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status do cartão alterado com sucesso",
                    content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao alterar o status do cartão")
    })
    @PutMapping("/alterar-status/{numCartao}")
    public ResponseEntity<Boolean> alterarStatusCartao(@PathVariable String numCartao,
                                                       @RequestBody AlterarStatusDTO dto, HttpServletRequest request) throws CartaoNaoEncontradoException, SQLException, CartaoNuloException, CartaoStatusException {
        long tempoInicio = System.currentTimeMillis();

        // Chama o serviço para alterar o status do cartão
        boolean sucesso = cartaoService.alterarStatus(numCartao, dto.isStatus());

        LOGGER.info("Alterar status do cartão {}", numCartao);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok(sucesso);
    }




    @Operation(
            summary = "Alterar limite do cartão",
            description = "Altera o limite de crédito de um cartão com base no número do cartão e no novo limite informado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Limite alterado com sucesso",
                    content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao alterar limite")
    })
    @PutMapping("/alterar-limite/{numCartao}")
    public ResponseEntity<Boolean> alterarLimite(@PathVariable String numCartao, @RequestBody LimiteDTO limiteDTO, HttpServletRequest request) throws CartaoStatusException, SQLException, CartaoNuloException, RegraNegocioException {
        long tempoInicio = System.currentTimeMillis();

        // Chama o serviço para alterar o limite do cartão
        boolean sucesso = cartaoService.alterarLimiteCartao(numCartao, limiteDTO.getNovoLimite());

        LOGGER.info("Alterar limite do cartão: {} ", numCartao);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;

        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok(sucesso);
    }



    @Operation(
            summary = "Verificar cartões vinculados à conta",
            description = "Este endpoint retorna uma lista de cartões vinculados a uma conta específica, baseada no número da conta fornecido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartões encontrados com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Cartao.class)))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Conta ou cartão não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/{numeroConta}")
    public ResponseEntity<List<Cartao>> verificarCartao(@PathVariable String numeroConta, HttpServletRequest request) throws SQLException, ContaNaoEncontradaException, CartaoNaoEncontradoException {
        long tempoInicio = System.currentTimeMillis();

        // Chama o serviço para buscar cartões vinculados à conta
        List<Cartao> cartoes = cartaoService.buscarCartaoPorConta(numeroConta);

        LOGGER.info("Verificar cartão {}", numeroConta);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;

        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok(cartoes);
    }


    @Operation(
            summary = "Realizar pagamento com cartão",
            description = "Este endpoint realiza o pagamento com o cartão, debitando o valor informado da conta vinculada ao cartão."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compra realizada com sucesso",
                    content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping("/compra-cartao")
    public Optional<Boolean> realizarPagamento(@RequestBody CompraCartaoDTO compraCartaoDTO, HttpServletRequest request) throws CartaoStatusException, CartaoNaoEncontradoException, SQLException, CartaoNuloException {
        long tempoInicio = System.currentTimeMillis();

        // Chama o serviço para realizar a compra
        boolean sucesso = cartaoService.realizarCompra(compraCartaoDTO);

        LOGGER.info("Realizar compra com cartão: {}", compraCartaoDTO);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;

        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return Optional.of(sucesso);
    }


    @Operation(
            summary = "Consultar fatura do cartão",
            description = "Este endpoint permite consultar a fatura de um cartão com base no número do cartão informado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fatura consultada com sucesso",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/fatura/{numCartao}")
    public ResponseEntity<Map<String, Object>> consultarFatura(@PathVariable String numCartao, HttpServletRequest request) throws SQLException {
        long tempoInicio = System.currentTimeMillis();

        // Chama o serviço para consultar a fatura do cartão
        cartaoService.consultarFatura(numCartao);

        LOGGER.info("Consultar fatura do cartão {}", numCartao);

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;

        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        // Retorna uma resposta OK com um mapa vazio (ou com dados da fatura no futuro)
        return ResponseEntity.ok(Map.of());
    }


    @Operation(
            summary = "Realizar pagamento de fatura",
            description = "Este endpoint permite realizar o pagamento de uma fatura de cartão com base no número do cartão e no valor do pagamento."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamento realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cartão ou fatura não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PutMapping("/pagar-fatura")
    public ResponseEntity<String> pagarFatura(@RequestBody PagamentoFaturaDTO dto, HttpServletRequest request) throws CartaoFaturaException, CartaoStatusException, SQLException, CartaoNuloException {
        long tempoInicio = System.currentTimeMillis();

        // Chama o serviço para realizar o pagamento da fatura
        cartaoService.realizarPagamentoFatura(dto.getNumCartao(), dto.getValor());

        LOGGER.info("Pagar fatura... ");

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;

        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        // Retorna uma resposta OK após o pagamento ser realizado com sucesso
        return ResponseEntity.ok("OK");
    }

    @Operation(
            summary = "Deletar Cartao",
            description = "Deleta um cartao com base no ID fornecido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartao deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cartao não encontrado"),
            @ApiResponse(responseCode = "400", description = "Erro ao tentar deletar a Cartao")
    })
    @DeleteMapping("/deletar-cartao/{cartaoId}")
    public ResponseEntity<String> deletarCartaoId(@PathVariable Long cartaoId, HttpServletRequest request) throws ClienteInvalidoException, SQLException {
        long tempoInicio = System.currentTimeMillis();

        cartaoService.deletarCartao(cartaoId);

        LOGGER.info("Deletar Cartao {}", cartaoId);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok("Cartao deletado com sucesso.");
    }


}
