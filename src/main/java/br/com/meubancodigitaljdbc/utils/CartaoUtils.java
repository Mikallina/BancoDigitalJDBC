package br.com.meubancodigitaljdbc.utils;
import java.util.Random;

public class CartaoUtils {

    private static final Random RANDOM = new Random();

    public String gerarNumeroAleatorio(int tamanho) {
        StringBuilder numero = new StringBuilder();

        for (int i = 0; i < tamanho; i++) {
            numero.append(RANDOM.nextInt(10));
        }

        return numero.toString();
    }

    public int calcularDigitoLuhn(String numeroParcial) {
        int soma = 0;
        boolean alternar = false;
        for (int i = numeroParcial.length() - 1; i >= 0; i--) {
            int digito = Integer.parseInt(String.valueOf(numeroParcial.charAt(i)));

            if (alternar) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9;
                }
            }

            soma += digito;
            alternar = !alternar;
        }

        return (10 - (soma % 10)) % 10;
    }
}
