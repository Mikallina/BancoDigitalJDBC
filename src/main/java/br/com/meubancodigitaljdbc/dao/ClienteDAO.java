package br.com.meubancodigitaljdbc.dao;

import br.com.meubancodigitaljdbc.mapper.ClienteRowMapper;
import br.com.meubancodigitaljdbc.model.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ClienteDAO {

    private final DataSource dataSource;
    private ClienteRowMapper rowMapper;
    @Autowired
    public ClienteDAO(DataSource dataSource, ClienteRowMapper rowMapper){
        this.dataSource = dataSource;
        this.rowMapper = rowMapper;
    }

    public void save(Cliente cliente) throws SQLException {

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL salvar_cliente(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {

            stmt.setString(1, cliente.getCpf());
            stmt.setString(2, cliente.getNome());
            stmt.setDate(3, Date.valueOf(cliente.getDataNascimento()));
            stmt.setString(4, cliente.getCategoria().name());
            stmt.setString(5, cliente.getEndereco().getBairro());
            stmt.setString(6, cliente.getEndereco().getCep());
            stmt.setString(7, cliente.getEndereco().getComplemento());
            stmt.setString(8, cliente.getEndereco().getLogradouro());
            stmt.setInt(9, cliente.getEndereco().getNumero());
            stmt.setString(10, cliente.getEndereco().getLocalidade());
            stmt.setString(11, cliente.getEndereco().getUf());
            stmt.registerOutParameter(12, java.sql.Types.BIGINT);

            stmt.executeUpdate();

            long idGerado = stmt.getLong(12);
            cliente.setIdCliente(idGerado);
        }
    }

    public Cliente findByCpf(String cpf) {

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL buscar_cpf_cliente(?)}")) {

            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rowMapper.mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Cliente> findAll() {
        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL buscar_todos_clientes()}");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clientes.add(rowMapper.mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientes;
    }

    public Optional<Cliente> findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser null");
        }

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL buscar_id_cliente(?)}")) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(rowMapper.mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public void deleteById(Long id) {

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL deletar_cliente(?)}")) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Cliente cliente) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL atualizar_cliente(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {

            stmt.setLong(1, cliente.getIdCliente());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getNome());
            stmt.setDate(4, Date.valueOf(cliente.getDataNascimento()));
            stmt.setString(5, cliente.getCategoria().name());
            stmt.setString(6, cliente.getEndereco().getBairro());
            stmt.setString(7, cliente.getEndereco().getCep());
            stmt.setString(8, cliente.getEndereco().getComplemento());
            stmt.setString(9, cliente.getEndereco().getLogradouro());
            stmt.setInt(10, cliente.getEndereco().getNumero());
            stmt.setString(11, cliente.getEndereco().getLocalidade());
            stmt.setString(12, cliente.getEndereco().getUf());

            stmt.executeUpdate();
        }
    }

}