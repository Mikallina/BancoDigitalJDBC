package br.com.meubancodigitaljdbc.execptions;

public class OperacoesExceptions extends Exception {

    public OperacoesExceptions(String message) {
        super(message);
    }
    public OperacoesExceptions(String message, Throwable cause) {
        super(message,cause);
    }
}
