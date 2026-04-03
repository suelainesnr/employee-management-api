package And.tec.CadastroFuncionario.dtos;

import And.tec.CadastroFuncionario.infraEstructure.entity.DependenteTipoEnum;
import jakarta.validation.constraints.NotNull;

public record DependenteUpdateRequest(

        @NotNull
        DependenteTipoEnum grauDeParentesco,
        @NotNull
        String nome
) {
}
