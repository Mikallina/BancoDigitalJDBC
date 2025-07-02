package br.com.meubancodigitaljdbc.application.domain.exceptions;

public class OperacoesException extends Exception {

    public OperacoesException(String message) {
        super(message);
    }
    public OperacoesException(String message, Throwable cause) {
        super(message,cause);
    }
}
