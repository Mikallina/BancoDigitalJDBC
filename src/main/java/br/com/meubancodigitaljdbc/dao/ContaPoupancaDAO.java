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

    public ContaPoupanca salvarConta(ContaPoupanca contaPoupanca) throws SQLException {
        String sql = "INSERT INTO conta_poupanca (taxa_rendimento, id_conta) VALUES(?,?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, contaPoupanca.getTaxaRendimento());
            stmt.setLong(2, contaPoupanca.getIdConta());

            stmt.executeUpdate();

        }
        return contaPoupanca;
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
