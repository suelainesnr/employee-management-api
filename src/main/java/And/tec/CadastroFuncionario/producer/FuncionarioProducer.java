package And.tec.CadastroFuncionario.producer;

import And.tec.CadastroFuncionario.client.dto.BeneficioRequest;
import And.tec.CadastroFuncionario.config.BeneficioRabbitMq;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class FuncionarioProducer {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;


    public void enviarMensagem(BeneficioRequest message) {
        String jsonMessage= objectMapper.writeValueAsString(message);
        rabbitTemplate.convertAndSend(BeneficioRabbitMq.EXCHANGE, BeneficioRabbitMq.ROUTING_KEY, jsonMessage);
    }


}
