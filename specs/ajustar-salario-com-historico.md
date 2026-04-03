# Spec: Ajuste de Salário com Histórico

Visão geral

Implementar rastreamento de histórico para ajustes de salário. Atualmente já existe um endpoint para atualizar salário; esta feature especifica a refatoração do endpoint e do service associado para receber um DTO com informações do ajuste e gravar um registro de histórico a cada ajuste.

Requisitos funcionais (fornecidos)

- Criar um DTO `AtualizarSalarioRequest` que recebe:
  - `idFuncionario` (Long) — identificador do funcionário
  - `percentual` (BigDecimal) — percentual de ajuste (positivo; percentuais negativos não são permitidos)
  - `motivo` (String) — texto explicando o motivo do ajuste

- Refatorar o endpoint existente de ajuste de salário para receber o DTO acima.
- Refatorar o service que o endpoint chama para aceitar o DTO.

- Criar uma entidade `HistoricoAjuste` com campos:
  - `id` (Long) — PK
  - `funcionarioId` (Long) — id do funcionário
  - `percentual` (BigDecimal) — percentual aplicado
  - `valorAtualSalario` (BigDecimal) — salário antes do ajuste
  - `valorAposAjuste` (BigDecimal) — salário depois do ajuste
  - `motivo` (String)
  - `dataAjuste` (LocalDateTime) — timestamp do ajuste (sugerido)

- Criar um repository para `HistoricoAjuste` (ex.: `HistoricoAjusteRepository extends JpaRepository<HistoricoAjuste, Long>`).
- Criar um service para `HistoricoAjuste` (ex.: `HistoricoAjusteService`) com método para persistir um registro de histórico.
- Usar o `HistoricoAjusteService` dentro do método `ajustarSalarioPorId` (já refatorado) para salvar os dados do ajuste.

Non-functional / comportamental

- A operação de ajuste e gravação do histórico deve ser transacional: se falhar a gravação do histórico, o ajuste de salário não deve ficar em estado inconsistente.
- Precisão numérica: usar BigDecimal com escala apropriada (ex.: 2 casas) para valores monetários e percentuais.
- Validações:
  - `idFuncionario` obrigatório e deve existir (retornar 404 se não encontrado).
  - `percentual` não nulo; deve ser POSITIVO (> 0). Percentuais negativos não são permitidos; se recebido um valor negativo, retornar 422 Unprocessable Entity.
  - Validar limites de negócio (ex.: não permitir aumento maior que limite configurável — regra aplicável se houver política de teto de aumento).
  - `motivo` obrigatório e tamanho mínimo/máximo (ex.: 5-500 caracteres).

Contrato HTTP (API)

- Endpoint (refatorado):
  - Método: POST ou PATCH — recomendação: PATCH /funcionarios/ajustar-salario ou PATCH /funcionarios/{id}/salario
  - Recomendação de assinatura: PATCH /funcionarios/{id}/salario
    - RequestPath: `id` path parameter (opcional se enviado no DTO)
    - Body: `AtualizarSalarioRequest` JSON

- Exemplo de request JSON:

{
  "idFuncionario": 123,
  "percentual": 10.5,
  "motivo": "Ajuste anual de desempenho"
}

- Responses esperadas:
  - 200 OK — retorno com `FuncionarioResponse` atualizado (ou 204 No Content se preferir)
  - 400 Bad Request — validação falhou (mensagem com detalhes)
  - 404 Not Found — funcionário não encontrado
  - 422 Unprocessable Entity — regra de negócio violada (ex.: percentual inválido)
  - 500 Internal Server Error — erro inesperado

Fluxo de operação (sequência)

1. Controller recebe `AtualizarSalarioRequest`.
2. Validações iniciais (request binding, formato, campos obrigatórios).
3. Controller chama `FuncionarioService.ajustarSalarioPorId(AtualizarSalarioRequest request)` (ou assinatura equivalente).
4. `FuncionarioService` carrega o `Funcionario` do banco (por id).
   - Se não existir → lançar `NotFoundException` para o controller.
