# Plano de Implementação: Cadastro e Identificação Individual de Fêmeas

## Visão Geral

Implementação incremental seguindo Clean Architecture + MVVM em Kotlin. As tarefas progridem da camada de dados (Room) para o domínio (use cases) e, por fim, para a apresentação (ViewModel + Compose), garantindo que cada camada seja validada antes de avançar.

## Tarefas

- [x] 1. Configurar banco de dados Room e entidades
  - Criar `SuinoGestorDatabase` em `data/db/SuinoGestorDatabase.kt` com `@Database` anotando `FemeaEntity` e `AlertaEntity`, versão 1, `exportSchema = true`
  - Criar `FemeaEntity` em `data/db/FemeaEntity.kt` com todos os campos do esquema (id, identificacao UNIQUE, dataNascimento, racaLinhagem, categoria, pesoEntradaKg, eccAtual, origem, fotoUri, status, paridade, dataEntrada, ativo)
  - Criar `AlertaEntity` em `data/db/AlertaEntity.kt` com FK para `femeas.id` ON DELETE CASCADE e índices em `femeaId`, `lido`, `dataGeracao`
  - Criar `FemeaDao` em `data/db/FemeaDao.kt` com `observarTodasAtivas()`, `observarPorId()`, `buscarPorIdentificacao()`, `inserir()`, `atualizar()`
  - Criar `AlertaDao` em `data/db/AlertaDao.kt` com `observarNaoLidos()`, `observarPorFemea()`, `inserir()`, `marcarComoLido()`
  - Adicionar dependências Room ao `libs.versions.toml` e `build.gradle.kts` se ainda não presentes
  - _Requisitos: 1.1, 1.2, 1.5_

- [x] 2. Implementar modelos de domínio e interfaces de repositório
  - Criar enums `CategoriaFemea`, `OrigemAnimal`, `StatusFemea`, `TipoAlerta`, `PrioridadeAlerta` em `domain/model/`, cada um em seu próprio arquivo
  - Criar `Femea` em `domain/model/Femea.kt` (data class Kotlin puro, zero dependências Android)
  - Criar `Alerta` em `domain/model/Alerta.kt`
  - Criar `ResultadoOperacao` sealed class em `domain/model/ResultadoOperacao.kt`
  - Criar `ErroNegocio` sealed class em `domain/model/ErroNegocio.kt` com `CampoObrigatorio`, `IdentificacaoDuplicada`, `EccForaDoIntervalo`
  - Criar `CadastrarFemeaComando` em `domain/usecase/CadastrarFemeaComando.kt`
  - Criar interfaces `FemeaRepository` em `domain/repository/FemeaRepository.kt` e `AlertaRepository` em `domain/repository/AlertaRepository.kt`
  - _Requisitos: 1.1, 1.2, 1.4_

- [x] 3. Implementar repositórios na camada de dados
  - Criar `FemeaRepositoryImpl` em `data/repository/FemeaRepositoryImpl.kt` com mapeadores `FemeaEntity ↔ Femea` e implementação de todos os métodos da interface
  - Criar `AlertaRepositoryImpl` em `data/repository/AlertaRepositoryImpl.kt` com mapeadores `AlertaEntity ↔ Alerta`
  - Criar módulo Hilt `DatabaseModule` em `data/db/DatabaseModule.kt` provendo `SuinoGestorDatabase` como `@Singleton`, os DAOs e vinculando as implementações via `@Binds`
  - _Requisitos: 1.1, 1.2, 1.4, 1.5_

  - [ ]* 3.1 Escrever testes de integração para FemeaRepositoryImpl (Room in-memory)
    - Testar inserção e recuperação por id
    - Testar `buscarPorIdentificacao` retorna null para identificação inexistente
    - Testar que índice UNIQUE lança exceção ao inserir duplicata via DAO
    - _Requisitos: 1.1, 1.4_

  - [ ]* 3.2 Escrever testes de integração para AlertaRepositoryImpl (Room in-memory)
    - Testar alerta salvo e recuperado por `femeaId`
    - _Requisitos: 1.6_

