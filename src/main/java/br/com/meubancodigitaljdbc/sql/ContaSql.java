package br.com.meubancodigitaljdbc.sql;

public class ContaSql {

    public static final String INSERIR_CONTA = "INSERT INTO conta (tipo_conta, agencia, num_conta, saldo, cliente_id) VALUES (?, ?, ?, ?, ?)";

    public static final String BUSCAR_POR_NUMERO = "SELECT c.*, cl.categoria FROM conta c JOIN cliente cl ON c.cliente_id = cl.id_cliente WHERE c.num_conta = ?";

    public static final String BUSCAR_CLIENTE_CPF =  "SELECT * FROM cliente WHERE cpf = ?";

    public static final String BUSCAR_CONTA_ID = "SELECT * FROM conta WHERE id_conta = ?";

    public static final String BUSCAR_CLIENTE_CAT = "SELECT c.*, cl.categoria FROM conta c JOIN cliente cl ON c.cliente_id = cl.id_cliente WHERE c.id_conta = ?";

    public static final String BUSCAR_CLIENTE_CONTA_ID = "SELECT * FROM conta WHERE cliente_id = ?";

    public static final String ATUALIZAR_SALDO = "UPDATE conta SET saldo = ? WHERE id_conta = ?";

    public static final String ATUALIZAR_CONTA = "UPDATE conta SET saldo = ?, agencia = ?, num_conta = ?, tipo_conta = ? WHERE id_conta = ?";

    public static final String LISTAR_CONTAS = "SELECT * FROM conta";

    public static final String ATUALIZAR_CONTA_CORRENTE = "UPDATE conta_corrente SET taxa_manutencao = ? WHERE id_conta = ?";

    public static final String ATUALIZAR_CONTA_POUPANCA = "UPDATE conta SET saldo = ? WHERE id_conta = ?";
}