5. Calcular `valorAjuste = valorAtual * (percentual / 100)` e `valorApos = valorAtual + valorAjuste`.
6. Aplicar o novo salário no `Funcionario` e persistir (em transação).
7. Montar um `HistoricoAjuste` com os dados: funcionarioId, percentual, valorAtualSalario, valorAposAjuste, motivo, dataAjuste.
8. Chamar `HistoricoAjusteService.salvar(historicoAjuste)` para persistir o registro de histórico (mesma transação).
9. Commit da transação e retornar resposta para o cliente.

Modelagem técnica

- DTO: `AtualizarSalarioRequest` (pacote `dtos`):
  - `Long idFuncionario`;
  - `BigDecimal percentual`;
  - `String motivo`;
  - (Opcional) validações com Bean Validation (@NotNull, @Size, @Digits).

- Entidade: `HistoricoAjuste` (pacote `domain` ou `infraEstructure`/`entity`):
  - `@Entity` `HistoricoAjuste` {
    - `@Id @GeneratedValue Long id`
    - `Long funcionarioId` (indexado)
    - `BigDecimal percentual`
    - `BigDecimal valorAtualSalario`
    - `BigDecimal valorAposAjuste`
    - `String motivo`
    - `LocalDateTime dataAjuste` (persistir como TIMESTAMP)
  }

- Repository: `HistoricoAjusteRepository extends JpaRepository<HistoricoAjuste, Long>`.
  - Métodos adicionais úteis: List<HistoricoAjuste> findByFuncionarioIdOrderByDataAjusteDesc(Long funcionarioId)

- Service: `HistoricoAjusteService` com métodos:
  - `HistoricoAjuste salvar(HistoricoAjuste historico)` — persiste um registro
  - `List<HistoricoAjuste> listarPorFuncionario(Long funcionarioId, Pageable pageable)` — listar histórico

Transacionalidade

- `FuncionarioService.ajustarSalarioPorId(AtualizarSalarioRequest)` deve ter `@Transactional` e, dentro dele, após atualizar o salário do funcionário e persistir, chamar `HistoricoAjusteService.salvar(...)`. Garantir que ambos persistam na mesma transação (ou que `HistoricoAjusteService` use o mesmo TransactionManager / propagation REQUIRED).

Esquema SQL exemplo (Migration):

CREATE TABLE historico_ajuste (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  funcionario_id BIGINT NOT NULL,
  percentual DECIMAL(10,4) NOT NULL,
  valor_atual_salario DECIMAL(19,2) NOT NULL,
  valor_apos_ajuste DECIMAL(19,2) NOT NULL,
  motivo VARCHAR(1024) NOT NULL,
  data_ajuste TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_historico_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionario(id)
);

(Adaptar tipos ao banco em uso e convenções do projeto.)

Exemplos de testes a criar

- Unit tests:
  - `AtualizarSalarioRequest` validações (BeanValidation) — campos obrigatórios e limites.
  - `FuncionarioService`:
    - ajuste positivo: calcula novo salário corretamente e chama repository.save para funcionario e historico service.
    - receber percentual negativo → deve resultar em 422 Unprocessable Entity (regra de validação de entrada).
    - funcionário inexistente → lança NotFoundException.
    - percentual inválido (ex.: 100000%) → lança RuleViolationException ou outra exceção de regra de negócio definida.
  - `HistoricoAjusteService`:
    - salvar cria entidade com campos preenchidos e delega ao repository.

- Integration tests:
  - fluxo completo via controller: request HTTP → resposta 200 e persistência do `HistoricoAjuste` e alteração no `Funcionario`.
  - transacionalidade: forçar falha na gravação do histórico e verificar que o salário não foi alterado (rollback).

Casos de borda e considerações

- Concurrency: dois ajustes simultâneos no mesmo funcionário. Estratégias:
  - Otimista (version/timestamp no funcionário) e retry em caso de conflito;
  - Ou lock pessimista se a política exigir.

