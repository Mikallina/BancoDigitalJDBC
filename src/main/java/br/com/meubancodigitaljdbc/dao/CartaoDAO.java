package br.com.meubancodigitaljdbc.dao;

import br.com.meubancodigitaljdbc.enuns.TipoCartao;
import br.com.meubancodigitaljdbc.model.Cartao;
import br.com.meubancodigitaljdbc.model.CartaoCredito;
import br.com.meubancodigitaljdbc.model.Conta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CartaoDAO {

    @Autowired
    private DataSource dataSource;

    public void save(Cartao cartao) throws SQLException {
        String sql = "INSERT INTO cartao (num_cartao, tipo, senha, status, limite, fatura, dia_vencimento, conta_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cartao.getNumCartao());
            stmt.setString(2, cartao.getTipoCartao().name());
            stmt.setInt(3, cartao.getSenha());
            stmt.setBoolean(4, cartao.isStatus());
            //stmt.setDouble(5, cartao.getLimite());
            stmt.setDouble(6, cartao.getFatura());
            //stmt.setString(7, cartao.getDiaVencimento());
            stmt.setLong(8, cartao.getConta().getIdConta());

            stmt.executeUpdate();
        }
    }

    public List<Cartao> buscarPorConta(Conta conta) throws SQLException {
        List<Cartao> cartoes = new ArrayList<>();
        String sql = "SELECT * FROM cartao WHERE conta_id = ?";

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
        String sql = "SELECT * FROM cartao WHERE num_cartao = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numCartao);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCartao(rs, null);
            }
        }

        return null;
    }

    public void atualizar(Cartao cartao) throws SQLException {
        String sql = "UPDATE cartao SET senha = ?, status = ?, limite = ?, fatura = ? WHERE num_cartao = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartao.getSenha());
            stmt.setBoolean(2, cartao.isStatus());
           // stmt.setDouble(3, cartao.getLimite());
            stmt.setDouble(4, cartao.getFatura());
            stmt.setString(5, cartao.getNumCartao());

            stmt.executeUpdate();
        }
    }

    private Cartao mapResultSetToCartao(ResultSet rs, Conta conta) throws SQLException {
        Cartao cartao = new CartaoCredito();
        cartao.setNumCartao(rs.getString("num_cartao"));
        cartao.setTipoCartao(TipoCartao.valueOf(rs.getString("tipo")));
        cartao.setSenha(rs.getInt("senha"));
        cartao.setStatus(rs.getBoolean("status"));
       // cartao.setLimite(rs.getDouble("limite"));
        cartao.setFatura(rs.getDouble("fatura"));
       // cartao.setDiaVencimento(rs.getString("dia_vencimento"));
        cartao.setConta(conta);
        return cartao;
    }
}

