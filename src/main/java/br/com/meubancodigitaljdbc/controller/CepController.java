package br.com.meubancodigitaljdbc.controller;

import br.com.meubancodigitaljdbc.model.Endereco;
import br.com.meubancodigitaljdbc.service.CepService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CepController {
	
	private final CepService cepService;

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    @GetMapping("/buscar-endereco/{cep}")
    public Endereco buscarEndereco(@PathVariable String cep) {
        return cepService.buscarEnderecoPorCep(cep);
    }

}
