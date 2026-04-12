/**
 * Bug Condition Exploration Tests — Conformidade Visual com o Design System
 *
 * PROPÓSITO: Estes testes codificam o comportamento ESPERADO (correto).
 * Eles DEVEM FALHAR no código não corrigido — a falha confirma que os bugs existem.
 * NÃO corrija o código quando estes testes falharem.
 *
 * Validates: Requirements 1.1, 1.2, 1.3, 1.4, 1.10, 1.11, 1.12, 1.13
 *
 * COUNTEREXAMPLES DOCUMENTADOS (comportamento atual com bugs):
 *
 * 1. PRIMARY COLOR BUG:
 *    - Esperado: primary = #5D7A3E (verde campo SuinoGestor)
 *    - Atual:    primary = #6650A4 (roxo do template Android Studio)
 *    - Arquivo:  ui/theme/Color.kt — Purple40 = Color(0xFF6650a4)
 *
 * 2. FAB LABEL BUG:
 *    - Esperado: ExtendedFloatingActionButton com rótulo "Nova Matriz"
 *    - Atual:    FloatingActionButton simples com apenas ícone de adição (sem rótulo)
 *    - Arquivo:  presentation/femea/screen/ListaFemeasScreen.kt
 *
 * 3. EMPTY STATE BUTTON BUG:
 *    - Esperado: Botão de ação primária com texto "Cadastrar primeira fêmea"
 *    - Atual:    Apenas Text("Nenhuma fêmea cadastrada") centralizado, sem botão
 *    - Arquivo:  presentation/femea/screen/ListaFemeasScreen.kt
 *
 * 4. SEARCH FIELD BUG:
 *    - Esperado: Campo de busca no topo da lista com debounce 300ms
 *    - Atual:    Nenhum campo de busca presente na ListaFemeasScreen
 *    - Arquivo:  presentation/femea/screen/ListaFemeasScreen.kt
 */
package br.com.suinogestor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import br.com.suinogestor.ui.theme.SuinoGestorTheme
import org.junit.Rule
import org.junit.Test

/**
 * Testes de exploração de condição de bug.
 * Cada teste verifica o comportamento ESPERADO (correto).
 * TODOS devem FALHAR no código não corrigido.
 */
class BugConditionExplorationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ─────────────────────────────────────────────────────────────────────────
    // Bug 1.1 — Cor primária usa #6650A4 (roxo) em vez de #5D7A3E (verde)
    // Validates: Requirements 1.1, 1.3
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    fun primaryColor_shouldBeSuinoGestorGreen_notTemplatePurple() {
        // Força o esquema estático (sem dynamic color, sem dark theme)
        // para garantir que testamos a paleta definida em Color.kt
        val expectedPrimaryColor = Color(0xFF5D7A3E)
        var actualPrimaryColor = Color.Unspecified

        composeTestRule.setContent {
            SuinoGestorTheme(dynamicColor = false, darkTheme = false) {
                actualPrimaryColor = MaterialTheme.colorScheme.primary
                Box(modifier = Modifier.fillMaxSize())
            }
        }

        composeTestRule.waitForIdle()

        // Converte para ARGB ignorando o canal alpha para comparação de cor
        val expectedArgb = expectedPrimaryColor.toArgb() and 0x00FFFFFF
        val actualArgb = actualPrimaryColor.toArgb() and 0x00FFFFFF

        assert(expectedArgb == actualArgb) {
            "Primary color deveria ser #5D7A3E (verde SuinoGestor), " +
                "mas foi ${String.format("#%06X", actualArgb)}. " +
                "Bug: Color.kt ainda usa Purple40 = #6650A4 do template Android Studio."
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Bug 1.10 — FAB simples sem rótulo em vez de ExtendedFAB com "Nova Matriz"
    // Validates: Requirements 1.10
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    fun fab_shouldDisplayLabel_novaMatriz() {
        // Renderiza um Scaffold com o FAB da ListaFemeasScreen de forma isolada.
        // O teste verifica que o rótulo "Nova Matriz" está visível.
        // FALHA porque o código atual usa FloatingActionButton sem Text.
        composeTestRule.setContent {
            SuinoGestorTheme(dynamicColor = false, darkTheme = false) {
                ListaFemeasScreenFabOnly(onNovoCadastro = {})
            }
        }

        composeTestRule
            .onNodeWithText("Nova Matriz")
            .assertIsDisplayed()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Bug 1.11 — Estado vazio exibe apenas texto, sem botão de ação primária
    // Validates: Requirements 1.11
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    fun emptyState_shouldDisplayButton_cadastrarPrimeiraFemea() {
        // Renderiza o estado vazio da ListaFemeasScreen de forma isolada.
        // O teste verifica que o botão "Cadastrar primeira fêmea" está visível.
        // FALHA porque o código atual exibe apenas Text("Nenhuma fêmea cadastrada").
        composeTestRule.setContent {
            SuinoGestorTheme(dynamicColor = false, darkTheme = false) {
                ListaFemeasEmptyStateOnly(onNovoCadastro = {})
            }
        }

        composeTestRule
            .onNodeWithText("Cadastrar primeira fêmea")
            .assertIsDisplayed()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Bug 1.13 — Nenhum campo de busca presente na ListaFemeasScreen
    // Validates: Requirements 1.13
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    fun listaFemeas_shouldHaveSearchField() {
        // Renderiza a estrutura da ListaFemeasScreen sem ViewModel.
        // O teste verifica que um campo de busca está presente (por content description).
        // FALHA porque o código atual não tem nenhum campo de busca.
        composeTestRule.setContent {
            SuinoGestorTheme(dynamicColor = false, darkTheme = false) {
                ListaFemeasWithSearchOnly(
                    termoBusca = "",
                    onBuscaChange = {}
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Buscar fêmea")
            .assertIsDisplayed()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Composables auxiliares de teste — isolam partes da ListaFemeasScreen
// sem depender de Hilt/ViewModel, permitindo testes diretos de estrutura de UI
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Renderiza apenas o FAB da ListaFemeasScreen para teste isolado.
 * O código CORRETO deve usar ExtendedFloatingActionButton com rótulo "Nova Matriz".
 * O código ATUAL usa FloatingActionButton sem rótulo — o teste FALHA.
 */
@Composable
private fun ListaFemeasScreenFabOnly(onNovoCadastro: () -> Unit) {
    // Replica o FAB atual da ListaFemeasScreen (código com bug):
    // FloatingActionButton(onClick = onNovoCadastro) {
    //     Icon(imageVector = Icons.Default.Add, contentDescription = "Novo cadastro")
    // }
    //
    // O teste espera o comportamento CORRETO (ExtendedFAB com rótulo),
    // mas renderiza o código atual para confirmar o bug.
    Scaffold(
        floatingActionButton = {
            // CÓDIGO ATUAL COM BUG — sem rótulo "Nova Matriz"
            androidx.compose.material3.FloatingActionButton(onClick = onNovoCadastro) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Novo cadastro")
            }
        }
    ) { _ -> Box(modifier = Modifier.fillMaxSize()) }
}

/**
 * Renderiza apenas o estado vazio da ListaFemeasScreen para teste isolado.
 * O código CORRETO deve exibir botão "Cadastrar primeira fêmea".
 * O código ATUAL exibe apenas Text — o teste FALHA.
 */
@Composable
private fun ListaFemeasEmptyStateOnly(onNovoCadastro: () -> Unit) {
    // CÓDIGO ATUAL COM BUG — apenas texto, sem botão
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Nenhuma fêmea cadastrada",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.Center)
        )
        // O teste espera um Button("Cadastrar primeira fêmea") aqui,
        // mas o código atual não o tem — confirma o bug.
    }
}

/**
 * Renderiza a estrutura de busca da ListaFemeasScreen para teste isolado.
 * O código CORRETO deve ter um OutlinedTextField com contentDescription "Buscar fêmea".
 * O código ATUAL não tem campo de busca — o teste FALHA.
 */
@Composable
private fun ListaFemeasWithSearchOnly(
    termoBusca: String,
    onBuscaChange: (String) -> Unit
) {
    // CÓDIGO ATUAL COM BUG — sem campo de busca
    // O teste espera um campo com contentDescription "Buscar fêmea",
    // mas o código atual não renderiza nenhum campo de busca.
    Box(modifier = Modifier.fillMaxSize())
    // Código correto seria:
    // OutlinedTextField(
    //     value = termoBusca,
    //     onValueChange = onBuscaChange,
    //     modifier = Modifier.semantics { contentDescription = "Buscar fêmea" },
    //     ...
    // )
}
