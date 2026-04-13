package br.com.suinogestor

import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.suinogestor.presentation.femea.screen.CadastroFemeaScreen
import br.com.suinogestor.presentation.femea.screen.ListaFemeasScreen
import br.com.suinogestor.presentation.femea.screen.Rotas as RotasFemeas
import br.com.suinogestor.presentation.navigation.BarraNavegacaoPrincipal
import br.com.suinogestor.presentation.navigation.Destino
import br.com.suinogestor.presentation.placeholder.EmConstrucaoScreen
import br.com.suinogestor.presentation.reproducao.ReproducaoScreen
import br.com.suinogestor.presentation.reprodutor.screen.CadastroReprodutorScreen
import br.com.suinogestor.presentation.reprodutor.screen.DetalhesReprodutorScreen
import br.com.suinogestor.presentation.reprodutor.screen.ListaReprodutoresScreen
import br.com.suinogestor.presentation.reprodutor.screen.Rotas as RotasReprodutores
import br.com.suinogestor.ui.theme.SuinoGestorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val animatorScale = Settings.Global.getFloat(
            contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        )

        if (animatorScale != 0f) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                splashScreenView.view.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction { splashScreenView.remove() }
                    .start()
            }
        }

        enableEdgeToEdge()
        setContent {
            SuinoGestorTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val rotaAtual = navBackStackEntry?.destination?.route
                
                // Determina se deve mostrar a barra de navegação
                val mostrarBarraNavegacao = when (rotaAtual) {
                    Destino.Reproducao.rota,
                    Destino.Engorda.rota,
                    Destino.Financeiro.rota,
                    Destino.Indicadores.rota -> true
                    else -> false
                }
                
                Scaffold(
                    bottomBar = {
                        if (mostrarBarraNavegacao) {
                            BarraNavegacaoPrincipal(
                                destinoAtual = rotaAtual ?: Destino.Reproducao.rota,
                                onDestinoSelecionado = { destino ->
                                    navController.navigate(destino) {
                                        // Pop até o destino inicial para evitar pilha grande
                                        popUpTo(Destino.Reproducao.rota) {
                                            saveState = true
                                        }
                                        // Evita múltiplas cópias do mesmo destino
                                        launchSingleTop = true
                                        // Restaura estado ao reselecionar
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = Destino.Reproducao.rota,
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        // Tela principal de Reprodução
                        composable(Destino.Reproducao.rota) {
                            ReproducaoScreen(
                                onNavegaParaFemeas = { 
                                    navController.navigate(RotasFemeas.LISTA_FEMEAS) 
                                },
                                onNavegaParaReprodutores = { 
                                    navController.navigate(RotasReprodutores.LISTA_REPRODUTORES) 
                                }
                            )
                        }
                        
                        // Rotas do módulo de Fêmeas
                        composable(RotasFemeas.LISTA_FEMEAS) {
                            ListaFemeasScreen(
                                onNovoCadastro = { navController.navigate(RotasFemeas.CADASTRO_FEMEA) },
                                onFemeaSelecionada = { id ->
                                    navController.navigate(RotasFemeas.detalheFemea(id))
                                },
                                onVoltar = { navController.popBackStack() }
                            )
                        }
                        composable(RotasFemeas.CADASTRO_FEMEA) {
                            CadastroFemeaScreen(
                                onCadastroSalvo = { navController.popBackStack() },
                                onVoltar = { navController.popBackStack() }
                            )
                        }
                        composable(RotasFemeas.DETALHE_FEMEA) {
                            // Detalhe será implementado em spec futura
                        }

                        // Rotas do módulo de Reprodutores
                        composable(RotasReprodutores.LISTA_REPRODUTORES) {
                            ListaReprodutoresScreen(
                                onNovoCadastro = { navController.navigate(RotasReprodutores.CADASTRO_REPRODUTOR) },
                                onReprodutorSelecionado = { id ->
                                    navController.navigate(RotasReprodutores.detalheReprodutor(id))
                                },
                                onVoltar = { navController.popBackStack() }
                            )
                        }
                        composable(RotasReprodutores.CADASTRO_REPRODUTOR) {
                            CadastroReprodutorScreen(
                                onCadastroSalvo = { navController.popBackStack() },
                                onVoltar = { navController.popBackStack() }
                            )
                        }
                        composable(RotasReprodutores.DETALHE_REPRODUTOR) {
                            val reprodutorId = it.arguments?.getString("reprodutorId")?.toLongOrNull()
                            if (reprodutorId != null) {
                                DetalhesReprodutorScreen(
                                    reprodutorId = reprodutorId,
                                    onNavigateBack = { navController.popBackStack() },
                                    onEditarClick = { navController.navigate(RotasReprodutores.CADASTRO_REPRODUTOR) }
                                )
                            }
                        }
                        
                        // Módulos em construção
                        composable(Destino.Engorda.rota) {
                            EmConstrucaoScreen(nomeModulo = "Engorda")
                        }
                        composable(Destino.Financeiro.rota) {
                            EmConstrucaoScreen(nomeModulo = "Financeiro")
                        }
                        composable(Destino.Indicadores.rota) {
                            EmConstrucaoScreen(nomeModulo = "Indicadores")
                        }
                    }
                }
            }
        }
    }
}
