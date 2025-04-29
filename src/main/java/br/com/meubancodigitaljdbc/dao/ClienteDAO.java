package br.com.meubancodigitaljdbc.dao;

import br.com.meubancodigitaljdbc.model.Cliente;
import br.com.meubancodigitaljdbc.model.Endereco;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Repository
public class ClienteDAO {


    private final DataSource dataSource;

    public ClienteDAO(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public void save(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (cpf, nome, data_nascimento, categoria, bairro, cep, complemento, logradouro, numero, localidade, uf) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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

    public Cliente buscarPorId(Long idCliente) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE id_cliente = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idCliente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setNome(rs.getString("nome"));
                cliente.setCpf(rs.getString("cpf"));
                return cliente;
            }
        }
        return null;
    }

    public Cliente findByCpf(String cpf) {
        String sql = "SELECT * FROM cliente WHERE cpf = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCliente(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Cliente> findAll() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientes;
    }

    public Optional<Cliente> findById(Long id) {
        String sql = "SELECT * FROM cliente WHERE id_cliente = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToCliente(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getLong("id_cliente"));
        cliente.setNome(rs.getString("nome"));
        cliente.setCpf(rs.getString("cpf"));
        cliente.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
        cliente.setCategoria(br.com.meubancodigitaljdbc.enuns.Categoria.valueOf(rs.getString("categoria")));

        Endereco endereco = new Endereco();
        endereco.setLogradouro(rs.getString("logradouro"));
        endereco.setNumero(rs.getInt("numero"));
        endereco.setBairro(rs.getString("bairro"));
        endereco.setLocalidade(rs.getString("localidade"));
        endereco.setUf(rs.getString("uf"));
        endereco.setCep(rs.getString("cep"));

        cliente.setEndereco(endereco);
        return cliente;
    }
}
