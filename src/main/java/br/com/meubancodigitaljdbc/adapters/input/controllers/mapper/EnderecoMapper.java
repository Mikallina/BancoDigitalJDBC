package br.com.meubancodigitaljdbc.adapters.input.controllers.mapper;

import br.com.meubancodigitaljdbc.adapters.input.controllers.response.EnderecoResponse;
import br.com.meubancodigitaljdbc.application.domain.model.Endereco;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface EnderecoMapper {

    EnderecoResponse endereco(Endereco endereco);

}
