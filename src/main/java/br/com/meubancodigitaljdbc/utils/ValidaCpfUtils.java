package br.com.meubancodigitaljdbc.utils;

import org.springframework.stereotype.Service;

import java.util.InputMismatchException;



public final class ValidaCpfUtils {

    private ValidaCpfUtils() {
        throw new UnsupportedOperationException("Classe utilitária");
    }

    public static boolean isCPF(String CPF) {
        if (CPF == null || CPF.length() != 11 ||
                CPF.equals("00000000000") || CPF.equals("11111111111") ||
                CPF.equals("22222222222") || CPF.equals("33333333333") ||
                CPF.equals("44444444444") || CPF.equals("55555555555") ||
                CPF.equals("66666666666") || CPF.equals("77777777777") ||
                CPF.equals("88888888888") || CPF.equals("99999999999")) {
            return false;
        }

        try {
            int sm = 0, peso = 10, num, r;
            char dig10, dig11;

            for (int i = 0; i < 9; i++) {
                num = CPF.charAt(i) - '0';
                sm += num * peso--;
            }

            r = 11 - (sm % 11);
            dig10 = (r == 10 || r == 11) ? '0' : (char)(r + '0');

            sm = 0;
            peso = 11;
            for (int i = 0; i < 10; i++) {
                num = CPF.charAt(i) - '0';
                sm += num * peso--;
            }

            r = 11 - (sm % 11);
            dig11 = (r == 10 || r == 11) ? '0' : (char)(r + '0');

            return dig10 == CPF.charAt(9) && dig11 == CPF.charAt(10);
        } catch (InputMismatchException | NumberFormatException e) {
            return false;
        }
    }

    public static String imprimeCPF(String CPF) {
        if (CPF == null || CPF.length() != 11) return CPF;
        return CPF.substring(0, 3) + "." + CPF.substring(3, 6) + "." +
                CPF.substring(6, 9) + "-" + CPF.substring(9, 11);
    }
}