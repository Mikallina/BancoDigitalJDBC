package br.com.meubancodigitaljdbc.dao;

import br.com.meubancodigitaljdbc.enuns.TipoCartao;
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

    @Autowired
    private DataSource dataSource;

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

        // Passo 1: Inserir na tabela cartao
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

        // Passo 2: Inserir dados específicos conforme o tipo
        if (cartao instanceof CartaoCredito) {
            CartaoCredito c = (CartaoCredito) cartao;
            String sqlCredito = "INSERT INTO cartao_credito (data_vencimento, limite_credito, pagamento, saldo_mes, taxa, id_cartao, dia_vencimento) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

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
                "WHERE c.id_conta = ?";;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, conta.getIdConta());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cartoes.add(mapResultSetToCartao(rs, conta));
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
                "WHERE c.num_cartao = ?";;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numCartao);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                long idConta = rs.getLong("id_conta");
                Conta conta = contaDAO.findById(idConta).orElse(null);
                return mapResultSetToCartao(rs, conta);
            }
        }

        return null;
    }

    public void atualizar(Cartao cartao) throws SQLException {
        String sql = "UPDATE cartao SET senha = ?, status = ?, fatura = ? WHERE num_cartao = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartao.getSenha());
            stmt.setBoolean(2, cartao.isStatus());
           // stmt.setDouble(3,cartao.);
            stmt.setDouble(3, cartao.getFatura());
            stmt.setString(4, cartao.getNumCartao());

            stmt.executeUpdate();
        }
    }

    private Cartao mapResultSetToCartao(ResultSet rs, Conta conta) throws SQLException {
        // Verifique se o tipo de cartão existe
        TipoCartao tipoCartao = TipoCartao.valueOf(rs.getString("tipo_cartao"));
        Cartao cartao;

        // Verifique o tipo de cartão e crie a instância correspondente
        if (tipoCartao == TipoCartao.CREDITO) {
            CartaoCredito credito = new CartaoCredito();
            credito.setLimiteCredito(rs.getDouble("limite_credito"));
            credito.setDiaVencimento(rs.getString("dia_vencimento"));
            credito.getSaldoMes();
            credito.setIdCartao(rs.getLong("id_cartao"));
            Date dataCompra = rs.getDate("data_compra");
            if (dataCompra != null) {
                credito.setDataCompra(dataCompra.toLocalDate());
            }

            // Verifique se a data de vencimento não é null
            Date dataVencimento = rs.getDate("data_vencimento");
            if (dataVencimento != null) {
                credito.setDataVencimento(dataVencimento.toLocalDate());
            }
            credito.setPagamento(rs.getDouble("pagamento"));
            credito.setTaxa(rs.getDouble("credito_taxa"));
            cartao = credito;
        } else if (tipoCartao == TipoCartao.DEBITO) {
            CartaoDebito debito = new CartaoDebito();
            debito.setLimiteDiario(rs.getDouble("limite_diario"));
            debito.setIdCartao(rs.getLong("id_cartao"));
            debito.setTaxa(rs.getDouble("debito_taxa"));
            debito.setTotalPgtoHoje(rs.getDouble("total_pgto_hoje"));
            cartao = debito;
        } else {
            throw new IllegalArgumentException("Tipo de cartão desconhecido: " + tipoCartao);
        }

        // Preencha os campos comuns aos dois tipos de cartão
        cartao.setTipoCartao(TipoCartao.valueOf(rs.getString("tipo_cartao")));
        cartao.setIdCartao(rs.getLong("id_cartao"));
        cartao.setNumCartao(rs.getString("num_cartao"));
        cartao.setSenha(rs.getInt("senha"));
        cartao.setFatura(rs.getDouble("fatura"));
        cartao.setStatus(rs.getBoolean("status"));
        cartao.setTipoCartao(tipoCartao);


        if (conta == null) {
            throw new IllegalArgumentException("Conta não encontrada para associar ao cartão.");
        }
        cartao.setConta(conta);

        return cartao;
    }

}

