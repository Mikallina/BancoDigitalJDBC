package br.com.meubancodigitaljdbc.adapters.output.mapper;

import br.com.meubancodigitaljdbc.application.domain.enuns.TipoCartao;
import br.com.meubancodigitaljdbc.application.domain.model.Cartao;
import br.com.meubancodigitaljdbc.application.domain.model.CartaoCredito;
import br.com.meubancodigitaljdbc.application.domain.model.CartaoDebito;
import br.com.meubancodigitaljdbc.application.domain.model.Conta;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CartaoRowMapper implements RowMapper<Cartao> {
    private final Conta conta;

    public CartaoRowMapper(Conta conta) {
        if (conta == null) {
            throw new IllegalArgumentException("Conta não pode ser nula");
        }
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
            credito.setDataCompra(rs.getDate("data_compra").toLocalDate());
            credito.setDiaVencimento(rs.getString("dia_vencimento"));
            credito.setLimiteCredito(rs.getDouble("limite_credito"));
            credito.setPagamento(rs.getDouble("pagamento"));
            credito.setSaldoMes(rs.getDouble("saldo_mes"));
           // credito.setTaxa(rs.getDouble("taxa"));
            credito.setIdCartao(rs.getLong("id_cartao"));

            java.sql.Date dataVencimento = rs.getDate("data_vencimento");
            if (dataVencimento != null) {
                credito.setDataVencimento(dataVencimento.toLocalDate());
            }

            cartao = credito;
        } else if (tipoCartao == TipoCartao.DEBITO) {
            CartaoDebito debito = new CartaoDebito();
            debito.setLimiteDiario(rs.getDouble("limite_diario"));
            debito.setTaxa(rs.getDouble("taxa"));
            debito.setTotalPgtoHoje(rs.getDouble("total_pgto_hoje"));
            debito.setIdCartao(rs.getLong("id_cartao"));
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
        conta.setIdConta(rs.getLong("id_conta"));

        if (conta == null) {
            throw new IllegalArgumentException("Conta não encontrada para associar ao cartão.");
        }

        cartao.setConta(conta);
        return cartao;
    }


}