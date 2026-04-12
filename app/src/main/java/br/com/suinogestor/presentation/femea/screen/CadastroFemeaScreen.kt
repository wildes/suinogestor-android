package br.com.suinogestor.presentation.femea.screen

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.suinogestor.domain.model.CategoriaFemea
import br.com.suinogestor.domain.model.OrigemAnimal
import br.com.suinogestor.domain.usecase.CadastrarFemeaComando
import br.com.suinogestor.presentation.femea.viewmodel.FemeaViewModel
import br.com.suinogestor.ui.component.AlertaBanner
import br.com.suinogestor.ui.component.CategoriaSelector
import br.com.suinogestor.ui.component.EccSelector
import br.com.suinogestor.ui.component.IdadeChip
import br.com.suinogestor.ui.component.OrigemDropdown
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroFemeaScreen(
    onCadastroSalvo: () -> Unit,
    onVoltar: () -> Unit,
    viewModel: FemeaViewModel = hiltViewModel()
) {
    val state by viewModel.cadastroState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Form fields — rememberSaveable preserva estado na rotação
    var identificacao       by rememberSaveable { mutableStateOf("") }
    var dataNascimentoTexto by rememberSaveable { mutableStateOf("") }
    var racaLinhagem        by rememberSaveable { mutableStateOf("") }
    var categoria           by rememberSaveable { mutableStateOf<CategoriaFemea?>(null) }
    var pesoEntrada         by rememberSaveable { mutableStateOf("") }
    var eccSelecionado      by rememberSaveable { mutableStateOf<Int?>(null) }
    var origemSelecionada   by rememberSaveable { mutableStateOf<OrigemAnimal?>(null) }
    var mostrarDatePicker   by rememberSaveable { mutableStateOf(false) }
    var mostrarDialogoDescarte by rememberSaveable { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = LocalDate.now()
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    )

    val temDadosPreenchidos = identificacao.isNotBlank()
        || dataNascimentoTexto.isNotBlank()
        || racaLinhagem.isNotBlank()
        || categoria != null
        || pesoEntrada.isNotBlank()
        || eccSelecionado != null
        || origemSelecionada != null

    BackHandler(enabled = temDadosPreenchidos) {
        mostrarDialogoDescarte = true
    }

    LaunchedEffect(state.sucesso) {
        if (state.sucesso) {
            snackbarHostState.showSnackbar(
                message  = "Fêmea cadastrada com sucesso!",
                duration = SnackbarDuration.Short
            )
            onCadastroSalvo()
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
                        viewModel.atualizarIdadeExibida(data)
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
                title = { Text("Cadastrar Fêmea") },
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

            if (state.alertaIdadeMinima) {
                AlertaBanner(
                    mensagem = "Fêmea abaixo da idade mínima de preparação reprodutiva (160 dias)"
                )
            }

            OutlinedTextField(
                value = identificacao,
                onValueChange = { identificacao = it },
                label = { Text("Identificação (brinco/tatuagem) *") },
                isError = state.erroIdentificacao != null,
                supportingText = state.erroIdentificacao?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Data de nascimento — DatePicker modal
            OutlinedTextField(
                value = dataNascimentoTexto,
                onValueChange = {},
                readOnly = true,
                label = { Text("Data de Nascimento *") },
                trailingIcon = {
                    Row {
                        if (state.idadeFormatada.isNotEmpty()) {
                            IdadeChip(idadeFormatada = state.idadeFormatada)
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

            OutlinedTextField(
                value = racaLinhagem,
                onValueChange = { racaLinhagem = it },
                label = { Text("Raça/Linhagem *") },
                isError = state.erroRaca != null,
                supportingText = state.erroRaca?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Categoria *", style = MaterialTheme.typography.labelLarge)
            CategoriaSelector(
                categoriaSelecionada = categoria,
                onCategoriaSelecionada = { categoria = it },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = pesoEntrada,
                onValueChange = { pesoEntrada = it },
                label = { Text("Peso de Entrada (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("ECC (1–5)", style = MaterialTheme.typography.labelLarge)
            EccSelector(
                eccSelecionado = eccSelecionado,
                onEccSelecionado = { eccSelecionado = it }
            )
            state.erroEcc?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Text("Origem", style = MaterialTheme.typography.labelLarge)
            OrigemDropdown(
                origemSelecionada = origemSelecionada,
                onOrigemSelecionada = { origemSelecionada = it }
            )

            state.erroGeral?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = {
                    val dataNasc = runCatching { LocalDate.parse(dataNascimentoTexto) }.getOrNull()
                    viewModel.cadastrar(
                        CadastrarFemeaComando(
                            identificacao = identificacao,
                            dataNascimento = dataNasc ?: LocalDate.now(),
                            racaLinhagem = racaLinhagem,
                            categoria = categoria ?: CategoriaFemea.MARRA,
                            pesoEntradaKg = pesoEntrada.toDoubleOrNull(),
                            eccAtual = eccSelecionado,
                            origem = origemSelecionada,
                            fotoUri = null
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.salvando
            ) {
                if (state.salvando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Cadastrar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
