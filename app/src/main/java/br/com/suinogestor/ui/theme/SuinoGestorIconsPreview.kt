package br.com.suinogestor.ui.theme

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// ---------------------------------------------------------------------------
// Ícones de Animais
// ---------------------------------------------------------------------------

@Preview(name = "Animais — Claro", uiMode = UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "Animais — Escuro", uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun AnimaisIconsPreview() {
    SuinoGestorTheme {
        Surface {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                IconPreviewItem(SuinoGestorIcons.Animais.Matriz, "Matriz")
                IconPreviewItem(SuinoGestorIcons.Animais.Reprodutor, "Reprodutor")
                IconPreviewItem(SuinoGestorIcons.Animais.Leitao, "Leitão")
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Ícones de Fases Reprodutivas
// ---------------------------------------------------------------------------

@Preview(name = "Fases Reprodutivas — Claro", uiMode = UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "Fases Reprodutivas — Escuro", uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun FasesReprodutivasIconsPreview() {
    SuinoGestorTheme {
        Surface {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                IconPreviewItem(SuinoGestorIcons.FasesReprodutivas.Cobertura, "Cobertura")
                IconPreviewItem(SuinoGestorIcons.FasesReprodutivas.Gestacao, "Gestação")
                IconPreviewItem(SuinoGestorIcons.FasesReprodutivas.Parto, "Parto")
                IconPreviewItem(SuinoGestorIcons.FasesReprodutivas.Lactacao, "Lactação")
                IconPreviewItem(SuinoGestorIcons.FasesReprodutivas.Desmame, "Desmame")
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Ícones de Módulos
// ---------------------------------------------------------------------------

@Preview(name = "Módulos — Claro", uiMode = UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "Módulos — Escuro", uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun ModulosIconsPreview() {
    SuinoGestorTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    IconPreviewItem(SuinoGestorIcons.Modulos.Reproducao.Outlined, "Reprod.\nOutlined")
                    IconPreviewItem(SuinoGestorIcons.Modulos.Reproducao.Filled, "Reprod.\nFilled")
                    IconPreviewItem(SuinoGestorIcons.Modulos.Engorda.Outlined, "Engorda\nOutlined")
                    IconPreviewItem(SuinoGestorIcons.Modulos.Engorda.Filled, "Engorda\nFilled")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    IconPreviewItem(SuinoGestorIcons.Modulos.Financeiro.Outlined, "Financ.\nOutlined")
                    IconPreviewItem(SuinoGestorIcons.Modulos.Financeiro.Filled, "Financ.\nFilled")
                    IconPreviewItem(SuinoGestorIcons.Modulos.Indicadores.Outlined, "Indic.\nOutlined")
                    IconPreviewItem(SuinoGestorIcons.Modulos.Indicadores.Filled, "Indic.\nFilled")
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Composable auxiliar
// ---------------------------------------------------------------------------

@Composable
private fun IconPreviewItem(
    iconRes: Int,
    label: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // 48dp
        Icon(
            painter = painterResource(iconRes),
            contentDescription = label,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        // 24dp
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
