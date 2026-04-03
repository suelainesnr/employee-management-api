package And.tec.CadastroFuncionario.exceptions;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErroResponse {

    private String message;
    private int errorCode;
    private LocalDateTime timestamp;
}
