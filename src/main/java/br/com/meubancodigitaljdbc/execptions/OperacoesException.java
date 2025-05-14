package br.com.meubancodigitaljdbc.execptions;

public class OperacoesException extends Exception {

    public OperacoesException(String message) {
        super(message);
    }
    public OperacoesException(String message, Throwable cause) {
        super(message,cause);
    }
}
