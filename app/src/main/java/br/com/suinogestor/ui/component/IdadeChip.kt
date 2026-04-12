package br.com.suinogestor.ui.component

import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.suinogestor.ui.theme.SuinoGestorTheme

@Composable
fun IdadeChip(
    idadeFormatada: String,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = {},
        label = {
            Text(
                text = idadeFormatada,
                style = MaterialTheme.typography.labelMedium
            )
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun IdadeChipPreview() {
    SuinoGestorTheme {
        IdadeChip(idadeFormatada = "1a3m15d")
    }
}
