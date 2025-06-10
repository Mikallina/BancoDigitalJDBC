package br.com.meubancodigitaljdbc.adapters.output.producers;


import br.com.meubancodigitaljdbc.application.domain.dto.EmailDTO;
import br.com.meubancodigitaljdbc.application.domain.model.Cliente;
import br.com.meubancodigitaljdbc.configs.RabbitMQConfig;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClienteProducer {

    final RabbitMQConfig rabbitMQConfig;

    final RabbitTemplate rabbitTemplate;

    @Autowired
    public ClienteProducer(RabbitMQConfig rabbitMQConfig, RabbitTemplate rabbitTemplate) {
        this.rabbitMQConfig = rabbitMQConfig;
        this.rabbitTemplate = rabbitTemplate;

    }

    @Value(value ="${broker.queue.email.name}")
    private String routingKey;

    public void publicarMensagemEmail(Cliente cliente){
        Long idCliente = cliente.getIdCliente();
        EmailDTO emailDto = new EmailDTO();
        emailDto.setIdCliente(idCliente);
        emailDto.setNome(cliente.getNome());
        emailDto.setEmailTo(cliente.getEmail());
        emailDto.setTitulo("Cadastro de email realizado com sucesso");
        emailDto.setTexto(cliente.getNome() + ", Seu cadastro de email foi efetuado com sucesso");

        rabbitTemplate.convertAndSend("", routingKey, emailDto);

    }
}
