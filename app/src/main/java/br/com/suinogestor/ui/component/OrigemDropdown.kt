package br.com.suinogestor.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.suinogestor.domain.model.OrigemAnimal
import br.com.suinogestor.ui.theme.SuinoGestorTheme

private val origemLabels = mapOf(
    OrigemAnimal.PROPRIA to "Granja Própria",
    OrigemAnimal.FORNECEDOR to "Fornecedor"
)

@Composable
fun OrigemDropdown(
    origemSelecionada: OrigemAnimal?,
    onOrigemSelecionada: (OrigemAnimal) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedButton(onClick = { expandido = true }) {
            Text(
                text = origemSelecionada?.let { origemLabels[it] } ?: "Selecionar origem",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            OrigemAnimal.entries.forEach { origem ->
                DropdownMenuItem(
                    text = { Text(origemLabels[origem] ?: origem.name) },
                    onClick = {
                        onOrigemSelecionada(origem)
                        expandido = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OrigemDropdownPreview() {
    SuinoGestorTheme {
        OrigemDropdown(origemSelecionada = OrigemAnimal.PROPRIA, onOrigemSelecionada = {})
    }
}
