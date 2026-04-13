package br.com.suinogestor

import androidx.core.content.ContextCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import br.com.suinogestor.R
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Testes instrumentados que verificam que todos os drawables ic_sg_* podem ser
 * inflados sem exceção e retornam drawable não nulo.
 *
 * Usa ContextCompat.getDrawable() — compatível com VectorDrawable e AnimatedVectorDrawable.
 * Dependência: androidx.core:core-ktx (explícita no build.gradle.kts).
 */
@RunWith(AndroidJUnit4::class)
class IconInflateTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val icSgDrawables = listOf(
        // Animais
        R.drawable.ic_sg_matriz,
        R.drawable.ic_sg_reprodutor,
        R.drawable.ic_sg_leitao,
        // Fases reprodutivas
        R.drawable.ic_sg_cobertura,
        R.drawable.ic_sg_gestacao,
        R.drawable.ic_sg_parto,
        R.drawable.ic_sg_lactacao,
        R.drawable.ic_sg_desmame,
        // Módulos outlined
        R.drawable.ic_sg_reproducao_outlined,
        R.drawable.ic_sg_engorda_outlined,
        R.drawable.ic_sg_financeiro_outlined,
        R.drawable.ic_sg_indicadores_outlined,
        // Módulos filled
        R.drawable.ic_sg_reproducao_filled,
        R.drawable.ic_sg_engorda_filled,
        R.drawable.ic_sg_financeiro_filled,
        R.drawable.ic_sg_indicadores_filled,
        // Splash
        R.drawable.ic_sg_splash_static,
        R.drawable.ic_sg_splash_animated,
    )

    @Test
    fun todosIconesSgInflamSemExcecao() {
        icSgDrawables.forEach { resId ->
            val resourceName = context.resources.getResourceEntryName(resId)
            val drawable = ContextCompat.getDrawable(context, resId)
            assertNotNull(
                "Drawable '$resourceName' não pôde ser inflado (retornou null)",
                drawable,
            )
        }
    }
}
