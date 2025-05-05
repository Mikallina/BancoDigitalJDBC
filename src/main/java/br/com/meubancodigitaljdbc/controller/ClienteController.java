package br.com.meubancodigitaljdbc.controller;

import java.util.List;
import java.util.Optional;

import br.com.meubancodigitaljdbc.model.Cliente;
import br.com.meubancodigitaljdbc.model.Endereco;
import br.com.meubancodigitaljdbc.service.CepService;
import br.com.meubancodigitaljdbc.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/cliente")
public class ClienteController {

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private CepService cepService;

	@PostMapping("/adicionar-cliente")
	public ResponseEntity<String> addCliente(@RequestBody Cliente cliente) {
		try {

			if (!clienteService.validarCpf(cliente.getCpf(), false, null)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CPF já cadastrado.");
			}
			if (cliente.getEndereco() == null || cliente.getEndereco().getCep() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CEP não informado ou endereço incompleto.");
			}

			Endereco endereco = cepService.buscarEnderecoPorCep(cliente.getEndereco().getCep());

			if (endereco != null) {
				cliente.getEndereco().setLogradouro(endereco.getLogradouro());
				cliente.getEndereco().setBairro(endereco.getBairro());
				cliente.getEndereco().setLocalidade(endereco.getLocalidade());
				cliente.getEndereco().setUf(endereco.getUf());
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CEP inválido ou não encontrado.");
			}

			clienteService.salvarCliente(cliente, false);

			return ResponseEntity.ok("Cliente adicionado com sucesso!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao adicionar cliente: " + e.getMessage());
		}
	}

	@GetMapping("/buscarCpf/{cpf}")
	public ResponseEntity<Cliente> buscarClientePorCpf(@PathVariable String cpf) {
		Cliente cliente = clienteService.buscarClientePorCpf(cpf);
		if (cliente != null) {
			return ResponseEntity.ok(cliente);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@GetMapping("/listAllCliente")
	public ResponseEntity<List<Cliente>> getAllClientes() {
		List<Cliente> clientes = clienteService.listarClientes();
		return new ResponseEntity<List<Cliente>>(clientes, HttpStatus.OK);
	}

	@GetMapping("/cadastro-cliente/{clienteId}")
	public ResponseEntity<Cliente> buscarClientePorID(@PathVariable Long clienteId) {
		Optional<Cliente> cliente = clienteService.findById(clienteId);

		if (cliente.isPresent()) {
			return ResponseEntity.ok(cliente.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/atualizar-cliente/{clienteId}")
	public ResponseEntity<Cliente> atualizarClientePorID(@PathVariable Long clienteId,
			@RequestBody Cliente clienteAtualizado) {
		Optional<Cliente> clienteExistente = clienteService.findById(clienteId);
		if (clienteExistente.isPresent()) {
			Cliente cliente = clienteExistente.get();

			if (clienteAtualizado.getNome() != null && !clienteAtualizado.getNome().isEmpty()) {
				cliente.setNome(clienteAtualizado.getNome());
			}
			if (clienteAtualizado.getCategoria() != null) {
				cliente.setCategoria(clienteAtualizado.getCategoria());
			}
			if (clienteAtualizado.getDataNascimento() != null) {
				cliente.setDataNascimento(clienteAtualizado.getDataNascimento());
			}
			if (clienteAtualizado.getEndereco() != null && clienteAtualizado.getEndereco().getCep() != null) {
				cliente.getEndereco().setCep(clienteAtualizado.getEndereco().getCep());
			}
			try {
				clienteService.salvarCliente(cliente, true);
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}

			return ResponseEntity.ok(cliente);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("deletar-cliente/{clienteId}")
	public ResponseEntity<Cliente> deletarClientePorID(@PathVariable Long clienteId) {
		Optional<Cliente> clienteExistente = clienteService.findById(clienteId);
		if (clienteExistente.isPresent()) {
			clienteService.deletarCliente(clienteId);
			return ResponseEntity.noContent().build(); // retorna 204
		}
		return ResponseEntity.notFound().build();

	}
}
