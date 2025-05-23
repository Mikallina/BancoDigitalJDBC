package br.com.meubancodigitaljdbc.dto;



public class AlterarSenhaDTO {

    private String cpf;

    private int senhaAntiga;

    private int senhaNova;

	public AlterarSenhaDTO(){}
	public AlterarSenhaDTO(String cpf, int senhaAntiga, int senhaNova) {
		super();
		this.cpf = cpf;
		this.senhaAntiga = senhaAntiga;
		this.senhaNova = senhaNova;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public int getSenhaAntiga() {
		return senhaAntiga;
	}
	public int getSenhaNova() {
		return senhaNova;
	}

}