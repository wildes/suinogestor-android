package br.com.suinogestor.presentation.femea.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.suinogestor.presentation.femea.viewmodel.FemeaResumo
import br.com.suinogestor.presentation.femea.viewmodel.FemeaViewModel
import br.com.suinogestor.ui.component.EstadoVazioFemeas
import br.com.suinogestor.ui.component.IdadeChip
import br.com.suinogestor.ui.component.StatusFemeaChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaFemeasScreen(
    onNovoCadastro: () -> Unit,
    onFemeaSelecionada: (Long) -> Unit,
    viewModel: FemeaViewModel = hiltViewModel()
) {
    val state by viewModel.listaState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Plantel de Fêmeas") })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNovoCadastro,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nova Matriz") }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.carregando -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
                            .semantics { contentDescription = "Buscar fêmea" },
                        singleLine = true
                    )

                    when {
                        state.femeas.isEmpty() -> EstadoVazioFemeas(
                            onCadastrarPrimeiraFemea = onNovoCadastro,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        state.femeasFiltradas.isEmpty() -> Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhuma fêmea encontrada com \"${state.termoBusca}\"",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        else -> LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.femeasFiltradas, key = { it.id }) { femea ->
                                FemeaCard(
                                    femea = femea,
                                    onClick = { onFemeaSelecionada(femea.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FemeaCard(
    femea: FemeaResumo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = femea.identificacao,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = femea.racaLinhagem,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                StatusFemeaChip(
                    status = femea.status,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IdadeChip(idadeFormatada = femea.idadeFormatada)
        }
    }
}
