package br.com.meubancodigitaljdbc.adapters.input.controllers.response;

import br.com.meubancodigitaljdbc.application.domain.enuns.Categoria;
import br.com.meubancodigitaljdbc.application.domain.model.Conta;
import br.com.meubancodigitaljdbc.application.domain.model.Endereco;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ClienteResponse {

    private Long idCliente;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private Endereco endereco;
    private Categoria categoria;
    private String email;
    private List<Conta> contas;
}
