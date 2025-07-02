package br.com.meubancodigitaljdbc.adapters.input.controllers.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;


@Data
public class CompraCartaoRequest {
	
	private Long id;
	@Schema(description = "Valor", example = "100.00")
	private double valor;
	@Schema(description = "Data da Compra", example = "13/05/2025")
	private LocalDate dataCompra;
	private String numCartao;
	

}
