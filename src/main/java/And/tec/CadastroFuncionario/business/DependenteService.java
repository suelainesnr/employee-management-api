package And.tec.CadastroFuncionario.business;

import And.tec.CadastroFuncionario.dtos.DependenteRequest;
import And.tec.CadastroFuncionario.exceptions.DependenteNotFoundException;
import And.tec.CadastroFuncionario.exceptions.FuncionarioNotFoundException;
import And.tec.CadastroFuncionario.infraEstructure.entity.Dependente;
import And.tec.CadastroFuncionario.infraEstructure.entity.Funcionario;
import And.tec.CadastroFuncionario.infraEstructure.repository.DependenteRepository;
import And.tec.CadastroFuncionario.infraEstructure.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DependenteService {
    private final DependenteRepository dependenteRepository;
    private final FuncionarioRepository funcionarioRepository;

    public List<Dependente> buscarDependentesPorFuncionarioId(Long funcionarioId) {
       funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() ->
                        new FuncionarioNotFoundException("Funcionário não encontrado"));

        return dependenteRepository.findByFuncionarioId(funcionarioId);
    }
    public Dependente buscarDependente(Long dependenteId,Long funcionarioId) {
        return dependenteRepository.findByIdAndFuncionarioId(dependenteId, funcionarioId)
                .orElseThrow(() ->
                        new DependenteNotFoundException("Dependente não encontrado para o funcionário especificado"));

    }

    public Dependente salvarDependente(Long funcionarioId, Dependente dependente) {

        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() ->
                        new FuncionarioNotFoundException("Funcionário não encontrado"));
        dependente.setFuncionario(funcionario);
        return dependenteRepository.save(dependente);
    }

    public void deletarDependentePorId(Long id, Long funcionarioId) {
        dependenteRepository.findByIdAndFuncionarioId(id, funcionarioId)
                .orElseThrow(() ->
                        new DependenteNotFoundException("Dependente não encontrado para o funcionário especificado"));
        dependenteRepository.deleteById(id);
    }

    public Dependente atualizarDependentePorId(Long dependenteId, Long funcionarioId, DependenteRequest dependenteRequest) {
        Dependente dependenteEntity = dependenteRepository.findByIdAndFuncionarioId(dependenteId, funcionarioId)
                .orElseThrow(() ->
                        new DependenteNotFoundException("Dependente não encontrado para o funcionário especificado"));
        if (dependenteRequest.nome()!= null) dependenteEntity.setNome(dependenteRequest.nome());
        if (dependenteRequest.grauDeParentesco()!= null) dependenteEntity.setGrauDeParentesco(dependenteRequest.grauDeParentesco());
        return dependenteRepository.save(dependenteEntity);
    }

    public List<Dependente> listarTodosOsDependentes(){
        return dependenteRepository.findAll();
    }




}
