package br.com.suinogestor.ui.theme

import androidx.annotation.DrawableRes
import br.com.suinogestor.R

/**
 * Catálogo tipado de todos os ícones customizados do SuinoGestor.
 *
 * Uso em Compose:
 * ```kotlin
 * Icon(
 *     painter = painterResource(SuinoGestorIcons.Animais.Matriz),
 *     contentDescription = "Matriz",
 *     tint = MaterialTheme.colorScheme.onSurfaceVariant
 * )
 * ```
 *
 * Convenção de nomenclatura dos drawables: `ic_sg_<nome>.xml`
 * Todos os ícones aceitam colorização via `tint` — sem cores hardcoded nos XMLs.
 */
object SuinoGestorIcons {

    /** Ícones de entidades animais (matrizes, reprodutores, leitões). */
    object Animais {
        @DrawableRes val Matriz: Int = R.drawable.ic_sg_matriz
        @DrawableRes val Reprodutor: Int = R.drawable.ic_sg_reprodutor
        @DrawableRes val Leitao: Int = R.drawable.ic_sg_leitao
    }

    /** Ícones das fases do ciclo reprodutivo. */
    object FasesReprodutivas {
        @DrawableRes val Cobertura: Int = R.drawable.ic_sg_cobertura
        @DrawableRes val Gestacao: Int = R.drawable.ic_sg_gestacao
        @DrawableRes val Parto: Int = R.drawable.ic_sg_parto
        @DrawableRes val Lactacao: Int = R.drawable.ic_sg_lactacao
        @DrawableRes val Desmame: Int = R.drawable.ic_sg_desmame
    }

    /** Ícones dos módulos de navegação, com variantes outlined (inativo) e filled (ativo). */
    object Modulos {

        object Reproducao {
            @DrawableRes val Outlined: Int = R.drawable.ic_sg_reproducao_outlined
            @DrawableRes val Filled: Int = R.drawable.ic_sg_reproducao_filled
        }

        object Engorda {
            @DrawableRes val Outlined: Int = R.drawable.ic_sg_engorda_outlined
            @DrawableRes val Filled: Int = R.drawable.ic_sg_engorda_filled
        }

        object Financeiro {
            @DrawableRes val Outlined: Int = R.drawable.ic_sg_financeiro_outlined
            @DrawableRes val Filled: Int = R.drawable.ic_sg_financeiro_filled
        }

        object Indicadores {
            @DrawableRes val Outlined: Int = R.drawable.ic_sg_indicadores_outlined
            @DrawableRes val Filled: Int = R.drawable.ic_sg_indicadores_filled
        }
    }
}
