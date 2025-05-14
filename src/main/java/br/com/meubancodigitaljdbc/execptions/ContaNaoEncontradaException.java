package br.com.meubancodigitaljdbc.execptions;



    public class ContaNaoEncontradaException extends Exception {
        public ContaNaoEncontradaException(String msg) {
            super(msg);
        }


    public ContaNaoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }
}
