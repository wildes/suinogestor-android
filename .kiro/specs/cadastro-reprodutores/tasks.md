# Implementation Plan: Cadastro e Identificação Individual de Reprodutores

## Overview

Este plano implementa o módulo de cadastro e gerenciamento de reprodutores (varrões) no SuinoGestor, seguindo Clean Architecture com três camadas (Data, Domain, Presentation). A implementação inclui validação de dados, cálculos automáticos (ração diária, categoria de idade), e preparação para integração futura com o sistema de alertas e módulo de coberturas.

**Linguagem de implementação**: Kotlin  
**Arquitetura**: Clean Architecture (Data → Domain → Presentation)  
**UI**: Jetpack Compose + Material 3 Expressive  
**Persistência**: Room Database (migration 1→2)

## Tasks

- [x] 1. Configurar estrutura base e dependências
  - Adicionar dependências do Kotest ao `gradle/libs.versions.toml`
  - Criar estrutura de pacotes: `domain/model`, `domain/usecase`, `domain/repository`, `data/db`, `data/repository`, `presentation/reprodutor/screen`, `presentation/reprodutor/viewmodel`
  - _Requirements: 2.1_

- [x] 2. Implementar camada de dados (Data Layer)
  - [x] 2.1 Criar modelo de domínio e enums
    - Criar `domain/model/Reprodutor.kt` com data class contendo todos os campos (id, identificacao, dataNascimento, racaLinhagem, tipoUso, pesoAtualKg, eccAtual, ativo, dataCadastro)
    - Criar `domain/model/TipoUsoReprodutor.kt` com enum (MONTA_NATURAL, COLETA_IA, AMBOS)
    - Criar `domain/model/CategoriaIdadeReprodutor.kt` com enum (TREINAMENTO, ADULTO, IMATURO)
    - _Requirements: 2.1, 2.2_

  - [x] 2.2 Criar entidade Room e DAO
    - Criar `data/db/ReprodutorEntity.kt` com anotações Room (@Entity, @PrimaryKey, @Index para identificacao única)
    - Adicionar constraint CHECK para eccAtual (1-5)
    - Criar `data/db/ReprodutorDao.kt` com operações: inserir, atualizar, buscarPorId, buscarPorIdentificacao, observarTodosAtivos (Flow)
    - _Requirements: 2.1, 2.2, 2.7_

  - [x] 2.3 Implementar migration do banco de dados
    - Criar `MIGRATION_1_2` em `data/db/SuinoGestorDatabase.kt`
    - Adicionar tabela `reprodutores` com todos os campos e constraints
    - Criar índice único em `identificacao`
    - Atualizar versão do banco para 2
    - Atualizar schema JSON em `app/schemas/`
    - _Requirements: 2.1_

  - [x] 2.4 Criar interface do repositório
    - Criar `domain/repository/ReprodutorRepository.kt` com métodos: cadastrar, atualizar, buscarPorId, buscarPorIdentificacao, observarTodos, contarCoberturasNaSemana (mock), obterDataUltimaCobertura (mock)
    - Definir `ResultadoOperacao<T>` sealed class para tratamento de erros
    - _Requirements: 2.1, 2.2_

  - [x] 2.5 Implementar repositório concreto
    - Criar `data/repository/ReprodutorRepositoryImpl.kt` implementando a interface
    - Implementar mappers: `ReprodutorEntity.toDomain()` e `Reprodutor.toEntity()`
    - Tratar exceções de banco (SQLiteConstraintException → ErroNegocio.IdentificacaoDuplicada)
    - Métodos de cobertura retornam valores mock (0 e null) até módulo de coberturas ser implementado
    - _Requirements: 2.1, 2.2, 2.7_

  - [ ]* 2.6 Escrever testes de integração do repositório
    - Testar CRUD completo com Room in-memory database
    - Testar constraint de identificacao única
    - Testar observação reativa via Flow
    - Testar mapeamento Entity ↔ Domain
    - _Requirements: 2.1, 2.2, 2.7_

