package br.com.meubancodigitaljdbc.adapters.input.controllers.response;


import br.com.meubancodigitaljdbc.application.domain.model.Conta;
import lombok.Data;

@Data
public class ContaResponse {
	
	private String nome;
    private String cpf;
    private Conta conta;


    public ContaResponse(String nome, String cpf, Conta conta) {
    }
}
