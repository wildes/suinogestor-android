package br.com.suinogestor.presentation.reproducao

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.com.suinogestor.R

/**
 * Tela principal do módulo de Reprodução.
 * 
 * Serve como hub de navegação para os submódulos:
 * - Gestão de Fêmeas (matrizes e marrãs)
 * - Gestão de Reprodutores (varrões)
 * - Coberturas (futuro)
 * - Partos (futuro)
 * - Desmames (futuro)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReproducaoScreen(
    onNavegaParaFemeas: () -> Unit,
    onNavegaParaReprodutores: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reprodução") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Gestão do Plantel",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item {
                CardModulo(
                    titulo = "Fêmeas",
                    descricao = "Matrizes e marrãs do plantel",
                    icone = R.drawable.ic_sg_matriz,
                    onClick = onNavegaParaFemeas
                )
            }
            
            item {
                CardModulo(
                    titulo = "Reprodutores",
                    descricao = "Varrões para monta natural e IA",
                    icone = R.drawable.ic_sg_reprodutor,
                    onClick = onNavegaParaReprodutores
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Manejo Reprodutivo",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item {
                CardModulo(
                    titulo = "Coberturas",
                    descricao = "Registro de montas e inseminações",
                    icone = R.drawable.ic_sg_cobertura,
                    onClick = { /* Em construção */ },
                    habilitado = false
                )
            }
            
            item {
                CardModulo(
                    titulo = "Partos",
                    descricao = "Registro de nascimentos e leitões",
                    icone = R.drawable.ic_sg_parto,
                    onClick = { /* Em construção */ },
                    habilitado = false
                )
            }
            
            item {
                CardModulo(
                    titulo = "Desmames",
                    descricao = "Controle de desmame e leitões desmamados",
                    icone = R.drawable.ic_sg_desmame,
                    onClick = { /* Em construção */ },
                    habilitado = false
                )
            }
        }
    }
}

@Composable
private fun CardModulo(
    titulo: String,
    descricao: String,
    icone: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = habilitado, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (habilitado) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(icone),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = if (habilitado) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    }
                )
                
                Column {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (habilitado) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        }
                    )
                    Text(
                        text = if (habilitado) descricao else "Em breve",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = if (habilitado) 1f else 0.5f
                        )
                    )
                }
            }
            
            if (habilitado) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Acessar $titulo",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
