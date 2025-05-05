package br.com.meubancodigitaljdbc.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.com.meubancodigitaljdbc.dto.*;
import br.com.meubancodigitaljdbc.enuns.TipoCartao;
import br.com.meubancodigitaljdbc.model.Cartao;
import br.com.meubancodigitaljdbc.model.Conta;
import br.com.meubancodigitaljdbc.service.CartaoService;
import br.com.meubancodigitaljdbc.service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/cartao")
public class CartaoController {

	@Autowired
	private CartaoService cartaoService;

	@Autowired
	private ContaService contaService;

	@PostMapping("/emitir-cartao")
	public ResponseEntity<Cartao> emitirCartao(@RequestParam String contaC, @RequestParam TipoCartao tipoCartao,
											   @RequestParam(required = false) String diaVencimento) throws SQLException {
		Conta conta = contaService.buscarContas(contaC);

		if (conta != null) {
			if (tipoCartao == TipoCartao.CREDITO && diaVencimento == null) {
				return ResponseEntity.badRequest().body(null);
			}
		
			Cartao cartao = cartaoService.criarCartao(conta, tipoCartao, 1234, diaVencimento);

			return ResponseEntity.ok(cartao);
		} else {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@PutMapping("/alterar-senha/{numCartao}")
	public ResponseEntity<String> alterarSenha(@PathVariable String numCartao, @RequestBody AlterarSenhaDTO dto)
			throws Exception {

		int senhaAntiga = dto.getSenhaAntiga();
		int senhaNova = dto.getSenhaNova();

		Optional<Cartao> cartaoOptional = Optional.ofNullable(cartaoService.buscarCartaoPorCliente(numCartao));

		if (cartaoOptional.isPresent()) {
			Cartao cartao = cartaoOptional.get();
			boolean sucesso = cartaoService.alterarSenha(senhaAntiga, senhaNova, cartao);
			if (sucesso) {
				return ResponseEntity.ok("Senha alterada com sucesso!");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Senha antiga incorreta!");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cartão não encontrado!");
		}
	}

	@GetMapping("/dados/{numCartao}")
	public ResponseEntity<Cartao> buscarCartao(@PathVariable String numCartao) throws SQLException {
		Cartao cartao = cartaoService.buscarCartaoPorCliente(numCartao);
		if (cartao == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(cartao);
	}

	@PutMapping("/alterar-status/{numCartao}")
	public ResponseEntity<String> alterarStatusCartao(@PathVariable String numCartao,
			@RequestBody AlterarStatusDTO dto) throws Exception {
		boolean alterado = cartaoService.alterarStatus(numCartao, dto.isStatus());

		if (alterado) {
			return ResponseEntity.ok("Status do cartão alterado com sucesso!");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao alterar o status do cartão.");
		}
	}

	@PutMapping("/alterar-limite/{numCartao}")
	public ResponseEntity<String> alterarLimite(@PathVariable String numCartao, @RequestBody LimiteDTO limiteDTO)
			throws Exception {
		boolean limiteAlterado = cartaoService.alterarLimiteCartao(numCartao, limiteDTO.getNovoLimite());

		if (limiteAlterado) {
			return ResponseEntity.ok("Limite atualizado com sucesso.");
		} else {
			return ResponseEntity.badRequest().body("Erro ao atualizar o limite do cartão.");
		}
	}

	@GetMapping("/{numeroConta}")
	public ResponseEntity<List<Cartao>> verificarCartao(@PathVariable String numeroConta) throws SQLException {
		Conta conta = contaService.buscarContas(numeroConta);

		if (conta == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Conta não encontrada
		}

		List<Cartao> cartoes = cartaoService.buscarCartaoPorConta(conta);

		if (cartoes == null || cartoes.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Nenhum cartão encontrado
		}

		return ResponseEntity.ok(cartoes);
	}

	@PostMapping("/compra-cartao")
	public ResponseEntity<String> realizarPagamento(@RequestBody CompraCartaoDTO compraCartaoDTO) throws Exception {

		String numCartao = compraCartaoDTO.getNumCartao();
		Cartao cartao = cartaoService.buscarCartaoPorNumero(numCartao);
		if (cartao == null) {
			return ResponseEntity.notFound().build();
		}

		boolean compraRealizado = cartaoService.realizarCompra(cartao.getNumCartao(), compraCartaoDTO.getValor(),
				compraCartaoDTO.getDataCompra());
		if (!compraRealizado) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pagamento não autorizado.");
		}

		return ResponseEntity.ok("Pagamento realizado com sucesso.");
	}

	@GetMapping("/fatura/{numCartao}")
	public ResponseEntity<?> consultarFatura(@PathVariable String numCartao) throws SQLException {
		double fatura = cartaoService.consultarFatura(numCartao);
		return ResponseEntity.ok(Map.of("fatura", fatura));
	}

	@PutMapping("/pagar-fatura")
	public ResponseEntity<String> pagarFatura(@RequestBody PagamentoFaturaDTO dto) throws Exception {
		boolean sucesso = cartaoService.realizarPagamentoFatura(dto.getNumCartao(), dto.getValor());
		if (sucesso) {
			return ResponseEntity.ok("Fatura paga com sucesso.");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao pagar fatura.");
		}
	}

}
