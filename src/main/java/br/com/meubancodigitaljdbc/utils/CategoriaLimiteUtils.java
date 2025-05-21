package br.com.meubancodigitaljdbc.utils;

import br.com.meubancodigitaljdbc.enuns.Categoria;
import org.springframework.stereotype.Service;

@Service
public class CategoriaLimiteUtils {

    private CategoriaLimiteUtils() {
    } // Evita instanciar

    public static double limiteCredito(Categoria categoria) {
        return switch (categoria) {
            case COMUM -> 1000.00;
            case SUPER -> 5000.00;
            case PREMIUM -> 10000.00;
            default -> throw new IllegalArgumentException("Categoria desconhecida: " + categoria);
        };
    }

    public static double limiteDiario(Categoria categoria) {
        return switch (categoria) {
            case COMUM -> 500.00;
            case SUPER -> 1000.00;
            case PREMIUM -> 5000.00;
            default -> throw new IllegalArgumentException("Categoria desconhecida: " + categoria);
        };
    }
}


