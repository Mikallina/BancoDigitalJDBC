package br.com.meubancodigitaljdbc.adapters.input.controllers.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TransferenciaRequest {
    private Long id;
    private String chave;
	@Schema(description = "Numero Conta Destino", example = "XXXXXXXXXXX")
    private String numContaDestino;
	@Schema(description = "Numero Conta Origem", example = "XXXXXXXXXXX")
    private String numContaOrigem;
    private Long idConta;
    private double valor;

}