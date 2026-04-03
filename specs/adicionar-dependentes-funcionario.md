# Especificação: Adicionar Dependentes ao Funcionário

Versão: 1.0
Data: 2026-02-27
Autor: Equipe de desenvolvimento

Resumo
-------
Descrever os requisitos, modelos de dados e o passo-a-passo para implementar a funcionalidade de gerenciar dependentes de um funcionário. Dependentes podem ser dos tipos: pai, mãe, filho e conjugue (cônjuge). Este documento explica o que deve ser feito, exemplos de payloads, validações, regras de negócio e critérios de aceite — sem implementar nada no código.

Objetivo
--------
Permitir que a API e a camada de persistência registrem, atualizem, listem e removam dependentes associados a um funcionário.

Escopo
------
- Modelagem de entidade Dependente
- Associação entre `Funcionario` e `Dependente` (1:N)
- Endpoints REST para CRUD de dependentes (adicionar, listar, atualizar, remover)
- Validações e regras de negócio
- Testes unitários e de integração
- Migração de banco de dados
- Documentação (OpenAPI/Swagger)

Requisitos funcionais
---------------------
1. Adicionar um dependente para um funcionário existente.
2. Listar todos os dependentes de um funcionário.
3. Obter um dependente pelo id (opcional).
4. Atualizar dados de um dependente.
5. Remover um dependente.
6. Validar tipo do dependente: somente os tipos permitidos (PAI, MAE, FILHO, CONJUGUE).
7. Não permitir adicionar dependentes para funcionário inexistente.
8. Permitir múltiplos dependentes do mesmo tipo (por exemplo, dois filhos).

Requisitos não-funcionais
-------------------------
- Performance: listagens por funcionário devem ser feitas com consultas otimizadas (fetch por id do funcionário).
- Segurança: endpoints devem respeitar as mesmas regras de autenticação/autorização existentes para o recurso `Funcionario`.
- Compatibilidade: alterações devem manter compatibilidade com APIs existentes (não mudar contratos de `Funcionario` já públicos; adicionar endpoints novos).

Modelagem de dados
------------------
1. Nova entidade: `Dependente`
   - id: Long (PK)
   - nome: String (não vazio)
   - dataNascimento: LocalDate (opcional, validar formato)
   - tipo: DependenteTipo (enum: PAI, MAE, FILHO, CONJUGUE)
   - cpf: String (opcional, validar formato se informado)
   - funcionario: referência para `Funcionario` (ManyToOne)

2. Enum: `DependenteTipo` com valores PAI, MAE, FILHO, CONJUGUE.

3. Relação: `Funcionario` deve ter uma coleção de dependentes
   - OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL, orphanRemoval = true)
   - Lazy fetch por padrão; quando listar dependentes, usar fetch explícito ou repository dedicado.

Banco de dados
---------------
- Nova tabela `dependente` com colunas:
  - id BIGINT PRIMARY KEY AUTO_INCREMENT
  - nome VARCHAR(255) NOT NULL
  - data_nascimento DATE NULL
  - tipo VARCHAR(50) NOT NULL
  - cpf VARCHAR(20) NULL
  - funcionario_id BIGINT NOT NULL (FK -> funcionario.id)
  - constraints FK e índice em funcionario_id

- Se o projeto usa migrações (Flyway/Liquibase): criar uma migration que adicione a tabela e índices.
- Se for usado schema auto DDL, testar geração e compatibilidade.

Regras de negócio e validações
------------------------------
- `nome` é obrigatório.
- `tipo` é obrigatório e deve ser um dos valores do enum.
- `cpf`, se fornecido, deve passar validação de formato (opcional aceitar apenas números) e não precisa ser único globalmente (por exemplo, dependente pode compartilhar CPF com funcionário em casos reais) — definir política conforme requisitos de negócio.
- Não adicionar dependente para `Funcionario` inexistente: retornar 404.
- Ao deletar um funcionário, dependentes associados devem ser removidos (cascade / orphanRemoval).
- Atualização parcial: permitir PATCH com campos parciais ou PUT para substituição total (especificar comportamento desejado no contrato).

