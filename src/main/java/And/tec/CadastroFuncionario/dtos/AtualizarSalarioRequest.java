package And.tec.CadastroFuncionario.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AtualizarSalarioRequest(
        @NotNull(message = "O id do funcionário é obrigatório")
        Long idFuncionario,
        @NotNull(message = "O percentual é obrigatório")
        @Positive(message = "O percentual deve ser maior que zero")
        BigDecimal percentual,
        @NotNull(message = "O motivo é obrigatório")
        @Size(min = 5, max = 500, message = "Motivo deve ter entre 5 e 500 caracteres")
        String motivo
) {
}

