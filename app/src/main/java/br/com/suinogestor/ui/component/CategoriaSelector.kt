package br.com.suinogestor.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.suinogestor.domain.model.CategoriaFemea
import br.com.suinogestor.ui.theme.SuinoGestorTheme

// TODO: Migrar para ButtonGroup (M3 Expressive) quando material3 >= 1.4.0 estiver disponível
// Requer BOM com material3:1.4.x — atualmente o cache local tem apenas 1.3.x
@Composable
fun CategoriaSelector(
    categoriaSelecionada: CategoriaFemea?,
    onCategoriaSelecionada: (CategoriaFemea) -> Unit,
    modifier: Modifier = Modifier
) {
    val opcoes = listOf(CategoriaFemea.MARRA to "Marrã", CategoriaFemea.MATRIZ to "Matriz")
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        opcoes.forEachIndexed { index, (categoria, label) ->
            SegmentedButton(
                selected = categoriaSelecionada == categoria,
                onClick = { onCategoriaSelecionada(categoria) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = opcoes.size),
                label = { Text(text = label, style = MaterialTheme.typography.labelLarge) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoriaSelectorPreview() {
    SuinoGestorTheme {
        CategoriaSelector(
            categoriaSelecionada = CategoriaFemea.MARRA,
            onCategoriaSelecionada = {}
        )
    }
}
