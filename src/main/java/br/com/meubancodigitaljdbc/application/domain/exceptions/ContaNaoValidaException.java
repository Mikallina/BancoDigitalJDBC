package br.com.meubancodigitaljdbc.application.domain.exceptions;

public class ContaNaoValidaException extends Exception {
    public ContaNaoValidaException(String message) {
        super(message);
    }

    public ContaNaoValidaException(String message, Throwable cause) {
        super(message, cause);
    }
}