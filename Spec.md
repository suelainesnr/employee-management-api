Spec - CadastroFuncionario
=========================

Visão geral
-----------
Projeto: CadastroFuncionario (Spring Boot)

Objetivo: API REST para gerenciar funcionários (CRUD + reajuste salarial).

Localização do código: src/main/java/And/tec/CadastroFuncionario

Sumário do que este Spec descreve:
- Endpoints expostos
- Formatos de request/response (DTOs)
- Regras de validação e domínio
- Exceções customizadas e mapeamento para HTTP
- Problemas encontrados atualmente no código (status: fixed/pendente)
- Comandos úteis para build e testes


Endpoints
---------
Base path: /api/v1/funcionarios

1) GET /api/v1/funcionarios
- Descrição: lista todos os funcionários
- Resposta: 200 OK
  - Body: List<FuncionarioResponse>

2) GET /api/v1/funcionarios/{id}
- Descrição: busca funcionário por id
- Path parameters:
  - id (Long) - id do funcionário
- Respostas:
  - 200 OK: FuncionarioResponse
  - 404 NOT FOUND: ErroResponse (quando não existe)

3) POST /api/v1/funcionarios
- Descrição: cria um novo funcionário
- Body: FuncionarioRequest (JSON)
- Respostas:
  - 201 CREATED: FuncionarioResponse
  - 400 BAD REQUEST: ErroResponse (validação ou CPF duplicado)

4) PUT /api/v1/funcionarios/{id}
- Descrição: atualiza campos do funcionário
- Body: FuncionarioRequest
- Respostas:
  - 200 OK: FuncionarioResponse
  - 404 NOT FOUND: ErroResponse
  - 400 BAD REQUEST: ErroResponse

5) DELETE /api/v1/funcionarios/{id}
- Descrição: remove o funcionário
- Respostas:
  - 200 OK: sem conteúdo
  - 404 NOT FOUND: ErroResponse
  - 500 INTERNAL SERVER ERROR: ErroResponse (em falha de deleção)

6) GET /api/v1/funcionarios/{id}/ajustar-salario?porcentagem={valor}
- Descrição: recalcula e persiste o salário reajustado e retorna o funcionário atualizado. (Controller atual chama o service `ajustarSalarioPorId` e retorna o DTO do funcionário salvo.)
- Query parameters:
  - porcentagem (BigDecimal) - porcentagem de reajuste (maior que zero)
- Respostas:
  - 200 OK: FuncionarioResponse (entidade atualizada com novo salário)
  - 400 BAD REQUEST: ErroResponse (validação)
  - 404 NOT FOUND: ErroResponse

7) PUT /api/v1/funcionarios/{id}/status
- Descrição: altera apenas o status do funcionário. (Controller atual implementado como `@PutMapping("/{id}/status")` e aceita `novoStatus` como `@RequestParam` do tipo `FuncionarioStatusEnum`.)
- Body/Params:
  - atualmente recebe `novoStatus` como request param (ex.: /{id}/status?novoStatus=INATIVO)
  - existe também o DTO `StatusUpdateRequest` no pacote `dtos/` (ver seção DTOs) — recomenda-se padronizar: preferir PATCH com body JSON {"status":"..."} ou aceitar request param de forma consistente.
- Respostas:
  - 200 OK: FuncionarioResponse (id e novo status)
  - 400 BAD REQUEST: ErroResponse (payload inválido)
  - 404 NOT FOUND: ErroResponse


DTOs
----
FuncionarioRequest (src/main/java/.../dtos/FuncionarioRequest.java)
- nome: String (obrigatório, size 3-120)
- cpf: String (obrigatório, validado como CPF BR)
- cargo: String (opcional)
- salario: Double (obrigatório, positivo)

FuncionarioResponse (src/main/java/.../dtos/FuncionarioResponse.java)
- id: Long
- nome: String
- CPF: String
- cargo: String
- salario: Double
- status: String

StatusUpdateRequest (src/main/java/.../dtos/StatusUpdateRequest.java)
- status: FuncionarioStatusEnum (obrigatório)

ErroResponse (DTO usado em handlers) — ver implementação em exceptions/ErroResponse.java
- message: String
- errorCode: int
- timestamp: LocalDateTime

Entidades e regras de domínio
-----------------------------
Funcionario (entity - src/main/java/.../infraEstructure/entity/Funcionario.java)
- id: Long
- nome: String
- cpf: String (unique, not null)
- cargo: String
- salario: BigDecimal
- dataDeAdmissao: LocalDate
- dataDeDemissao: LocalDate
- status: FuncionarioStatusEnum

Regras implementadas:
- `validarSalarioEPorcentagem(BigDecimal salario, BigDecimal porcentagem)`
  - Lança `IllegalArgumentException` se salário ou porcentagem nulos
  - Lança `IllegalArgumentException` se valores não maiores que zero
- `ajustarSalario(BigDecimal porcentagem)` aplica o reajuste ao campo `salario`

