package br.com.meubancodigitaljdbc.adapters.input.controllers.request;


import lombok.Data;

@Data
public class AlterarStatusResquest {
    private String cpf;
    private boolean status;

}