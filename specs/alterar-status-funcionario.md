# Especificação: Alterar status do funcionário

Versão: 1.0
Data: 2026-02-25
Autor: Plan (gerado automaticamente)

## 1. Resumo

Objetivo: Implementar um endpoint que permita alterar apenas o campo `status` do funcionário que está no contexto (identificado por `id`), sem alterar quaisquer outros campos (nome, cpf, salário, datas, etc.). A alteração deve ser validada contra o Enum de status existente e respeitar regras de negócio mínimas (idempotência e transições permitidas).

## 2. Contexto e motivação

Atualmente a aplicação possui operações que criam e atualizam funcionários. Precisamos de uma operação leve e explícita para mudar somente o status do funcionário — útil para workflows onde RH ou sistemas externos só devem ativar/inativar/suspender colaboradores sem tocar outras informações.

Observações importantes sobre o repositório atual:
- Enum existente: `And.tec.CadastroFuncionario.infraEstructure.entity.FuncionarioStatusEnum` (valores observados no código: `ATIVO`, `INATIVO`, `SUSPENSO`).
- Se for necessário suportar valores adicionais (ex.: `DEMITIDO`), isso deve ser tratado em ticket separado e o enum atualizado.

Decisão tomada nesta especificação:
- O endpoint NÃO altera `dataDeAdmissao` nem `dataDeDemissao`. Se for necessário alterar datas ao demitir/recontratar, será feito em endpoint separado.

## 3. Contrato HTTP / REST

- Endpoint: PATCH /api/funcionarios/{id}/status
- Método HTTP: PATCH (parcial — apenas status)
- Headers:
  - Content-Type: application/json
  - Accept: application/json
  - Authorization: Bearer <token> (quando aplicável)
- Path params:
  - id: Long (ID do funcionário)

### Corpo da requisição (JSON)

Exemplo mínimo:
{
  "status": "ATIVO"
}

JSON Schema (descritivo):
- status: string, obrigatório, valor deve pertencer ao enum `FuncionarioStatusEnum` (ATIVO|INATIVO|SUSPENSO)

### Respostas

- 200 OK
  - Body: `StatusUpdateResponse` (ver seção DTOs) com os valores atuais do funcionário (pelo menos id e status). Exemplo:
  {
    "id": 123,
    "status": "INATIVO"
  }

- 204 No Content
  - Opcional: usada se preferirmos não retornar body no sucesso (escolha do time). Nesta especificação usamos 200 OK com body para facilitar testes e confirmações.

- 400 Bad Request
  - Payload inválido (ex.: body vazio, `status` missing, tipo inválido)

- 401 Unauthorized / 403 Forbidden
  - Falta ou insuficiência de permissões

- 404 Not Found
  - Funcionário com `id` informado não existe

- 409 Conflict
  - Tentativa de transição explicitamente proibida (ex.: regras de transição estritas definidas pelo negócio)

- 422 Unprocessable Entity
  - Validações de negócio (ex.: status não permitido em razão de regras extras)

### Exemplos (Happy Path)

Requisição:
PATCH /api/funcionarios/123/status
Content-Type: application/json

{
  "status": "INATIVO"
}

Resposta (200):
{
  "id": 123,
  "status": "INATIVO"
}

Exemplo (status inválido):
Requisição com body { "status": "DEMITIDO" }
Resposta: 400 Bad Request ou 422 Unprocessable Entity (se o valor não estiver presente no enum do sistema)

Exemplo (id não encontrado):
Resposta: 404 Not Found

## 4. Regras de negócio e validações

- Apenas o campo `status` deve ser alterado por este endpoint. Nenhum outro campo do `Funcionario` (nome, cpf, cargo, salario, dataDeAdmissao, dataDeDemissao etc.) deve ser modificado.

- Validações:
  - `status` é obrigatório e não pode ser nulo.
  - `status` deve ser um dos valores do enum `FuncionarioStatusEnum` (ATIVO, INATIVO, SUSPENSO). Se o payload contiver um valor desconhecido, retornar 400 ou 422.

- Idempotência:
  - Aplicar o mesmo status atual deve ser tratado como operação idempotente: retornar 200 OK e não alterar a entidade.

- Transições recomendadas (matriz mínima):
  - ATIVO -> INATIVO
  - ATIVO -> SUSPENSO
  - INATIVO -> ATIVO
  - SUSPENSO -> ATIVO

- Transições proibidas/observações:
  - O enum atual não possui `DEMITIDO`. Se quiser suportar um estado imutável (ex.: `DEMITIDO` que não permitiria reativação), isso deve ser decidido e implementado em ticket separado.
  - Se houver regras de auditoria ou gravação de `dataDeDemissao` ao marcar um funcionário como demitido, isso deve ser feito por um endpoint específico — não aqui.

