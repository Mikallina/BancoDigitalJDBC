package br.com.meubancodigitaljdbc.controller;

import br.com.meubancodigitaljdbc.dto.*;
import br.com.meubancodigitaljdbc.enuns.TipoCartao;
import br.com.meubancodigitaljdbc.execptions.*;
import br.com.meubancodigitaljdbc.model.Cartao;
import br.com.meubancodigitaljdbc.service.CartaoService;
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

    @PutMapping("/alterar-senha/{numCartao}")
    public Boolean alterarSenha(@PathVariable String numCartao, @RequestBody AlterarSenhaDTO dto, HttpServletRequest request) throws CartaoStatusException, SQLException, CartaoNuloException {

        long tempoInicio = System.currentTimeMillis();

        boolean sucesso = cartaoService.alterarSenha(dto.getSenhaAntiga(), dto.getSenhaNova(), numCartao);

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;

        LOGGER.info("Alterando senha: {} ", numCartao);
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return sucesso;
    }

    @GetMapping("/dados/{numCartao}")
    public ResponseEntity<Cartao> buscarCartao(@PathVariable String numCartao,  HttpServletRequest request) throws SQLException {

        Cartao cartao = cartaoService.buscarCartaoPorCliente(numCartao);

        long tempoInicio = System.currentTimeMillis();
        LOGGER.info("Buscando cartão: {} ", numCartao);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());


        return ResponseEntity.ok(cartao);
    }

    @PutMapping("/alterar-status/{numCartao}")
    public ResponseEntity<Boolean> alterarStatusCartao(@PathVariable String numCartao,
                                                       @RequestBody AlterarStatusDTO dto, HttpServletRequest request) throws CartaoNaoEncontradoException, SQLException, CartaoNuloException {
        long tempoInicio = System.currentTimeMillis();
        boolean sucesso = cartaoService.alterarStatus(numCartao, dto.isStatus());
        LOGGER.info("Alterar status do cartão {}", numCartao);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());

        return ResponseEntity.ok(sucesso);

    }

    @PutMapping("/alterar-limite/{numCartao}")
    public ResponseEntity<Boolean> alterarLimite(@PathVariable String numCartao, @RequestBody LimiteDTO limiteDTO, HttpServletRequest request) throws CartaoStatusException, SQLException, CartaoNuloException {
        long tempoInicio = System.currentTimeMillis();

        boolean sucesso = cartaoService.alterarLimiteCartao(numCartao, limiteDTO.getNovoLimite());

        LOGGER.info("Alterar limite do cartão: {} ", numCartao);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;

        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());


        return ResponseEntity.ok(sucesso);
    }


    @GetMapping("/{numeroConta}")
    public ResponseEntity<List<Cartao>> verificarCartao(@PathVariable String numeroConta, HttpServletRequest request) throws SQLException, ContaNaoEncontradaException, CartaoNaoEncontradoException {

        long tempoInicio = System.currentTimeMillis();

        List<Cartao> cartoes = cartaoService.buscarCartaoPorConta(numeroConta);

        LOGGER.info("Verificar cartão {}", numeroConta);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;

        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());


        return ResponseEntity.ok(cartoes);
    }

    @PostMapping("/compra-cartao")
    public Optional<Boolean> realizarPagamento(@RequestBody CompraCartaoDTO compraCartaoDTO, HttpServletRequest request) throws CartaoStatusException, CartaoNaoEncontradoException, SQLException, CartaoNuloException {
        long tempoInicio = System.currentTimeMillis();

       boolean sucesso = cartaoService.realizarCompra(compraCartaoDTO);

        LOGGER.info("Realizar compra com cartão: {}", compraCartaoDTO);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;

        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());


        return Optional.of(sucesso);
    }

    @GetMapping("/fatura/{numCartao}")
    public ResponseEntity<Map<String, Object>>  consultarFatura(@PathVariable String numCartao, HttpServletRequest request) throws SQLException {
        long tempoInicio = System.currentTimeMillis();

        cartaoService.consultarFatura(numCartao);

        LOGGER.info("Consultar fatura do cartão {}", numCartao);

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;

        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());


        return ResponseEntity.ok(Map.of());
    }

    @PutMapping("/pagar-fatura")
    public ResponseEntity<String> pagarFatura(@RequestBody PagamentoFaturaDTO dto, HttpServletRequest request) throws CartaoFaturaException, CartaoStatusException, SQLException, CartaoNuloException {
        long tempoInicio = System.currentTimeMillis();

        cartaoService.realizarPagamentoFatura(dto.getNumCartao(), dto.getValor());

        LOGGER.info("Pagar fatura... ");

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;

        LOGGER.info(LOG_TEMPO_DECORRIDO, tempototal, request.getRequestURI());


        return ResponseEntity.ok("OK");

    }

}
