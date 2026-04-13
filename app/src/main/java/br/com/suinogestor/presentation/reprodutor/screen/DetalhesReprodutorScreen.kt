package br.com.suinogestor.presentation.reprodutor.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.suinogestor.domain.model.CategoriaIdadeReprodutor
import br.com.suinogestor.domain.model.Reprodutor
import br.com.suinogestor.domain.model.TipoUsoReprodutor
import br.com.suinogestor.domain.usecase.ReprodutorDetalhes
import br.com.suinogestor.presentation.reprodutor.component.CategoriaIdadeChip
import br.com.suinogestor.presentation.reprodutor.component.RacaoDiariaDisplay
import br.com.suinogestor.presentation.reprodutor.viewmodel.ReprodutorDetalhesUiState
import br.com.suinogestor.presentation.reprodutor.viewmodel.ReprodutorViewModel
import br.com.suinogestor.ui.component.EccSelector
import br.com.suinogestor.ui.theme.SuinoGestorTheme
import java.time.LocalDate

/**
 * Tela de detalhes de um reprodutor.
 *
 * Exibe informações completas do reprodutor organizadas em seções:
 * - Identificação: número, raça, tipo de uso
 * - Dados Físicos: idade com categoria, peso, ECC, ração diária
 * - Alertas Ativos: placeholder para integração futura com sistema de alertas
 *
 * Ações disponíveis:
 * - Editar reprodutor (IconButton no TopAppBar)
 * - Ver histórico de uso (Button desabilitado - placeholder para módulo futuro)
 *
 * **Validates: Requirements 2.1, 2.2, 2.3**
 *
 * @param reprodutorId ID do reprodutor a ser exibido
 * @param onNavigateBack Callback para navegação de volta
 * @param onEditarClick Callback para navegação para tela de edição
 * @param viewModel ViewModel para gerenciar estado e operações
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesReprodutorScreen(
    reprodutorId: Long,
    onNavigateBack: () -> Unit,
    onEditarClick: (Long) -> Unit,
    viewModel: ReprodutorViewModel = hiltViewModel()
) {
    val detalhesState by viewModel.obterDetalhes(reprodutorId).collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Reprodutor") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onEditarClick(reprodutorId) },
                        enabled = detalhesState is ReprodutorDetalhesUiState.Success
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar reprodutor"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = detalhesState) {
            is ReprodutorDetalhesUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ReprodutorDetalhesUiState.Success -> {
                DetalhesContent(
                    detalhes = state.detalhes,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is ReprodutorDetalhesUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.mensagem,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun DetalhesContent(
    detalhes: ReprodutorDetalhes,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Seção: Identificação
        SecaoIdentificacao(detalhes = detalhes)

        // Seção: Dados Físicos
        SecaoDadosFisicos(detalhes = detalhes)

        // Seção: Alertas Ativos (placeholder)
        SecaoAlertasAtivos()

        // Botão: Ver Histórico de Uso (placeholder desabilitado)
        Button(
            onClick = { /* TODO: implementar quando módulo de coberturas estiver pronto */ },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Histórico de Uso")
        }
    }
}

@Composable
private fun SecaoIdentificacao(
    detalhes: ReprodutorDetalhes,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Identificação",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Número de identificação (destaque)
            Text(
                text = detalhes.reprodutor.identificacao,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Raça/linhagem
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Raça/Linhagem:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = detalhes.reprodutor.racaLinhagem,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Tipo de uso (chip)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tipo de Uso:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = when (detalhes.reprodutor.tipoUso) {
                                TipoUsoReprodutor.MONTA_NATURAL -> "Monta Natural"
                                TipoUsoReprodutor.COLETA_IA -> "Coleta IA"
                                TipoUsoReprodutor.AMBOS -> "Ambos"
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun SecaoDadosFisicos(
    detalhes: ReprodutorDetalhes,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Dados Físicos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Idade com chip de categoria
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Idade:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${detalhes.idadeDias} dias",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                CategoriaIdadeChip(categoria = detalhes.categoria)
            }

            // Peso
            if (detalhes.reprodutor.pesoAtualKg != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Peso Atual:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "%.1f kg".format(detalhes.reprodutor.pesoAtualKg),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // ECC visual
            if (detalhes.reprodutor.eccAtual != null) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Escore de Condição Corporal (ECC):",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    EccSelector(
                        eccSelecionado = detalhes.reprodutor.eccAtual,
                        onEccSelecionado = { /* read-only */ }
                    )
                }
            }

            // Ração diária
            if (detalhes.racaoDiariaKg != null) {
                Spacer(modifier = Modifier.height(8.dp))
                RacaoDiariaDisplay(racaoKg = detalhes.racaoDiariaKg)
            }
        }
    }
}

@Composable
private fun SecaoAlertasAtivos(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Alertas Ativos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Placeholder vazio por enquanto
            Text(
                text = "Nenhum alerta ativo no momento",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Previews

@Preview(name = "Detalhes - Adulto com todos os dados", showBackground = true)
@Composable
private fun DetalhesContentPreviewCompleto() {
    SuinoGestorTheme {
        DetalhesContent(
            detalhes = ReprodutorDetalhes(
                reprodutor = Reprodutor(
                    id = 1,
                    identificacao = "R-042",
                    dataNascimento = LocalDate.now().minusDays(450),
                    racaLinhagem = "Duroc",
                    tipoUso = TipoUsoReprodutor.AMBOS,
                    pesoAtualKg = 280.0,
                    eccAtual = 3,
                    ativo = true,
                    dataCadastro = LocalDate.now().minusDays(450)
                ),
                idadeDias = 450,
                categoria = CategoriaIdadeReprodutor.ADULTO,
                racaoDiariaKg = 2.8
            )
        )
    }
}

@Preview(name = "Detalhes - Treinamento sem peso/ECC", showBackground = true)
@Composable
private fun DetalhesContentPreviewTreinamento() {
    SuinoGestorTheme {
        DetalhesContent(
            detalhes = ReprodutorDetalhes(
                reprodutor = Reprodutor(
                    id = 2,
                    identificacao = "R-015",
                    dataNascimento = LocalDate.now().minusDays(250),
                    racaLinhagem = "Landrace",
                    tipoUso = TipoUsoReprodutor.MONTA_NATURAL,
                    pesoAtualKg = null,
                    eccAtual = null,
                    ativo = true,
                    dataCadastro = LocalDate.now().minusDays(250)
                ),
                idadeDias = 250,
                categoria = CategoriaIdadeReprodutor.TREINAMENTO,
                racaoDiariaKg = null
            )
        )
    }
}

@Preview(name = "Detalhes - Imaturo", showBackground = true)
@Composable
private fun DetalhesContentPreviewImaturo() {
    SuinoGestorTheme {
        DetalhesContent(
            detalhes = ReprodutorDetalhes(
                reprodutor = Reprodutor(
                    id = 3,
                    identificacao = "R-089",
                    dataNascimento = LocalDate.now().minusDays(180),
                    racaLinhagem = "Hampshire",
                    tipoUso = TipoUsoReprodutor.COLETA_IA,
                    pesoAtualKg = 120.0,
                    eccAtual = 2,
                    ativo = true,
                    dataCadastro = LocalDate.now().minusDays(180)
                ),
                idadeDias = 180,
                categoria = CategoriaIdadeReprodutor.IMATURO,
                racaoDiariaKg = 1.2
            )
        )
    }
}
