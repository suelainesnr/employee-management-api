package And.tec.CadastroFuncionario.client.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;


import java.math.BigDecimal;

public record BeneficioRequest (
        @NotNull(message = "A descrição do benefício é obrigatória")
       DescricaoBeneficioEnum descricao,
        @NotNull(message = "O ID do funcionário é obrigatório")
    Long funcionarioId,
        @DecimalMin(value = "0.00", message = "A porcentagem da empresa deve ser no mínimo 0%")
        @DecimalMax(value = "100.00", message = "A porcentagem da empresa deve ser no máximo 100%")
        @NotNull
    BigDecimal porcentagemEmpresa,
        @DecimalMin(value = "0.00", message = "A porcentagem do funcionário deve ser no mínimo 0%")
        @DecimalMax(value = "100.00", message = "A porcentagem do funcionário deve ser no máximo 100%")
        @NotNull
    BigDecimal porcentagemFuncionario



) {
}