- [x] 4. Implementar `CalcularIdadeFormatadaUseCase`
  - Criar `CalcularIdadeFormatadaUseCase` em `domain/usecase/CalcularIdadeFormatadaUseCase.kt` usando `Period.between` do `java.time`
  - Retornar string no formato `"XaYmZd"` (ex.: `"1a3m15d"`)
  - _Requisitos: 1.3_

  - [ ]* 4.1 Escrever teste de propriedade para `CalcularIdadeFormatadaUseCase`
    - **Property 1: Formato de idade é sempre matematicamente consistente**
    - Para qualquer `dataNascimento ≤ dataReferencia`, verificar: (a) resultado corresponde ao padrão `\d+a\d+m\d+d`; (b) valores de anos, meses e dias são exatamente iguais aos de `Period.between`
    - Usar `Arb.localDate()` do Kotest, mínimo 100 iterações
    - **Valida: Requisito 1.3**

  - [ ]* 4.2 Escrever testes unitários para `CalcularIdadeFormatadaUseCase`
    - Exemplos concretos: `"0a0m0d"`, `"1a3m15d"`, `"0a5m9d"`
    - _Requisitos: 1.3_

- [x] 5. Implementar `CadastrarFemeaUseCase`
  - Criar `CadastrarFemeaUseCase` em `domain/usecase/CadastrarFemeaUseCase.kt`
  - Implementar validação de campos obrigatórios (`identificacao`, `dataNascimento`, `racaLinhagem`, `categoria`) retornando `ErroNegocio.CampoObrigatorio` para campos em branco
  - Implementar validação de ECC (1..5) retornando `ErroNegocio.EccForaDoIntervalo` quando fora do intervalo
  - Implementar verificação de unicidade via `femeaRepository.buscarPorIdentificacao` retornando `ErroNegocio.IdentificacaoDuplicada` com mensagem `"Identificação já cadastrada no plantel"`
  - Determinar `statusInicial` conforme categoria (`MARRA → MARRA_PREPARACAO`, `MATRIZ → AGUARDANDO_COBERTURA`)
  - Persistir fêmea via `femeaRepository.salvar`
  - Verificar alerta de idade mínima: se `MARRA` e `diasDeVida < 160`, salvar `Alerta(tipo = IDADE_MINIMA_PREPARACAO, mensagem = "Fêmea abaixo da idade mínima de preparação reprodutiva (160 dias)", prioridade = MEDIO)`
  - Retornar `ResultadoOperacao.Sucesso(id)` em caso de sucesso
  - _Requisitos: 1.1, 1.2, 1.4, 1.5, 1.6_

  - [ ]* 5.1 Escrever teste de propriedade para validação de campos obrigatórios
    - **Property 3: Validação de campos obrigatórios e opcionais**
    - Para qualquer `CadastrarFemeaComando` com campos obrigatórios aleatoriamente em branco/nulos → `Erro(CampoObrigatorio)`; com todos obrigatórios presentes e qualquer subconjunto de opcionais ausentes → `Sucesso`
    - Usar `FakeFemeaRepository` in-memory que nunca retorna duplicata
    - Mínimo 100 iterações
    - **Valida: Requisitos 1.1, 1.2**

  - [ ]* 5.2 Escrever teste de propriedade para rejeição de identificação duplicada
    - **Property 2: Identificação duplicada é sempre rejeitada**
    - Para qualquer identificação já cadastrada, qualquer nova tentativa com a mesma identificação deve retornar `ResultadoOperacao.Erro(IdentificacaoDuplicada)` com mensagem `"Identificação já cadastrada no plantel"`
    - Usar `FakeFemeaRepository` e `FakeAlertaRepository` in-memory
    - Mínimo 100 iterações
    - **Valida: Requisito 1.4**

  - [ ]* 5.3 Escrever teste de propriedade para alerta de idade mínima
    - **Property 4: Alerta de idade mínima gerado se e somente se marrã < 160 dias**
    - Para qualquer combinação de `dataNascimento` e `categoria`: `MARRA + diasDeVida < 160 → alerta IDADE_MINIMA_PREPARACAO salvo`; `MARRA + diasDeVida >= 160 → nenhum alerta`; `MATRIZ → nenhum alerta`
    - Injetar `LocalDate.now()` via parâmetro para controle determinístico
    - Usar `FakeAlertaRepository` para capturar alertas salvos
    - Mínimo 100 iterações
    - **Valida: Requisito 1.6**

  - [ ]* 5.4 Escrever testes unitários para `CadastrarFemeaUseCase`
    - Cadastro bem-sucedido com todos os campos
    - Cadastro bem-sucedido sem campos opcionais
    - Rejeição com identificação duplicada (mensagem exata)
    - Rejeição com identificação em branco
    - Rejeição com ECC = 0 e ECC = 6
    - Foto vinculada é persistida (Req 1.5)
    - _Requisitos: 1.1, 1.2, 1.4, 1.5, 1.6_

