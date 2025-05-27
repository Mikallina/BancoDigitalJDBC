package br.com.meubancodigitaljdbc.mapper;

import br.com.meubancodigitaljdbc.enuns.Categoria;
import br.com.meubancodigitaljdbc.model.Cliente;
import br.com.meubancodigitaljdbc.model.Endereco;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ClienteRowMapper implements RowMapper <Cliente> {


    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getLong("id_cliente"));
        cliente.setCpf(rs.getString("cpf"));
        cliente.setNome(rs.getString("nome"));
        cliente.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
        cliente.setCategoria(Categoria.valueOf(rs.getString("categoria")));

        Endereco endereco = new Endereco();
        endereco.setBairro(rs.getString("bairro"));
        endereco.setCep(rs.getString("cep"));
        endereco.setComplemento(rs.getString("complemento"));
        endereco.setLogradouro(rs.getString("logradouro"));
        endereco.setNumero(rs.getInt("numero"));
        endereco.setLocalidade(rs.getString("localidade"));
        endereco.setUf(rs.getString("uf"));


        cliente.setEndereco(endereco);
        return cliente;
    }


    @Override
    public Cliente mapRow(ResultSet rs, int rowNum) throws SQLException {
        return mapResultSetToCliente(rs);
    }

    public Cliente mapRow(ResultSet rs) throws SQLException {
        return mapResultSetToCliente(rs);
    }
}
