package br.com.meubancodigitaljdbc.adapters.input.controllers.mapper;


import br.com.meubancodigitaljdbc.adapters.input.controllers.response.CartaoResponse;
import br.com.meubancodigitaljdbc.application.domain.model.Cartao;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartaoMapper {

    // Cartao cartaoToRequest(CartaoRequest cartaoRequest);
    CartaoResponse cartaToResponse(Cartao cartao);

}
