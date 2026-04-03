package And.tec.CadastroFuncionario.controller;

import And.tec.CadastroFuncionario.business.HistoricoAjusteService;
import And.tec.CadastroFuncionario.infraEstructure.entity.HistoricoAjuste;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/funcionarios")
@Tag(name = "Histórico de Ajustes", description = "Endpoints responsáveis pelo gerenciamento do histórico de ajustes dos funcionários")
public class HistoricoAjusteController {

    private final HistoricoAjusteService historicoAjusteService;

    @GetMapping("/{id}/ajustes")
    @Operation(
            summary = "Listar ajustes por funcionário",
            description = "Retorna a lista de ajustes realizados para um funcionário específico com base no ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de ajustes retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = HistoricoAjuste.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida (ex: ID nulo ou formato incorreto)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Funcionário não encontrado para o ID informado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor",
                    content = @Content
            )
    })
    public ResponseEntity<List<HistoricoAjuste>> listarAjustesPorFuncionarioId(
            @Parameter(
                    description = "ID do funcionário para buscar o histórico de ajustes",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {

        List<HistoricoAjuste> ajustes = historicoAjusteService.listarPorFuncionario(id);
        return ResponseEntity.ok(ajustes);
    }
}

