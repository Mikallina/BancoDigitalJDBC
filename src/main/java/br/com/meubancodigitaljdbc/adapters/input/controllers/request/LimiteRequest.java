package br.com.meubancodigitaljdbc.adapters.input.controllers.request;

import lombok.Data;

@Data
public class LimiteRequest {

	private Long id;
	private double limite;
	private double novoLimite;


}
