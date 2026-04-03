package And.tec.CadastroFuncionario.controller;

import And.tec.CadastroFuncionario.business.FuncionarioService;
import And.tec.CadastroFuncionario.client.dto.AtualizarBeneficioRequest;
import And.tec.CadastroFuncionario.client.dto.BeneficioRequest;
import And.tec.CadastroFuncionario.client.dto.BeneficioResponse;
import And.tec.CadastroFuncionario.dtos.FuncionarioRequest;
import And.tec.CadastroFuncionario.dtos.FuncionarioResponse;
import And.tec.CadastroFuncionario.infraEstructure.entity.Funcionario;
import And.tec.CadastroFuncionario.infraEstructure.entity.FuncionarioStatusEnum;
import And.tec.CadastroFuncionario.mapper.FuncionarioMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/funcionarios")
@Tag(name = "Funcionário", description = "Endpoints para cadastro de funcionários")

public class FuncionarioController {

    private final FuncionarioMapper funcionarioMapper;
    private final FuncionarioService funcionarioService;

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar funcionário por ID",
            description = "Retorna os dados detalhados de um funcionário a partir do ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Funcionário encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FuncionarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID inválido fornecido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Funcionário não encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor",
                    content = @Content
            )
    })
    public ResponseEntity<FuncionarioResponse> buscarFuncionarioPorId(
            @Parameter(
                    description = "ID único do funcionário",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id
    ) {
        Funcionario funcionario = funcionarioService.buscarFuncionarioPorId(id);

        FuncionarioResponse response = funcionarioMapper.toDto(funcionario);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Listar todos os funcionários",
            description = "Retorna uma lista completa de todos os funcionários cadastrados no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de funcionários retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FuncionarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Nenhum funcionário encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor",
                    content = @Content
            )
    })
    public ResponseEntity<List<FuncionarioResponse>> listarTodosOsFuncionarios() {
        List<Funcionario> funcionarios = funcionarioService.listarTodosOsFuncionarios();

        List<FuncionarioResponse> response = funcionarioMapper.toDtoList(funcionarios);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar novo funcionário",
            description = "Cria um novo funcionário a partir dos dados fornecidos na requisição."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Funcionário criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FuncionarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida (ex: CPF já cadastrado ou dados inconsistentes)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito de dados (ex: CPF duplicado)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor",
                    content = @Content
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Objeto contendo os dados necessários para criação do funcionário",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FuncionarioRequest.class)
            )
    )
    public ResponseEntity<FuncionarioResponse> salvarFuncionario(
            @Valid
            @RequestBody FuncionarioRequest funcionarioRequest
    ) {
        Funcionario funcionarioEntity = funcionarioMapper.toEntity(funcionarioRequest);

        Funcionario funcionarioSalvo = funcionarioService.salvarFuncionario(funcionarioEntity);

        FuncionarioResponse funcionarioResponse =
                funcionarioMapper.toDto(funcionarioSalvo);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(funcionarioResponse);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualiza um funcionário por ID",
            description = "Permite atualizar os detalhes de um funcionário específico com base no ID fornecido."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Funcionário atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FuncionarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida, por exemplo, se os dados fornecidos forem inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FuncionarioRequest.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Funcionário não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FuncionarioRequest.class)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados para atualização do funcionário",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FuncionarioRequest.class)
            )
    )
    public ResponseEntity<FuncionarioResponse> atualizarFuncionarioPorId(
            @PathVariable
            @Parameter(description = "ID do funcionário a ser atualizado", example = "1")
            Long id,

            @RequestBody
            @Valid
            FuncionarioRequest funcionarioRequest
    ) {
        Funcionario funcionarioAtualizado =
                funcionarioService.atualizarFuncionarioPorId(
                        id,
                        funcionarioMapper.toEntity(funcionarioRequest)
                );

        return ResponseEntity.ok(
                funcionarioMapper.toDto(funcionarioAtualizado)
        );
    }
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar funcionário por ID",
            description = "Remove um funcionário específico com base no ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Funcionário deletado com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Funcionário não encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor",
                    content = @Content
            )
    })
    public ResponseEntity<Void> deletarFuncionarioPorId(
            @Parameter(description = "ID do funcionário a ser deletado", example = "1")
            @PathVariable Long id
    ) {
        funcionarioService.deletarFuncionarioPorId(id);

        return ResponseEntity.ok().build();
    }


    @PutMapping("/{id}/ajustar-salario")
    @Operation(
            summary = "Ajustar salário por ID",
            description = "Atualiza o salário de um funcionário com base em um percentual e motivo informados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Salário ajustado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FuncionarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida (percentual inválido ou inconsistência de ID)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Funcionário não encontrado",
                    content = @Content
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados para ajuste de salário (percentual e motivo)",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = And.tec.CadastroFuncionario.dtos.AtualizarSalarioRequest.class
                    )
            )
    )
    public ResponseEntity<FuncionarioResponse> ajustarSalario(
            @Parameter(description = "ID do funcionário", example = "1")
            @PathVariable Long id,

            @Valid
            @RequestBody And.tec.CadastroFuncionario.dtos.AtualizarSalarioRequest request
    ) {
        if (request.idFuncionario() != null && !request.idFuncionario().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        And.tec.CadastroFuncionario.dtos.AtualizarSalarioRequest requestWithId =
                new And.tec.CadastroFuncionario.dtos.AtualizarSalarioRequest(
                        id,
                        request.percentual(),
                        request.motivo()
                );

        FuncionarioResponse response =
                funcionarioMapper.toDto(
                        funcionarioService.ajustarSalarioPorId(requestWithId)
                );

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}/status")
    @Operation(
            summary = "Alterar status do funcionário",
            description = "Atualiza o status de um funcionário com base no ID e no novo status informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Status alterado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FuncionarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida (status inválido ou transição não permitida)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Funcionário não encontrado",
                    content = @Content
            )
    })
    public ResponseEntity<FuncionarioResponse> alterarStatus(
            @Parameter(description = "ID do funcionário", example = "1")
            @PathVariable Long id,

            @Parameter(
                    description = "Novo status do funcionário",
                    example = "ATIVO",
                    schema = @Schema(implementation = FuncionarioStatusEnum.class)
            )
            @RequestParam FuncionarioStatusEnum novoStatus
    ) {
        Funcionario funcionarioAtualizado =
                funcionarioService.alterarStatus(id, novoStatus);

        return ResponseEntity.ok(
                funcionarioMapper.toDto(funcionarioAtualizado)
        );
    }


    @PostMapping("/criar-beneficios")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar benefício",
            description = "Cria um benefício vinculado a um funcionário."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Benefício criado com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida ou funcionário não encontrado",
                    content = @Content
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados do benefício e ID do funcionário",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BeneficioRequest.class)
            )
    )
    public ResponseEntity<Void> salvarBeneficio(
            @Valid
            @RequestBody BeneficioRequest beneficioRequest
    ) {
        funcionarioService.criarBeneficioParaFuncionario(beneficioRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/{funcionarioId}/beneficios")
    @Operation(
            summary = "Listar benefícios por funcionário",
            description = "Retorna os benefícios de um funcionário filtrando por status (ativo/inativo)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Benefícios encontrados com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Funcionário não encontrado",
                    content = @Content
            )
    })
    public ResponseEntity<List<BeneficioResponse>> buscarBeneficiosPorFuncionarioId(
            @Parameter(description = "ID do funcionário", example = "1")
            @PathVariable Long funcionarioId,

            @Parameter(description = "Filtrar por ativo/inativo", example = "true")
            @RequestParam Boolean ativo
    ) {
        return funcionarioService
                .listarBeneficiosPorFuncionarioId(funcionarioId, ativo);
    }


    @GetMapping("/beneficios/{id}/")
    @Operation(
            summary = "Buscar benefício por ID",
            description = "Retorna os dados de um benefício específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Benefício encontrado com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Benefício não encontrado",
                    content = @Content
            )
    })
    public ResponseEntity<BeneficioResponse> buscarBeneficioPorId(
            @Parameter(description = "ID do benefício", example = "1")
            @PathVariable Long id
    ) {
        return funcionarioService.buscarBeneficioPorId(id);
    }


    @PutMapping("/{id}/beneficios")
    @Operation(
            summary = "Atualizar benefício",
            description = "Atualiza os dados de um benefício específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Benefício atualizado com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Benefício não encontrado",
                    content = @Content
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados para atualização do benefício",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AtualizarBeneficioRequest.class)
            )
    )
    public ResponseEntity<BeneficioResponse> atualizarBeneficioPorId(
            @Parameter(description = "ID do benefício", example = "1")
            @PathVariable Long id,

            @Valid
            @RequestBody AtualizarBeneficioRequest atualizarBeneficioRequest
    ) {
        return funcionarioService
                .atualizarBeneficioPorId(id, atualizarBeneficioRequest);
    }


    @PutMapping("/beneficios/{id}/desativar")
    @Operation(
            summary = "Desativar benefício",
            description = "Desativa um benefício com base no ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Benefício desativado com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Benefício não encontrado",
                    content = @Content
            )
    })
    public ResponseEntity<BeneficioResponse> desativarBeneficioPorId(
            @Parameter(description = "ID do benefício", example = "1")
            @PathVariable Long id
    ) {
        return funcionarioService.desativarBeneficioPorId(id);
    }


    @PutMapping("/beneficios/{id}/ativar")
    @Operation(
            summary = "Ativar benefício",
            description = "Ativa um benefício com base no ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Benefício ativado com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Benefício não encontrado",
                    content = @Content
            )
    })
    public ResponseEntity<BeneficioResponse> ativarBeneficioPorId(
            @Parameter(description = "ID do benefício", example = "1")
            @PathVariable Long id
    ) {
        return funcionarioService.ativarBeneficioPorId(id);
    }



}