- [x] 3. Implementar camada de domínio (Domain Layer)
  - [x] 3.1 Criar use case de cadastro
    - Criar `domain/usecase/CadastrarReprodutorUseCase.kt`
    - Validar campos obrigatórios (identificacao, dataNascimento, racaLinhagem, tipoUso)
    - Validar ECC (1-5 se presente)
    - Validar peso (> 0 se presente)
    - Validar data de nascimento (não futura)
    - Verificar duplicidade via repositório
    - Retornar `ResultadoOperacao<Long>` com ID do reprodutor cadastrado
    - _Requirements: 2.1, 2.7_

  - [ ]* 3.2 Escrever property test para validação de campos obrigatórios
    - **Property 1: Required Fields Validation**
    - **Validates: Requirements 2.1**
    - Criar generator `Arb.cadastroComandoIncompleto()` que gera comandos com campos faltando
    - Verificar que cadastro é aceito se e somente se todos os campos obrigatórios estão presentes
    - _Requirements: 2.1_

  - [ ]* 3.3 Escrever property test para persistência round-trip
    - **Property 2: Data Persistence Round-Trip**
    - **Validates: Requirements 2.2**
    - Criar generator `Arb.reprodutor()` para gerar reprodutores válidos
    - Verificar que peso e ECC são preservados após salvar e recuperar do repositório
    - _Requirements: 2.2_

  - [ ]* 3.4 Escrever property test para rejeição de duplicidade
    - **Property 7: Duplicate Identification Rejection**
    - **Validates: Requirements 2.7**
    - Verificar que cadastrar reprodutor com identificacao já existente retorna erro
    - _Requirements: 2.7_

  - [x] 3.5 Criar use case de cálculo de ração diária
    - Criar `domain/usecase/CalcularRacaoDiariaUseCase.kt`
    - Implementar fórmula: `racaoDiariaKg = pesoKg * 0.01`
    - Validar peso > 0
    - _Requirements: 2.3_

  - [ ]* 3.6 Escrever property test para cálculo de ração
    - **Property 3: Feed Calculation Accuracy**
    - **Validates: Requirements 2.3**
    - Gerar pesos aleatórios entre 80.0 e 400.0 kg
    - Verificar que ração calculada é exatamente 1% do peso (tolerância 0.001)
    - _Requirements: 2.3_

  - [x] 3.7 Criar use case de cálculo de categoria de idade
    - Criar `domain/usecase/CalcularCategoriaIdadeReprodutorUseCase.kt`
    - Implementar lógica: < 210 dias = IMATURO, 210-300 dias = TREINAMENTO, 300+ dias = ADULTO
    - _Requirements: 2.5, 2.6_

  - [x] 3.8 Criar use case de listagem
    - Criar `domain/usecase/ListarReprodutoresUseCase.kt`
    - Retornar `Flow<List<Reprodutor>>` do repositório
    - Permitir filtro por status ativo/inativo
    - _Requirements: 2.1_

  - [x] 3.9 Criar use case de obtenção de detalhes
    - Criar `domain/usecase/ObterReprodutorUseCase.kt`
    - Buscar por ID via repositório
    - Calcular dados derivados (idade em dias, categoria, ração diária se peso disponível)
    - _Requirements: 2.1, 2.2, 2.3_

  - [ ]* 3.10 Escrever testes unitários dos use cases
    - Testar `CadastrarReprodutorUseCase`: casos de sucesso, validações, duplicidade
    - Testar `CalcularRacaoDiariaUseCase`: diversos valores de peso
    - Testar `CalcularCategoriaIdadeReprodutorUseCase`: limites de idade (209, 210, 300, 301 dias)
    - Testar `ListarReprodutoresUseCase`: filtros
    - Testar `ObterReprodutorUseCase`: cálculos derivados
    - _Requirements: 2.1, 2.2, 2.3, 2.5, 2.6_

- [ ] 4. Checkpoint - Validar camadas Data e Domain
  - Ensure all tests pass, ask the user if questions arise.