- Precisão e arredondamento: decidir regra de arredondamento para `valorAposAjuste` (ex.: HALF_UP, scale=2).
- Percentual como número absoluto ou relativo: explicar ao front-end que percentual é em unidades percentuais (ex.: 10.5 representa +10.5%).
- Auditoria adicional: registrar quem solicitou o ajuste (usuário) e IP/reqId, se for requisito futuro — sugerir adicionar campos `usuarioId` e `requestId` no histórico.

Mapping e responsabilidades de pacotes

- `controller`:
  - atualizar endpoint para receber `AtualizarSalarioRequest` e delegar ao `FuncionarioService`.

- `dtos`:
  - novo `AtualizarSalarioRequest`.

- `business` (ou `service`):
  - atualizar `FuncionarioService.ajustarSalarioPorId(AtualizarSalarioRequest)` (ou método com nome equivalente) para chamar `HistoricoAjusteService`.
  - criar `HistoricoAjusteService`.

- `infraEstructure` / `repository`:
  - criar `HistoricoAjusteRepository`.

Tarefas de implementação (passo a passo)

1. Criar DTO `AtualizarSalarioRequest` com validações.
2. Alterar controller existente que ajusta salário para aceitar esse DTO.
3. Refatorar `FuncionarioService.ajustarSalarioPorId` para receber DTO e extrair lógica de cálculo para método privado/auxiliar (testável).
4. Criar entidade JPA `HistoricoAjuste` e migration SQL.
5. Criar `HistoricoAjusteRepository`.
6. Criar `HistoricoAjusteService` com método `salvar` e `listarPorFuncionario`.
7. Integrar `HistoricoAjusteService` dentro de `FuncionarioService.ajustarSalarioPorId` para persistir histórico na mesma transação.
8. Adicionar unit/integration tests cobrindo os cenários listados.
9. Atualizar documentação API (OpenAPI/Swagger) e contratos se necessário.

Exemplos de assinatura de métodos sugeridas (Java):

- DTO:
  - public class AtualizarSalarioRequest { private Long idFuncionario; private BigDecimal percentual; private String motivo; /* getters/setters + validations */ }

- Controller:
  - @PatchMapping("/funcionarios/{id}/salario")
    public ResponseEntity<FuncionarioResponse> ajustarSalario(@PathVariable Long id, @RequestBody @Valid AtualizarSalarioRequest request)

- FuncionarioService:
  - public FuncionarioResponse ajustarSalarioPorId(AtualizarSalarioRequest request)

- HistoricoAjusteService:
  - public HistoricoAjuste salvar(HistoricoAjuste historico)

Requisitos de segurança e autorização

- Verificar permissão do usuário que solicita o ajuste (ex.: ROLE_HR, ROLE_ADMIN) antes de permitir a operação.
- Registrar o usuário que executou o ajuste no histórico se a aplicação já tem contexto de usuário autenticado.

Critérios de aceitação

- Endpoint refatorado aceita o novo DTO e valida corretamente.
- Após ajuste de salário bem-sucedido, existe um registro em `historico_ajuste` para o funcionário contendo os valores antes e depois.
- Transação garante consistência: não persiste ajuste sem persistir histórico (ou vice-versa).
- Testes automatizados (unit + integração) cobrindo os cenários críticos.

Observações finais

Este arquivo é apenas a especificação (sem implementação). Na implementação, atentar para as convenções do projeto (naming, packages, mapeamentos, tratamentos de exceção). Recomenda-se criar testes de integração que rodem com um banco em memória (H2) para validar a transacionalidade.

---

Requirements coverage:
- DTO `AtualizarSalarioRequest` criado: Spec criado ✔️
- Refatorar endpoint para receber DTO: Spec criado ✔️
- Refatorar service para receber DTO: Spec criado ✔️
- Entidade `HistoricoAjuste` com campos especificados: Spec criado ✔️
- Repository e Service para `HistoricoAjuste`: Spec criado ✔️
- Uso do service no `ajustarSalarioPorId` refatorado para salvar ajuste: Spec criado ✔️

Próximos passos (implementação): seguir a lista de tarefas de implementação acima.
