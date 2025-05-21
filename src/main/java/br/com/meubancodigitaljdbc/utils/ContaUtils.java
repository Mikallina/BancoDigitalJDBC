package br.com.meubancodigitaljdbc.utils;

import br.com.meubancodigitaljdbc.enuns.TipoConta;
import org.springframework.stereotype.Service;

@Service
public class ContaUtils {

    public String gerarNumeroConta(int agencia, TipoConta tipoConta) {
        StringBuilder conta = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            conta.append((int) (Math.random() * 10));
        }

        // Calcular o dígito verificador (8º número) usando módulo 11
        int soma = 0;
        for (int i = 0; i < conta.length(); i++) {
            soma += (conta.charAt(i) - '0') * (i + 2);
        }

        int dv = soma % 11;
        if (dv == 10) {
            dv = 0;
        }

        conta.append(dv);

        String tipoContaString = tipoConta.getTipoAbreviado();
        return String.format("%s-%04d-%s", tipoContaString, agencia, conta.toString());
    }

}