- [x] 5. Implementar componentes UI customizados
  - [x] 5.1 Criar TipoUsoSelector
    - Criar `presentation/reprodutor/component/TipoUsoSelector.kt`
    - Usar ConnectedButtonGroup com 3 opções: "Monta Natural", "Coleta IA", "Ambos"
    - Aplicar shape morph M3 Expressive na seleção
    - Adicionar @Preview
    - _Requirements: 2.1_

  - [x] 5.2 Criar CategoriaIdadeChip
    - Criar `presentation/reprodutor/component/CategoriaIdadeChip.kt`
    - Mapear categoria para label e cor: IMATURO (tertiaryContainer), TREINAMENTO (secondaryContainer), ADULTO (primaryContainer)
    - Usar AssistChip do Material 3
    - Adicionar @Preview para cada categoria
    - _Requirements: 2.5, 2.6_

  - [x] 5.3 Criar RacaoDiariaDisplay
    - Criar `presentation/reprodutor/component/RacaoDiariaDisplay.kt`
    - Layout: Row com label "Ração diária recomendada" e valor em destaque
    - Background: primaryContainer, shape: medium
    - Formato: "X.XX kg"
    - Adicionar @Preview
    - _Requirements: 2.3_

- [ ] 6. Implementar camada de apresentação (Presentation Layer)
  - [x] 6.1 Criar ViewModel
    - Criar `presentation/reprodutor/viewmodel/ReprodutorViewModel.kt`
    - Injetar use cases via Hilt: CadastrarReprodutorUseCase, ListarReprodutoresUseCase, CalcularRacaoDiariaUseCase, CalcularCategoriaIdadeReprodutorUseCase
    - Expor `StateFlow<List<Reprodutor>>` para lista de reprodutores
    - Expor `StateFlow<ReprodutorUiState>` para estado da UI (Loading, Success, Error)
    - Implementar função `cadastrar(comando: CadastrarReprodutorComando)`
    - Implementar função `calcularRacaoDiaria(pesoKg: Double): Double`
    - _Requirements: 2.1, 2.2, 2.3_

  - [x] 6.2 Criar tela de listagem
    - Criar `presentation/reprodutor/screen/ListaReprodutoresScreen.kt`
    - TopAppBar com título "Reprodutores" e campo de busca (debounce 300ms)
    - LazyColumn com cards de reprodutores mostrando: identificação, raça, categoria de idade, peso, ECC, ração diária
    - ExtendedFAB "Novo Reprodutor" no canto inferior direito
    - Estados: vazio (ilustração + mensagem + botão), sem resultado, carregando, lista
    - Navegação para CadastroReprodutorScreen (FAB) e DetalhesReprodutorScreen (click no card)
    - _Requirements: 2.1, 2.2, 2.3_

  - [x] 6.3 Criar tela de cadastro
    - Criar `presentation/reprodutor/screen/CadastroReprodutorScreen.kt`
    - Formulário vertical com campos: identificação (TextField obrigatório), data de nascimento (DatePicker obrigatório com idade calculada abaixo), raça/linhagem (TextField obrigatório), tipo de uso (TipoUsoSelector obrigatório), peso atual (TextField numérico opcional com cálculo de ração abaixo), ECC atual (EccSelector opcional reutilizado)
    - Validação inline: duplicidade de identificação, ECC 1-5, peso > 0, data não futura
    - Botão "Cadastrar Reprodutor" desabilitado se campos obrigatórios vazios
    - Loading indicator durante salvamento
    - Feedback: Snackbar de sucesso + navegação para lista, mensagens inline para erros
    - Preservar estado com rememberSaveable
    - _Requirements: 2.1, 2.2, 2.3, 2.7_

  - [x] 6.4 Criar tela de detalhes
    - Criar `presentation/reprodutor/screen/DetalhesReprodutorScreen.kt`
    - Seções: Identificação (número, raça, tipo de uso chip), Dados Físicos (idade com chip de categoria, peso, ECC visual, ração diária), Alertas Ativos (placeholder vazio por enquanto)
    - IconButton de editar no TopAppBar
    - Button secundário "Ver Histórico de Uso" (placeholder desabilitado)
    - _Requirements: 2.1, 2.2, 2.3_

  - [ ]* 6.5 Escrever testes de UI
    - Testar jornada completa: cadastro com sucesso
    - Testar validação de campos obrigatórios
    - Testar exibição de erro de duplicidade
    - Testar cálculo automático de ração ao inserir peso
    - Testar navegação entre telas
    - _Requirements: 2.1, 2.2, 2.3, 2.7_