Exceções customizadas e mapeamento HTTP
--------------------------------------
Exceções presentes em `src/main/java/And/tec/CadastroFuncionario/exceptions`:
- `CpfjaCadastradoException` -> RuntimeException (mapped to 400 Bad Request)
- `FuncionarioNotFoundException` -> RuntimeException (mapped to 404 Not Found)
- `FuncionarioException` -> RuntimeException (mapped to 400 Bad Request)
- `SalarioNegativoException` -> RuntimeException (mapped to 400 Bad Request)
- `NegocioException` -> RuntimeException (tratamento presente em `ExceptionHandlerController`, ver observações abaixo)

Handlers: `ExceptionHandlerController` (RestControllerAdvice)
- Mapeamentos principais:
  - `FuncionarioException` -> 400
  - `FuncionarioNotFoundException` -> 404
  - `CpfjaCadastradoException` -> 400
  - `SalarioNegativoException` -> 400
  - `MethodArgumentNotValidException` -> 400 (constrói mensagem concatenada)
  - `IllegalArgumentException` -> 400
  - `NegocioException` -> mapeado no handler (atualmente inconsistente, ver problemas abaixo)
  - `Exception` -> 500 (fallback)
  - `RuntimeException` -> 500 (fallback)

Problemas encontrados (status atual)
-----------------------------------
1) `CpfjaCadastradoException` perda de mensagem no construtor — Status: FIXED
   - Agora o construtor chama `super(mensagem)`.
2) `buscarFuncionarioPorId` lançando `RuntimeException` que gerava 500 em vez de 404 — Status: FIXED
   - `FuncionarioService.buscarFuncionarioPorId` agora lança `FuncionarioNotFoundException("Funcionário não encontrado")`.
3) Lançamentos genéricos de `RuntimeException` reduzidos — Status: PARCIALMENTE FIXED
   - O bloco de deleção agora lança `NegocioException` (handler existe). Ainda manter atenção a lançamentos genéricos em outras camadas.
4) Controller `SalarioReajustado` que usava métodos/variáveis inexistentes — Status: FIXED
   - `FuncionarioController` foi atualizado para usar `funcionarioService.ajustarSalarioPorId(id, porcentagem)` e retorna o DTO do funcionário salvo.
5) Import redundante em `ExceptionHandlerController`: `import java.lang.IllegalArgumentException;` — Status: FIXED (removido).
6) `ResponseEntity.created(null)` — Status: PENDENTE (melhoria)
   - Em `FuncionarioController.salvarFuncionario` ainda existe `return ResponseEntity.created(null).body(funcionarioResponse);` — recomenda-se substituir por `ResponseEntity.status(HttpStatus.CREATED).body(...)` ou fornecer `Location` válido.
7) Handler de `NegocioException` inconsistente — Status: PENDENTE
   - No handler atual `handleNegocioException` o código monta um `ErroResponse` com `errorCode` igual a `HttpStatus.INTERNAL_SERVER_ERROR.value()` mas retorna `ResponseEntity.badRequest()` (HTTP 400). Recomenda-se retornar `ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)` para consistência, ou ajustar o `errorCode` para 400 se desejar mapear para Bad Request.

Comandos úteis
--------------
- Compilar e rodar testes:

```powershell
cd C:\dev\projetos\CadastroFuncionario
.\mvnw.cmd -DskipTests=false test
```

- Rodar a aplicação:

```powershell
cd C:\dev\projetos\CadastroFuncionario
.\mvnw.cmd spring-boot:run
```

- Procurar ocorrências de exceções/lancamentos:

```powershell
Get-ChildItem -Recurse -Include *.java | Select-String -Pattern "throw new " | Select-Object Path,LineNumber,Line
```

Próximos passos sugeridos (prioridade)
-------------------------------------
1) Substituir `ResponseEntity.created(null)` em `FuncionarioController.salvarFuncionario` por um retorno adequado (por exemplo `status(HttpStatus.CREATED).body(...)` ou construir um Location com URI do recurso).
2) Corrigir `ExceptionHandlerController.handleNegocioException` para retornar o status HTTP coerente com o `errorCode` (recomenda-se `HttpStatus.INTERNAL_SERVER_ERROR`).
3) Padronizar o contrato de alteração de status: usar PATCH com body `{ "status": "..." }` (StatusUpdateRequest) ou documentar claramente a utilização do request param atual. (Melhoria de API/UX)
4) Rodar novamente `.
\mvnw.cmd -DskipTests=false test` e garantir que testes passem.


Especificação rápida do contrato do endpoint de reajuste (sugestão final)
------------------------------------------------------------------------
- Método: GET
- URL: /api/v1/funcionarios/{id}/salarioReajustado
- Query param: porcentagem (BigDecimal) — required, > 0
- Sucesso: 200 OK — Body: { "salario": 1234.56 }
- Erros:
  - 400 BAD REQUEST — mensagem de validação
  - 404 NOT FOUND — funcionário não encontrado


Fim do Spec
