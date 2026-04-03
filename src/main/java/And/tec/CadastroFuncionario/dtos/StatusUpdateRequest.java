package And.tec.CadastroFuncionario.dtos;

import And.tec.CadastroFuncionario.infraEstructure.entity.FuncionarioStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;


public record StatusUpdateRequest (
    @NotNull
    FuncionarioStatusEnum status
) {}