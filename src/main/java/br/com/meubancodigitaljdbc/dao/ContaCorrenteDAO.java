package br.com.meubancodigitaljdbc.dao;

import br.com.meubancodigitaljdbc.model.ContaCorrente;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class ContaCorrenteDAO {
    private final DataSource dataSource;

    public ContaCorrenteDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void atualizarConta(ContaCorrente contaCorrente) throws SQLException {

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL atualizar_conta_corrente(?, ?)}")){

            stmt.setDouble(1, contaCorrente.getTaxaManutencao());
            stmt.setLong(2, contaCorrente.getIdConta());

            stmt.executeUpdate();
        }

    }
}
