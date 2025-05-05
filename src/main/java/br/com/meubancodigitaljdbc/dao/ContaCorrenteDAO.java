package br.com.meubancodigitaljdbc.dao;

import br.com.meubancodigitaljdbc.model.ContaCorrente;
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
        String sql = "UPDATE conta_corrente SET taxa_manutencao = ? WHERE id_conta = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, contaCorrente.getTaxaManutencao());
            stmt.setLong(2, contaCorrente.getIdConta());

            stmt.executeUpdate();
        }

    }
}
