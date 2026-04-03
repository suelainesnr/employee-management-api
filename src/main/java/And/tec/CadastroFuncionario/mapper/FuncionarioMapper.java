package And.tec.CadastroFuncionario.mapper;

import And.tec.CadastroFuncionario.dtos.FuncionarioRequest;
import And.tec.CadastroFuncionario.dtos.FuncionarioResponse;
import And.tec.CadastroFuncionario.infraEstructure.entity.Funcionario;
import And.tec.CadastroFuncionario.infraEstructure.vo.Cpf;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface FuncionarioMapper {
    @Mapping(target = "cpf", source = "cpf")
    Funcionario toEntity(FuncionarioRequest funcionarioRequest);
    @Mapping(target = "cpf", source = "cpf")
    FuncionarioResponse toDto(Funcionario funcionario);

     List<FuncionarioResponse> toDtoList(List<Funcionario> funcionarios);

    // 🔥 String -> VO
    default Cpf map(String cpf) {
        return cpf == null ? null :  Cpf.of(cpf);
    }

    // 🔥 VO -> String
    default String map(Cpf cpf) {
        return cpf == null ? null : cpf.getValor();
    }

}
