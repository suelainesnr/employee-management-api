package And.tec.CadastroFuncionario.business;

import And.tec.CadastroFuncionario.client.dto.AtualizarBeneficioRequest;
import And.tec.CadastroFuncionario.client.dto.BeneficioRequest;
import And.tec.CadastroFuncionario.client.dto.BeneficioResponse;
import And.tec.CadastroFuncionario.client.BeneficioClient;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BeneficioClientService {


    private final BeneficioClient beneficioClient;

    public ResponseEntity<BeneficioResponse> criarBeneficio(BeneficioRequest beneficioRequest) {
        return beneficioClient.salvarBeneficio(beneficioRequest);
    }

    public ResponseEntity<BeneficioResponse> buscarBeneficioPorId(Long id) {
        return beneficioClient.buscarBeneficioPorId(id);
    }


    public ResponseEntity<List<BeneficioResponse>> listarBeneficiosPorFuncionarioId(Long funcionarioId, Boolean ativo) {
        return beneficioClient.buscarBeneficiosPorFuncionarioId(funcionarioId, ativo);
    }

    public ResponseEntity<BeneficioResponse> atualizarBeneficioPorId(Long id, AtualizarBeneficioRequest atualizarBeneficioRequest) {
        return beneficioClient.atualizarBeneficioPorId(id, atualizarBeneficioRequest);
    }

    public ResponseEntity<BeneficioResponse> desativarBeneficioPorId(Long id) {
        return beneficioClient.desativarBeneficioPorId(id);
    }

    public ResponseEntity<BeneficioResponse> ativarBeneficioPorId(Long id) {
        return beneficioClient.ativarBeneficioPorId(id);
    }


}
