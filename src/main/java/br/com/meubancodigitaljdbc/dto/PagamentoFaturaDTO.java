package br.com.meubancodigitaljdbc.dto;

public class PagamentoFaturaDTO {
	
	private String numCartao;
    private double valor;
	public PagamentoFaturaDTO(String numCartao, double valor) {
		super();
		this.numCartao = numCartao;
		this.valor = valor;
	}
	public String getNumCartao() {
		return numCartao;
	}
	public void setNumCartao(String numCartao) {
		this.numCartao = numCartao;
	}
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}
    
    

}
