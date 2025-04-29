package br.com.meubancodigitaljdbc.dao;

import br.com.meubancodigitaljdbc.config.ConexaoJDBC;
import br.com.meubancodigitaljdbc.enuns.TipoConta;
import br.com.meubancodigitaljdbc.model.Cliente;
import br.com.meubancodigitaljdbc.model.Conta;
import br.com.meubancodigitaljdbc.model.ContaCorrente;
import br.com.meubancodigitaljdbc.model.ContaPoupanca;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
@Repository
public class ContaDAO {

    private DataSource dataSource;
    public Conta salvarConta(Conta conta) throws SQLException {
        String sql = "INSERT INTO conta (tipo_conta, id_conta, agencia, num_conta, cliente_id) VALUES (?, ?, ?, ?, ?)";


        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, conta.getTipoConta().name());
            stmt.setLong(2, conta.getIdConta());
            stmt.setDouble(3, conta.getAgencia());
            stmt.setString(4, conta.getNumConta());
            stmt.setLong(5, conta.getCliente().getIdCliente());

            stmt.executeUpdate();

            // Obtendo a chave gerada para o id da conta
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                conta.setIdConta(rs.getLong(1));
            }
        }
        return conta;
    }

    // Método para buscar conta por número
    public Conta buscarPorNumero(String numeroConta) throws SQLException {
        String sql = "SELECT * FROM conta WHERE num_conta = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numeroConta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearConta(rs);
            }
        }
        return null;
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
        String sql = "UPDATE conta SET saldo = ? WHERE id = ?";

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
        Cliente cliente = new Cliente(); // Você deve obter esse cliente por meio do ClienteDAO usando o cliente_id
        cliente.setIdCliente(rs.getLong("cliente_id"));

        Conta conta;
        if (tipo == TipoConta.CORRENTE) {
            conta = new ContaCorrente(cliente, rs.getInt("agencia"), rs.getString("num_conta"), tipo);
        } else {
            conta = new ContaPoupanca(cliente, rs.getInt("agencia"), rs.getString("num_conta"), tipo);
        }

        conta.setIdConta(rs.getLong("id"));
        conta.setSaldo(rs.getDouble("saldo"));
        return conta;
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
                        continue; // ignora este registro
                }

                conta.setIdConta(rs.getLong("id"));
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
