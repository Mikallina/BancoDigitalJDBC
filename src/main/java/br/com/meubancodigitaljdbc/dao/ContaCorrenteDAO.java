package br.com.meubancodigitaljdbc.dao;

import br.com.meubancodigitaljdbc.model.ContaCorrente;
import br.com.meubancodigitaljdbc.sql.ContaSql;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
public class ContaCorrenteDAO {
    private final DataSource dataSource;

    public ContaCorrenteDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void atualizarConta(ContaCorrente contaCorrente) throws SQLException {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(ContaSql.ATUALIZAR_CONTA_CORRENTE)) {

            stmt.setDouble(1, contaCorrente.getTaxaManutencao());
            stmt.setLong(2, contaCorrente.getIdConta());

            stmt.executeUpdate();
        }

    }
}
