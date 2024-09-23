package br.com.kaskin.roteirizador

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Summarize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.kaskin.roteirizador.features.clientes.CostumerScreen
import br.com.kaskin.roteirizador.features.entregas.EntregasScreen
import br.com.kaskin.roteirizador.features.remessas.RemessasScreen
import br.com.kaskin.roteirizador.features.roteirizador.RoteirizadorScreen
import br.com.kaskin.roteirizador.features.settings.SettingsScreen
import br.com.kaskin.roteirizador.shared.AppBarState
import br.com.kaskin.roteirizador.shared.snackbar.ObserveAsEvents
import br.com.kaskin.roteirizador.shared.snackbar.SnackbarController
import br.com.kaskin.roteirizador.theme.AppTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Composable
internal fun App(isDark: Boolean) = AppTheme(isDark = isDark) {
    val (appBarState, onAppStateChanged) = remember {
        mutableStateOf(AppBarState())
    }

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navigations = remember {
        listOf(
            NavItem(
                name = "Sincronização de Clientes",
                onClick = {
                    navController.navigate(NavigationPoints.Clientes)
                },
                navigationPoint = NavigationPoints.Clientes,
                icon = Icons.Rounded.Storage
            ),
            NavItem(
                name = "Pedidos",
                navigationPoint = NavigationPoints.Remessas,
                onClick = { navController.navigate(NavigationPoints.Remessas) },
                icon = Icons.Rounded.Summarize,
            ),
            NavItem(
                name = "Cargas",
                onClick = {
                    navController.navigate(NavigationPoints.Entregas)
                },
                navigationPoint = NavigationPoints.Entregas,
                icon = Icons.Rounded.Sell
            ),
            NavItem(
                name = "Roteirizador", navigationPoint = NavigationPoints.Roteirizador, onClick = {
                    navController.navigate(NavigationPoints.Roteirizador)
                }, icon = Icons.Rounded.Home
            ),
            NavItem(
                name = "Configurações", navigationPoint = NavigationPoints.Settings, onClick = {
                    navController.navigate(NavigationPoints.Settings)
                }, icon = Icons.Rounded.Settings
            ),
        )
    }

    PermanentNavigationDrawer(modifier = Modifier.fillMaxSize(), drawerContent = {
        Column(modifier = Modifier.width(96.dp).padding(4.dp)) {
            navigations.forEach { navigation ->
                val isSelected by derivedStateOf { currentRoute == navigation.navigationPoint::class.qualifiedName }
                NavigationRailItem(isSelected, icon = {
                    Icon(
                        imageVector = navigation.icon,
                        contentDescription = navigation.name
                    )
                }, onClick = {
                    navigation.onClick()
                }, label = { Text(text = navigation.name, textAlign = TextAlign.Center) })
            }
        }
    }) {
        val snackbarHostState = remember {
            SnackbarHostState()
        }
        val scope = rememberCoroutineScope()

        ObserveAsEvents(flow = SnackbarController.events, key1 = snackbarHostState) { event ->
            scope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()

                snackbarHostState.showSnackbar(
                    message = event.message,
                    actionLabel = event.action?.name
                )
                    .let {
                        if (it == SnackbarResult.ActionPerformed) {
                            event.action?.action?.invoke()
                        }
                    }
            }
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = appBarState.topAppBar ?: {},
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
            floatingActionButton = appBarState.floatActionButton ?: {},
            floatingActionButtonPosition = appBarState.floatActionButtonPosition ?: FabPosition.End
        ) { innerPadding ->
            NavHost(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                navController = navController,
                startDestination = NavigationPoints.Clientes
            ) {
                composable<NavigationPoints.Roteirizador> {
                    RoteirizadorScreen()
                }
                composable<NavigationPoints.Entregas> {
                    EntregasScreen()
                }
                composable<NavigationPoints.Clientes> {
                    CostumerScreen()
                }
                composable<NavigationPoints.Remessas> {
                    RemessasScreen()
                }
                composable<NavigationPoints.Settings> {
                    SettingsScreen()
                }
            }
        }
    }
}

sealed interface NavigationPoints {
    @Serializable
    data object Entregas : NavigationPoints

    @Serializable
    data object Clientes : NavigationPoints

    @Serializable
    data object Remessas : NavigationPoints

    @Serializable
    data object Roteirizador : NavigationPoints

    @Serializable
    data object Settings : NavigationPoints
}

data class NavItem(
    val name: String,
    val navigationPoint: NavigationPoints,
    val onClick: () -> Unit,
    val icon: ImageVector
)