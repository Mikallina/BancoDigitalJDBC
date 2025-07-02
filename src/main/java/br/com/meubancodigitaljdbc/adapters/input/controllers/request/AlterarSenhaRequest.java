package br.com.meubancodigitaljdbc.adapters.input.controllers.request;

import lombok.Data;

@Data
public class AlterarSenhaRequest {

    private String cpf;
    private int senhaAntiga;
    private int senhaNova;
}
