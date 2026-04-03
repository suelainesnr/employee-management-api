package And.tec.CadastroFuncionario.infraEstructure.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status possíveis de um funcionário")
public enum FuncionarioStatusEnum {
    @Schema(description = "Funcionário ativo e em exercício") ATIVO,
    @Schema(description = "Funcionário desligado da empresa") INATIVO,
    @Schema (description = "Funcionário afastado temporariamente") SUSPENSO
}
