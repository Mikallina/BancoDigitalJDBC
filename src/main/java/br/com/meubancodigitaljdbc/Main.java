package br.com.meubancodigitaljdbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableCaching
public class Main {
    public static void main(String[] args)  {


        SpringApplication.run(Main.class, args);
    }
}