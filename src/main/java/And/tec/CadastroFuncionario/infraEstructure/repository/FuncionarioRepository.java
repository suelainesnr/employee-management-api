package And.tec.CadastroFuncionario.infraEstructure.repository;

import And.tec.CadastroFuncionario.infraEstructure.entity.Funcionario;
import And.tec.CadastroFuncionario.infraEstructure.vo.Cpf;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {


    Optional<Funcionario> findByCpf( Cpf cpf);
}
