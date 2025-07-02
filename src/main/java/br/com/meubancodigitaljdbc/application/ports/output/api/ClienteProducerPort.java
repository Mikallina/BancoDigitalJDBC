package br.com.meubancodigitaljdbc.application.ports.output.api;

import br.com.meubancodigitaljdbc.application.domain.model.Cliente;

public interface ClienteProducerPort {
    void publicarMensagemEmail(Cliente cliente);
}
