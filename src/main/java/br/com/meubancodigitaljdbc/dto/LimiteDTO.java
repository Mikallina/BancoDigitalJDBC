package br.com.meubancodigitaljdbc.dto;

public class LimiteDTO {

	private Long id;
	private double limite;
	private double novoLimite;

	public LimiteDTO(){}
	public LimiteDTO(Long id, double limite, double novoLimite) {
		super();
		this.id = id;
		this.limite = limite;
		this.novoLimite = novoLimite;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getNovoLimite() {
		return novoLimite;
	}

}
