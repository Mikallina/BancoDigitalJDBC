package br.com.meubancodigitaljdbc.config;

import br.com.meubancodigitaljdbc.mapper.ContaRowMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
@Configuration
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