API REST (sugestão de endpoints)
--------------------------------
- POST /funcionarios/{funcionarioId}/dependentes
  - Descrição: adiciona um novo dependente ao funcionário.
  - Request: DependenteRequest
  - Response: 201 Created + DependenteResponse (com Location header: /funcionarios/{funcionarioId}/dependentes/{dependenteId})

- GET /funcionarios/{funcionarioId}/dependentes
  - Descrição: lista todos os dependentes do funcionário.
  - Response: 200 OK + List<DependenteResponse>

- GET /funcionarios/{funcionarioId}/dependentes/{dependenteId}
  - Descrição: obtém um dependente específico
  - Response: 200 OK + DependenteResponse ou 404 se não existir

- PUT /funcionarios/{funcionarioId}/dependentes/{dependenteId}
  - Descrição: substitui os dados do dependente
  - Response: 200 OK + DependenteResponse

- PATCH /funcionarios/{funcionarioId}/dependentes/{dependenteId}
  - Descrição: atualiza parcialmente
  - Response: 200 OK + DependenteResponse

- DELETE /funcionarios/{funcionarioId}/dependentes/{dependenteId}
  - Descrição: remove o dependente
  - Response: 204 No Content ou 404 se não existir

DTOs (sugestão)
---------------
- DependenteRequest
  - nome: string
  - tipo: string (PAI|MAE|FILHO|CONJUGUE)
  - dataNascimento: string (ISO date, opcional)
  - cpf: string (opcional)

- DependenteResponse
  - id: long
  - nome, tipo, dataNascimento, cpf
  - funcionarioId: long

- Mapear usando o mapper existente (ex: MapStruct) e criar `DependenteMapper`.

Fluxo na camada de serviço
--------------------------
1. `FuncionarioService` (ou nova `DependenteService`):
   - validar existência do funcionário (buscarPorId)
   - validar request (nome, tipo)
   - criar entidade Dependente e setar `funcionario`
   - salvar via `DependenteRepository` (ou salvar via cascade em `Funcionario`)
   - retornar entidade salva

2. Caso de atualização:
   - buscar dependente por id assegurando pertence ao funcionário informado (evitar inconsistências)
   - aplicar alterações e salvar

3. Exclusão:
   - buscar dependente, validar pertence ao funcionário
   - remover via repository.delete(dependente)

Repositórios
------------
- Criar `DependenteRepository extends JpaRepository<Dependente, Long>`
- Métodos úteis:
  - List<Dependente> findByFuncionarioId(Long funcionarioId);
  - Optional<Dependente> findByIdAndFuncionarioId(Long id, Long funcionarioId);

Validação e tratamento de exceções
---------------------------------
- Reutilizar estrutura de exceções existente (por exemplo, `FuncionarioNotFoundException`, `NegocioException`) ou criar `DependenteNotFoundException`.
- Mapear exceções para respostas HTTP via `ExceptionHandlerController` já existente.
- Erros esperados e códigos:
  - 400 Bad Request: payload inválido, tipo inválido, data inválida
  - 404 Not Found: funcionário ou dependente não encontrado
  - 409 Conflict (opcional): tentativa de duplicação quando houver regra de unicidade

Testes
------
- Unitários:
  - Testar `DependenteService` com mocks de `FuncionarioRepository` e `DependenteRepository`.
  - Casos: criar dependente com sucesso, criar dependente para funcionario inexistente, validar campos obrigatórios, atualização parcial, remoção.

- Integração:
  - Testes que usam slice WebMvc ou testes de integração com banco em memória (H2) para garantir endpoints REST.
  - Cenários: adicionar e listar dependentes, atualizar dependente que não pertence ao funcionário (deve retornar 404/erro), remoção.

