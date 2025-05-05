package br.com.meubancodigitaljdbc.execptions;

public class ClienteInvalidoException extends Exception{

    public ClienteInvalidoException(String message) {
        super(message);
    }
}
