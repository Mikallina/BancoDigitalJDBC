package br.com.meubancodigitaljdbc.adapters.input.controllers.request;

import br.com.meubancodigitaljdbc.application.domain.enuns.TipoConta;
import br.com.meubancodigitaljdbc.application.domain.model.Conta;
import lombok.Data;

@Data
public class ContaRequest {
    private String nome;
    private String cpf;
    private Conta conta;
    private TipoConta tipoConta;
    private int agencia;



}
