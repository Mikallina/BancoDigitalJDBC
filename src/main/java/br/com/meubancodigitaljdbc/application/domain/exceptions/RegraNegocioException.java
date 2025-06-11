package br.com.meubancodigitaljdbc.application.domain.exceptions;

public class RegraNegocioException extends Exception {

    public RegraNegocioException(String message) {
        super(message);
    }
    public RegraNegocioException(String message, Throwable cause) {
        super(message,cause);
    }
}
