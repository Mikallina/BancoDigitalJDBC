package br.com.meubancodigitaljdbc.application.ports.output.repository;

import br.com.meubancodigitaljdbc.application.domain.model.Cliente;


import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {
    Cliente save(Cliente cliente) throws SQLException;
    Cliente findByCpf(String cpf);
    List<Cliente> findAll();
    Optional<Cliente> findById(Long id);
    void deleteById(Long id);
    void update(Cliente cliente) throws SQLException;
}




