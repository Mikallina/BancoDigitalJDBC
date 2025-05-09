package br.com.meubancodigitaljdbc.dao;

import br.com.meubancodigitaljdbc.mapper.ClienteRowMapper;
import br.com.meubancodigitaljdbc.model.Cliente;
import br.com.meubancodigitaljdbc.sql.ClienteSql;
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

    public ClienteDAO(DataSource dataSource, ClienteRowMapper rowMapper){
        this.dataSource = dataSource;
        this.rowMapper = rowMapper;
    }

    public void save(Cliente cliente) throws SQLException {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(ClienteSql.INSERIR_CLIENTE, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getCpf());
            stmt.setString(2, cliente.getNome());
            stmt.setDate(3, Date.valueOf(cliente.getDataNascimento()));
            stmt.setString(4, cliente.getCategoria().name());
            stmt.setString(5, cliente.getEndereco().getBairro());
            stmt.setString(6, cliente.getEndereco().getCep());
            stmt.setString(7,cliente.getEndereco().complemento());
            stmt.setString(8, cliente.getEndereco().getLogradouro());
            stmt.setInt(9, cliente.getEndereco().getNumero());
            stmt.setString(10, cliente.getEndereco().getLocalidade());
            stmt.setString(11, cliente.getEndereco().getUf());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setIdCliente(generatedKeys.getLong(1));
                }
            }
        }
    }

    public Cliente findByCpf(String cpf) {


        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(ClienteSql.BUSCAR_POR_CPF)) {

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

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(ClienteSql.BUSCAR_TODOS);
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


        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(ClienteSql.BUSCAR_POR_ID)) {

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

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(ClienteSql.DELETAR_CLIENTE)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}