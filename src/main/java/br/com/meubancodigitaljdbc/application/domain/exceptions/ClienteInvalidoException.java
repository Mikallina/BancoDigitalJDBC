package br.com.meubancodigitaljdbc.application.domain.exceptions;

public class ClienteInvalidoException extends Exception{

    public ClienteInvalidoException(String message) {
        super(message);
    }
}
