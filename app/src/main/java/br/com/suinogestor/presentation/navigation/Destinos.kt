package br.com.suinogestor.presentation.navigation

import androidx.annotation.DrawableRes
import br.com.suinogestor.R

/**
 * Definição dos destinos principais de navegação do SuinoGestor.
 * 
 * Cada destino representa um módulo principal do aplicativo e contém
 * informações para exibição na barra de navegação.
 */
sealed class Destino(
    val rota: String,
    val titulo: String,
    @DrawableRes val iconeInativo: Int,
    @DrawableRes val iconeAtivo: Int
) {
    data object Reproducao : Destino(
        rota = "reproducao",
        titulo = "Reprodução",
        iconeInativo = R.drawable.ic_sg_reproducao_outlined,
        iconeAtivo = R.drawable.ic_sg_reproducao_filled
    )
    
    data object Engorda : Destino(
        rota = "engorda",
        titulo = "Engorda",
        iconeInativo = R.drawable.ic_sg_engorda_outlined,
        iconeAtivo = R.drawable.ic_sg_engorda_filled
    )
    
    data object Financeiro : Destino(
        rota = "financeiro",
        titulo = "Financeiro",
        iconeInativo = R.drawable.ic_sg_financeiro_outlined,
        iconeAtivo = R.drawable.ic_sg_financeiro_filled
    )
    
    data object Indicadores : Destino(
        rota = "indicadores",
        titulo = "Indicadores",
        iconeInativo = R.drawable.ic_sg_indicadores_outlined,
        iconeAtivo = R.drawable.ic_sg_indicadores_filled
    )
}
