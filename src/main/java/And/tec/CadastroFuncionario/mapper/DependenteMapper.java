package And.tec.CadastroFuncionario.mapper;

import And.tec.CadastroFuncionario.dtos.DependenteRequest;
import And.tec.CadastroFuncionario.dtos.DependenteResponse;
import And.tec.CadastroFuncionario.infraEstructure.entity.Dependente;
import And.tec.CadastroFuncionario.infraEstructure.vo.Cpf;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface DependenteMapper {
    @Mapping(target = "cpf", source = "cpf")
    Dependente toEntity(DependenteRequest dependenteRequest);
    @Mapping(target = "cpf", source = "cpf")
    @Mapping(target = "funcionarioId", source = "funcionario.id")
    DependenteResponse toDto(Dependente dependente);

    List<DependenteResponse> toDtoList(List<Dependente> dependentes);

    // 🔥 String -> VO
    default Cpf map(String cpf) {
        return cpf == null ? null :  Cpf.of(cpf);
    }

    // 🔥 VO -> String
    default String map(Cpf cpf) {
        return cpf == null ? null : cpf.getValor();
    }
}
