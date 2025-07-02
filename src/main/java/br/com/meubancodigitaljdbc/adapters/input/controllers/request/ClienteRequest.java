package br.com.meubancodigitaljdbc.adapters.input.controllers.request;

import br.com.meubancodigitaljdbc.application.domain.enuns.Categoria;
import br.com.meubancodigitaljdbc.application.domain.model.Conta;
import br.com.meubancodigitaljdbc.application.domain.model.Endereco;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class ClienteRequest {
    private Long idCliente;
    private String nome;
    private String cpf;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dataNascimento;
    private Endereco endereco;
    private Categoria categoria;
    private String email;
    private List<Conta> contas = new ArrayList<>();

}