- [x] 6. Checkpoint — Garantir que todos os testes passam
  - Garantir que todos os testes passam; perguntar ao usuário se houver dúvidas.

- [x] 7. Implementar use cases de leitura e ViewModel
  - Criar `ObterFemeaUseCase` em `domain/usecase/ObterFemeaUseCase.kt`
  - Criar `ListarFemeasUseCase` em `domain/usecase/ListarFemeasUseCase.kt`
  - Criar `FemeaUiState` e `FemeaResumo` em `presentation/femea/viewmodel/FemeaUiState.kt`
  - Criar `CadastroFemeaUiState` em `presentation/femea/viewmodel/CadastroFemeaUiState.kt`
  - Criar `FemeaViewModel` em `presentation/femea/viewmodel/FemeaViewModel.kt` com `@HiltViewModel`, expondo `listaState: StateFlow<FemeaUiState>` e `cadastroState: StateFlow<CadastroFemeaUiState>`
  - Implementar `carregarFemeas()`, `atualizarIdadeExibida(dataNascimento)` e `cadastrar(comando)` no ViewModel
  - Mapear cada `ErroNegocio` para o campo correto do `CadastroFemeaUiState`
  - _Requisitos: 1.1, 1.2, 1.3, 1.4, 1.6_

  - [ ]* 7.1 Escrever testes unitários para `FemeaViewModel`
    - Estado de erro mapeado corretamente para cada `ErroNegocio`
    - Estado `sucesso = true` após cadastro bem-sucedido
    - _Requisitos: 1.1, 1.4_

- [x] 8. Implementar componentes Compose reutilizáveis
  - Criar `IdadeChip.kt` em `ui/component/` exibindo idade no formato `"XaYmZd"` com `@Preview`
  - Criar `EccSelector.kt` em `ui/component/` com 5 botões (1–5) e `@Preview`
  - Criar `AlertaBanner.kt` em `ui/component/` com ícone e mensagem de alerta e `@Preview`
  - Criar `CategoriaSelector.kt` em `ui/component/` com `SegmentedButton` Marrã/Matriz e `@Preview`
  - Criar `OrigemDropdown.kt` em `ui/component/` com opções Granja Própria/Fornecedor e `@Preview`
  - Todos os componentes devem usar tokens M3 Expressive (cor, tipografia, shape)
  - _Requisitos: 1.1, 1.2, 1.3, 1.6_

- [x] 9. Implementar telas Compose e navegação
  - Criar `ListaFemeasScreen` em `presentation/femea/screen/ListaFemeasScreen.kt` com `LazyColumn` de `FemeaCard`, `FloatingActionButton` para novo cadastro e observação de `listaState`
  - Criar `CadastroFemeaScreen` em `presentation/femea/screen/CadastroFemeaScreen.kt` com todos os campos (obrigatórios e opcionais), exibição de `idadeFormatada` em tempo real, erros inline por campo e `AlertaBanner` para marrã < 160 dias
  - Definir `Rotas` em `presentation/femea/screen/Rotas.kt` com `LISTA_FEMEAS`, `CADASTRO_FEMEA`, `DETALHE_FEMEA`
  - Registrar as rotas no `NavGraph` da `MainActivity` (ou arquivo de navegação existente)
  - _Requisitos: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6_

  - [ ]* 9.1 Escrever testes de UI para `CadastroFemeaScreen`
    - Campos obrigatórios exibem erro inline ao tentar salvar vazio
    - Banner de alerta aparece quando marrã < 160 dias
    - Idade formatada atualiza em tempo real ao selecionar data
    - _Requisitos: 1.1, 1.3, 1.6_

  - [ ]* 9.2 Escrever testes de UI para `ListaFemeasScreen`
    - Lista exibe identificação e idade de cada fêmea cadastrada
    - _Requisitos: 1.1, 1.3_

- [x] 10. Checkpoint final — Garantir que todos os testes passam
  - Garantir que todos os testes passam; perguntar ao usuário se houver dúvidas.

## Notas

- Tarefas marcadas com `*` são opcionais e podem ser puladas para um MVP mais rápido
- Cada tarefa referencia requisitos específicos para rastreabilidade
- Testes de propriedade usam **Kotest Property Testing** (`io.kotest:kotest-property`), mínimo 100 iterações cada
- Testes de integração usam Room in-memory (sem mock do Room)
- Todos os arquivos de código devem ser Kotlin puro; nenhum arquivo Java deve ser introduzido
