package br.com.meubancodigitaljdbc.adapters.output.dao;


import br.com.meubancodigitaljdbc.application.domain.model.ContaPoupanca;
import br.com.meubancodigitaljdbc.application.ports.output.repository.ContaPoupancaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;

@Repository
public class ContaPoupancaDAO implements ContaPoupancaRepository {

    private final DataSource dataSource;

    @Autowired
    public ContaPoupancaDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void atualizarConta(ContaPoupanca contaPoupanca) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL atualizar_conta_poupanca(?, ?)}")) {

            stmt.setLong(1, contaPoupanca.getIdConta());
            stmt.setDouble(2, contaPoupanca.getTaxaRendimento());

            stmt.executeUpdate();
        }
    }
}
