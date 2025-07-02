package br.com.meubancodigitaljdbc.adapters.input.controllers.mapper;


import br.com.meubancodigitaljdbc.adapters.input.controllers.request.ContaRequest;
import br.com.meubancodigitaljdbc.adapters.input.controllers.response.ContaResponse;
import br.com.meubancodigitaljdbc.application.domain.model.Conta;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContaMapper {


    ContaResponse contaToResponse(Conta conta);
}
