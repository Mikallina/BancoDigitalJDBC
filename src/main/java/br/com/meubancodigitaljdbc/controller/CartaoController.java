package br.com.meubancodigitaljdbc.controller;

import br.com.meubancodigitaljdbc.dto.*;
import br.com.meubancodigitaljdbc.enuns.TipoCartao;
import br.com.meubancodigitaljdbc.execptions.CartaoNaoEncontradoException;
import br.com.meubancodigitaljdbc.execptions.ContaNaoEncontradaException;
import br.com.meubancodigitaljdbc.model.Cartao;
import br.com.meubancodigitaljdbc.model.Conta;
import br.com.meubancodigitaljdbc.service.CartaoService;
import br.com.meubancodigitaljdbc.service.ContaService;
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

    @Autowired
    private CartaoService cartaoService;

    @Autowired
    private ContaService contaService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CartaoController.class);

    @PostMapping("/emitir-cartao")
    public ResponseEntity<Cartao> emitirCartao(@RequestParam String contaC, @RequestParam TipoCartao tipoCartao,
                                               @RequestParam(required = false) String diaVencimento, HttpServletRequest request) throws SQLException {
        long tempoInicio = System.currentTimeMillis();
        Conta conta = contaService.buscarContas(contaC);
        Cartao cartao = cartaoService.criarCartao(conta, tipoCartao, 1234, diaVencimento);
        LOGGER.info("Emitir cartão" + tipoCartao);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info("Tempo Decorrido: " + tempototal + " millisegundos: " + request.getRequestURI());
        return ResponseEntity.ok(cartao);

    }

    @PutMapping("/alterar-senha/{numCartao}")
    public String alterarSenha(@PathVariable String numCartao, @RequestBody AlterarSenhaDTO dto, HttpServletRequest request)
            throws Exception {
        long tempoInicio = System.currentTimeMillis();
        Cartao cartao = cartaoService.buscarCartaoPorCliente(numCartao);
        boolean sucesso = cartaoService.alterarSenha(dto.getSenhaAntiga(), dto.getSenhaNova(), cartao);
        long tempoFinal = System.currentTimeMillis();
        LOGGER.info("Alterando senha " + numCartao);
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info("Tempo Decorrido: " + tempototal + " millisegundos: " + request.getRequestURI());
        return "Senha alterada";
    }

    @GetMapping("/dados/{numCartao}")
    public ResponseEntity<Cartao> buscarCartao(@PathVariable String numCartao,  HttpServletRequest request) throws SQLException {
        Cartao cartao = cartaoService.buscarCartaoPorCliente(numCartao);
        long tempoInicio = System.currentTimeMillis();
        LOGGER.info("Buscando cartão " + numCartao);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info("Tempo Decorrido: " + tempototal + " millisegundos: " + request.getRequestURI());
        return ResponseEntity.ok(cartao);
    }

    @PutMapping("/alterar-status/{numCartao}")
    public ResponseEntity<Boolean> alterarStatusCartao(@PathVariable String numCartao,
                                                       @RequestBody AlterarStatusDTO dto, HttpServletRequest request) throws Exception {
        long tempoInicio = System.currentTimeMillis();
        boolean sucesso = cartaoService.alterarStatus(numCartao, dto.isStatus());
        LOGGER.info("Alterar status do cartão.. " + numCartao);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info("Tempo Decorrido: " + tempototal + " millisegundos: " + request.getRequestURI());
        return ResponseEntity.ok(sucesso);

    }

    @PutMapping("/alterar-limite/{numCartao}")
    public ResponseEntity<Boolean> alterarLimite(@PathVariable String numCartao, @RequestBody LimiteDTO limiteDTO, HttpServletRequest request)
            throws Exception {
        long tempoInicio = System.currentTimeMillis();
        boolean sucesso = cartaoService.alterarLimiteCartao(numCartao, limiteDTO.getNovoLimite());
        LOGGER.info("Alterar limite do cartão.. " + numCartao);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info("Tempo Decorrido: " + tempototal + " millisegundos: " + request.getRequestURI());
        return ResponseEntity.ok(sucesso);
    }


    @GetMapping("/{numeroConta}")
    public ResponseEntity<List<Cartao>> verificarCartao(@PathVariable String numeroConta, HttpServletRequest request) throws SQLException, ContaNaoEncontradaException, CartaoNaoEncontradoException {
        long tempoInicio = System.currentTimeMillis();
        List<Cartao> cartoes = cartaoService.buscarCartaoPorConta(numeroConta);
        LOGGER.info("Verificar cartão.. " + numeroConta);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info("Tempo Decorrido: " + tempototal + " millisegundos: " + request.getRequestURI());
        return ResponseEntity.ok(cartoes);
    }

    @PostMapping("/compra-cartao")
    public Optional<Boolean> realizarPagamento(@RequestBody CompraCartaoDTO compraCartaoDTO, HttpServletRequest request) throws Exception {
        long tempoInicio = System.currentTimeMillis();
        boolean sucesso = cartaoService.realizarCompra(compraCartaoDTO);
        LOGGER.info("Realizar compra com cartão... " + compraCartaoDTO);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info("Tempo Decorrido: " + tempototal + " millisegundos: " + request.getRequestURI());
        return Optional.of(sucesso);
    }

    @GetMapping("/fatura/{numCartao}")
    public ResponseEntity<?> consultarFatura(@PathVariable String numCartao, HttpServletRequest request) throws SQLException {
        long tempoInicio = System.currentTimeMillis();
        cartaoService.consultarFatura(numCartao);
        LOGGER.info("Consultar fatura do cartão... " + numCartao);
        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info("Tempo Decorrido: " + tempototal + " millisegundos: " + request.getRequestURI());
        return ResponseEntity.ok(Map.of());
    }

    @PutMapping("/pagar-fatura")
    public ResponseEntity<String> pagarFatura(@RequestBody PagamentoFaturaDTO dto, HttpServletRequest request) throws Exception {
        long tempoInicio = System.currentTimeMillis();
        boolean sucesso = cartaoService.realizarPagamentoFatura(dto.getNumCartao(), dto.getValor());
        LOGGER.info("Pagar fatura... ");

        long tempoFinal = System.currentTimeMillis();
        long tempototal = tempoFinal - tempoInicio;
        LOGGER.info("Tempo Decorrido: " + tempototal + " millisegundos: " + request.getRequestURI());
        return ResponseEntity.ok("OK");

    }

}
