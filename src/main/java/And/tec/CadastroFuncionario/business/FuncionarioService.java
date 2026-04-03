package And.tec.CadastroFuncionario.business;

import And.tec.CadastroFuncionario.client.dto.AtualizarBeneficioRequest;
import And.tec.CadastroFuncionario.client.dto.BeneficioRequest;
import And.tec.CadastroFuncionario.client.dto.BeneficioResponse;
import And.tec.CadastroFuncionario.exceptions.CpfjaCadastradoException;
import And.tec.CadastroFuncionario.exceptions.FuncionarioNotFoundException;
import And.tec.CadastroFuncionario.exceptions.NegocioException;
import And.tec.CadastroFuncionario.infraEstructure.entity.Funcionario;
import And.tec.CadastroFuncionario.infraEstructure.entity.FuncionarioStatusEnum;
import And.tec.CadastroFuncionario.infraEstructure.repository.FuncionarioRepository;
import And.tec.CadastroFuncionario.producer.FuncionarioProducer;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import And.tec.CadastroFuncionario.dtos.AtualizarSalarioRequest;
import And.tec.CadastroFuncionario.infraEstructure.entity.HistoricoAjuste;
import And.tec.CadastroFuncionario.business.HistoricoAjusteService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuncionarioService {
    private final FuncionarioRepository funcionarioRepository;
    private final BeneficioClientService beneficioClientService;
    private final FuncionarioProducer funcionarioProducer;
    private final HistoricoAjusteService historicoAjusteService;

    public Funcionario buscarFuncionarioPorId(Long id){
        return funcionarioRepository.findById(id).orElseThrow(() -> new FuncionarioNotFoundException("Funcionário não encontrado"));
    }
    public Funcionario salvarFuncionario(Funcionario funcionario){
        Funcionario funcionariocpf = funcionarioRepository.findByCpf(funcionario.getCpf()).orElse(null);
        if(funcionariocpf != null) {
            throw new CpfjaCadastradoException("CPF já cadastrado para outro funcionário");
        }
        funcionario.setStatus(FuncionarioStatusEnum.ATIVO);
       return funcionarioRepository.saveAndFlush(funcionario);
    }

    public void deletarFuncionarioPorId(Long id){
        Funcionario funcionario = buscarFuncionarioPorId(id);
        try {
            funcionarioRepository.deleteById(id);
        }catch (Exception e){
            throw new NegocioException("Erro ao deletar funcionário: " + e.getMessage());
        }

    }

    public Funcionario atualizarFuncionarioPorId(Long id, Funcionario funcionario){
            Funcionario funcionarioEntity = buscarFuncionarioPorId(id);
            if(funcionario.getNome() != null) funcionarioEntity.setNome(funcionario.getNome());
            if(funcionario.getCpf() != null) funcionarioEntity.setCpf(funcionario.getCpf());
            if(funcionario.getCargo() != null) funcionarioEntity.setCargo(funcionario.getCargo());
            if(funcionario.getSalario() != null) funcionarioEntity.setSalario(funcionario.getSalario());
            if(funcionario.getStatus() != null) funcionarioEntity.setStatus(funcionario.getStatus());

            return funcionarioRepository.save(funcionarioEntity);
    }
    public List<Funcionario> listarTodosOsFuncionarios(){
            return funcionarioRepository.findAll();
    }

    @org.springframework.transaction.annotation.Transactional
    public Funcionario ajustarSalarioPorId(AtualizarSalarioRequest request){
        Long id = request.idFuncionario();
        BigDecimal percentual = request.percentual();
        Funcionario funcionario = buscarFuncionarioPorId(id);
        // validações de domínio (reaproveitar método da entidade)
        funcionario.validarSalarioEPorcentagem(funcionario.getSalario(), percentual);

        BigDecimal salarioAtual = funcionario.getSalario();
        funcionario.ajustarSalario(percentual);
        Funcionario salvo = funcionarioRepository.save(funcionario);

        // montar e salvar histórico
        HistoricoAjuste historico = HistoricoAjuste.builder()
                .funcionarioId(salvo.getId())
                .percentual(percentual)
                .valorAtualSalario(salarioAtual)
                .valorAposAjuste(salvo.getSalario())
                .motivo(request.motivo())
                .dataAjuste(LocalDateTime.now())
                .build();
        historicoAjusteService.salvar(historico);

        return salvo;
    }
    public Funcionario alterarStatus(Long id, FuncionarioStatusEnum novoStatus){
        Funcionario funcionario = buscarFuncionarioPorId(id);
        funcionario.alterarStatus(novoStatus);
        return funcionarioRepository.save(funcionario);
    }

    public void criarBeneficioParaFuncionario(BeneficioRequest beneficioRequest) {
        Funcionario funcionario = buscarFuncionarioPorId(beneficioRequest.funcionarioId());
        if (funcionario.getStatus() != FuncionarioStatusEnum.ATIVO) {
            throw new NegocioException("Benefícios só podem ser atribuídos a funcionários ativos");
        }
        // Lógica para criar benefício e associar ao funcionário
        // Exemplo: chamar um serviço de benefícios ou salvar diretamente no banco
       funcionarioProducer.enviarMensagem(beneficioRequest);
    }

    public ResponseEntity<List<BeneficioResponse>> listarBeneficiosPorFuncionarioId(Long funcionarioId, Boolean ativo) {
        Funcionario funcionario = buscarFuncionarioPorId(funcionarioId);
        if (funcionario.getStatus() != FuncionarioStatusEnum.ATIVO) {
            throw new NegocioException("Benefícios só podem ser listados para funcionários ativos");
        }
        return beneficioClientService.listarBeneficiosPorFuncionarioId(funcionarioId, ativo);
    }

    public ResponseEntity<BeneficioResponse> buscarBeneficioPorId(Long beneficioId) {
        return beneficioClientService.buscarBeneficioPorId(beneficioId);
    }

    public ResponseEntity<BeneficioResponse> atualizarBeneficioPorId(Long beneficioId, AtualizarBeneficioRequest atualizarBeneficioRequest) {
        return beneficioClientService.atualizarBeneficioPorId(beneficioId, atualizarBeneficioRequest);
    }

    public ResponseEntity<BeneficioResponse> desativarBeneficioPorId(Long beneficioId) {
        return beneficioClientService.desativarBeneficioPorId(beneficioId);
    }

    public ResponseEntity<BeneficioResponse> ativarBeneficioPorId(Long beneficioId) {
        return beneficioClientService.ativarBeneficioPorId(beneficioId);
    }

}
