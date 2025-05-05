package br.com.meubancodigitaljdbc.dao;

import br.com.meubancodigitaljdbc.mapper.CartaoRowMapper;
import br.com.meubancodigitaljdbc.model.Cartao;
import br.com.meubancodigitaljdbc.model.CartaoCredito;
import br.com.meubancodigitaljdbc.model.CartaoDebito;
import br.com.meubancodigitaljdbc.model.Conta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CartaoDAO {

    private DataSource dataSource;

    @Autowired
    public CartaoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Autowired
    private ContaDAO contaDAO;


    public Cartao save(Cartao cartao) throws SQLException {
        if (cartao.getConta() == null || cartao.getConta().getIdConta() == null) {
            throw new IllegalArgumentException("Cartão não possui uma conta válida associada.");
        }

        Optional<Conta> optionalConta = contaDAO.findById(cartao.getConta().getIdConta());
        if (optionalConta.isEmpty()) {
            throw new IllegalArgumentException("Conta com ID " + cartao.getConta().getIdConta() + " não encontrada.");
        }

        String sqlCartao = "INSERT INTO cartao (tipo_cartao, num_cartao, senha, status, id_conta, fatura) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlCartao, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cartao.getTipoCartao().name());
            stmt.setString(2, cartao.getNumCartao());
            stmt.setInt(3, cartao.getSenha());
            stmt.setBoolean(4, cartao.isStatus());
            stmt.setLong(5, cartao.getConta().getIdConta());
            stmt.setDouble(6, cartao.getFatura());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cartao.setIdCartao(rs.getLong(1));
                } else {
                    throw new SQLException("Falha ao obter ID do cartão.");
                }
            }
        }

        if (cartao instanceof CartaoCredito) {
            CartaoCredito c = (CartaoCredito) cartao;
            String sqlCredito = "INSERT INTO cartao_credito (data_compra, data_vencimento, limite_credito, pagamento, saldo_mes, taxa, id_cartao, dia_vencimento) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlCredito)) {

                if (c.getDataCompra() != null) {
                    stmt.setDate(1, Date.valueOf(c.getDataCompra()));
                } else {
                    stmt.setNull(1, java.sql.Types.DATE);  // Ou algum valor default, se necessário
                }

                // Verificar se a data de vencimento não é nula antes de setar
                if (c.getDataVencimento() != null) {
                    stmt.setDate(2, Date.valueOf(c.getDataVencimento()));
                } else {
                    stmt.setNull(2, java.sql.Types.DATE);  // Ou algum valor default, se necessário
                }
                stmt.setDouble(3, c.getLimiteCredito());
                stmt.setDouble(4, c.getPagamento());
                stmt.setDouble(5, c.getSaldoMes());
                stmt.setDouble(6, c.getTaxa());
                stmt.setLong(7, cartao.getIdCartao());
                stmt.setString(8, c.getDiaVencimento());

                stmt.executeUpdate();
            }

        } else if (cartao instanceof CartaoDebito) {
            CartaoDebito c = (CartaoDebito) cartao;
            String sqlDebito = "INSERT INTO cartao_debito (limite_diario, taxa, total_pgto_hoje, id_cartao) VALUES (?, ?, ?, ?)";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlDebito)) {

                stmt.setDouble(1, c.getLimiteDiario());
                stmt.setDouble(2, c.getTaxa());
                stmt.setDouble(3, c.getTotalPgtoHoje());
                stmt.setLong(4, cartao.getIdCartao());

                stmt.executeUpdate();
            }
        }

        return cartao;
    }


    public List<Cartao> buscarPorConta(Conta conta) throws SQLException {
        List<Cartao> cartoes = new ArrayList<>();
        String sql = "SELECT c.*, cc.data_compra, cc.data_vencimento, cc.limite_credito, cc.pagamento, cc.saldo_mes, cc.taxa AS credito_taxa, cc.dia_vencimento, " +
                "cd.limite_diario, cd.taxa AS debito_taxa, cd.total_pgto_hoje " +
                "FROM cartao c " +
                "LEFT JOIN cartao_credito cc ON cc.id_cartao = c.id_cartao " +
                "LEFT JOIN cartao_debito cd ON cd.id_cartao = c.id_cartao " +
                "WHERE c.id_conta = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, conta.getIdConta());
            ResultSet rs = stmt.executeQuery();

            CartaoRowMapper rowMapper = new CartaoRowMapper(conta);
            int rowNum = 0;
            while (rs.next()) {
                cartoes.add(rowMapper.mapRow(rs, rowNum++));
            }
        }

        return cartoes;
    }

    public Cartao buscarPorNumero(String numCartao) throws SQLException {
        String sql = "SELECT c.*, cc.data_compra, cc.data_vencimento, cc.limite_credito, cc.pagamento, cc.saldo_mes, cc.taxa AS credito_taxa, cc.dia_vencimento, " +
                "cd.limite_diario, cd.taxa AS debito_taxa, cd.total_pgto_hoje " +
                "FROM cartao c " +
                "LEFT JOIN cartao_credito cc ON cc.id_cartao = c.id_cartao " +
                "LEFT JOIN cartao_debito cd ON cd.id_cartao = c.id_cartao " +
                "WHERE c.num_cartao = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numCartao);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                long idConta = rs.getLong("id_conta");
                Conta conta = contaDAO.findById(idConta).orElse(null);
                return CartaoRowMapper.map(rs, conta);
            }
        }

        return null;
    }

    public void atualizar(Cartao cartao) throws SQLException {
        String sqlCartao = "UPDATE cartao SET senha = ?, status = ?, fatura = ? WHERE num_cartao = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlCartao)) {

            stmt.setInt(1, cartao.getSenha());
            stmt.setBoolean(2, cartao.isStatus());
            stmt.setDouble(3, cartao.getFatura());
            stmt.setString(4, cartao.getNumCartao());

            stmt.executeUpdate();
        }

        if (cartao instanceof CartaoCredito credito) {

            String sqlCredito = "UPDATE cartao_credito SET limite_credito = ?, data_compra = ?, pagamento = ? , saldo_mes = ? WHERE id_cartao = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlCredito)) {

                stmt.setDouble(1, credito.getLimiteCredito());
                stmt.setDate(2, Date.valueOf(credito.getDataCompra()));
                stmt.setDouble(3, credito.getPagamento());
                stmt.setDouble(4, credito.getSaldoMes());
                stmt.setLong(5, credito.getIdCartao());


                stmt.executeUpdate();
            }
        }
    }


}

