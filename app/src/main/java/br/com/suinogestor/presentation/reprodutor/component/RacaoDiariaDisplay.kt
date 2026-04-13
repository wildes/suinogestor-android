package br.com.suinogestor.presentation.reprodutor.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.suinogestor.ui.theme.SuinoGestorTheme

/**
 * Componente read-only para exibir a quantidade de ração diária recomendada.
 * 
 * Exibe um layout horizontal com o label "Ração diária recomendada" à esquerda
 * e o valor formatado em destaque à direita. O valor é formatado com duas casas
 * decimais seguido da unidade "kg".
 * 
 * Utiliza primaryContainer como cor de fundo e shape medium para consistência
 * com o design system do SuinoGestor.
 * 
 * **Validates: Requirements 2.3** - Exibição do cálculo de ração diária (1% do peso vivo)
 * 
 * @param racaoKg Quantidade de ração diária em quilogramas
 * @param modifier Modificador opcional para customização do layout
 */
@Composable
fun RacaoDiariaDisplay(
    racaoKg: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.shapes.medium
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Ração diária recomendada",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = "%.2f kg".format(racaoKg),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Preview(name = "Ração 2.80 kg", showBackground = true)
@Composable
private fun RacaoDiariaDisplayPreview280() {
    SuinoGestorTheme {
        RacaoDiariaDisplay(racaoKg = 2.80)
    }
}

@Preview(name = "Ração 3.50 kg", showBackground = true)
@Composable
private fun RacaoDiariaDisplayPreview350() {
    SuinoGestorTheme {
        RacaoDiariaDisplay(racaoKg = 3.50)
    }
}

@Preview(name = "Ração 1.20 kg", showBackground = true)
@Composable
private fun RacaoDiariaDisplayPreview120() {
    SuinoGestorTheme {
        RacaoDiariaDisplay(racaoKg = 1.20)
    }
}
