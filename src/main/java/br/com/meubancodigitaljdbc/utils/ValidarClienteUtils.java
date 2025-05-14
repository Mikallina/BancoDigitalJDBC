package br.com.meubancodigitaljdbc.utils;

import br.com.meubancodigitaljdbc.model.Endereco;

import java.time.LocalDate;

public class ValidarClienteUtils {

    public static boolean validarNome(String nome) {
        return nome != null &&
                nome.length() >= 2 &&
                nome.length() <= 100 &&
                nome.matches("[a-zA-Z ]+");
    }

    public static boolean validarEndereco(Endereco endereco) {
        return endereco != null &&
                !endereco.getLogradouro().isEmpty() &&
                endereco.getNumero() != null &&
                !endereco.getBairro().isEmpty() &&
                !endereco.getLocalidade().isEmpty();
    }

    public static boolean validarDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null || dataNascimento.isAfter(LocalDate.now())) {
            return false;
        }
        return calcularIdade(dataNascimento) >= 18;
    }

    private static int calcularIdade(LocalDate dataNascimento) {
        LocalDate hoje = LocalDate.now();
        int idade = hoje.getYear() - dataNascimento.getYear();
        if (dataNascimento.getMonthValue() > hoje.getMonthValue() ||
                (dataNascimento.getMonthValue() == hoje.getMonthValue() &&
                        dataNascimento.getDayOfMonth() > hoje.getDayOfMonth())) {
            idade--;
        }
        return idade;
    }
}


