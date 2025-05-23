package br.com.meubancodigitaljdbc.dao;


import br.com.meubancodigitaljdbc.mapper.ContaRowMapper;
import br.com.meubancodigitaljdbc.model.Cliente;
import br.com.meubancodigitaljdbc.model.Conta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ContaDAO {

    private final ClienteDAO clienteDAO;
    private final DataSource dataSource;
    private final ContaRowMapper rowMapper;
    @Autowired
    public ContaDAO(ClienteDAO clienteDAO, DataSource dataSource, ContaRowMapper rowMapper) {
        this.clienteDAO = clienteDAO;
        this.dataSource = dataSource;
        this.rowMapper = rowMapper;
    }

    public Conta salvarConta(Conta conta) throws SQLException {

        Optional<Cliente> optionalCliente = clienteDAO.findById(conta.getCliente().getIdCliente());
        if (optionalCliente.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado com ID: " + conta.getCliente().getIdCliente());
        }
        Cliente cliente = optionalCliente.get();

              try (Connection conn = dataSource.getConnection();
                   CallableStatement stmt = conn.prepareCall("{CALL salvar_conta(?, ?, ?, ?, ?, ?)}")) {

            stmt.setString(1, conta.getTipoConta().name());
            stmt.setDouble(2, conta.getAgencia());
            stmt.setString(3, conta.getNumConta());
            stmt.setDouble(4, conta.getSaldo());
            stmt.setLong(5, conta.getCliente().getIdCliente());
            stmt.registerOutParameter(6, Types.BIGINT);
            stmt.executeUpdate();

           long idGerado = stmt.getLong(6);
           conta.setIdConta(idGerado);
        }

        return conta;
    }

    // Método para buscar conta por número
    public Conta buscarPorNumero(String numeroConta) throws SQLException {

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL buscar_conta_numero(?)}")) {

            stmt.setString(1, numeroConta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rowMapper.mapRow(rs);
            }
        }
        return null;
    }

    public Cliente buscarClientePorCpf(String cpf) {
        return clienteDAO.findByCpf(cpf);
    }

    public Conta buscarContaPorId(Long idConta) throws SQLException {

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL buscar_id_conta(?)}")) {

            stmt.setLong(1, idConta);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rowMapper.mapearconta(rs);
                } else {
                    return null;
                }
            }
        }
    }


    public Optional<Conta> findById(Long idConta) {
        if (idConta == null) {
            throw new IllegalArgumentException("ID da conta não pode ser null");
        }
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL buscar_id_conta_cat(?)}")) {

            stmt.setLong(1, idConta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(rowMapper.mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }


    // Método para buscar contas por cliente_id
    public List<Conta> buscarPorClienteId(Long clienteId) throws SQLException {
        List<Conta> contas = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL buscar_conta_cliente_id(?)}")) {
            stmt.setLong(1, clienteId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                contas.add(rowMapper.mapearconta(rs));

            }
        }
        return contas;
    }


    // Método para atualizar o saldo da conta
    public void atualizarSaldo(Long idConta, double novoSaldo) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL buscar_conta_cliente_id(?, ?)}")){

            stmt.setDouble(1, novoSaldo);
            stmt.setLong(2, idConta);
            stmt.executeUpdate();
        }
    }

    public Conta atualizarConta(Conta conta) throws SQLException {

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL atualizar_conta(?, ?, ?, ?, ?)}")){

            stmt.setDouble(1, conta.getSaldo());
            stmt.setInt(2, conta.getAgencia());
            stmt.setString(3, conta.getNumConta());
            stmt.setString(4, conta.getTipoConta().name());
            stmt.setLong(5, conta.getIdConta());

            stmt.executeUpdate();
            return conta;
        }
    }


/*

    // Método para buscar todas as contas
    public List<Conta> findAll() {

        List<Conta> contas = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL listar_contas()}");
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
    }*/

    public void deleteById(Long id) {

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL deletar_conta(?)}")) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}