package br.com.suinogestor.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.suinogestor.domain.model.StatusFemea
import br.com.suinogestor.ui.theme.SuinoGestorTheme

@Composable
fun StatusFemeaChip(
    status: StatusFemea,
    modifier: Modifier = Modifier
) {
    val (containerColor, contentColor) = when (status) {
        StatusFemea.GESTANTE ->
            MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        StatusFemea.LACTANTE ->
            MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        StatusFemea.VAZIA ->
            MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        StatusFemea.DESCARTADA ->
            MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        StatusFemea.MARRA_PREPARACAO,
        StatusFemea.AGUARDANDO_COBERTURA ->
            MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    val label = when (status) {
        StatusFemea.GESTANTE             -> "Gestante"
        StatusFemea.LACTANTE             -> "Lactante"
        StatusFemea.VAZIA                -> "Vazia"
        StatusFemea.DESCARTADA           -> "Descartada"
        StatusFemea.MARRA_PREPARACAO     -> "Marrã"
        StatusFemea.AGUARDANDO_COBERTURA -> "Aguardando"
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = containerColor,
        contentColor = contentColor
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusFemeaChipPreview() {
    SuinoGestorTheme {
        StatusFemeaChip(status = StatusFemea.GESTANTE)
    }
}
