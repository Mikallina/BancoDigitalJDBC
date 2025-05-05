package br.com.meubancodigitaljdbc.execptions;

public class ContaNaoValidaException extends Exception {
    public ContaNaoValidaException(String message) {
        super(message);
    }

    public ContaNaoValidaException(String message, Throwable cause) {
        super(message, cause);
    }
}