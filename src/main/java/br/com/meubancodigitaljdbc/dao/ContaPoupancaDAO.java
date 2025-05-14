package br.com.meubancodigitaljdbc.dao;

import br.com.meubancodigitaljdbc.model.ContaCorrente;
import br.com.meubancodigitaljdbc.model.ContaPoupanca;
import br.com.meubancodigitaljdbc.sql.ContaSql;
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

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(ContaSql.ATUALIZAR_CONTA_POUPANCA)) {

            stmt.setDouble(1, conta.getSaldo());
            stmt.setLong(2, conta.getIdConta());

            stmt.executeUpdate();
        }
    }

}