- [x] 7. Integrar com navegação principal
  - [x] 7.1 Adicionar rotas de navegação
    - Adicionar rotas no NavHost: `listaReprodutores`, `cadastroReprodutor`, `detalhesReprodutor/{id}`
    - Configurar navegação do módulo Reprodução para ListaReprodutoresScreen
    - _Requirements: 2.1_

  - [x] 7.2 Configurar injeção de dependências
    - Criar módulo Hilt para repositório e use cases
    - Registrar ReprodutorDao no DatabaseModule
    - Adicionar migration ao SuinoGestorDatabase
    - _Requirements: 2.1_

- [ ] 8. Preparar infraestrutura de alertas (implementação futura)
  - [ ] 8.1 Criar use case de geração de alertas (estrutura apenas)
    - Criar `domain/usecase/GerarAlertasReprodutoresUseCase.kt` com lógica comentada
    - Documentar regras: inatividade (15+ dias), superutilização adultos (6+ montas/semana), uso excessivo treinamento (>1 monta/semana)
    - Adicionar TODO para implementação após módulo de coberturas
    - _Requirements: 2.4, 2.5, 2.6_

  - [ ]* 8.2 Escrever property tests para alertas (estrutura apenas)
    - **Property 4: Inactivity Alert Generation** (comentado, aguardando módulo de coberturas)
    - **Property 5: Adult Overuse Alert Generation** (comentado, aguardando módulo de coberturas)
    - **Property 6: Training Overuse Alert Generation** (comentado, aguardando módulo de coberturas)
    - Criar estrutura de testes com generators, marcar como @Disabled
    - _Requirements: 2.4, 2.5, 2.6_

  - [ ] 8.3 Adicionar tipos de alerta ao enum
    - Adicionar ao `TipoAlerta` enum: REPRODUTOR_INATIVO, REPRODUTOR_SUPERUTILIZADO, REPRODUTOR_TREINAMENTO_EXCESSO
    - _Requirements: 2.4, 2.5, 2.6_

- [ ] 9. Checkpoint final - Validação completa
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 10. Polimento e acessibilidade
  - [ ] 10.1 Revisar acessibilidade
    - Verificar contentDescription em todos os ícones interativos
    - Verificar área de toque mínima 48dp
    - Verificar contraste 4.5:1 em ambos os temas
    - Testar com TalkBack
    - _Requirements: 2.1_

  - [ ] 10.2 Adicionar documentação de código
    - KDoc em classes públicas (use cases, repositório, ViewModel)
    - Comentários explicativos em lógica de validação e cálculos
    - _Requirements: 2.1_

## Notes

- Tasks marcadas com `*` são opcionais e podem ser puladas para MVP mais rápido
- Cada task referencia requirements específicos para rastreabilidade
- Checkpoints garantem validação incremental
- Property tests validam propriedades universais de correção
- Testes unitários validam exemplos específicos e casos de borda
- Sistema de alertas será completamente implementado após módulo de coberturas estar pronto
- Migration do banco deve ser testada manualmente antes de merge para main

## Dependencies

Adicionar ao `gradle/libs.versions.toml`:

```toml
[versions]
kotest = "5.8.0"

[libraries]
kotest-runner-junit5 = { module = "io.kotest:kotest-runner-junit5", version.ref = "kotest" }
kotest-assertions-core = { module = "io.kotest:kotest-assertions-core", version.ref = "kotest" }
kotest-property = { module = "io.kotest:kotest-property", version.ref = "kotest" }
```
