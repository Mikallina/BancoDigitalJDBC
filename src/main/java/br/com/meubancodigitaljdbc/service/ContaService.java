package br.com.meubancodigitaljdbc.service;

import br.com.meubancodigitaljdbc.dao.ClienteDAO;
import br.com.meubancodigitaljdbc.dao.ContaDAO;
import br.com.meubancodigitaljdbc.enuns.TipoConta;
import br.com.meubancodigitaljdbc.model.Cliente;
import br.com.meubancodigitaljdbc.model.Conta;
import br.com.meubancodigitaljdbc.model.ContaCorrente;
import br.com.meubancodigitaljdbc.model.ContaPoupanca;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;


@Service
public class ContaService {

    @Autowired
    private ContaDAO contaDAO;

    @Autowired
    private ClienteDAO clienteDAO;
    double taxaRendimento = 0;

    public void salvarConta(Cliente cliente, Conta conta, TipoConta tipoConta) throws SQLException {
        if (conta.getNumConta() == null) {
            throw new IllegalArgumentException("Erro: O número da conta é obrigatório.");
        }
        if (cliente == null) {
            throw new IllegalArgumentException("Erro: Cliente inválido");
        }

        if (conta instanceof ContaCorrente) {
            taxaManutencaoCC(cliente, tipoConta, (ContaCorrente) conta);
        }

        if (conta instanceof ContaPoupanca) {
            taxaManutencaoCP(cliente, tipoConta, (ContaPoupanca) conta);
        }

        contaDAO.salvarConta(conta);

        System.out.println("Conta salva com sucesso: " + conta.getNumConta());
    }

    public List<Conta> buscarContasPorCliente(Cliente cliente) throws SQLException {
        return contaDAO.buscarPorClienteId(cliente.getIdCliente());
    }

    public void listarContas() {
        List<Conta> contas = contaDAO.findAll();

        if (contas.isEmpty()) {
            System.out.println("Nenhuma conta cadastrada");
        } else {
            for (Conta conta : contas) {
                System.out.println("Conta: " + conta.getNumConta());
            }
        }
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

    public Conta criarConta(Cliente cliente, int agencia, TipoConta tipoConta) throws SQLException {
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

    public boolean aplicarTaxaOuRendimento(Long idConta, TipoConta tipoConta, boolean aplicarTaxa) throws SQLException {
        Conta conta = (Conta) contaDAO.buscarPorClienteId(idConta);

        double valorAplicado;

        if (aplicarTaxa) {
            if (!(conta instanceof ContaCorrente)) {
                throw new IllegalArgumentException("Este endpoint é apenas para contas correntes");
            }

            ContaCorrente contaCorrente = (ContaCorrente) conta;
            Cliente cliente = contaCorrente.getCliente();
            valorAplicado = taxaManutencaoCC(cliente, tipoConta, contaCorrente);
            contaCorrente.setSaldo(contaCorrente.getSaldo() - valorAplicado);

        } else {
            if (!(conta instanceof ContaPoupanca)) {
                throw new IllegalArgumentException("Este endpoint é apenas para contas poupanças");
            }

            ContaPoupanca contaPoupanca = (ContaPoupanca) conta;
            Cliente cliente = contaPoupanca.getCliente();
            valorAplicado = taxaManutencaoCP(cliente, tipoConta, contaPoupanca);
            contaPoupanca.setSaldo(contaPoupanca.getSaldo() + valorAplicado);
        }

        contaDAO.salvarConta(conta);
        return true;
    }

    public double taxaManutencaoCC(Cliente cliente, TipoConta tipoConta, ContaCorrente contaC) {
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

    public double taxaManutencaoCP(Cliente cliente, TipoConta tipoConta, ContaPoupanca contaP) {
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

        System.out.println("Saldo atual: " + saldoAtual);
        System.out.println("Taxa mensal: " + taxaMensal);
        System.out.println("Rendimento mensal: " + rendimentoMensal);

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

    public double obterSaldo(String cpf) {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean realizarDeposito(String numContaDestino, double valor) throws SQLException {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor do depósito não pode ser zero");
        }

        Conta conta = contaDAO.buscarPorNumero(numContaDestino);
        System.out.println("Número da conta recebido no backend: " + numContaDestino);
        if (conta == null) {
            throw new RuntimeException("Conta Não encontrada");
        }

        double novoSaldo = conta.getSaldo() + valor;
        conta.setSaldo(novoSaldo);
        contaDAO.salvarConta(conta);

        return true;
    }

    public boolean realizarTransferencia(double valor, String numContaOrigem, String numContaDestino,
                                         boolean transferenciaPoupança, boolean transferenciaPix, boolean transferenciaOutrasContas) throws SQLException {

        Conta contaOrigem = contaDAO.buscarPorNumero(numContaOrigem);
        Conta contaDestino = contaDAO.buscarPorNumero(numContaDestino);
        System.out.println("Saldo atual da conta origem: " + contaOrigem.getSaldo() + numContaOrigem);
        System.out.println("Valor a transferir: " + valor);


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
        Cliente cliente = clienteDAO.findByCpf(cpf);

        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }

        // Busca as contas do cliente
        List<Conta> contas = contaDAO.buscarPorClienteId(cliente.getIdCliente());

        // Filtra a conta com base no número da conta
        return contas.stream().filter(c -> c.getNumConta().equals(numConta)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada."));
    }

}
