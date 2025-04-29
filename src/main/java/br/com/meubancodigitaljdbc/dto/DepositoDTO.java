 
package br.com.meubancodigitaljdbc.dto;


public class DepositoDTO {
	
	private String cpf;
    private double valor;
    private String numContaDestino;
    

	public String getNumContaDestino() {
		return numContaDestino;
	}

	public void setNumContaDestino(String numConta) {
		this.numContaDestino = numConta;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}

	public DepositoDTO(String cpf, double valor, String numContaDestino) {
		super();
		this.cpf = cpf;
		this.valor = valor;
		this.numContaDestino = numContaDestino;
	}
    
    

}
