package br.com.meubancodigitaljdbc.adapters.input.controllers.response;


import br.com.meubancodigitaljdbc.application.domain.enuns.TipoCartao;
import br.com.meubancodigitaljdbc.application.domain.model.Conta;
import lombok.Data;

@Data
public class CartaoResponse {

    private Long idCartao;
    private String numCartao;
    private Conta conta;
    private boolean status;
    protected TipoCartao tipoCartao;
    private int senha;
    private double fatura;
}
