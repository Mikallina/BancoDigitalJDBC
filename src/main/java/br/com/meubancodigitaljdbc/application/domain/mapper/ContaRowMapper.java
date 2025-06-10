package br.com.meubancodigitaljdbc.application.domain.mapper;

import br.com.meubancodigitaljdbc.adapters.output.dao.ClienteDAO;
import br.com.meubancodigitaljdbc.application.domain.enuns.Categoria;
import br.com.meubancodigitaljdbc.application.domain.enuns.TipoConta;
import br.com.meubancodigitaljdbc.application.domain.model.Cliente;
import br.com.meubancodigitaljdbc.application.domain.model.Conta;
import br.com.meubancodigitaljdbc.application.domain.model.ContaCorrente;
import br.com.meubancodigitaljdbc.application.domain.model.ContaPoupanca;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
@Component
public class ContaRowMapper implements RowMapper<Conta> {

    @Autowired
    private ClienteDAO clienteDAO;


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


    @Override
    public Conta mapRow(ResultSet rs, int rowNum) throws SQLException {
        return mapResultSetToConta(rs);
    }

    public Conta mapRow(ResultSet rs) throws SQLException {
        return mapResultSetToConta(rs);
    }

    public Conta mapearconta(ResultSet rs) throws  SQLException{
        return mapearConta(rs);
    }
}
