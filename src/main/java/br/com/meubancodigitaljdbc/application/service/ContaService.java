package br.com.meubancodigitaljdbc.application.service;


import br.com.meubancodigitaljdbc.application.domain.enuns.TipoConta;
import br.com.meubancodigitaljdbc.application.domain.exceptions.ClienteInvalidoException;
import br.com.meubancodigitaljdbc.application.domain.exceptions.ContaNaoValidaException;
import br.com.meubancodigitaljdbc.application.domain.exceptions.OperacoesException;
import br.com.meubancodigitaljdbc.application.domain.model.Cliente;
import br.com.meubancodigitaljdbc.application.domain.model.Conta;
import br.com.meubancodigitaljdbc.application.domain.model.ContaCorrente;
import br.com.meubancodigitaljdbc.application.domain.model.ContaPoupanca;
import br.com.meubancodigitaljdbc.application.ports.input.usecases.ContaUseCase;
import br.com.meubancodigitaljdbc.application.ports.output.repository.ContaCorrenteRepositoryPort;
import br.com.meubancodigitaljdbc.application.ports.output.repository.ContaPoupancaRepository;
import br.com.meubancodigitaljdbc.application.ports.output.repository.ContaRepositoryPort;
import br.com.meubancodigitaljdbc.utils.ContaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@Service
public class ContaService implements ContaUseCase {

    private final ContaRepositoryPort contaRepositoryPort;

    private final TaxaService taxaService;

    private final ContaCorrenteRepositoryPort contaCorrenteRepository;

    private final ContaPoupancaRepository contaPoupancaRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContaService.class);


    public ContaService(ContaRepositoryPort contaRepositoryPort, TaxaService taxaService, ContaCorrenteRepositoryPort contaCorrenteRepository, ContaPoupancaRepository contaPoupancaRepository) {
        this.contaRepositoryPort = contaRepositoryPort;
        this.taxaService = taxaService;
        this.contaCorrenteRepository = contaCorrenteRepository;
        this.contaPoupancaRepository = contaPoupancaRepository;
    }


    public void salvarConta(Conta conta, boolean isAtualizar) throws ContaNaoValidaException {
        LOGGER.info("Iniciando a operação de salvar/atualizar a conta.");
        if (conta == null || conta.getNumConta() == null) {
            LOGGER.error("Conta ou número da conta não pode ser nulo.");
            throw new ContaNaoValidaException("Erro: Conta ou número da conta não pode ser nulo.");
        }

        try {
            if (isAtualizar) {
                contaRepositoryPort.atualizarConta(conta);
                LOGGER.info("Conta atualizada com sucesso: {}", conta.getNumConta());
            } else {
                contaRepositoryPort.salvarConta(conta);
                LOGGER.info("Conta atualizada com sucesso: {}", conta.getNumConta());
            }
        } catch (SQLException e) {
            throw new ContaNaoValidaException("Erro ao salvar ou atualizar a conta: " + e.getMessage(), e);
        }
    }

    public List<Conta> buscarContasPorCliente(Cliente cliente) throws SQLException {
        LOGGER.info("Buscando contas para o cliente com ID: {}", cliente.getIdCliente());
        return contaRepositoryPort.buscarPorClienteId(cliente.getIdCliente());
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

        return contaRepositoryPort.salvarConta(conta);
    }

    public boolean aplicarTaxaOuRendimento(Long idConta, TipoConta tipoConta, boolean aplicarTaxa) throws Exception {
        LOGGER.info("Iniciando a aplicação de taxa na conta com ID: {}", idConta);
        Conta conta;
        try {
            conta = contaRepositoryPort.buscarContaPorId(idConta);
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
                contaCorrenteRepository.atualizarConta(contaCorrente);
                LOGGER.info("Taxa de manutenção aplicada na conta corrente. Valor aplicado: {}", valorAplicado);
            }
        } else {
            if (tipoConta == TipoConta.POUPANCA && conta instanceof ContaPoupanca contaPoupanca) {
                Cliente cliente = contaPoupanca.getCliente();
                valorAplicado = taxaService.taxaManutencaoCP(cliente, contaPoupanca);
                contaPoupanca.setSaldo(contaPoupanca.getSaldo() + valorAplicado);
                contaPoupancaRepository.atualizarConta(contaPoupanca);
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

        }


        return true;
    }




    public boolean realizarDeposito(String numContaDestino, double valor) throws OperacoesException, SQLException, ContaNaoValidaException {
        LOGGER.info("Iniciando a operação de depósito na conta destino: {}", numContaDestino);

        if (valor <= 0) {
            LOGGER.error("Valor do depósito não pode ser zero ou negativo. Valor informado: {}", valor);
            throw new OperacoesException("Valor do depósito não pode ser zero");
        }
        Conta conta = contaRepositoryPort.buscarPorNumero(numContaDestino);

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
                                         boolean transferenciaPoupanca, boolean transferenciaPix, boolean transferenciaOutrasContas) throws SQLException {
        LOGGER.info("Iniciando a operação de transferência de {} para a conta de destino: {}", valor, numContaDestino);

        Conta contaOrigem = contaRepositoryPort.buscarPorNumero(numContaOrigem);
        Conta contaDestino = contaRepositoryPort.buscarPorNumero(numContaDestino);

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

        if (transferenciaPoupanca || transferenciaOutrasContas) {
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

        LOGGER.info("Saldo atualizado para a conta origem (ID: {}): {}", contaOrigem.getIdConta(), contaOrigem.getSaldo());

        if (contaDestino != null && (transferenciaPoupanca || transferenciaOutrasContas)) {
            contaRepositoryPort.atualizarSaldo(contaDestino.getIdConta(), contaDestino.getSaldo());
            LOGGER.info("Saldo atualizado para a conta destino ID: {}): {}", contaDestino.getIdConta(), contaDestino.getSaldo());
        }
        contaRepositoryPort.atualizarSaldo(contaOrigem.getIdConta(),contaOrigem.getSaldo());
        return true;
    }

    public Conta buscarContas(String conta) throws SQLException {
        LOGGER.info("Buscando conta pelo número: {}", conta);
        return contaRepositoryPort.buscarPorNumero(conta);
    }

    public Conta buscarContaPorClienteEConta(String cpf, String numConta) throws SQLException {
        LOGGER.info("Buscando conta para o cliente com CPF: {} e número da conta: {}", cpf, numConta);

        Cliente cliente = contaRepositoryPort.buscarClientePorCpf(cpf);
        if (cliente == null) {
            LOGGER.error("Cliente não encontrado com CPF: {}", cpf);
            throw new IllegalArgumentException("Cliente não encontrado.");
        }

        List<Conta> contas = contaRepositoryPort.buscarPorClienteId(cliente.getIdCliente());


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
        Optional<Conta> contaExistente = contaRepositoryPort.findById(contaId);

        if (contaExistente.isEmpty()) {
            LOGGER.warn("Conta com ID {} não encontrado para deleção.", contaId);
            throw new ClienteInvalidoException("Conta com ID " + contaId + " não encontrado.");
        }

        contaRepositoryPort.deleteById(contaId);
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
