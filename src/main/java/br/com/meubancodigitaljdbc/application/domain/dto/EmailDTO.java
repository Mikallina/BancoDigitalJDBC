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

    public Long getIdEmail() {
        return idEmail;
    }

    public void setIdEmail(Long idEmail) {
        this.idEmail = idEmail;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDateTime getDataEmail() {
        return dataEmail;
    }

    public void setDataEmail(LocalDateTime dataEmail) {
        this.dataEmail = dataEmail;
    }

    public StatusEmail getStatusEmail() {
        return statusEmail;
    }

    public void setStatusEmail(StatusEmail statusEmail) {
        this.statusEmail = statusEmail;
    }
}
