package br.com.meubancodigitaljdbc.controller;

import java.sql.SQLException;
import java.util.List;

import br.com.meubancodigitaljdbc.dto.ContaResponseDTO;
import br.com.meubancodigitaljdbc.dto.DepositoDTO;
import br.com.meubancodigitaljdbc.dto.TransferenciaDTO;
import br.com.meubancodigitaljdbc.enuns.TipoConta;
import br.com.meubancodigitaljdbc.model.Cliente;
import br.com.meubancodigitaljdbc.model.Conta;
import br.com.meubancodigitaljdbc.service.ClienteService;
import br.com.meubancodigitaljdbc.service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/conta")
@CrossOrigin(origins = "http://localhost:5500")
public class ContaController {

	@Autowired
	private ContaService contaService;

	@Autowired
	private ClienteService clienteService;

	@PostMapping("/criarConta")
	public ResponseEntity<Conta> criarConta(@RequestParam String cpf, @RequestParam int agencia,
											@RequestParam TipoConta tipoConta) throws SQLException {
		Cliente cliente = clienteService.buscarClientePorCpf(cpf);
		if (cliente != null) {
			Conta conta = contaService.criarConta(cliente, agencia, tipoConta);
			return ResponseEntity.ok(conta);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@GetMapping("/buscarConta/{cpf}")
	public ResponseEntity<List<Conta>> buscarContasPorCpf(@PathVariable String cpf) throws SQLException {
		Cliente cliente = clienteService.buscarClientePorCpf(cpf);
		if (cliente != null) {
			List<Conta> contas = contaService.buscarContasPorCliente(cliente);
			if (!contas.isEmpty()) {
				return ResponseEntity.ok(contas);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	}

	@PostMapping("/depositar")
	public ResponseEntity<String> depositar(@RequestBody DepositoDTO depositoDTO) throws SQLException {
		boolean sucesso = contaService.realizarDeposito(depositoDTO.getNumConta(), depositoDTO.getValor());

		if (sucesso) {
			return ResponseEntity.ok("Depósito realizado com sucesso!");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao realizar depósito.");
		}
	}

	@PostMapping("/efetuarPIX")
	public ResponseEntity<String> efetuarPIX(@RequestBody TransferenciaDTO transferenciaDTO) {
		try {
			boolean sucesso = contaService.realizarTransferenciaPIX(transferenciaDTO.getValor(),
					transferenciaDTO.getNumContaOrigem(), transferenciaDTO.getChave());
			if (sucesso) {
				return ResponseEntity.ok("PIX realizado com sucesso!");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao realizar PIX.");
			}
		} catch (IllegalArgumentException | SQLException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/transferirPoupanca")
	public ResponseEntity<String> transferirPoupanca(@RequestBody TransferenciaDTO transferenciaDTO) throws SQLException {
		boolean sucesso = contaService.realizarTransferenciaPoupanca(transferenciaDTO.getValor(),
				transferenciaDTO.getNumContaOrigem(), transferenciaDTO.getNumContaDestino());
		if (sucesso) {
			return ResponseEntity.ok("Transferência para conta poupança realizada com sucesso!");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao transferir para conta poupança.");
		}
	}

	@PostMapping("/transferirOutrasContas")
	public ResponseEntity<String> transferirOutrasContas(@RequestBody TransferenciaDTO transferenciaDTO) throws SQLException {
		boolean sucesso = contaService.realizarTransferenciaOutrasContas(transferenciaDTO.getValor(),
				transferenciaDTO.getNumContaDestino(), transferenciaDTO.getNumContaOrigem());
		if (sucesso) {
			return ResponseEntity.ok("Transferência realizada com sucesso!");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao transferir para outra conta.");
		}
	}

	@PutMapping("/{idConta}/manutencao")
	public ResponseEntity<String> aplicarTaxaManutencao(@PathVariable Long idConta, TipoConta tipoConta) {

		try {
			boolean sucesso = contaService.aplicarTaxaOuRendimento(idConta, tipoConta.CORRENTE, true);
			if (sucesso) {
				return ResponseEntity.ok("Taxa de Manutenção aplicada com sucesso");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao aplicar a taxa");
			}
		} catch (IllegalArgumentException | SQLException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}

	}

	@PutMapping("/{idConta}/rendimentos")
	public ResponseEntity<String> aplicarRendimentos(@PathVariable Long idConta, TipoConta tipoConta) {
		try {
			boolean sucesso = contaService.aplicarTaxaOuRendimento(idConta, tipoConta.CORRENTE, false);
			if (sucesso) {
				return ResponseEntity.ok("Rendimento aplicado com sucesso");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao aplicar a taxa");
			}
		} catch (IllegalArgumentException | SQLException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/exibirSaldoDetalhado")
	public ResponseEntity<?> exibirSaldoDetalhado(@RequestParam String cpf, @RequestParam String numConta) {
		try {
			Conta conta = contaService.buscarContaPorClienteEConta(cpf, numConta);

			// Retorna os dados da conta em um DTO
			ContaResponseDTO contaResponseDTO = new ContaResponseDTO(conta.getCliente().getNome(),
					conta.getCliente().getCpf(), conta);

			return ResponseEntity.ok(contaResponseDTO);

		} catch (IllegalArgumentException | SQLException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

}
