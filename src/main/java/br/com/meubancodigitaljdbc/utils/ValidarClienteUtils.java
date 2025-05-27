package br.com.meubancodigitaljdbc.utils;

import br.com.meubancodigitaljdbc.dao.ClienteDAO;
import br.com.meubancodigitaljdbc.model.Cliente;
import br.com.meubancodigitaljdbc.model.Endereco;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
@Service
public class ValidarClienteUtils {

    public static boolean validarNome(String nome) {
        return nome != null &&
                nome.length() >= 2 &&
                nome.length() <= 100 &&
                nome.matches("[a-zA-Z ]+");
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
    ClienteDAO clienteDAO;
    public boolean validarCpf(String cpf, boolean isAtualizar, Long clienteId) {
        if (!ValidaCpfUtils.isCPF(cpf)) {
            return false;
        }
        if (isAtualizar) {
            Cliente clienteExistente = clienteDAO.findByCpf(cpf);

            if (clienteExistente != null && !clienteExistente.getIdCliente().equals(clienteId)) {
                return false;
            }
        } else {
            Cliente clienteExistente = clienteDAO.findByCpf(cpf);
            if (clienteExistente != null) {
                return false;
            }
        }
        return true;
    }
}


