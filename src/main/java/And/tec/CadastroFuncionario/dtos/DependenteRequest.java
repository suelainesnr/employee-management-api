package And.tec.CadastroFuncionario.dtos;

import And.tec.CadastroFuncionario.infraEstructure.entity.DependenteTipoEnum;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record DependenteRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 120, message = "nome deve ter entre 3 e 120 caracteres")
        String nome,
        @NotNull(message = "O CPF é obrigatório")
        @CPF(message = "CPF inválido")
        String cpf,
        LocalDate dataDeNascimento,
        @NotNull(message = "O tipo é obrigatório")
        DependenteTipoEnum grauDeParentesco

) {
}
