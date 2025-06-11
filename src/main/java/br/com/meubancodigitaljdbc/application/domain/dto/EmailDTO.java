package br.com.meubancodigitaljdbc.application.domain.dto;

import br.com.meubancodigitaljdbc.application.domain.enuns.StatusEmail;

import java.time.LocalDateTime;

public class EmailDTO {

    private Long idEmail;
    private String nome;
    private Long idCliente;
    private String emailFrom;
    private String emailTo;
    private String titulo;
    private String texto;
    private LocalDateTime dataEmail;
    private StatusEmail statusEmail;

    public EmailDTO() {
    }



    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

}
