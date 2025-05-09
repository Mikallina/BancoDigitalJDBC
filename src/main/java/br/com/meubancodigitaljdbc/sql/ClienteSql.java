package br.com.meubancodigitaljdbc.sql;

public class ClienteSql {

    public static final String INSERIR_CLIENTE = "INSERT INTO cliente (cpf, nome, data_nascimento, categoria, bairro, cep, complemento, logradouro, numero, localidade, uf) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String BUSCAR_POR_CPF = "SELECT * FROM cliente WHERE cpf = ?";

    public static final String BUSCAR_TODOS = "SELECT * FROM cliente";

    public static final String BUSCAR_POR_ID = "SELECT * FROM cliente WHERE id_cliente = ?";

    public static final String DELETAR_CLIENTE = "DELETE FROM cliente WHERE id_cliente = ?";
}
