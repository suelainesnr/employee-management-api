package And.tec.CadastroFuncionario.infraEstructure.repository;

import And.tec.CadastroFuncionario.infraEstructure.entity.HistoricoAjuste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoAjusteRepository extends JpaRepository<HistoricoAjuste, Long> {
    List<HistoricoAjuste> findByFuncionarioIdOrderByDataAjusteDesc(Long funcionarioId);
}

