package And.tec.CadastroFuncionario.client.dto;



import java.math.BigDecimal;

public record BeneficioResponse(
        Long id,
        DescricaoBeneficioEnum descricao,
        Boolean ativo,
        Long funcionarioId,
        BigDecimal porcentagemEmpresa,
        BigDecimal porcentagemFuncionario

) {
}
