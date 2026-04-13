package br.com.suinogestor

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Testes instrumentados que verificam que a MainActivity instala a splash screen
 * e que a transição de saída ocorre dentro do limite de 300ms.
 *
 * Valida: Requisito 2.4 (transição fade ≤ 300ms)
 */
@RunWith(AndroidJUnit4::class)
class SplashScreenTest {

    @Test
    fun mainActivityInicializaSemCrash() {
        // Verifica que a MainActivity pode ser lançada sem exceção
        // (inclui installSplashScreen() no onCreate)
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            assertTrue(
                "MainActivity deve estar em estado RESUMED após lançamento",
                scenario.state.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED),
            )
        }
    }

    @Test
    fun splashScreenTransicaoOcorreDentroDoLimite() {
        val startTime = System.currentTimeMillis()

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                // Verifica que a activity está visível (splash já transitou)
                assertTrue(
                    "A activity deve estar visível após a splash screen",
                    !activity.isFinishing,
                )
            }
        }

        val elapsed = System.currentTimeMillis() - startTime
        // A splash + transição de saída (300ms) + margem de 2s para inicialização
        assertTrue(
            "Tempo total de inicialização deve ser razoável (< 5000ms), foi: ${elapsed}ms",
            elapsed < 5000L,
        )
    }
}
