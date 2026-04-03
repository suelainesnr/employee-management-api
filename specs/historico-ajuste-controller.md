# Spec: Histórico de Ajustes de Salário - Controller

Visão geral

Criar um endpoint para expor os registros de histórico de ajustes de salário por funcionário. O endpoint deve delegar para `HistoricoAjusteService` e retornar lista paginada/ordenada dos ajustes do funcionário.

Requisitos funcionais

- Endpoint: GET /api/v1/funcionarios/{id}/ajustes
  - Path parameter: `id` (Long) - id do funcionário
  - Query params opcionais:
    - `page` (int) - número da página (default 0)
    - `size` (int) - tamanho da página (default 20)
  - Response 200 OK: lista (ou página) de registros `HistoricoAjuste` ou `HistoricoAjusteResponse` contendo:
    - `id` (Long)
    - `funcionarioId` (Long)
    - `percentual` (BigDecimal)
    - `valorAtualSalario` (BigDecimal)
    - `valorAposAjuste` (BigDecimal)
    - `motivo` (String)
    - `dataAjuste` (LocalDateTime)
  - Responses de erro:
    - 404 Not Found — se o funcionário não existir
    - 400 Bad Request — parâmetros inválidos

Considerações técnicas

- O controller deve usar `HistoricoAjusteService.listarPorFuncionario(Long funcionarioId, Pageable pageable)` se for implementada paginação; caso contrário usar `listarPorFuncionario(Long funcionarioId)` e aplicar paginação manualmente.
- Autorização: endpoint deve exigir permissão (ex.: ROLE_HR) — implementar `@PreAuthorize` quando a segurança for utilizada.
- Mapping: eventualmente mapear `HistoricoAjuste` para DTO `HistoricoAjusteResponse` que contenha apenas campos públicos necessários.

Casos de teste sugeridos

- Happy path: retorna 200 e lista ordenada por `dataAjuste` decrescente.
- Funcionário inexistente: 404 Not Found.
- Paginação: page e size funcionam corretamente.

Notas

- Este arquivo é uma especificação; a implementação será realizada em código separadamente.

