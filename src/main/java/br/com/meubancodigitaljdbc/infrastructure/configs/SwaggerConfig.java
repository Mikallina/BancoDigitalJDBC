package br.com.meubancodigitaljdbc.infrastructure.configs;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Banco Digital")
                        .version("1.0")
                        .description("Documentação da API do Banco Digital com Swagger")
                        .contact(new Contact()
                                .name("Michelle Borges")
                                .email("mi_borges@msn.com")
                                .url("https://github.com/Mikallina"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}


