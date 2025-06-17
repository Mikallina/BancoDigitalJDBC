 
package br.com.meubancodigitaljdbc.adapters.input.controllers.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DepositoRequest {
	
	private String cpf;
	@Schema(description = "Valor a ser depositado", example = "250.00")
    private double valor;
	@Schema(description = "Número da conta destino", example = "CC-1234-xxxxxxxx")
    private String numContaDestino;

}
