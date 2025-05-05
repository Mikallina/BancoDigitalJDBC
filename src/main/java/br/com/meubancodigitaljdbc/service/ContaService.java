package br.com.meubancodigitaljdbc.service;

import br.com.meubancodigitaljdbc.dao.ClienteDAO;
import br.com.meubancodigitaljdbc.dao.ContaCorrenteDAO;
import br.com.meubancodigitaljdbc.dao.ContaDAO;
import br.com.meubancodigitaljdbc.dao.ContaPoupancaDAO;
import br.com.meubancodigitaljdbc.enuns.TipoConta;
import br.com.meubancodigitaljdbc.execptions.ContaNaoEncontradaException;
import br.com.meubancodigitaljdbc.execptions.ContaNaoValidaException;
import br.com.meubancodigitaljdbc.execptions.OperacoesExceptions;
import br.com.meubancodigitaljdbc.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Service
public class ContaService {

    private final ContaDAO contaDAO;
    private final ClienteDAO clienteDAO;
    private final DataSource dataSource;

    @Autowired
    public ContaService(ContaDAO contaDAO, ClienteDAO clienteDAO, DataSource dataSource) {
        this.contaDAO = contaDAO;
        this.clienteDAO = clienteDAO;
        this.dataSource = dataSource;
    }
    double taxaRendimento = 0;

    public void salvarConta(Conta conta, boolean isAtualizar) throws ContaNaoValidaException {
        if (conta == null || conta.getNumConta() == null) {
            throw new ContaNaoValidaException("Erro: Conta ou número da conta não pode ser nulo.");
        }

        try {
            if (isAtualizar) {
                contaDAO.atualizarConta(conta);
            } else {
                contaDAO.salvarConta(conta);
            }
        } catch (SQLException e) {
            throw new ContaNaoValidaException("Erro ao salvar ou atualizar a conta: " + e.getMessage(), e);
        }
    }

    public List<Conta> buscarContasPorCliente(Cliente cliente) throws SQLException {
        return contaDAO.buscarPorClienteId(cliente.getIdCliente());
    }


    public String gerarNumeroConta(int agencia, TipoConta tipoConta) {
        StringBuilder conta = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            conta.append((int) (Math.random() * 10));
        }

        // Calcular o dígito verificador (8º número) usando módulo 11
        int soma = 0;
        for (int i = 0; i < conta.length(); i++) {
            soma += (conta.charAt(i) - '0') * (i + 2);
        }

        int dv = soma % 11;
        if (dv == 10) {
            dv = 0;
        }

        conta.append(dv);

