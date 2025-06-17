package br.com.meubancodigitaljdbc.adapters.input.controllers.mapper;

import br.com.meubancodigitaljdbc.adapters.input.controllers.request.ClienteRequest;
import br.com.meubancodigitaljdbc.adapters.input.controllers.response.ClienteResponse;
import br.com.meubancodigitaljdbc.application.domain.model.Cliente;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface ClienteMapper {

    Cliente toRequest(ClienteRequest clienteRequest);
    ClienteResponse toResponse(Cliente cliente);

}
