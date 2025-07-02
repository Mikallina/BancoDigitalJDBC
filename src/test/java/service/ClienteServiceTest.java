package service;
import br.com.meubancodigitaljdbc.adapters.output.producers.ClienteProducer;
import br.com.meubancodigitaljdbc.application.domain.exceptions.ClienteInvalidoException;
import br.com.meubancodigitaljdbc.application.domain.model.Cliente;
import br.com.meubancodigitaljdbc.application.domain.model.Endereco;
import br.com.meubancodigitaljdbc.application.ports.output.repository.ClienteRepositoryPort;
import br.com.meubancodigitaljdbc.application.service.CepService;
import br.com.meubancodigitaljdbc.application.service.ClienteService;
import br.com.meubancodigitaljdbc.utils.ValidaCpfUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private CepService cepService;

    @Mock
    private ClienteProducer clienteProducer;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        Endereco endereco = new Endereco();
        endereco.setCep("12345678");
        endereco.setNumero(100);
        endereco.setComplemento("Apt 202");

        cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setNome("João da Silva");
        cliente.setCpf("12345678909");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        cliente.setEndereco(endereco);
    }

    @Test
    public void deveSalvarClienteComSucesso() throws Exception {
        when(cepService.buscarEnderecoPorCep(anyString())).thenReturn(new Endereco());
        when(clienteRepositoryPort.findByCpf(anyString())).thenReturn(null);
        when(clienteRepositoryPort.save(any(Cliente.class))).thenReturn(cliente);

        Cliente resultado = clienteService.salvarCliente(cliente, false);

        assertNotNull(resultado);
        verify(clienteRepositoryPort).save(any(Cliente.class));
    }

    @Test
    public void deveFalharSeCpfInvalido() {
        cliente.setCpf("00000000000");
        assertThrows(ClienteInvalidoException.class, () -> {
            clienteService.salvarCliente(cliente, false);
        });
    }

    @Test
    public void deveBuscarClientePorCpf() {
        when(clienteRepositoryPort.findByCpf("12345678909")).thenReturn(cliente);

        Cliente resultado = clienteService.buscarClientePorCpf("12345678909");

        assertEquals("João da Silva", resultado.getNome());
    }

    @Test
    public void deveDeletarCliente() throws ClienteInvalidoException {
        when(clienteRepositoryPort.findById(1L)).thenReturn(Optional.of(cliente));

        clienteService.deletarCliente(1L);

        verify(clienteRepositoryPort).deleteById(1L);
    }

    @Test
    public void deveLancarExcecaoAoDeletarClienteInexistente() {
        when(clienteRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClienteInvalidoException.class, () -> {
            clienteService.deletarCliente(99L);
        });
    }

    @Test
    public void deveAtualizarCliente() throws Exception {
        when(clienteRepositoryPort.findById(1L)).thenReturn(Optional.of(cliente));
        doNothing().when(clienteRepositoryPort).update(any(Cliente.class));

        Cliente atualizado = clienteService.atualizarCliente(1L, cliente);

        assertNotNull(atualizado);
        verify(clienteRepositoryPort).update(cliente);
    }

    @Test
    public void deveListarClientes() {
        List<Cliente> lista = List.of(cliente);
        when(clienteRepositoryPort.findAll()).thenReturn(lista);

        List<Cliente> resultado = clienteService.listarClientes();

        assertEquals(1, resultado.size());
        assertEquals("João da Silva", resultado.get(0).getNome());
        verify(clienteRepositoryPort).findAll();
    }

    @Test
    public void deveBuscarClientePorId() {
        when(clienteRepositoryPort.findById(1L)).thenReturn(Optional.of(cliente));

        Optional<Cliente> resultado = clienteService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals("João da Silva", resultado.get().getNome());
        verify(clienteRepositoryPort).findById(1L);
    }

    @Test
    public void deveValidarCpfQuandoNovoClienteECpfValidoENaoExiste() {
        // CPF válido e não existe no sistema
        when(clienteRepositoryPort.findByCpf(cliente.getCpf())).thenReturn(null);

        boolean resultado = clienteService.validarCpf(cliente.getCpf(), false, null);

        assertTrue(resultado);
    }

    @Test
    public void deveValidarCpfQuandoAtualizarClienteComMesmoCpf() {
        // Cliente existente com mesmo ID
        Cliente existente = new Cliente();
        existente.setIdCliente(1L);

        when(clienteRepositoryPort.findByCpf(cliente.getCpf())).thenReturn(existente);

        boolean resultado = clienteService.validarCpf(cliente.getCpf(), true, 1L);

        assertTrue(resultado);
    }

    @Test
    public void naoDeveValidarCpfQuandoAtualizarComCpfDeOutroCliente() {
        // CPF já em uso por outro cliente
        Cliente outro = new Cliente();
        outro.setIdCliente(2L);

        when(clienteRepositoryPort.findByCpf(cliente.getCpf())).thenReturn(outro);

        boolean resultado = clienteService.validarCpf(cliente.getCpf(), true, 1L);

        assertFalse(resultado);
    }



}
