package br.com.meubancodigitaljdbc.adapters.output.producers;

import br.com.meubancodigitaljdbc.adapters.input.controllers.request.EmailRequest;
import br.com.meubancodigitaljdbc.application.domain.model.Conta;
import br.com.meubancodigitaljdbc.infrastructure.configs.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ContaProducer {

    final RabbitMQConfig rabbitMQConfig;

    final RabbitTemplate rabbitTemplate;

    @Autowired
    public ContaProducer(RabbitMQConfig rabbitMQConfig, RabbitTemplate rabbitTemplate) {
        this.rabbitMQConfig = rabbitMQConfig;
        this.rabbitTemplate = rabbitTemplate;

    }

    @Value(value ="${broker.queue.email.name}")
    private String routingKey;

    public void publicarMensagemConta(Conta conta) {
        if (conta == null || conta.getCliente() == null) {
            throw new IllegalArgumentException("Conta ou cliente da conta está nulo.");
        }

        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setIdCliente(conta.getCliente().getIdCliente());
        emailRequest.setNome(conta.getCliente().getNome());
        emailRequest.setEmailTo(conta.getCliente().getEmail());
        emailRequest.setTitulo("Conta criada com sucesso");
        emailRequest.setTexto(String.format(
                "Olá %s,\n\nSua conta foi criada com sucesso!\n\nNúmero: %s\nTipo: %s\nSaldo: R$ %.2f",
                conta.getCliente().getNome(),
                conta.getNumConta(),
                conta.getTipoConta(),
                conta.getSaldo()
        ));

        rabbitTemplate.convertAndSend("", routingKey, emailRequest);
    }
}
