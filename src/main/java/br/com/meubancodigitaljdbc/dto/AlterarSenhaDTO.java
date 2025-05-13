package br.com.meubancodigitaljdbc.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class AlterarSenhaDTO {
	@Schema(description = "CPF", example = "XXXXXXXXXXX")
    private String cpf;
	@Schema(description = "Senha Antiga", example = "1234")
    private int senhaAntiga;
	@Schema(description = "Nova Senha", example = "1235")
    private int senhaNova;
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