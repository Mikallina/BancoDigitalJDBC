package service;

import br.com.meubancodigitaljdbc.application.domain.enuns.TipoConta;
import br.com.meubancodigitaljdbc.application.domain.exceptions.ContaNaoValidaException;
import br.com.meubancodigitaljdbc.application.domain.exceptions.OperacoesException;
import br.com.meubancodigitaljdbc.application.domain.model.Cliente;
import br.com.meubancodigitaljdbc.application.domain.model.Conta;
import br.com.meubancodigitaljdbc.application.domain.model.ContaCorrente;
import br.com.meubancodigitaljdbc.application.domain.model.ContaPoupanca;
import br.com.meubancodigitaljdbc.application.ports.output.repository.ContaCorrenteRepositoryPort;
import br.com.meubancodigitaljdbc.application.ports.output.repository.ContaPoupancaRepository;
import br.com.meubancodigitaljdbc.application.ports.output.repository.ContaRepositoryPort;
import br.com.meubancodigitaljdbc.application.service.ContaService;
import br.com.meubancodigitaljdbc.application.service.TaxaService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ContaServiceTest {

    @Mock
    private ContaRepositoryPort contaRepositoryPort;

    @Mock
    private TaxaService taxaService;

    @Mock
    private ContaCorrenteRepositoryPort contaCorrenteRepository;

    @Mock
    private ContaPoupancaRepository contaPoupancaRepository;

    @InjectMocks
    private ContaService contaService;

    @Test
    public void deveSalvarContaNovaComSucesso() throws Exception {
        Conta conta = new ContaCorrente();
        conta.setNumConta("123");

        contaService.salvarConta(conta, false);

        verify(contaRepositoryPort).salvarConta(conta);
    }

    @Test
    public void deveAtualizarContaComSucesso() throws Exception {
        Conta conta = new ContaCorrente();
        conta.setNumConta("123");

        contaService.salvarConta(conta, true);

        verify(contaRepositoryPort).atualizarConta(conta);
    }

    @Test
    public void deveLancarExcecaoQuandoContaNula() {
        Exception exception = assertThrows(ContaNaoValidaException.class, () -> contaService.salvarConta(null, false));

        assertTrue(exception.getMessage().contains("não pode ser nulo"));
    }

    @Test
    public void deveCriarContaCorrente() throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);

        when(contaRepositoryPort.salvarConta(any())).thenReturn(new ContaCorrente());

        Conta conta = contaService.criarConta(cliente, 1, TipoConta.CORRENTE);

        assertNotNull(conta);
        verify(contaRepositoryPort).salvarConta(any());
    }

    @Test
    public void deveAplicarTaxaContaCorrente() throws Exception {
        ContaCorrente conta = new ContaCorrente();
        conta.setNumConta("123");
        conta.setSaldo(500);
        conta.setCliente(new Cliente());

        when(contaRepositoryPort.buscarContaPorId(1L)).thenReturn(conta);
        when(taxaService.taxaManutencaoCC(any(), eq(conta))).thenReturn(50.0);

        boolean resultado = contaService.aplicarTaxaOuRendimento(1L, TipoConta.CORRENTE, true);

        assertTrue(resultado);
        verify(contaCorrenteRepository).atualizarConta(conta);
    }

    @Test
    public void deveAplicarRendimentoContaPoupanca() throws Exception {
        ContaPoupanca conta = new ContaPoupanca();
        conta.setNumConta("123");
        conta.setSaldo(1000);
        conta.setCliente(new Cliente());

        when(contaRepositoryPort.buscarContaPorId(1L)).thenReturn(conta);
        when(taxaService.taxaManutencaoCP(any(), eq(conta))).thenReturn(100.0);

        boolean resultado = contaService.aplicarTaxaOuRendimento(1L, TipoConta.POUPANCA, false);

        assertTrue(resultado);
        verify(contaPoupancaRepository).atualizarConta(conta);
    }

    @Test
    public void deveRealizarDepositoComSucesso() throws Exception {
        Conta conta = new ContaCorrente();
        conta.setNumConta("123");
        conta.setSaldo(100.0);

        when(contaRepositoryPort.buscarPorNumero("123")).thenReturn(conta);

        boolean resultado = contaService.realizarDeposito("123", 200.0);

        assertTrue(resultado);
        assertEquals(300.0, conta.getSaldo());
        verify(contaRepositoryPort).buscarPorNumero("123");
        verify(contaRepositoryPort).atualizarConta(conta); // ou salvarConta, dependendo da sua regra
    }

    @Test
    public void deveLancarExcecaoDepositoInvalido() {
        Exception exception = assertThrows(OperacoesException.class, () -> contaService.realizarDeposito("123", 0.0));

        assertTrue(exception.getMessage().contains("não pode ser zero"));
    }

    @Test
    public void deveRealizarTransferenciaEntreContas() throws SQLException {
        Conta contaOrigem = new ContaCorrente();
        contaOrigem.setNumConta("111");
        contaOrigem.setSaldo(1000);
        contaOrigem.setIdConta(1L);

        Conta contaDestino = new ContaCorrente();
        contaDestino.setNumConta("222");
        contaDestino.setSaldo(500);
        contaDestino.setIdConta(2L);

        when(contaRepositoryPort.buscarPorNumero("111")).thenReturn(contaOrigem);
        when(contaRepositoryPort.buscarPorNumero("222")).thenReturn(contaDestino);

        boolean resultado = contaService.realizarTransferencia(200, "111", "222", true, false, false);

        assertTrue(resultado);
        assertEquals(800, contaOrigem.getSaldo());
        assertEquals(700, contaDestino.getSaldo());

        verify(contaRepositoryPort).atualizarSaldo(1L, 800);
        verify(contaRepositoryPort).atualizarSaldo(2L, 700);
    }

    @Test
    public void deveLancarExcecaoTransferenciaSaldoInsuficiente() throws SQLException {
        Conta contaOrigem = new ContaCorrente();
        contaOrigem.setNumConta("111");
        contaOrigem.setSaldo(100);

        when(contaRepositoryPort.buscarPorNumero("111")).thenReturn(contaOrigem);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> contaService.realizarTransferencia(200, "111", "222", true, false, false));

        assertEquals("Saldo insuficiente.", exception.getMessage());
    }
}
