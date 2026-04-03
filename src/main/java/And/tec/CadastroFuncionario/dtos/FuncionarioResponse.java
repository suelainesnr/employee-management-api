package And.tec.CadastroFuncionario.dtos;

import And.tec.CadastroFuncionario.infraEstructure.entity.FuncionarioStatusEnum;
import lombok.*;

import java.time.LocalDate;


public record FuncionarioResponse (
    Long id,
    String nome,
    String cpf,
    String cargo,
    Double salario,
    LocalDate dataDeAdmissao,
    LocalDate dataDeDemissao,
    FuncionarioStatusEnum status

) {}