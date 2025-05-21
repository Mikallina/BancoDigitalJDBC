package br.com.meubancodigitaljdbc.service;

import br.com.meubancodigitaljdbc.dao.ContaCorrenteDAO;
import br.com.meubancodigitaljdbc.dao.ContaDAO;
import br.com.meubancodigitaljdbc.dao.ContaPoupancaDAO;
import br.com.meubancodigitaljdbc.enuns.TipoConta;
import br.com.meubancodigitaljdbc.execptions.ClienteInvalidoException;
import br.com.meubancodigitaljdbc.execptions.ContaNaoEncontradaException;
import br.com.meubancodigitaljdbc.execptions.ContaNaoValidaException;
import br.com.meubancodigitaljdbc.execptions.OperacoesException;
import br.com.meubancodigitaljdbc.model.*;
import br.com.meubancodigitaljdbc.utils.ContaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@Service
public class ContaService {

    private final ContaDAO contaDAO;

    private final DataSource dataSource;

    private final TaxaService taxaService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContaService.class);

    @Autowired
    public ContaService(ContaDAO contaDAO, DataSource dataSource, TaxaService taxaService) {
        this.contaDAO = contaDAO;
        this.dataSource = dataSource;
        this.taxaService = taxaService;
    }


    public void salvarConta(Conta conta, boolean isAtualizar) throws ContaNaoValidaException {
        LOGGER.info("Iniciando a operação de salvar/atualizar a conta.");
        if (conta == null || conta.getNumConta() == null) {
            LOGGER.error("Conta ou número da conta não pode ser nulo.");
            throw new ContaNaoValidaException("Erro: Conta ou número da conta não pode ser nulo.");
        }

        try {
            if (isAtualizar) {
                contaDAO.atualizarConta(conta);
                LOGGER.info("Conta atualizada com sucesso: {}", conta.getNumConta());
            } else {
                contaDAO.salvarConta(conta);
                LOGGER.info("Conta atualizada com sucesso: {}", conta.getNumConta());
            }
        } catch (SQLException e) {
            throw new ContaNaoValidaException("Erro ao salvar ou atualizar a conta: " + e.getMessage(), e);
        }
    }

    public List<Conta> buscarContasPorCliente(Cliente cliente) throws SQLException {
        LOGGER.info("Buscando contas para o cliente com ID: {}", cliente.getIdCliente());
        return contaDAO.buscarPorClienteId(cliente.getIdCliente());
    }



    public Conta criarConta(Cliente cliente, int agencia, TipoConta tipoConta) throws SQLException{
        LOGGER.info("Criando conta para o cliente com ID: {}", cliente.getIdCliente());
        ContaUtils contaUtils = new ContaUtils();
        String numConta = contaUtils.gerarNumeroConta(agencia, tipoConta);
        Conta conta;

        if (tipoConta == TipoConta.CORRENTE) {
            conta = new ContaCorrente(cliente, agencia, numConta, tipoConta);
            LOGGER.info("Conta corrente criada com sucesso: {}", numConta);
        } else if (tipoConta == TipoConta.POUPANCA) {
            conta = new ContaPoupanca(cliente, agencia, numConta, tipoConta);
            LOGGER.info("Conta poupança criada com sucesso: {}", numConta);
        } else {
            LOGGER.error("Tipo de conta inválido: {}", tipoConta);
            throw new IllegalArgumentException("Tipo de conta inválido.");
        }

        return contaDAO.salvarConta(conta);
    }

    public boolean aplicarTaxaOuRendimento(Long idConta, TipoConta tipoConta, boolean aplicarTaxa) throws SQLException {
        LOGGER.info("Iniciando a aplicação de taxa ou rendimento na conta com ID: {}", idConta);
        Conta conta;
        try {
            conta = contaDAO.buscarContaPorId(idConta);
            LOGGER.info("Conta encontrada: {}", conta.getNumConta());
        } catch (SQLException e) {
            LOGGER.error("Erro ao buscar conta com ID: {}", idConta, e);

            return false;
        }


        double valorAplicado;

        // Verificação para aplicar a taxa de manutenção
        if (aplicarTaxa) {
            if (tipoConta == TipoConta.CORRENTE && conta instanceof ContaCorrente contaCorrente) {
                Cliente cliente = contaCorrente.getCliente();
                valorAplicado = taxaService.taxaManutencaoCC(cliente, contaCorrente);
                contaCorrente.setSaldo(contaCorrente.getSaldo() - valorAplicado);
                ContaCorrenteDAO contaCorrenteDAO = new ContaCorrenteDAO(dataSource);
                contaCorrenteDAO.atualizarConta(contaCorrente);
                LOGGER.info("Taxa de manutenção aplicada na conta corrente. Valor aplicado: {}", valorAplicado);
            } else {
                LOGGER.error("Este endpoint é apenas para contas correntes");
                throw new IllegalArgumentException("Este endpoint é apenas para contas correntes");
            }
        } else {
            if (tipoConta == TipoConta.POUPANCA && conta instanceof ContaPoupanca contaPoupanca) {
                Cliente cliente = contaPoupanca.getCliente();
                valorAplicado = taxaService.taxaManutencaoCP(cliente, contaPoupanca);
                contaPoupanca.setSaldo(contaPoupanca.getSaldo() + valorAplicado);
                ContaPoupancaDAO contaPoupancaDAO = new ContaPoupancaDAO(dataSource);
                contaPoupancaDAO.atualizarConta(contaPoupanca);
                LOGGER.info("Rendimento aplicado na conta poupança. Valor aplicado: {}", valorAplicado);

            } else {
                LOGGER.error("Este endpoint é apenas para contas poupanças");
                throw new IllegalArgumentException("Este endpoint é apenas para contas poupanças");
            }
        }

        try {
            salvarConta(conta, true);
            LOGGER.info("Conta salva com sucesso após aplicação de taxa ou rendimento.");
        } catch (ContaNaoValidaException e) {
            LOGGER.error("Erro ao salvar a conta após aplicação de taxa ou rendimento: {}", e.getMessage(), e);
            // Não re-lança a exceção, apenas registra o erro
        }


        return true;
    }




    public boolean realizarDeposito(String numContaDestino, double valor) throws OperacoesException, SQLException, ContaNaoValidaException {
        LOGGER.info("Iniciando a operação de depósito na conta destino: {}", numContaDestino);

        if (valor <= 0) {
            LOGGER.error("Valor do depósito não pode ser zero ou negativo. Valor informado: {}", valor);
            throw new OperacoesException("Valor do depósito não pode ser zero");
        }
        Conta conta = contaDAO.buscarPorNumero(numContaDestino);

        if (conta == null) {
            LOGGER.error("Conta não encontrada para o número: {}", numContaDestino);
            throw new OperacoesException("Conta Não encontrada");
        }
        double novoSaldo = conta.getSaldo() + valor;
        conta.setSaldo(novoSaldo);
        LOGGER.info("Depósito realizado com sucesso. Novo saldo da conta {}: {}", numContaDestino, novoSaldo);
        salvarConta(conta, true);

        return true;
    }

    public boolean realizarTransferencia(double valor, String numContaOrigem, String numContaDestino,
                                         boolean transferenciaPoupança, boolean transferenciaPix, boolean transferenciaOutrasContas) throws SQLException {
        LOGGER.info("Iniciando a operação de transferência de {} para a conta de destino: {}", valor, numContaDestino);

        Conta contaOrigem = contaDAO.buscarPorNumero(numContaOrigem);
        Conta contaDestino = contaDAO.buscarPorNumero(numContaDestino);

        if (contaOrigem == null) {
            LOGGER.error("Conta de origem não encontrada: {}", numContaOrigem);
            throw new IllegalArgumentException("Conta de origem não encontrada.");
        }

        if (valor <= 0) {
            LOGGER.error("Valor da transferência não pode ser zero ou negativo. Valor informado: {}", valor);
            throw new IllegalArgumentException("Valor não pode ser zero ou negativo.");
        }

        if (valor > contaOrigem.getSaldo()) {
            LOGGER.error("Saldo insuficiente na conta de origem. Saldo disponível: {}, valor solicitado: {}", contaOrigem.getSaldo(), valor);
            throw new IllegalArgumentException("Saldo insuficiente.");
        }

        if (transferenciaPoupança || transferenciaOutrasContas) {
            if (contaDestino == null) {
                throw new IllegalArgumentException("Conta de destino não encontrada.");
            }
            contaOrigem.setSaldo(contaOrigem.getSaldo() - valor);
            contaDestino.setSaldo(contaDestino.getSaldo() + valor);
            LOGGER.info("Transferência realizada entre contas. Conta origem: {}, Conta destino: {}", numContaOrigem, numContaDestino);
        }

        if (transferenciaPix) {
            contaOrigem.setSaldo(contaOrigem.getSaldo() - valor);
            LOGGER.info("Transferência realizada via PIX. Conta origem: {}", numContaOrigem);
        }

        contaDAO.atualizarSaldo(contaOrigem.getIdConta(), contaOrigem.getSaldo());
        LOGGER.info("Saldo atualizado para a conta origem (ID: {}): {}", contaOrigem.getIdConta(), contaOrigem.getSaldo());

        // 	Se houve alguma modificação na conta de destino, salvar também
        if (contaDestino != null && (transferenciaPoupança || transferenciaOutrasContas)) {
            contaDAO.atualizarSaldo(contaDestino.getIdConta(), contaDestino.getSaldo());
            LOGGER.info("Saldo atualizado para a conta destino ID: {}): {}", contaDestino.getIdConta(), contaDestino.getSaldo());
        }

        return true;
    }

    public Conta buscarContas(String conta) throws SQLException {
        LOGGER.info("Buscando conta pelo número: {}", conta);
        return contaDAO.buscarPorNumero(conta);
    }

    public Conta buscarContaPorClienteEConta(String cpf, String numConta) throws SQLException {
        LOGGER.info("Buscando conta para o cliente com CPF: {} e número da conta: {}", cpf, numConta);

        Cliente cliente = contaDAO.buscarClientePorCpf(cpf);
        if (cliente == null) {
            LOGGER.error("Cliente não encontrado com CPF: {}", cpf);
            throw new IllegalArgumentException("Cliente não encontrado.");
        }

        List<Conta> contas = contaDAO.buscarPorClienteId(cliente.getIdCliente());


        Conta conta = contas.stream()
                .filter(c -> c.getNumConta().equals(numConta))
                .findFirst()
                .orElseThrow(() -> {
                    LOGGER.error("Conta não encontrada para o cliente com CPF: {} e número de conta: {}", cpf, numConta);
                    return new IllegalArgumentException("Conta não encontrada.");
                });

        LOGGER.info("Conta encontrada: {}", conta.getNumConta());
        return conta;
    }

    public void deletarConta(Long contaId) throws ClienteInvalidoException {
        LOGGER.info("Tentando deletar conta com ID: {}", contaId);
        Optional<Conta> contaExistente = contaDAO.findById(contaId);

        if (contaExistente.isEmpty()) {
            LOGGER.warn("Conta com ID {} não encontrado para deleção.", contaId);
            throw new ClienteInvalidoException("Conta com ID " + contaId + " não encontrado.");
        }

        contaDAO.deleteById(contaId);
        LOGGER.info("Conta com ID {} deletado com sucesso", contaId);
    }

    public boolean realizarTransferenciaPoupanca(double valor, String numContaOrigem, String numContaDestino) throws SQLException {

        return realizarTransferencia(valor, numContaOrigem, numContaDestino, true, false, false);
    }

    public boolean realizarTransferenciaPIX(double valor, String numContaOrigem, String chaveDestino) throws SQLException {

        return realizarTransferencia(valor, numContaOrigem, chaveDestino, false, true, false);
    }

    public boolean realizarTransferenciaOutrasContas(double valor, String numContaOrigem, String numContaDestino) throws SQLException {

        return realizarTransferencia(valor, numContaOrigem, numContaDestino, false, false, true);
    }




}
