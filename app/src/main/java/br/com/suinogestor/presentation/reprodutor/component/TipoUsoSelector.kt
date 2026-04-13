package br.com.suinogestor.presentation.reprodutor.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.suinogestor.domain.model.TipoUsoReprodutor
import br.com.suinogestor.ui.theme.SuinoGestorTheme

/**
 * Seletor de tipo de uso do reprodutor.
 * 
 * Permite escolher entre três opções: Monta Natural, Coleta IA ou Ambos.
 * Utiliza SegmentedButton do Material 3 com shape morph na seleção.
 * 
 * TODO: Migrar para ConnectedButtonGroup (M3 Expressive) quando material3 >= 1.4.0 estiver disponível
 * 
 * @param tipoSelecionado Tipo de uso atualmente selecionado (pode ser null se nenhum selecionado)
 * @param onTipoSelecionado Callback invocado quando o usuário seleciona um tipo
 * @param modifier Modificador opcional para customização do layout
 */
@Composable
fun TipoUsoSelector(
    tipoSelecionado: TipoUsoReprodutor?,
    onTipoSelecionado: (TipoUsoReprodutor) -> Unit,
    modifier: Modifier = Modifier
) {
    val opcoes = listOf(
        TipoUsoReprodutor.MONTA_NATURAL to "Monta Natural",
        TipoUsoReprodutor.COLETA_IA to "Coleta IA",
        TipoUsoReprodutor.AMBOS to "Ambos"
    )
    
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        opcoes.forEachIndexed { index, (tipo, label) ->
            SegmentedButton(
                selected = tipoSelecionado == tipo,
                onClick = { onTipoSelecionado(tipo) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = opcoes.size),
                label = { Text(text = label, style = MaterialTheme.typography.labelLarge) }
            )
        }
    }
}

@Preview(name = "Nenhum selecionado", showBackground = true)
@Composable
private fun TipoUsoSelectorPreviewNone() {
    SuinoGestorTheme {
        TipoUsoSelector(
            tipoSelecionado = null,
            onTipoSelecionado = {}
        )
    }
}

@Preview(name = "Monta Natural selecionada", showBackground = true)
@Composable
private fun TipoUsoSelectorPreviewMontaNatural() {
    SuinoGestorTheme {
        TipoUsoSelector(
            tipoSelecionado = TipoUsoReprodutor.MONTA_NATURAL,
            onTipoSelecionado = {}
        )
    }
}

@Preview(name = "Coleta IA selecionada", showBackground = true)
@Composable
private fun TipoUsoSelectorPreviewColetaIA() {
    SuinoGestorTheme {
        TipoUsoSelector(
            tipoSelecionado = TipoUsoReprodutor.COLETA_IA,
            onTipoSelecionado = {}
        )
    }
}

@Preview(name = "Ambos selecionado", showBackground = true)
@Composable
private fun TipoUsoSelectorPreviewAmbos() {
    SuinoGestorTheme {
        TipoUsoSelector(
            tipoSelecionado = TipoUsoReprodutor.AMBOS,
            onTipoSelecionado = {}
        )
    }
}