- Testes de contrato (opcional): garantir que o JSON de entrada e saída seguem o contrato definido.

Exemplos de payloads
---------------------
- Request (POST /funcionarios/1/dependentes)

{
  "nome": "João da Silva",
  "tipo": "PAI",
  "dataNascimento": "1955-06-12",
  "cpf": "12345678901"
}

- Response (201 Created)
{
  "id": 10,
  "nome": "João da Silva",
  "tipo": "PAI",
  "dataNascimento": "1955-06-12",
  "cpf": "12345678901",
  "funcionarioId": 1
}

Critérios de aceite
-------------------
- É possível adicionar pelo menos um dependente para um funcionário existente.
- Listagem retorna somente dependentes do funcionário especificado.
- Remoção de dependente funciona e não deixa dados órfãos.
- API retorna códigos HTTP apropriados para erros e sucessos.
- Cobertura de testes unitários e integração conforme políticas do projeto (ex.: cobertura mínima para novas classes).

Migração e deploy
------------------
- Incluir migration SQL que cria a tabela `dependente` e índices.
- Testar migração localmente com o banco usado no projeto (H2/Postgres/MySQL conforme configuração).

Documentação e OpenAPI
----------------------
- Atualizar o contrato OpenAPI (se o projeto gera docs automaticamente) com os novos endpoints e DTOs.
- Atualizar README e HELP.md com instruções de uso (ex.: como adicionar dependente via curl/postman).

Segurança e permissões
----------------------
- Verificar qual papel/permiso é necessário para manipular dependentes e aplicar a mesma política usada para `Funcionario`.

Considerações e decisões pendentes
---------------------------------
- Unicidade de CPF de dependente: definir se CPF deve ser único entre dependentes ou entre funcionários.
- Políticas de dados pessoais (LGPD): armazenar CPF e data de nascimento tem implicações legais; considerar criptografia/mascaramento conforme política da empresa.
- Estratégia de cascade: salvar dependente através do `Funcionario` (cascade) ou via repositório próprio — escolher uma para manter coerência com o projeto.

Passo-a-passo de implementação (tarefa por tarefa)
-------------------------------------------------
1. Criar enum `DependenteTipo`.
2. Criar entidade `Dependente` em `infraEstructure.entity`.
3. Adicionar coleção `dependentes` em `Funcionario` (OneToMany, mappedBy = "funcionario").
4. Criar `DependenteRepository`.
5. Criar DTOs (`DependenteRequest`, `DependenteResponse`) em `dtos`.
6. Criar `DependenteMapper` (MapStruct) para conversão entre entidade e DTO.
7. Criar `DependenteService` ou estender `FuncionarioService` com métodos:
   - adicionarDependente(funcionarioId, DependenteRequest)
   - listarDependentes(funcionarioId)
   - atualizarDependente(funcionarioId, dependenteId, DependenteRequest)
   - removerDependente(funcionarioId, dependenteId)
8. Criar endpoints no `FuncionarioController` (ou criar `DependenteController` aninhado) com rotas sugeridas.
9. Adicionar migração SQL para tabela `dependente`.
10. Implementar e rodar testes unitários e de integração.
11. Atualizar documentação (OpenAPI/README).

Tarefas de validação antes do merge
-----------------------------------
- Build do projeto deve passar (mvn clean test)
- Todos os testes adicionados devem passar
- Verificar cobertura mínima desejada (se aplicável)
- Revisão de código (PR) com foco em segurança de dados e validações

Anexos e referências
--------------------
- Exemplos de endpoints e payloads (veja seção "Exemplos de payloads").
- Arquitetura do repositório e mapeamentos existentes: seguir convenções já usadas no projeto.

Observações finais
------------------
Este documento serve como especificação de alto nível e passo-a-passo técnico para desenvolvimento. Não foi feita nenhuma alteração no código — siga estas instruções para implementar a funcionalidade de dependentes de forma consistente com o projeto existente.

