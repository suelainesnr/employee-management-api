package And.tec.CadastroFuncionario.infraEstructure.repository;

import And.tec.CadastroFuncionario.infraEstructure.entity.Dependente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DependenteRepository extends JpaRepository <Dependente, Long> {

    List <Dependente>findByFuncionarioId(Long funcionarioId);

    Optional <Dependente>findByIdAndFuncionarioId(Long id, Long funcionarioId);
}
