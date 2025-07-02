package br.com.meubancodigitaljdbc.adapters.input.controllers.response;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class OperacaoResponse {

    private String mensagem;
    private LocalDateTime timestamp;

    public OperacaoResponse(String mensagem, LocalDateTime timestamp) {
        this.mensagem = mensagem;
        this.timestamp = timestamp;
    }
}
