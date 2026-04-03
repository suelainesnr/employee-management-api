package And.tec.CadastroFuncionario.business;

import And.tec.CadastroFuncionario.infraEstructure.entity.HistoricoAjuste;
import And.tec.CadastroFuncionario.infraEstructure.repository.HistoricoAjusteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoricoAjusteService {
    private final HistoricoAjusteRepository historicoAjusteRepository;

    @Transactional
    public HistoricoAjuste salvar(HistoricoAjuste historicoAjuste){
        return historicoAjusteRepository.save(historicoAjuste);
    }

    public List<HistoricoAjuste> listarPorFuncionario(Long funcionarioId){
        return historicoAjusteRepository.findByFuncionarioIdOrderByDataAjusteDesc(funcionarioId);
    }
}

