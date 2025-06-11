package br.com.meubancodigitaljdbc.application.ports.input.usecases;

import br.com.meubancodigitaljdbc.application.domain.model.Endereco;

public interface CepUserCase {

    Endereco buscarEnderecoPorCep(String cep);


}
