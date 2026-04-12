package br.com.suinogestor.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.suinogestor.ui.theme.SuinoGestorTheme

@Composable
fun EccSelector(
    eccSelecionado: Int?,
    onEccSelecionado: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        (1..5).forEach { valor ->
            FilterChip(
                selected = eccSelecionado == valor,
                onClick = { onEccSelecionado(valor) },
                label = {
                    Text(
                        text = valor.toString(),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EccSelectorPreview() {
    SuinoGestorTheme {
        EccSelector(eccSelecionado = 3, onEccSelecionado = {})
    }
}
