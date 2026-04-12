package br.com.suinogestor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.suinogestor.presentation.femea.screen.CadastroFemeaScreen
import br.com.suinogestor.presentation.femea.screen.ListaFemeasScreen
import br.com.suinogestor.presentation.femea.screen.Rotas
import br.com.suinogestor.ui.theme.SuinoGestorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuinoGestorTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Rotas.LISTA_FEMEAS
                ) {
                    composable(Rotas.LISTA_FEMEAS) {
                        ListaFemeasScreen(
                            onNovoCadastro = { navController.navigate(Rotas.CADASTRO_FEMEA) },
                            onFemeaSelecionada = { id ->
                                navController.navigate(Rotas.detalheFemea(id))
                            }
                        )
                    }
                    composable(Rotas.CADASTRO_FEMEA) {
                        CadastroFemeaScreen(
                            onCadastroSalvo = { navController.popBackStack() },
                            onVoltar = { navController.popBackStack() }
                        )
                    }
                    composable(Rotas.DETALHE_FEMEA) {
                        // Detalhe será implementado em spec futura
                    }
                }
            }
        }
    }
}
