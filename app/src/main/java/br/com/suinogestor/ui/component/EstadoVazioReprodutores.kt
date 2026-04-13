package br.com.suinogestor.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.suinogestor.ui.theme.SuinoGestorIcons
import br.com.suinogestor.ui.theme.SuinoGestorTheme

/**
 * Componente de estado vazio para a tela de listagem de reprodutores.
 *
 * Exibe ilustração temática, mensagem contextual e botão de ação
 * quando não há reprodutores cadastrados no plantel.
 *
 * Segue o padrão de design system do SuinoGestor para estados vazios.
 */
@Composable
fun EstadoVazioReprodutores(
    onCadastrarPrimeiroReprodutor: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = painterResource(SuinoGestorIcons.Animais.Reprodutor),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "Nenhum reprodutor cadastrado ainda",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Cadastre seu primeiro varrão para começar a gerenciar a utilização dos machos",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Button(onClick = onCadastrarPrimeiroReprodutor) {
            Text("Cadastrar primeiro reprodutor")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EstadoVazioReprodutoresPreview() {
    SuinoGestorTheme {
        EstadoVazioReprodutores(onCadastrarPrimeiroReprodutor = {})
    }
}
