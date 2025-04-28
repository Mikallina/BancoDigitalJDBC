package br.com.meubancodigitaljdbc.dto;

import java.time.LocalDate;


public class CompraCartaoDTO { 
	
	private Long id;
	private double valor;
	private LocalDate dataCompra;
	private String numCartao;
	
	public String getNumCartao() {
		return numCartao;
	}
	public void setNumeroCartao(String numCartao) {
		this.numCartao = numCartao;
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
	public void setDataCompra(LocalDate dataCompra) {
		this.dataCompra = dataCompra;
	}
	public CompraCartaoDTO(Long id, double valor, LocalDate dataCompra, String numCartao) {
		super();
		this.id = id;
		this.valor = valor;
		this.dataCompra = dataCompra;
		this.numCartao = numCartao;
	}
	

}
