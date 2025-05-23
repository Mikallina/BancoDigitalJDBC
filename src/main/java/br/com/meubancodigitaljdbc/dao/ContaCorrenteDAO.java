package br.com.meubancodigitaljdbc.dao;

import br.com.meubancodigitaljdbc.model.ContaCorrente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class ContaCorrenteDAO {
    private final DataSource dataSource;
    @Autowired
    public ContaCorrenteDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void atualizarConta(ContaCorrente contaCorrente) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL atualizar_conta_corrente(?, ?)}")) {

            stmt.setLong(1, contaCorrente.getIdConta());
            stmt.setDouble(2, contaCorrente.getTaxaManutencao());

            stmt.executeUpdate();
        }
    }
}
