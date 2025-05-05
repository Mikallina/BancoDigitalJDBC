package br.com.meubancodigitaljdbc.mapper;

import br.com.meubancodigitaljdbc.enuns.TipoCartao;
import br.com.meubancodigitaljdbc.model.Cartao;
import br.com.meubancodigitaljdbc.model.CartaoCredito;
import br.com.meubancodigitaljdbc.model.CartaoDebito;
import br.com.meubancodigitaljdbc.model.Conta;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class CartaoRowMapper implements RowMapper<Cartao> {
    private final Conta conta;

    public CartaoRowMapper(Conta conta) {
        this.conta = conta;
    }

    @Override
    public Cartao mapRow(ResultSet rs, int rowNum) throws SQLException {
        return map(rs, conta);
    }

    public static Cartao map(ResultSet rs, Conta conta) throws SQLException {
        TipoCartao tipoCartao = TipoCartao.valueOf(rs.getString("tipo_cartao"));
        Cartao cartao;

        if (tipoCartao == TipoCartao.CREDITO) {
            CartaoCredito credito = new CartaoCredito();
            credito.setLimiteCredito(rs.getDouble("limite_credito"));
            credito.setDiaVencimento(rs.getString("dia_vencimento"));
            credito.setSaldoMes(rs.getDouble("saldo_mes"));
            java.sql.Date dataCompra = rs.getDate("data_compra");
            if (dataCompra != null) {
                credito.setDataCompra(dataCompra.toLocalDate());
            }
            java.sql.Date dataVencimento = rs.getDate("data_vencimento");
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

        cartao.setTipoCartao(tipoCartao);
        cartao.setIdCartao(rs.getLong("id_cartao"));
        cartao.setNumCartao(rs.getString("num_cartao"));
        cartao.setSenha(rs.getInt("senha"));
        cartao.setFatura(rs.getDouble("fatura"));
        cartao.setStatus(rs.getBoolean("status"));

        if (conta == null) {
            throw new IllegalArgumentException("Conta não encontrada para associar ao cartão.");
        }

        cartao.setConta(conta);
        return cartao;
    }
}