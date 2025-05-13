package br.com.meubancodigitaljdbc.dto;


import io.swagger.v3.oas.annotations.media.Schema;

public class TransferenciaDTO {
    private Long id;
    private String chave;
	@Schema(description = "Numero Conta Destino", example = "XXXXXXXXXXX")
    private String numContaDestino;
	@Schema(description = "Numero Conta Origem", example = "XXXXXXXXXXX")
    private String numContaOrigem;
    private Long idConta;
    private double valor;
	
	
    public TransferenciaDTO() {}
    

	public TransferenciaDTO(Long id, String chave, String numContaDestino, String numContaOrigem, Long idConta,
			double valor) {
		super();
		this.id = id;
		this.chave = chave;
		this.numContaDestino = numContaDestino;
		this.numContaOrigem = numContaOrigem;
		this.idConta = idConta;
		this.valor = valor;
	}
	

	public String getNumContaOrigem() {
		return numContaOrigem;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getChave() {
		return chave;
	}
	public String getNumContaDestino() {
		return numContaDestino;
	}
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}


}