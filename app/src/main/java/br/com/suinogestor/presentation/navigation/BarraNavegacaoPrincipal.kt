package br.com.suinogestor.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * Barra de navegação principal do SuinoGestor.
 * 
 * Exibe os 4 módulos principais (Reprodução, Engorda, Financeiro, Indicadores)
 * com ícones e rótulos sempre visíveis.
 * 
 * Segue as diretrizes do Design System:
 * - Altura mínima: 80dp
 * - Área de toque por item: 48dp
 * - Rótulos sempre visíveis
 * - Destino ativo com fundo secondaryContainer
 * 
 * @param destinoAtual Rota do destino atualmente selecionado
 * @param onDestinoSelecionado Callback quando um destino é selecionado
 * @param modifier Modificador opcional
 */
@Composable
fun BarraNavegacaoPrincipal(
    destinoAtual: String,
    onDestinoSelecionado: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        val destinos = listOf(
            Destino.Reproducao,
            Destino.Engorda,
            Destino.Financeiro,
            Destino.Indicadores
        )
        
        destinos.forEach { destino ->
            val selecionado = destinoAtual == destino.rota
            
            NavigationBarItem(
                selected = selecionado,
                onClick = { onDestinoSelecionado(destino.rota) },
                icon = {
                    Icon(
                        painter = painterResource(
                            if (selecionado) destino.iconeAtivo else destino.iconeInativo
                        ),
                        contentDescription = destino.titulo
                    )
                },
                label = {
                    Text(
                        text = destino.titulo,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