- Concorrência:
  - Em atualizações concorrentes, usar lock otimista (versão) ou checar o estado atual no repositório antes de persistir. Em caso de conflito, retornar 409 Conflict.

## 5. Contrato DTOs (sugestões Java)

Sugestão de classes DTO (anotações com Jakarta Validation e Lombok):

StatusUpdateRequest.java
```java
package And.tec.CadastroFuncionario.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequest {
    @NotNull
    private FuncionarioStatusEnum status; // alinhar import com o pacote do enum

    // getters/setters
}
```

StatusUpdateResponse.java
```java
package And.tec.CadastroFuncionario.dtos;

public class StatusUpdateResponse {
    private Long id;
    private FuncionarioStatusEnum status;
    // getters/setters
}
```

Observações:
- Pode-se usar `String status` em vez do enum no DTO e mapear/validar no Service para melhor tolerância a payloads externos.
- Validar entradas com `@Valid` no controller.

## 6. Segurança e autorização

- Recomendação: apenas usuários com permissão RH ou ROLE_ADMIN podem alterar o status.
- Comportamento:
  - Sem token válido: 401 Unauthorized
  - Token válido mas sem ROLE_RH: 403 Forbidden

- Implementação: usar as anotações de segurança já existentes no projeto (ex.: `@PreAuthorize("hasRole('RH')")` ou similar). Se não existir, criar regra simples a ser revisada.

## 7. Critérios de aceitação e testes automatizados

Testes unitários (Service + Mapper):
- TC-01 (happy path): alterar status de ATIVO para INATIVO — verificar que apenas `status` foi alterado e o repositório foi chamado para persistir.
- TC-02 (idempotência): aplicar mesmo status atual retorna sem alteração.
- TC-03 (status inválido): payload com status não pertencente ao enum -> validação falha.
- TC-04 (funcionário não encontrado): lançar `FuncionarioNotFoundException` -> 404.
- TC-05 (tentativa de transição proibida): se a regra estiver ativa, retornar `NegocioException` -> 409 ou 422.

Testes de integração (end-to-end via MockMVC ou ambiente de teste):
- IT-01: chamada PATCH /api/funcionarios/{id}/status atualiza somente o campo status na base (assert comparando entidade antes e depois — outros campos iguais).
- IT-02: requisição sem autorização -> 401/403.
- IT-03: payload inválido -> 400.

Casos de borda:
- payload nulo/JSON vazio
- enum desconhecido (tolerância vs erro)
- concorrência: duas requisições simultâneas tentando alterar para valores diferentes -> garantir que um dos updates falhe com 409 ou que versão seja incrementada corretamente.

## 8. Exemplos de cURL / PowerShell

PowerShell (exemplo):

$body = '{"status":"INATIVO"}';
Invoke-RestMethod -Uri "https://api.local/api/funcionarios/123/status" -Method Patch -Headers @{"Authorization"="Bearer <token>";"Content-Type"="application/json"} -Body $body

cURL:

curl -X PATCH "https://api.local/api/funcionarios/123/status" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"status":"INATIVO"}'

## 9. Tarefas de implementação mínima (sugestão de tickets)

- T1: Criar DTO `StatusUpdateRequest` e `StatusUpdateResponse` em `dtos/`.
- T2: Criar método no `FuncionarioService` para alterar status: `public Funcionario alterarStatus(Long id, FuncionarioStatusEnum novoStatus)` com validações e regras de negócio descritas.
- T3: Adicionar endpoint no `FuncionarioController`: `@PatchMapping("/{id}/status")` que delega ao service e retorna `StatusUpdateResponse`.
- T4: Implementar Testes Unitários para service e controller.
- T5: Implementar Testes de Integração (MockMVC) garantindo que apenas status muda.
- T6: Documentar autorização (security config) e aplicar anotação `@PreAuthorize` quando necessário.

## 10. Observações / Próximos passos

- Se o time decidir que marcar `DEMITIDO` deve automaticamente preencher `dataDeDemissao`, implementar endpoint separado ou estender este endpoint com parâmetros opcionais (mas documentar o impacto). Essa alteração requer alteração do enum se `DEMITIDO` não existir.
- Confirmar com Product Owner quais roles podem executar essa ação (ROLE_RH, ROLE_ADMIN etc.).
- Validar se existe padrão no projeto para endpoints parciais (uso de PATCH + JSON Patch vs PATCH com body simples). Esta especificação recomenda PATCH com body simples {"status":"..."} por simplicidade.

---

Arquivo gerado automaticamente: specs/alterar-status-funcionario.md
