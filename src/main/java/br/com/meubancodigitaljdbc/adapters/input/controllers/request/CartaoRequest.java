package br.com.meubancodigitaljdbc.adapters.input.controllers.request;

import br.com.meubancodigitaljdbc.application.domain.enuns.TipoCartao;
import br.com.meubancodigitaljdbc.application.domain.model.Conta;
import lombok.Data;

@Data
public class CartaoRequest {

    private Long idCartao;

    private String numCartao;

    private Conta conta;

    private boolean status;

    protected TipoCartao tipoCartao;

    private int senha;

    private double fatura;
}