        String tipoContaString = tipoConta.getTipoAbreviado();
        return String.format("%s-%04d-%s", tipoContaString, agencia, conta.toString());
    }

    public Conta criarConta(Cliente cliente, int agencia, TipoConta tipoConta) throws SQLException, ContaNaoEncontradaException {


        String numConta = gerarNumeroConta(agencia, tipoConta);
        Conta conta;

        if (tipoConta == TipoConta.CORRENTE) {
            conta = new ContaCorrente(cliente, agencia, numConta, tipoConta);
        } else if (tipoConta == TipoConta.POUPANCA) {
            conta = new ContaPoupanca(cliente, agencia, numConta, tipoConta);
        } else {
            throw new IllegalArgumentException("Tipo de conta inválido.");
        }

        return contaDAO.salvarConta(conta);
    }

    public boolean aplicarTaxaOuRendimento(Long idConta, TipoConta tipoConta, boolean aplicarTaxa) throws ContaNaoEncontradaException, SQLException {
        Conta conta;
        try {
            conta = buscarContaPorId(idConta);
        } catch (SQLException e) {
            throw new ContaNaoEncontradaException("Erro ao buscar conta", e);
        }

        if (conta == null) {
            throw new ContaNaoEncontradaException("Conta não encontrada.");
        }

        double valorAplicado;

        // Verificação para aplicar a taxa de manutenção
        if (aplicarTaxa) {
            if (tipoConta == TipoConta.CORRENTE && conta instanceof ContaCorrente contaCorrente) {
                Cliente cliente = contaCorrente.getCliente();
                valorAplicado = taxaManutencaoCC(cliente, contaCorrente);
                contaCorrente.setSaldo(contaCorrente.getSaldo() - valorAplicado);
                ContaCorrenteDAO contaCorrenteDAO = new ContaCorrenteDAO(dataSource);
                contaCorrenteDAO.atualizarConta(contaCorrente);
            } else {
                throw new IllegalArgumentException("Este endpoint é apenas para contas correntes");
            }
        } else {
            if (tipoConta == TipoConta.POUPANCA && conta instanceof ContaPoupanca contaPoupanca) {
                Cliente cliente = contaPoupanca.getCliente();
                valorAplicado = taxaManutencaoCP(cliente, contaPoupanca);  // Calcula o rendimento
                contaPoupanca.setSaldo(contaPoupanca.getSaldo() + valorAplicado);  // Atualiza o saldo
                ContaPoupancaDAO contaPoupancaDAO = new ContaPoupancaDAO(dataSource);
                contaPoupancaDAO.atualizarConta(contaPoupanca);  // Atualiza a conta poupança no banco
            } else {
                throw new IllegalArgumentException("Este endpoint é apenas para contas poupanças");
            }
        }

        // No final, salvamos a conta no banco de dados, para garantir que as mudanças sejam persistidas
        try {
            salvarConta(conta, true);
        } catch (ContaNaoValidaException e) {
            throw new RuntimeException("Erro ao salvar conta", e);
        }

        return true;
    }

    public Conta buscarContaPorId(Long idConta) throws SQLException {
        String sql = "SELECT * FROM conta WHERE id_conta = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idConta);  // Definindo o valor do idConta na consulta

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearConta(rs);
                } else {
                    return null;
                }
            }
        }
    }

    private Conta mapearConta(ResultSet rs) throws SQLException {
        Long idConta = rs.getLong("id_conta");
        String numConta = rs.getString("num_conta");
        double saldo = rs.getDouble("saldo");
        int agencia = rs.getInt("agencia");
        String tipoContaStr = rs.getString("tipo_conta");
        Long clienteId = rs.getLong("cliente_id");

        TipoConta tipoConta = TipoConta.valueOf(tipoContaStr);

        // Buscando o cliente
        Cliente cliente = clienteDAO.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + clienteId));

        Conta conta;

        if (tipoConta == TipoConta.CORRENTE) {
            ContaCorrente contaCorrente = new ContaCorrente(cliente, agencia, numConta, tipoConta);
            contaCorrente.setIdConta(idConta);
            contaCorrente.setSaldo(saldo);

            // opcional: buscar taxa_manutencao da tabela conta_corrente se necessário
            // contaCorrente.setTaxaManutencao(...)

            conta = contaCorrente;
        } else if (tipoConta == TipoConta.POUPANCA) {
            ContaPoupanca contaPoupanca = new ContaPoupanca(cliente, agencia, numConta, tipoConta);
            contaPoupanca.setIdConta(idConta);
            contaPoupanca.setSaldo(saldo);

            // opcional: buscar taxa_rendimento da tabela conta_poupanca se necessário
            // contaPoupanca.setTaxaRendimento(...)

            conta = contaPoupanca;
        } else {
            throw new SQLException("Tipo de conta desconhecido: " + tipoContaStr);
        }

        return conta;
    }



    public double taxaManutencaoCC(Cliente cliente, ContaCorrente contaC) {
        double taxaManutencao = 0;

        if (cliente.getCategoria().getDescricao().equals("Comum")) {

            taxaManutencao = 12;

        } else if (cliente.getCategoria().getDescricao().equals("Super")) {

            taxaManutencao = 8;

        } else {
            taxaManutencao = 2;
        }

        if (contaC.getSaldo() < taxaManutencao) {
            throw new IllegalArgumentException("Saldo insuficiente para aplicar a taxa de manutenção");
        }

        contaC.setTaxaManutencao(taxaManutencao);
        return taxaManutencao;
    }

    public double taxaManutencaoCP(Cliente cliente, ContaPoupanca contaP) {
        double saldoAtual = contaP.getSaldo();

        if (cliente.getCategoria().getDescricao().equals("Comum")) {

            taxaRendimento = 0.5;

        } else if (cliente.getCategoria().getDescricao().equals("Super")) {

            taxaRendimento = 0.7;

        } else {
            taxaRendimento = 0.9;
        }

        double taxaMensal = taxaRendimento / 12;
        double saldoRendimento = saldoAtual * Math.pow(1 + (taxaMensal / 100), 1);
        double rendimentoMensal = saldoRendimento - saldoAtual;

        contaP.setTaxaRendimento(taxaRendimento);

        return rendimentoMensal;
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

    public boolean realizarDeposito(String numContaDestino, double valor) throws OperacoesExceptions, SQLException, ContaNaoValidaException {
        if (valor <= 0) {
            throw new OperacoesExceptions("Valor do depósito não pode ser zero");
        }
        Conta conta = contaDAO.buscarPorNumero(numContaDestino);

        if (conta == null) {
            throw new OperacoesExceptions("Conta Não encontrada");
        }
        double novoSaldo = conta.getSaldo() + valor;
        conta.setSaldo(novoSaldo);
        salvarConta(conta, true);

        return true;
    }

    public boolean realizarTransferencia(double valor, String numContaOrigem, String numContaDestino,
                                         boolean transferenciaPoupança, boolean transferenciaPix, boolean transferenciaOutrasContas) throws SQLException {

        Conta contaOrigem = contaDAO.buscarPorNumero(numContaOrigem);
        Conta contaDestino = contaDAO.buscarPorNumero(numContaDestino);

        if (contaOrigem == null) {
            throw new IllegalArgumentException("Conta de origem não encontrada.");
        }

        if (valor <= 0) {
            throw new IllegalArgumentException("Valor não pode ser zero ou negativo.");
        }

        if (valor > contaOrigem.getSaldo()) {
            throw new IllegalArgumentException("Saldo insuficiente.");
        }

        if (transferenciaPoupança || transferenciaOutrasContas) {
            if (contaDestino == null) {
                throw new IllegalArgumentException("Conta de destino não encontrada.");
            }
            contaOrigem.setSaldo(contaOrigem.getSaldo() - valor);
            contaDestino.setSaldo(contaDestino.getSaldo() + valor);
        }

        if (transferenciaPix) {
            contaOrigem.setSaldo(contaOrigem.getSaldo() - valor);
        }

        contaDAO.atualizarSaldo(contaOrigem.getIdConta(), contaOrigem.getSaldo());

        // 	Se houve alguma modificação na conta de destino, salvar também
        if (contaDestino != null && (transferenciaPoupança || transferenciaOutrasContas)) {
            contaDAO.atualizarSaldo(contaDestino.getIdConta(), contaDestino.getSaldo());
        }

        return true;
    }

    public Conta buscarContas(String conta) throws SQLException {
        return contaDAO.buscarPorNumero(conta);
    }

    public Conta buscarContaPorClienteEConta(String cpf, String numConta) throws SQLException {
        // Busca o cliente com o CPF fornecido
        Cliente cliente = buscarClientePorCpf(cpf);  // Assumindo que existe um método para buscar cliente pelo CPF
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }

        // Agora, busca as contas desse cliente pelo id
        List<Conta> contas = contaDAO.buscarPorClienteId(cliente.getIdCliente());

        // Filtra a conta com base no número da conta
        return contas.stream()
                .filter(c -> c.getNumConta().equals(numConta))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada."));
    }

    public Cliente buscarClientePorCpf(String cpf) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE cpf = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setIdCliente(rs.getLong("id_cliente"));
                    cliente.setNome(rs.getString("nome"));
                    cliente.setCpf(rs.getString("cpf"));
                    // Preencher outras propriedades de Cliente
                    return cliente;
                } else {
                    return null;  // Retorna null se não encontrar o cliente
                }
            }
        }
    }


}
