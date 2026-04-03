package And.tec.CadastroFuncionario.controller;

import And.tec.CadastroFuncionario.business.DependenteService;
import And.tec.CadastroFuncionario.dtos.DependenteRequest;
import And.tec.CadastroFuncionario.dtos.DependenteResponse;
import And.tec.CadastroFuncionario.infraEstructure.entity.Dependente;
import And.tec.CadastroFuncionario.mapper.DependenteMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dependentes")

public class DependenteController {

    private final DependenteMapper dependenteMapper;
    private final DependenteService dependenteService;

    @PostMapping("/{funcionarioId}")
    @Operation(
            summary = "Cadastrar dependente",
            description = "Cria um novo dependente e o associa a um funcionário existente através do ID informado na URL. " +
                    "O dependente será persistido e retornado com seus dados completos.",
            tags = {"Dependentes"}
    )
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "201",
                    description = "Dependente criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DependenteResponse.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de resposta",
                                    value = """
                                        {
                                          "id": 1,
                                          "nome": "João da Silva",
                                          "dataNascimento": "2015-08-10",
                                          "parentesco": "FILHO"
                                        }
                                        """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida. Pode ocorrer quando os dados do dependente são inválidos ou o ID do funcionário não existe.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(
                                    value = "Erro de validação: nome do dependente é obrigatório."
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Funcionário não encontrado para o ID informado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(
                                    value = "Funcionário com ID 10 não encontrado."
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    public ResponseEntity<DependenteResponse> salvarDependente(

            @Parameter(
                    description = "ID do funcionário ao qual o dependente será vinculado",
                    example = "1",
                    required = true
            )
            @PathVariable
            Long funcionarioId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objeto contendo os dados do dependente a ser criado",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = DependenteRequest.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de requisição",
                                    value = """
                                        {
                                          "nome": "João da Silva",
                                          "dataNascimento": "2015-08-10",
                                          "parentesco": "FILHO"
                                        }
                                        """
                            )
                    )
            )
            @RequestBody
            @Valid
            DependenteRequest dependenteRequest
    ) {
        Dependente dependenteSalvo = dependenteService.salvarDependente(
                funcionarioId,
                dependenteMapper.toEntity(dependenteRequest)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dependenteMapper.toDto(dependenteSalvo));
    }


    @GetMapping("/{funcionarioId}")
    @Operation(
            summary = "Listar dependentes por funcionário",
            description = "Retorna uma lista de todos os dependentes associados a um funcionário específico com base no ID informado.",
            tags = {"Dependentes"}
    )
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de dependentes retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = DependenteResponse.class)),
                            examples = @ExampleObject(
                                    value = """
                                        [
                                          {
                                            "id": 1,
                                            "nome": "Maria Silva",
                                            "dataNascimento": "2010-05-10",
                                            "parentesco": "FILHO"
                                          }
                                        ]
                                        """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Funcionário não encontrado",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor"
            )
    })
    public ResponseEntity<List<DependenteResponse>> buscarDependentesPorFuncionarioId(
            @Parameter(
                    description = "ID do funcionário",
                    example = "1",
                    required = true
            )
            @PathVariable Long funcionarioId
    ) {
        List<Dependente> dependentes = dependenteService.buscarDependentesPorFuncionarioId(funcionarioId);
        return ResponseEntity.ok(dependenteMapper.toDtoList(dependentes));
    }

    @GetMapping("/{dependenteId}/{funcionarioId}")
    @Operation(
            summary = "Buscar dependente por ID",
            description = "Retorna os dados de um dependente específico associado a um funcionário.",
            tags = {"Dependentes"}
    )
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "200",
                    description = "Dependente encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DependenteResponse.class)
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Dependente ou funcionário não encontrado",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor"
            )
    })
    public ResponseEntity<DependenteResponse> buscarDependentePorId(

            @Parameter(description = "ID do dependente", example = "1", required = true)
            @PathVariable Long dependenteId,

            @Parameter(description = "ID do funcionário", example = "1", required = true)
            @PathVariable Long funcionarioId
    ) {
        Dependente dependente = dependenteService.buscarDependente(dependenteId, funcionarioId);
        return ResponseEntity.ok(dependenteMapper.toDto(dependente));
    }

    @PutMapping("/{dependenteId}/{funcionarioId}")
    @Operation(
            summary = "Atualizar dependente",
            description = "Atualiza os dados de um dependente existente associado a um funcionário.",
            tags = {"Dependentes"}
    )
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "200",
                    description = "Dependente atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DependenteResponse.class)
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos para atualização",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Dependente ou funcionário não encontrado",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor"
            )
    })
    public ResponseEntity<DependenteResponse> atualizarDependentePorId(

            @Parameter(description = "ID do dependente", example = "1", required = true)
            @PathVariable Long dependenteId,

            @Parameter(description = "ID do funcionário", example = "1", required = true)
            @PathVariable Long funcionarioId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados atualizados do dependente",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = DependenteRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                          "nome": "Maria Atualizada",
                                          "dataNascimento": "2011-06-15",
                                          "parentesco": "FILHO"
                                        }
                                        """
                            )
                    )
            )
            @RequestBody @Valid DependenteRequest dependenteRequest
    ) {
        Dependente dependenteAtualizado = dependenteService.atualizarDependentePorId(
                dependenteId,
                funcionarioId,
                dependenteRequest
        );
        return ResponseEntity.ok(dependenteMapper.toDto(dependenteAtualizado));
    }
    @DeleteMapping("/{dependenteId}/{funcionarioId}")
    @Operation(
            summary = "Remover dependente",
            description = "Remove um dependente específico associado a um funcionário.",
            tags = {"Dependentes"}
    )
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "204",
                    description = "Dependente removido com sucesso"
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Dependente ou funcionário não encontrado",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor"
            )
    })
    public ResponseEntity<Void> deletarDependentePorId(

            @Parameter(description = "ID do dependente", example = "1", required = true)
            @PathVariable Long dependenteId,

            @Parameter(description = "ID do funcionário", example = "1", required = true)
            @PathVariable Long funcionarioId
    ) {
        dependenteService.deletarDependentePorId(dependenteId, funcionarioId);
        return ResponseEntity.noContent().build();
    }


}
