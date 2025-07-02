package br.com.meubancodigitaljdbc.adapters.input.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/menuprincipal")
public class MenuController {

    @GetMapping("/menuprincipal")
    public String exibirMenu() {
        return "menuprincipal/menuprincipal";
    }

    @GetMapping("/cadastro-cliente")
    public String cadastroCliente() {
        return "menuprincipal/cadastrodeclientes";
    }

    @GetMapping("/criar-conta")
    public String aberturaConta() {
        return "menuprincipal/criarconta"; 
    }
    
    @GetMapping("/operacoes-conta")
    public String operacoesConta() {
        return "menuprincipal/operacoesconta"; 
    }

    @GetMapping("/consultar-cliente")
    public String consultarCliente() {
    	return "menuprincipal/consultarcliente";
    }
    @GetMapping("/operacoes-cartao")
    public String operacoesCartao() {
    	return "menuprincipal/operacoescartao";
    }
    
    @GetMapping("/atualizar-cliente")
    public String atualizarCliente() {
    	return "menuprincipal/atualizar-cliente";
    }
    @GetMapping("/cambio")
    public String cambio() {
    	return "menuprincipal/cambio";
    }
}
