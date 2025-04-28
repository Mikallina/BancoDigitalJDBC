 
package br.com.meubancodigitaljdbc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DepositoDTO {
	
	private Long id;
    private double valor;
    @JsonProperty("numContaDestino")
    private String numConta;
    

	public String getNumConta() {
		return numConta;
	}

	public void setNumConta(String numConta) {
		this.numConta = numConta;
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

	public DepositoDTO(Long id, double valor, String numConta) {
		super();
		this.id = id;
		this.valor = valor;
		this.numConta = numConta;
	}
    
    

}
