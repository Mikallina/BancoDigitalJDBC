package br.com.meubancodigitaljdbc.dao;

import br.com.meubancodigitaljdbc.model.ContaCorrente;
import br.com.meubancodigitaljdbc.model.ContaPoupanca;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
@Repository
public class ContaPoupancaDAO {

    private final DataSource dataSource;

    public ContaPoupancaDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void atualizarConta(ContaPoupanca conta) throws SQLException {
        String sql = "UPDATE conta SET saldo = ? WHERE id_conta = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, conta.getSaldo());
            stmt.setLong(2, conta.getIdConta());

            stmt.executeUpdate();
        }
    }

}
