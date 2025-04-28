package br.com.meubancodigitaljdbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

import java.sql.SQLException;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class Main {
    public static void main(String[] args) throws SQLException {


        SpringApplication.run(Main.class, args);
    }
}