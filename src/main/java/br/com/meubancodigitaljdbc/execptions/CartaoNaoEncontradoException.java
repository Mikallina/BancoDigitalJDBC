package br.com.meubancodigitaljdbc.execptions;

public class CartaoNaoEncontradoException extends Exception{
    public CartaoNaoEncontradoException(String message) {
        super(message);
    }
}
