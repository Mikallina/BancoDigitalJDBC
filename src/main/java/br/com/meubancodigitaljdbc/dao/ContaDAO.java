package br.com.meubancodigitaljdbc.dao;

import br.com.meubancodigitaljdbc.enuns.Categoria;
import br.com.meubancodigitaljdbc.enuns.TipoConta;
import br.com.meubancodigitaljdbc.model.Cliente;
import br.com.meubancodigitaljdbc.model.Conta;
import br.com.meubancodigitaljdbc.model.ContaCorrente;
import br.com.meubancodigitaljdbc.model.ContaPoupanca;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ContaDAO {


    @Autowired
    private ClienteDAO clienteDAO;
    private final DataSource dataSource;

    public ContaDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Conta salvarConta(Conta conta) throws SQLException {

        Optional<Cliente> optionalCliente = clienteDAO.findById(conta.getCliente().getIdCliente());
        if (optionalCliente.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado com ID: " + conta.getCliente().getIdCliente());
        }
        Cliente cliente = optionalCliente.get();

        String sql = "INSERT INTO conta (tipo_conta, agencia, num_conta, saldo, cliente_id) VALUES (?, ?, ?, ?, ?)";


        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, conta.getTipoConta().name());
            stmt.setDouble(2, conta.getAgencia());
            stmt.setString(3, conta.getNumConta());
            stmt.setDouble(4, conta.getSaldo());
            stmt.setLong(5, conta.getCliente().getIdCliente());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    conta.setIdConta(generatedKeys.getLong(1));
                }
            }
        }

        return conta;
    }

    // Método para buscar conta por número
    public Conta buscarPorNumero(String numeroConta) throws SQLException {
        String sql = "SELECT c.*, cl.categoria FROM conta c JOIN cliente cl ON c.cliente_id = cl.id_cliente WHERE c.num_conta = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numeroConta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Conta conta = mapResultSetToConta(rs);
                System.out.println("→ Conta encontrada: " + conta.getNumConta() + " | Saldo do banco: " + conta.getSaldo());
                return conta;
            }
        }
        return null;
    }


    public Optional<Conta> findById(Long id) {
        String sql = "SELECT c.*, cl.categoria FROM conta c JOIN cliente cl ON c.cliente_id = cl.id_cliente WHERE c.id_conta = ?";
        if (id == null) {
            throw new IllegalArgumentException("ID da conta não pode ser null");
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToConta(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }


    private Conta mapResultSetToConta(ResultSet rs) throws SQLException {
        Conta conta = new ContaCorrente();
        conta.setTipoConta(TipoConta.valueOf(rs.getString("tipo_conta")));
        conta.setIdConta(rs.getLong("id_conta"));
        conta.setAgencia(rs.getInt("agencia"));
        conta.setNumConta(rs.getString("num_conta"));
        conta.setSaldo(rs.getDouble("saldo"));

        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getLong("cliente_id"));

        String categoriaStr = rs.getString("categoria");

        if (categoriaStr != null && !categoriaStr.isEmpty()) {
            cliente.setCategoria(Categoria.valueOf(categoriaStr));
        } else {
            throw new SQLException("Categoria não encontrada ou inválida.");
        }

        conta.setCliente(cliente);

        return conta;
    }

    // Método para buscar contas por cliente_id
    public List<Conta> buscarPorClienteId(Long clienteId) throws SQLException {
        List<Conta> contas = new ArrayList<>();
        String sql = "SELECT * FROM conta WHERE cliente_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, clienteId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                contas.add(mapearConta(rs));

            }
        }
        return contas;
    }


    // Método para atualizar o saldo da conta
    public void atualizarSaldo(Long idConta, double novoSaldo) throws SQLException {
        String sql = "UPDATE conta SET saldo = ? WHERE id_conta = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, novoSaldo);
            stmt.setLong(2, idConta);
            stmt.executeUpdate();
        }
    }

    // Método auxiliar para mapear o ResultSet para a entidade Conta
    private Conta mapearConta(ResultSet rs) throws SQLException {
        TipoConta tipo = TipoConta.valueOf(rs.getString("tipo_conta"));
        long clienteId = rs.getLong("cliente_id");

        // Busca o cliente associada à conta
        Optional<Cliente> optionalCliente = clienteDAO.findById(clienteId);

        if (optionalCliente.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado com ID: " + clienteId);
        }

        Cliente cliente = optionalCliente.get();

        // Verifica se o cliente não é nulo e se a categoria do cliente está presente
        if (cliente == null) {
            throw new RuntimeException("Cliente é nulo.");
        }
        if (cliente.getCategoria() == null) {
            throw new RuntimeException("Categoria do cliente está nula.");
        }

        Conta conta;

        // Mapeia a conta de acordo com o tipo
        if (tipo == TipoConta.CORRENTE) {
            conta = new ContaCorrente(cliente, rs.getInt("agencia"), rs.getString("num_conta"), tipo);
        } else if (tipo == TipoConta.POUPANCA) {
            conta = new ContaPoupanca(cliente, rs.getInt("agencia"), rs.getString("num_conta"), tipo);
        } else {
            throw new RuntimeException("Tipo de conta desconhecido: " + tipo);
        }

        // Define os dados da conta
        conta.setIdConta(rs.getLong("id_conta"));
        double saldo = rs.getDouble("saldo");
        conta.setSaldo(saldo);

        // Retorna a conta com o cliente corretamente associado
        return conta;
    }
    public Conta atualizarConta(Conta conta) throws SQLException {
        String sql = "UPDATE conta SET saldo = ?, agencia = ?, num_conta = ?, tipo_conta = ? WHERE id_conta = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, conta.getSaldo());
            stmt.setInt(2, conta.getAgencia());
            stmt.setString(3, conta.getNumConta());
            stmt.setString(4, conta.getTipoConta().name());
            stmt.setLong(5, conta.getIdConta());

            stmt.executeUpdate();
            return conta;
        }
    }


    // Método para buscar todas as contas
    public List<Conta> findAll() {

        List<Conta> contas = new ArrayList<>();
        String sql = "SELECT * FROM conta";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String tipoConta = rs.getString("tipo_conta");
                Conta conta = null;

                switch (tipoConta) {
                    case "CORRENTE":
                        conta = new ContaCorrente();
                        break;
                    case "POUPANCA":
                        conta = new ContaPoupanca();
                        break;
                    default:
                        System.out.println("Tipo de conta desconhecido: " + tipoConta);
                        continue;
                }

                conta.setIdConta(rs.getLong("id_conta"));
                conta.setNumConta(rs.getString("num_conta"));
                conta.setSaldo(rs.getDouble("saldo"));

                contas.add(conta);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contas;
    }


}
