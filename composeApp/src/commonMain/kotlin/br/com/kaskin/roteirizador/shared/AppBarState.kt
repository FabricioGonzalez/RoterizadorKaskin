package br.com.kaskin.roteirizador.shared

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.FabPosition
import androidx.compose.runtime.Composable


data class AppBarState(
    val topAppBar: (@Composable () -> Unit)? = null,
    val floatActionButton: (@Composable () -> Unit)? = null,
    val floatActionButtonPosition: FabPosition? = null,
    val snackbarHost: (@Composable () -> Unit)? = null,
    val navigationItems: (@Composable ColumnScope.() -> Unit)? = null
)