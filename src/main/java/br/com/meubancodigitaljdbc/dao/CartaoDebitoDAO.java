package br.com.meubancodigitaljdbc.dao;

import br.com.meubancodigitaljdbc.model.CartaoDebito;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
public class CartaoDebitoDAO {
    private final DataSource dataSource;

    public CartaoDebitoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public CartaoDebito salvarCartao(CartaoDebito cartaoDebito) throws SQLException {
        String sql = "INSERT INTO cartao_debito(limite_diario, taxa, total_pgto_hoje, id_cartao) VALUES (?,?,?,?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, cartaoDebito.getLimiteDiario());
            stmt.setDouble(2, cartaoDebito.getTaxa());
            stmt.setDouble(3, cartaoDebito.getTotalPgtoHoje());
            stmt.setLong(4, cartaoDebito.getIdCartao());

            stmt.executeUpdate();
        }
        return cartaoDebito;
    }

}


