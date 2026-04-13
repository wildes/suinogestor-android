package br.com.suinogestor.presentation.reprodutor.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.suinogestor.ui.theme.SuinoGestorTheme

/**
 * Componente visual read-only para exibir o ECC (Escore de Condição Corporal).
 * 
 * Exibe uma escala visual de 1 a 5 com o valor atual destacado.
 * Utilizado na tela de detalhes para mostrar o ECC de forma visual e intuitiva.
 * 
 * @param ecc Valor do ECC (1-5), ou null se não disponível
 * @param modifier Modificador opcional para customização do layout
 */
@Composable
fun EccVisualDisplay(
    ecc: Int?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        (1..5).forEach { valor ->
            val isSelected = ecc == valor
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = valor.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(name = "ECC 3 selecionado", showBackground = true)
@Composable
private fun EccVisualDisplayPreview() {
    SuinoGestorTheme {
        EccVisualDisplay(ecc = 3)
    }
}

@Preview(name = "ECC 1 selecionado", showBackground = true)
@Composable
private fun EccVisualDisplayPreviewMin() {
    SuinoGestorTheme {
        EccVisualDisplay(ecc = 1)
    }
}

@Preview(name = "ECC 5 selecionado", showBackground = true)
@Composable
private fun EccVisualDisplayPreviewMax() {
    SuinoGestorTheme {
        EccVisualDisplay(ecc = 5)
    }
}

@Preview(name = "Sem ECC", showBackground = true)
@Composable
private fun EccVisualDisplayPreviewNull() {
    SuinoGestorTheme {
        EccVisualDisplay(ecc = null)
    }
}
