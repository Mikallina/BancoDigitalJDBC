package br.com.meubancodigitaljdbc.sql;

public class CartaoSql {

    public static final String BUSCAR_POR_CONTA = "SELECT c.*, cc.data_compra, cc.data_vencimento, cc.limite_credito, cc.pagamento, cc.saldo_mes, cc.taxa AS credito_taxa, cc.dia_vencimento, " +
            "cd.limite_diario, cd.taxa AS debito_taxa, cd.total_pgto_hoje " +
            "FROM cartao c " +
            "LEFT JOIN cartao_credito cc ON cc.id_cartao = c.id_cartao " +
            "LEFT JOIN cartao_debito cd ON cd.id_cartao = c.id_cartao " +
            "WHERE c.id_conta = ?";


    public static final String BUSCAR_POR_NUMERO = "SELECT c.*, cc.data_compra, cc.data_vencimento, cc.limite_credito, cc.pagamento, cc.saldo_mes, cc.taxa AS credito_taxa, cc.dia_vencimento, " +
                "cd.limite_diario, cd.taxa AS debito_taxa, cd.total_pgto_hoje " +
                "FROM cartao c " +
                "LEFT JOIN cartao_credito cc ON cc.id_cartao = c.id_cartao " +
                "LEFT JOIN cartao_debito cd ON cd.id_cartao = c.id_cartao " +
                "WHERE c.num_cartao = ?";


    public static final String UPDATE_CARTAO = "UPDATE cartao SET senha = ?, status = ?, fatura = ? WHERE num_cartao = ?";

    public static final String UPDATE_CARTAO_CREDITO ="UPDATE cartao_credito SET limite_credito = ?, data_compra = ?, pagamento = ? , saldo_mes = ? WHERE id_cartao = ?";

    public static final String INSERIR_CARTAO =
            "INSERT INTO cartao (tipo_cartao, num_cartao, senha, status, id_conta, fatura) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    public static final String INSERIR_CARTAO_CREDITO =
            "INSERT INTO cartao_credito (data_compra, data_vencimento, limite_credito, pagamento, saldo_mes, taxa, id_cartao, dia_vencimento) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String INSERIR_CARTAO_DEBITO =
            "INSERT INTO cartao_debito (limite_diario, taxa, total_pgto_hoje, id_cartao) " +
                    "VALUES (?, ?, ?, ?)";

    }
