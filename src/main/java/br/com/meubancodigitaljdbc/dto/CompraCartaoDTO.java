package br.com.meubancodigitaljdbc.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;


public class CompraCartaoDTO { 
	
	private Long id;
	@Schema(description = "Valor", example = "100.00")
	private double valor;
	@Schema(description = "Data da Compra", example = "13/05/2025")
	private LocalDate dataCompra;
	private String numCartao;
	
	public String getNumCartao() {
		return numCartao;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}
	public LocalDate getDataCompra() {
		return dataCompra;
	}

	public CompraCartaoDTO(Long id, double valor, LocalDate dataCompra, String numCartao) {
		super();
		this.id = id;
		this.valor = valor;
		this.dataCompra = dataCompra;
		this.numCartao = numCartao;
	}
	

}
