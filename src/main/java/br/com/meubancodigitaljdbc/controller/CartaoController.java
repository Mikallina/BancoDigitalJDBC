package br.com.meubancodigitaljdbc.controller;

import br.com.meubancodigitaljdbc.dto.*;
import br.com.meubancodigitaljdbc.enuns.TipoCartao;
import br.com.meubancodigitaljdbc.execptions.CartaoNaoEncontradoException;
import br.com.meubancodigitaljdbc.execptions.ContaNaoEncontradaException;
import br.com.meubancodigitaljdbc.model.Cartao;
import br.com.meubancodigitaljdbc.model.Conta;
import br.com.meubancodigitaljdbc.service.CartaoService;
import br.com.meubancodigitaljdbc.service.ContaService;
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
                                               @RequestParam(required = false) String diaVencimento) throws SQLException {
        Conta conta = contaService.buscarContas(contaC);
        Cartao cartao = cartaoService.criarCartao(conta, tipoCartao, 1234, diaVencimento);
        LOGGER.info("Emitir cartão" + tipoCartao);
        return ResponseEntity.ok(cartao);

    }

    @PutMapping("/alterar-senha/{numCartao}")
    public String alterarSenha(@PathVariable String numCartao, @RequestBody AlterarSenhaDTO dto)
            throws Exception {
        Cartao cartao = cartaoService.buscarCartaoPorCliente(numCartao);
        boolean sucesso = cartaoService.alterarSenha(dto.getSenhaAntiga(), dto.getSenhaNova(), cartao);
        LOGGER.info("Alterando senha " + numCartao);
        return "Senha alterada";
    }

    @GetMapping("/dados/{numCartao}")
    public ResponseEntity<Cartao> buscarCartao(@PathVariable String numCartao) throws SQLException {
        Cartao cartao = cartaoService.buscarCartaoPorCliente(numCartao);
        LOGGER.info("Buscando cartão " + numCartao);
        return ResponseEntity.ok(cartao);
    }

    @PutMapping("/alterar-status/{numCartao}")
    public ResponseEntity<Boolean> alterarStatusCartao(@PathVariable String numCartao,
                                                      @RequestBody AlterarStatusDTO dto) throws Exception {
        boolean sucesso =  cartaoService.alterarStatus(numCartao, dto.isStatus());
        LOGGER.info("Alterar status do cartão.. " + numCartao);
        return ResponseEntity.ok(sucesso);

    }

    @PutMapping("/alterar-limite/{numCartao}")
    public ResponseEntity<Boolean> alterarLimite(@PathVariable String numCartao, @RequestBody LimiteDTO limiteDTO)
            throws Exception {
        boolean sucesso = cartaoService.alterarLimiteCartao(numCartao, limiteDTO.getNovoLimite());
        LOGGER.info("Alterar limite do cartão.. " + numCartao);
        return ResponseEntity.ok(sucesso);
    }


    @GetMapping("/{numeroConta}")
    public ResponseEntity<List<Cartao>> verificarCartao(@PathVariable String numeroConta) throws SQLException, ContaNaoEncontradaException, CartaoNaoEncontradoException {
        List<Cartao> cartoes = cartaoService.buscarCartaoPorConta(numeroConta);
        LOGGER.info("Verificar cartão.. " + numeroConta);
        return ResponseEntity.ok(cartoes);
    }

    @PostMapping("/compra-cartao")
    public Optional<Boolean> realizarPagamento(@RequestBody CompraCartaoDTO compraCartaoDTO) throws Exception {
        boolean sucesso = cartaoService.realizarCompra(compraCartaoDTO);
        LOGGER.info("Realizar compra com cartão... " + compraCartaoDTO);
        return Optional.of(sucesso);
    }

    @GetMapping("/fatura/{numCartao}")
    public ResponseEntity<?> consultarFatura(@PathVariable String numCartao) throws SQLException {
        cartaoService.consultarFatura(numCartao);
        LOGGER.info("Consultar fatura do cartão... " + numCartao);
        return ResponseEntity.ok(Map.of());
    }

    @PutMapping("/pagar-fatura")
    public ResponseEntity<String> pagarFatura(@RequestBody PagamentoFaturaDTO dto) throws Exception {
        boolean sucesso = cartaoService.realizarPagamentoFatura(dto.getNumCartao(), dto.getValor());
        LOGGER.info("Pagar fatura... ");
        return ResponseEntity.ok("OK");

    }

}
