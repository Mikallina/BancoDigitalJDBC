package br.com.meubancodigitaljdbc.infrastructure.configs;

import br.com.meubancodigitaljdbc.adapters.output.mapper.ContaRowMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
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

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        return new Jackson2ObjectMapperBuilder().modules(new JavaTimeModule());
    }
}
