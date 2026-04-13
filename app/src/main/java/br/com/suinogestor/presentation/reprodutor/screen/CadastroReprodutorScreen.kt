package br.com.suinogestor.presentation.reprodutor.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.suinogestor.domain.model.TipoUsoReprodutor
import br.com.suinogestor.domain.usecase.CadastrarReprodutorComando
import br.com.suinogestor.presentation.reprodutor.component.RacaoDiariaDisplay
import br.com.suinogestor.presentation.reprodutor.component.TipoUsoSelector
import br.com.suinogestor.presentation.reprodutor.viewmodel.ReprodutorUiState
import br.com.suinogestor.presentation.reprodutor.viewmodel.ReprodutorViewModel
import br.com.suinogestor.ui.component.EccSelector
import br.com.suinogestor.ui.component.IdadeChip
import br.com.suinogestor.ui.theme.SuinoGestorTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

/**
 * Tela de cadastro de novo reprodutor.
 *
 * Apresenta formulário vertical com campos obrigatórios e opcionais,
 * validação inline, cálculo automático de ração diária e preservação
 * de estado durante rotações de tela.
 *
 * Campos obrigatórios:
 * - Identificação (único no plantel)
 * - Data de nascimento (não futura)
 * - Raça/linhagem
 * - Tipo de uso (Monta Natural, Coleta IA, Ambos)
 *
 * Campos opcionais:
 * - Peso atual (exibe cálculo de ração se preenchido)
 * - ECC atual (1-5)
 *
 * **Validates: Requirements 2.1, 2.2, 2.3, 2.7**
 *
 * @param onCadastroSalvo Callback invocado após cadastro bem-sucedido
 * @param onVoltar Callback para navegação de volta
 * @param viewModel ViewModel injetado via Hilt
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroReprodutorScreen(
    onCadastroSalvo: () -> Unit,
    onVoltar: () -> Unit,
    viewModel: ReprodutorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Form fields — rememberSaveable preserva estado na rotação
    var identificacao       by rememberSaveable { mutableStateOf("") }
    var dataNascimentoTexto by rememberSaveable { mutableStateOf("") }
    var racaLinhagem        by rememberSaveable { mutableStateOf("") }
    var tipoUso             by rememberSaveable { mutableStateOf<TipoUsoReprodutor?>(null) }
    var pesoAtual           by rememberSaveable { mutableStateOf("") }
    var eccSelecionado      by rememberSaveable { mutableStateOf<Int?>(null) }
    var mostrarDatePicker   by rememberSaveable { mutableStateOf(false) }
    var mostrarDialogoDescarte by rememberSaveable { mutableStateOf(false) }

    // Validação inline
    var erroIdentificacao by rememberSaveable { mutableStateOf<String?>(null) }
    var erroDataNascimento by rememberSaveable { mutableStateOf<String?>(null) }
    var erroRaca by rememberSaveable { mutableStateOf<String?>(null) }
    var erroPeso by rememberSaveable { mutableStateOf<String?>(null) }
    var erroEcc by rememberSaveable { mutableStateOf<String?>(null) }

    // Cálculo de idade e ração
    var idadeFormatada by rememberSaveable { mutableStateOf("") }
    var racaoDiaria by rememberSaveable { mutableStateOf<Double?>(null) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = LocalDate.now()
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    )

    val temDadosPreenchidos = identificacao.isNotBlank()
        || dataNascimentoTexto.isNotBlank()
        || racaLinhagem.isNotBlank()
        || tipoUso != null
        || pesoAtual.isNotBlank()
        || eccSelecionado != null

    val camposObrigatoriosPreenchidos = identificacao.isNotBlank()
        && dataNascimentoTexto.isNotBlank()
        && racaLinhagem.isNotBlank()
        && tipoUso != null

    BackHandler(enabled = temDadosPreenchidos) {
        mostrarDialogoDescarte = true
    }

    // Observar estado de sucesso
    LaunchedEffect(uiState) {
        when (uiState) {
            is ReprodutorUiState.Success -> {
                snackbarHostState.showSnackbar(
                    message = (uiState as ReprodutorUiState.Success).mensagem,
                    duration = SnackbarDuration.Short
                )
                viewModel.resetarEstado()
                onCadastroSalvo()
            }
            is ReprodutorUiState.Error -> {
                val mensagem = (uiState as ReprodutorUiState.Error).mensagem
                // Mapear erro para campo específico
                when {
                    mensagem.contains("Identificação já cadastrada") -> {
                        erroIdentificacao = mensagem
                    }
                    mensagem.contains("Data de nascimento") -> {
                        erroDataNascimento = mensagem
                    }
                    mensagem.contains("ECC") -> {
                        erroEcc = mensagem
                    }
                    mensagem.contains("Peso") -> {
                        erroPeso = mensagem
                    }
                    else -> {
                        snackbarHostState.showSnackbar(
                            message = mensagem,
                            duration = SnackbarDuration.Long
                        )
                    }
                }
                viewModel.resetarEstado()
            }
            else -> {}
        }
    }

    // Calcular idade quando data de nascimento muda
    LaunchedEffect(dataNascimentoTexto) {
        if (dataNascimentoTexto.isNotBlank()) {
            runCatching {
                val dataNasc = LocalDate.parse(dataNascimentoTexto)
                val hoje = LocalDate.now()
                
                // Validar data não futura
                if (dataNasc.isAfter(hoje)) {
                    erroDataNascimento = "Data de nascimento não pode ser futura"
                    idadeFormatada = ""
                } else {
                    erroDataNascimento = null
                    val dias = ChronoUnit.DAYS.between(dataNasc, hoje)
                    idadeFormatada = "$dias dias"
                }
            }.onFailure {
                idadeFormatada = ""
            }
        } else {
            idadeFormatada = ""
        }
    }

    // Calcular ração diária quando peso muda
    LaunchedEffect(pesoAtual) {
        if (pesoAtual.isNotBlank()) {
            val peso = pesoAtual.toDoubleOrNull()
            if (peso != null && peso > 0) {
                racaoDiaria = viewModel.calcularRacaoDiaria(peso)
                erroPeso = null
            } else if (peso != null && peso <= 0) {
                erroPeso = "Peso deve ser maior que zero"
                racaoDiaria = null
            } else {
                erroPeso = null
                racaoDiaria = null
            }
        } else {
            racaoDiaria = null
            erroPeso = null
        }
    }

    if (mostrarDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val data = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                        dataNascimentoTexto = data.toString()
                    }
                    mostrarDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (mostrarDialogoDescarte) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoDescarte = false },
            title   = { Text("Descartar alterações?") },
            text    = { Text("Os dados preenchidos serão perdidos.") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoDescarte = false
                    onVoltar()
                }) { Text("Descartar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoDescarte = false }) {
                    Text("Continuar editando")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cadastrar Reprodutor") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (temDadosPreenchidos) mostrarDialogoDescarte = true
                        else onVoltar()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Campo: Identificação
            OutlinedTextField(
                value = identificacao,
                onValueChange = { 
                    identificacao = it
                    erroIdentificacao = null
                },
                label = { Text("Número de identificação *") },
                placeholder = { Text("Ex: R-001") },
                isError = erroIdentificacao != null,
                supportingText = erroIdentificacao?.let { 
                    { Text(it, color = MaterialTheme.colorScheme.error) } 
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Campo: Data de Nascimento
            OutlinedTextField(
                value = dataNascimentoTexto,
                onValueChange = {},
                readOnly = true,
                label = { Text("Data de nascimento *") },
                isError = erroDataNascimento != null,
                supportingText = erroDataNascimento?.let { 
                    { Text(it, color = MaterialTheme.colorScheme.error) } 
                },
                trailingIcon = {
                    Row {
                        if (idadeFormatada.isNotEmpty()) {
                            IdadeChip(idadeFormatada = idadeFormatada)
                        }
                        IconButton(onClick = { mostrarDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Selecionar data"
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Campo: Raça/Linhagem
            OutlinedTextField(
                value = racaLinhagem,
                onValueChange = { 
                    racaLinhagem = it
                    erroRaca = null
                },
                label = { Text("Raça ou linhagem genética *") },
                placeholder = { Text("Ex: Duroc, Landrace") },
                isError = erroRaca != null,
                supportingText = erroRaca?.let { 
                    { Text(it, color = MaterialTheme.colorScheme.error) } 
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Campo: Tipo de Uso
            Text("Tipo de uso *", style = MaterialTheme.typography.labelLarge)
            TipoUsoSelector(
                tipoSelecionado = tipoUso,
                onTipoSelecionado = { tipoUso = it },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo: Peso Atual (opcional)
            OutlinedTextField(
                value = pesoAtual,
                onValueChange = { pesoAtual = it },
                label = { Text("Peso atual (kg)") },
                placeholder = { Text("Ex: 280") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = erroPeso != null,
                supportingText = erroPeso?.let { 
                    { Text(it, color = MaterialTheme.colorScheme.error) } 
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Exibir cálculo de ração se peso válido
            if (racaoDiaria != null) {
                RacaoDiariaDisplay(racaoKg = racaoDiaria!!)
            }

            // Campo: ECC Atual (opcional)
            Text("ECC atual (1–5)", style = MaterialTheme.typography.labelLarge)
            EccSelector(
                eccSelecionado = eccSelecionado,
                onEccSelecionado = { 
                    eccSelecionado = it
                    erroEcc = null
                }
            )
            erroEcc?.let {
                Text(
                    it, 
                    color = MaterialTheme.colorScheme.error, 
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Botão de Cadastro
            Button(
                onClick = {
                    val dataNasc = runCatching { 
                        LocalDate.parse(dataNascimentoTexto) 
                    }.getOrNull() ?: LocalDate.now()
                    
                    viewModel.cadastrar(
                        CadastrarReprodutorComando(
                            identificacao = identificacao,
                            dataNascimento = dataNasc,
                            racaLinhagem = racaLinhagem,
                            tipoUso = tipoUso ?: TipoUsoReprodutor.MONTA_NATURAL,
                            pesoAtualKg = pesoAtual.toDoubleOrNull(),
                            eccAtual = eccSelecionado
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = camposObrigatoriosPreenchidos && uiState !is ReprodutorUiState.Loading
            ) {
                if (uiState is ReprodutorUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Cadastrar Reprodutor")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CadastroReprodutorScreenPreview() {
    SuinoGestorTheme {
        CadastroReprodutorScreen(
            onCadastroSalvo = {},
            onVoltar = {}
        )
    }
}
