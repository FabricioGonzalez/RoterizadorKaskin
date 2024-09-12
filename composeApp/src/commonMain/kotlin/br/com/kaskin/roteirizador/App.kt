package br.com.kaskin.roteirizador

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Summarize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.kaskin.roteirizador.features.clientes.CostumerScreen
import br.com.kaskin.roteirizador.features.entregas.EntregasScreen
import br.com.kaskin.roteirizador.features.remessas.RemessasScreen
import br.com.kaskin.roteirizador.features.roteirizador.RoteirizadorScreen
import br.com.kaskin.roteirizador.shared.AppBarState
import roterizador_kaskin.composeapp.generated.resources.*
import br.com.kaskin.roteirizador.theme.AppTheme
import br.com.kaskin.roteirizador.theme.LocalThemeIsDark
import kotlinx.coroutines.isActive
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun App() = AppTheme {
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
        )
    }

    PermanentNavigationDrawer(modifier = Modifier.fillMaxSize(), drawerContent = {
        Column {
            navigations.forEach { navigation ->
                val isSelected by derivedStateOf { currentRoute == navigation.navigationPoint::class.qualifiedName }
                NavigationRailItem(isSelected, icon = {
                    Icon(
                        imageVector = navigation.icon,
                        contentDescription = navigation.name
                    )
                }, onClick = {
                    navigation.onClick()
                }, label = { Text(text = navigation.name) })
            }
        }
    }) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = appBarState.topAppBar ?: {},
            snackbarHost = appBarState.snackbarHost ?: {},
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
}

data class NavItem(
    val name: String,
    val navigationPoint: NavigationPoints,
    val onClick: () -> Unit,
    val icon: ImageVector
)