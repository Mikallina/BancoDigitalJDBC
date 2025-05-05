package br.com.meubancodigitaljdbc.mapper;

import br.com.meubancodigitaljdbc.dao.ClienteDAO;
import br.com.meubancodigitaljdbc.enuns.Categoria;
import br.com.meubancodigitaljdbc.enuns.TipoConta;
import br.com.meubancodigitaljdbc.model.Cliente;
import br.com.meubancodigitaljdbc.model.Conta;
import br.com.meubancodigitaljdbc.model.ContaCorrente;
import br.com.meubancodigitaljdbc.model.ContaPoupanca;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ContaRowMapper implements RowMapper<Conta> {

    @Autowired
    private ClienteDAO clienteDAO;

    @Override
    public Conta mapRow(ResultSet rs, int rowNum) throws SQLException {
        TipoConta tipoConta = TipoConta.valueOf(rs.getString("tipo_conta"));
        Conta conta;

        if (tipoConta == TipoConta.CORRENTE) {
            conta = new ContaCorrente();
        } else if (tipoConta == TipoConta.POUPANCA) {
            conta = new ContaPoupanca();
        } else {
            throw new SQLException("Tipo de conta desconhecido: " + tipoConta);
        }

        // Preenche os dados básicos da Conta
        conta.setIdConta(rs.getLong("id_conta"));
        conta.setTipoConta(tipoConta);
        conta.setAgencia(rs.getInt("agencia"));
        conta.setNumConta(rs.getString("num_conta"));
        conta.setSaldo(rs.getDouble("saldo"));

        // Recupera o Cliente
        long clienteId = rs.getLong("cliente_id");
        Optional<Cliente> optionalCliente = clienteDAO.findById(clienteId);

        if (optionalCliente.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado com ID: " + clienteId);
        }

        Cliente cliente = optionalCliente.get();

        // Verifica a categoria do Cliente
        String categoriaStr = rs.getString("categoria");
        if (categoriaStr != null && !categoriaStr.isEmpty()) {
            cliente.setCategoria(Categoria.valueOf(categoriaStr));
        } else {
            throw new SQLException("Categoria do cliente não encontrada ou inválida.");
        }

        // Atribui o Cliente à Conta
        conta.setCliente(cliente);

        return conta;
    }
}
