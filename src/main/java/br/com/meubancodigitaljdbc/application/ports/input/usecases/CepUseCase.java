package br.com.meubancodigitaljdbc.application.ports.input.usecases;

import br.com.meubancodigitaljdbc.application.domain.model.Endereco;

public interface CepUseCase {

    Endereco buscarEnderecoPorCep(String cep);


}
