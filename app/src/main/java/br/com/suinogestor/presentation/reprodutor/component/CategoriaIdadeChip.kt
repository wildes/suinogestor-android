package br.com.suinogestor.presentation.reprodutor.component

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.suinogestor.domain.model.CategoriaIdadeReprodutor
import br.com.suinogestor.ui.theme.SuinoGestorTheme

/**
 * Chip visual para exibir a categoria de idade do reprodutor.
 * 
 * Mapeia cada categoria para um label descritivo e uma cor de container:
 * - IMATURO: "Imaturo" com tertiaryContainer (< 210 dias)
 * - TREINAMENTO: "Treinamento" com secondaryContainer (210-300 dias)
 * - ADULTO: "Adulto" com primaryContainer (300+ dias)
 * 
 * Utiliza AssistChip do Material 3 para consistência visual.
 * 
 * @param categoria Categoria de idade do reprodutor
 * @param modifier Modificador opcional para customização do layout
 */
@Composable
fun CategoriaIdadeChip(
    categoria: CategoriaIdadeReprodutor,
    modifier: Modifier = Modifier
) {
    val (label, containerColor) = when (categoria) {
        CategoriaIdadeReprodutor.IMATURO -> 
            "Imaturo" to MaterialTheme.colorScheme.tertiaryContainer
        CategoriaIdadeReprodutor.TREINAMENTO -> 
            "Treinamento" to MaterialTheme.colorScheme.secondaryContainer
        CategoriaIdadeReprodutor.ADULTO -> 
            "Adulto" to MaterialTheme.colorScheme.primaryContainer
    }
    
    AssistChip(
        onClick = {},
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor
        ),
        modifier = modifier
    )
}

@Preview(name = "Categoria Imaturo", showBackground = true)
@Composable
private fun CategoriaIdadeChipPreviewImaturo() {
    SuinoGestorTheme {
        CategoriaIdadeChip(categoria = CategoriaIdadeReprodutor.IMATURO)
    }
}

@Preview(name = "Categoria Treinamento", showBackground = true)
@Composable
private fun CategoriaIdadeChipPreviewTreinamento() {
    SuinoGestorTheme {
        CategoriaIdadeChip(categoria = CategoriaIdadeReprodutor.TREINAMENTO)
    }
}

@Preview(name = "Categoria Adulto", showBackground = true)
@Composable
private fun CategoriaIdadeChipPreviewAdulto() {
    SuinoGestorTheme {
        CategoriaIdadeChip(categoria = CategoriaIdadeReprodutor.ADULTO)
    }
}
