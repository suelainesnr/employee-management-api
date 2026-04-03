package And.tec.CadastroFuncionario.client;

import And.tec.CadastroFuncionario.client.dto.AtualizarBeneficioRequest;
import And.tec.CadastroFuncionario.client.dto.BeneficioRequest;
import And.tec.CadastroFuncionario.client.dto.BeneficioResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "beneficio-service", url = "http://localhost:8080/api/v1/beneficios")
public interface BeneficioClient {

    @PostMapping
    ResponseEntity<BeneficioResponse> salvarBeneficio(@RequestBody @Valid BeneficioRequest beneficioRequest);

    @GetMapping("/{id}")
    ResponseEntity<BeneficioResponse> buscarBeneficioPorId(@PathVariable("id") Long id);

    @GetMapping("/funcionario/{funcionarioId}")
    ResponseEntity<List<BeneficioResponse>> buscarBeneficiosPorFuncionarioId(@PathVariable("funcionarioId") Long funcionarioId, @RequestParam("ativo") Boolean ativo);

    @PutMapping("/{id}")
    ResponseEntity<BeneficioResponse> atualizarBeneficioPorId(@PathVariable Long id, @RequestBody @Valid AtualizarBeneficioRequest atualizarBeneficioRequest);

    @PutMapping("/{id}/desativar")
    ResponseEntity<BeneficioResponse> desativarBeneficioPorId(@PathVariable Long id);

    @PutMapping("/{id}/ativar")
    ResponseEntity<BeneficioResponse> ativarBeneficioPorId(@PathVariable Long id);

}