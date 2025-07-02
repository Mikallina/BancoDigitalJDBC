package br.com.meubancodigitaljdbc.adapters.input.controllers.request;

import lombok.Data;

@Data
public class PagamentoFaturaRequest {
	
	private String numCartao;
    private double valor;

    

}
