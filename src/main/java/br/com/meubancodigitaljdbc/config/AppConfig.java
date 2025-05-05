package br.com.meubancodigitaljdbc.config;

import br.com.meubancodigitaljdbc.mapper.ContaRowMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ContaRowMapper contaRowMapper() {
        return new ContaRowMapper();
    }


}
