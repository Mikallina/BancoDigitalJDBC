 
package br.com.meubancodigitaljdbc.application.domain.dto;


import io.swagger.v3.oas.annotations.media.Schema;

public class DepositoDTO {
	
	private String cpf;
	@Schema(description = "Valor a ser depositado", example = "250.00")
    private double valor;
	@Schema(description = "Número da conta destino", example = "CC-1234-xxxxxxxx")
    private String numContaDestino;

	public DepositoDTO() {

	}


	public String getNumContaDestino() {
		return numContaDestino;
	}


	public void setNumContaDestino(String numContaDestino) {
		this.numContaDestino = numContaDestino;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}

	public DepositoDTO(String cpf, Double valor, String numContaDestino) {
		super();
		this.cpf = cpf;
		this.valor = valor;
		this.numContaDestino = numContaDestino;
	}
    
    

}
