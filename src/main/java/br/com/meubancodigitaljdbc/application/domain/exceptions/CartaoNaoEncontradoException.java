package br.com.meubancodigitaljdbc.application.domain.exceptions;

public class CartaoNaoEncontradoException extends Exception{
    public CartaoNaoEncontradoException(String message) {
        super(message);
    }
}
