package br.com.suinogestor.presentation.reprodutor.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.suinogestor.domain.model.CategoriaIdadeReprodutor
import br.com.suinogestor.presentation.reprodutor.component.CategoriaIdadeChip
import br.com.suinogestor.presentation.reprodutor.viewmodel.ReprodutorResumo
import br.com.suinogestor.presentation.reprodutor.viewmodel.ReprodutorViewModel
import br.com.suinogestor.ui.component.EstadoVazioReprodutores
import br.com.suinogestor.ui.theme.SuinoGestorIcons
import br.com.suinogestor.ui.theme.SuinoGestorTheme

/**
 * Tela de listagem de reprodutores do plantel.
 *
 * Exibe lista de reprodutores com busca, filtros e navegação para
 * cadastro e detalhes. Segue o padrão estabelecido em ListaFemeasScreen.
 *
 * **Layout:**
 * - TopAppBar com título "Reprodutores" e campo de busca
 * - LazyColumn com cards de reprodutores
 * - ExtendedFAB "Novo Reprodutor" fixo no canto inferior direito
 *
 * **Estados:**
 * - Vazio: ilustração + mensagem + botão
 * - Sem resultado: mensagem contextual
 * - Carregando: CircularProgressIndicator
 * - Lista: cards com dados do reprodutor
 *
 * **Validates: Requirements 2.1, 2.2, 2.3**
 *
 * @param onNovoCadastro Callback para navegação para tela de cadastro
 * @param onReprodutorSelecionado Callback para navegação para tela de detalhes
 * @param viewModel ViewModel injetado via Hilt
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaReprodutoresScreen(
    onNovoCadastro: () -> Unit,
    onReprodutorSelecionado: (Long) -> Unit,
    onVoltar: () -> Unit = {},
    viewModel: ReprodutorViewModel = hiltViewModel()
) {
    val state by viewModel.listaState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reprodutores") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNovoCadastro,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Novo Reprodutor") }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.carregando -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
                state.erro != null -> Text(
                    text = state.erro ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
                else -> Column(modifier = Modifier.fillMaxSize()) {
                    // Campo de busca
                    OutlinedTextField(
                        value = state.termoBusca,
                        onValueChange = { viewModel.atualizarBusca(it) },
                        placeholder = { Text("Buscar por identificação") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (state.termoBusca.isNotEmpty()) {
                                IconButton(onClick = { viewModel.atualizarBusca("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Limpar busca")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .semantics { contentDescription = "Buscar reprodutor" },
                        singleLine = true
                    )

                    when {
                        state.reprodutores.isEmpty() -> EstadoVazioReprodutores(
                            onCadastrarPrimeiroReprodutor = onNovoCadastro,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        state.reprodutoresFiltrados.isEmpty() -> Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhum reprodutor encontrado com \"${state.termoBusca}\"",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        else -> LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.reprodutoresFiltrados, key = { it.id }) { reprodutor ->
                                ReprodutorCard(
                                    reprodutor = reprodutor,
                                    onClick = { onReprodutorSelecionado(reprodutor.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card de reprodutor para exibição em lista.
 *
 * **Layout conforme design doc:**
 * ```
 * ┌─────────────────────────────────────┐
 * │ 🐗 R-042                            │
 * │ Duroc | Adulto (450 dias)           │
 * │ Peso: 280kg | ECC: 3                │
 * │ Ração diária: 2.8kg                 │
 * │ ⚠️ 18 dias sem uso                  │ (se houver alerta)
 * └─────────────────────────────────────┘
 * ```
 *
 * @param reprodutor Dados resumidos do reprodutor
 * @param onClick Callback ao clicar no card
 * @param modifier Modificador opcional
 */
@Composable
private fun ReprodutorCard(
    reprodutor: ReprodutorResumo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Linha 1: Ícone + Identificação
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(SuinoGestorIcons.Animais.Reprodutor),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = reprodutor.identificacao,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Linha 2: Raça | Categoria (idade)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = reprodutor.racaLinhagem,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "|",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                CategoriaIdadeChip(categoria = reprodutor.categoria)
                Text(
                    text = "(${reprodutor.idadeDias} dias)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Linha 3: Peso | ECC
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (reprodutor.pesoAtualKg != null) {
                    Text(
                        text = "Peso: ${String.format("%.0f", reprodutor.pesoAtualKg)}kg",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (reprodutor.eccAtual != null) {
                    Text(
                        text = "ECC: ${reprodutor.eccAtual}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Linha 4: Ração diária
            if (reprodutor.racaoDiariaKg != null) {
                Text(
                    text = "Ração diária: ${String.format("%.2f", reprodutor.racaoDiariaKg)}kg",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Linha 5: Alerta de inatividade (se houver)
            if (reprodutor.diasSemUso != null && reprodutor.diasSemUso > 15) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "${reprodutor.diasSemUso} dias sem uso",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReprodutorCardPreview() {
    SuinoGestorTheme {
        ReprodutorCard(
            reprodutor = ReprodutorResumo(
                id = 1,
                identificacao = "R-042",
                racaLinhagem = "Duroc",
                categoria = CategoriaIdadeReprodutor.ADULTO,
                idadeDias = 450,
                pesoAtualKg = 280.0,
                eccAtual = 3,
                racaoDiariaKg = 2.8,
                diasSemUso = 18
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReprodutorCardSemAlertaPreview() {
    SuinoGestorTheme {
        ReprodutorCard(
            reprodutor = ReprodutorResumo(
                id = 2,
                identificacao = "R-015",
                racaLinhagem = "Landrace",
                categoria = CategoriaIdadeReprodutor.TREINAMENTO,
                idadeDias = 250,
                pesoAtualKg = 180.0,
                eccAtual = 4,
                racaoDiariaKg = 1.8,
                diasSemUso = null
            ),
            onClick = {}
        )
    }
}
