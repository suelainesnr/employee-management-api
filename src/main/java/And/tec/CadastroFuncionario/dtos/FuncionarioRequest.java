package And.tec.CadastroFuncionario.dtos;

import And.tec.CadastroFuncionario.infraEstructure.entity.FuncionarioStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;


public record FuncionarioRequest(
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 120, message = "nome deve ter entre 3 e 120 caracteres")
    String nome,

    @NotNull(message = "O CPF é obrigatório")
    @CPF(message = "CPF inválido")
    String cpf,

     String cargo,

    @NotNull(message = "O salário é obrigatório")
    @Positive(message = "O salário deve ser maior que zero")
    Double salario,

    LocalDate dataDeAdmissao


) {}