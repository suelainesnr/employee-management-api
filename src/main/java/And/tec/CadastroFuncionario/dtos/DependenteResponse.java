package And.tec.CadastroFuncionario.dtos;

import And.tec.CadastroFuncionario.infraEstructure.entity.DependenteTipoEnum;

import java.time.LocalDate;

public record DependenteResponse(
        Long id,
        String nome,
        String cpf,
        LocalDate dataDeNascimento,
        DependenteTipoEnum grauDeParentesco,
        Long funcionarioId
) {
}
