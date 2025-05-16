package br.com.meubancodigitaljdbc.execptions;

public class RegraNegocioException extends Exception {

    public RegraNegocioException(String message) {
        super(message);
    }
    public RegraNegocioException(String message, Throwable cause) {
        super(message,cause);
    }
}
