package br.com.meubancodigitaljdbc.application.domain.dto;

public class AlterarStatusDTO {
    private String cpf;
    private boolean status;

    public void AlterarSenhaDTO(){}

    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public boolean isStatus() {
        return status;
    }

}