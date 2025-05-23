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

    private final DataSource dataSource;
    private final ContaDAO contaDAO;

    private CartaoRowMapper rowMapper;

    @Autowired
    public CartaoDAO(DataSource dataSource, ContaDAO contaDAO) {
        this.dataSource = dataSource;
        this.contaDAO = contaDAO;
    }


    public Cartao save(Cartao cartao) throws SQLException {
        if (cartao.getConta() == null || cartao.getConta().getIdConta() == null) {
            throw new IllegalArgumentException("Cartão não possui uma conta válida associada.");
        }

        Optional<Conta> optionalConta = contaDAO.findById(cartao.getConta().getIdConta());
        if (optionalConta.isEmpty()) {
            throw new IllegalArgumentException("Conta com ID " + cartao.getConta().getIdConta() + " não encontrada.");
        }


        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL salvar_cartao(?, ?, ?, ?, ?, ?, ?)}")) {

            stmt.setString(1, cartao.getTipoCartao().name());
            stmt.setString(2, cartao.getNumCartao());
            stmt.setInt(3, cartao.getSenha());
            stmt.setBoolean(4, cartao.isStatus());
            stmt.setLong(5, cartao.getConta().getIdConta());
            stmt.setDouble(6, cartao.getFatura());

            stmt.registerOutParameter(7, Types.BIGINT);

            stmt.execute();
            long idCartao = stmt.getLong(7);
            cartao.setIdCartao(idCartao);


        }

        if (cartao instanceof CartaoCredito c) {

            try (Connection conn = dataSource.getConnection();
                 CallableStatement stmt = conn.prepareCall("{CALL salvar_cartao_credito(?, ?, ?, ?, ?, ?, ?, ?)}")) {

                if (c.getDataCompra() != null) {
                    stmt.setDate(1, Date.valueOf(c.getDataCompra()));
                } else {
                    stmt.setNull(1, java.sql.Types.DATE);
                }
                if (c.getDataVencimento() != null) {
                    stmt.setDate(2, Date.valueOf(c.getDataVencimento()));
                } else {
                    stmt.setNull(2, java.sql.Types.DATE);
                }
                stmt.setDouble(3, c.getLimiteCredito());
                stmt.setDouble(4, c.getPagamento());
                stmt.setDouble(5, c.getSaldoMes());
                stmt.setDouble(6, c.getTaxa());
                stmt.setLong(7, cartao.getIdCartao());
                stmt.setString(8, c.getDiaVencimento());

                stmt.executeUpdate();
            }

        } else if (cartao instanceof CartaoDebito c) {
            try (Connection conn = dataSource.getConnection();
                 CallableStatement stmt = conn.prepareCall("{CALL salvar_cartao_debito(?, ?, ?, ?)}")) {

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

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL buscar_cartoes_conta(?)}")) {

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

    public Cartao buscarPorNumeroCartao(String numCartao) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL buscar_cartoes_numero(?)}")) {
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



    public boolean alterarStatusCartao(String numCartao, boolean status) throws SQLException {
        String sql = "{CALL alterar_status_cartao(?,?)}";

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, numCartao);
            stmt.setBoolean(2, status);

            int resultado = stmt.executeUpdate();
            return resultado > 0;
        }
    }

    public boolean alterarSenhaCartao(String numCartao, int senhaAntiga, int novaSenha) throws SQLException {
        String sql = "{CALL alterar_senha_cartao(?, ?, ?, ?)}";

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, numCartao);
            stmt.setInt(2, senhaAntiga);
            stmt.setInt(3, novaSenha);
            stmt.registerOutParameter(4, Types.BOOLEAN);

            stmt.execute();

            return stmt.getBoolean(4);
        }
    }

    public boolean alterarLimiteCartao(String numCartao, double novoLimite) throws SQLException {
        String sql = "{CALL alterar_limite_cartao(?, ?, ?)}";

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, numCartao);
            stmt.setDouble(2, novoLimite);
            stmt.registerOutParameter(3, Types.BOOLEAN);

            stmt.execute();

            return stmt.getBoolean(3);
        }
    }


    public void deleteById(Long idCartao) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL deletar_cartao(?)}")) {
            stmt.setLong(1, idCartao);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }


    public Optional<Cartao> findById(Long idCartao) {
        if (idCartao == null) {
            throw new IllegalArgumentException("ID do cartao não pode ser null");
        }

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL buscar_id_cartao(?)}")) {

            stmt.setLong(1, idCartao);
            ResultSet rs = stmt.executeQuery();


            if (rs.next()) {
                Long idConta = rs.getLong("id_conta");

                Optional<Conta> contaOpt = contaDAO.findById(idConta);
                Conta conta = contaOpt.orElseThrow(() ->
                        new IllegalArgumentException("Conta não encontrada para o cartão"));

                CartaoRowMapper rowMapper = new CartaoRowMapper(conta);
                return Optional.of(rowMapper.mapRow(rs, 0));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }


}

