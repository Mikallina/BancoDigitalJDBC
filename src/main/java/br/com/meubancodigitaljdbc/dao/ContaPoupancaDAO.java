package br.com.meubancodigitaljdbc.dao;


import br.com.meubancodigitaljdbc.model.ContaPoupanca;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;

@Repository
public class ContaPoupancaDAO {

    private final DataSource dataSource;

    public ContaPoupancaDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void atualizarConta(ContaPoupanca contaPoupanca) throws SQLException {

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL atualizar_conta_poupanca(?, ?)}")){

            stmt.setDouble(1, contaPoupanca.setTaxaRendimento());
            stmt.setLong(2, contaPoupanca.getIdConta());

            stmt.executeUpdate();
        }
    }

}
