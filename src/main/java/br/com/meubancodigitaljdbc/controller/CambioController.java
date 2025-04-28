package br.com.meubancodigitaljdbc.controller;
import java.util.Map;

import br.com.meubancodigitaljdbc.service.CambioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cambio")
public class CambioController {

    @Autowired
    private CambioService cambioService;

    @GetMapping("/converter")
    public ResponseEntity<?> converter(
            @RequestParam double valor,
            @RequestParam String moedaBase,
            @RequestParam String moedaDestino) {
        try {
            double valorConvertido = cambioService.converterMoeda(valor, moedaBase, moedaDestino);
            return ResponseEntity.ok("Valor convertido: R$ " + valorConvertido);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro: " + e.getMessage());
        }
    }
    @GetMapping("/moedas")
    public ResponseEntity<?> listarMoedas() {
        try {
            Map<String, String> moedas = cambioService.obterMoedasDisponiveis();
            return ResponseEntity.ok(moedas);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao obter as moedas disponíveis: " + e.getMessage());
        }
    }
    
}