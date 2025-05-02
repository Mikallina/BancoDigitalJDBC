package br.com.meubancodigitaljdbc.dao;

import br.com.meubancodigitaljdbc.model.CartaoCredito;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

@Repository
public class CartaoCreditoDAO {

    private final DataSource dataSource;

    public CartaoCreditoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public CartaoCredito salvarCartao(CartaoCredito cartaoCredito) throws SQLException {
        String sql = "INSERT INTO cartao_credito(data_compra, data_vencimento, limite_credito, limite_diario, pagamento, saldo_mes, taxa, id_cartao, dia_vencimento) VALUES (?,?,?,?,?,?,?,?,?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LocalDate dataCompra = cartaoCredito.getDataCompra();
            stmt.setDate(1, java.sql.Date.valueOf(dataCompra));
            LocalDate dataVencimento = cartaoCredito.getDataCompra();
            stmt.setDate(1, java.sql.Date.valueOf(dataVencimento));
            stmt.setDouble(3, cartaoCredito.getLimiteCredito());
            stmt.setDouble(4,cartaoCredito.getPagamento());
            stmt.setDouble(5,cartaoCredito.getSaldoMes());
            stmt.setDouble(6,cartaoCredito.getTaxa());
            stmt.setLong(7, cartaoCredito.getIdCartao());
            stmt.setString(8,cartaoCredito.getDiaVencimento());

            stmt.executeUpdate();
        }
        return cartaoCredito;
    }
}
