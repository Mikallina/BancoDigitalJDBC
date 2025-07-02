package br.com.meubancodigitaljdbc.adapters.input.controllers.request;

import br.com.meubancodigitaljdbc.application.domain.enuns.StatusEmail;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmailRequest {

    private Long idEmail;
    private String nome;
    private Long idCliente;
    private String emailFrom;
    private String emailTo;
    private String titulo;
    private String texto;
    private LocalDateTime dataEmail;
    private StatusEmail statusEmail;

 }

