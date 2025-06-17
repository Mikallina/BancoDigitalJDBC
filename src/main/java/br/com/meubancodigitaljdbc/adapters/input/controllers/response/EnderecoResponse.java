package br.com.meubancodigitaljdbc.adapters.input.controllers.response;


import lombok.Data;

@Data
public class EnderecoResponse {

    private String cep;
    private String logradouro;
    private String bairro;
    private String cidade;
    private String estado;
}
