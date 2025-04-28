package br.com.meubancodigitaljdbc.dto;


public class TransferenciaDTO {
    private Long id;
    private String chave;
    private String numContaDestino;
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

	public void setNumContaOrigem(String numContaOrigem) {
		this.numContaOrigem = numContaOrigem;
	}

	public Long getIdConta() {
		return idConta;
	}

	public void setIdConta(Long idConta) {
		this.idConta = idConta;
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
	public void setChave(String chave) {
		this.chave = chave;
	}
	public String getNumContaDestino() {
		return numContaDestino;
	}
	public void setNumContaDestino(String numContaDestino) {
		this.numContaDestino = numContaDestino;
	}
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}


}