package br.com.kaskin.roteirizador.shared.uistate

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> ManagementResourceUiState(
    modifier: Modifier = Modifier,
    resourceUiState: ResourceUiState<T>,
    successView: @Composable (data: T) -> Unit,
    loadingView: @Composable () -> Unit = { Loading(modifier) },
    onTryAgain: () -> Unit = {},
    msgTryAgain: String = "An error has occurred",
    onCheckAgain: () -> Unit = {},
    msgCheckAgain: String = "No data to show"
) {
    Box(
        modifier = modifier
    ) {
        when (resourceUiState) {
            is ResourceUiState.Empty -> Empty(
                modifier = modifier,
                onCheckAgain = onCheckAgain,
                msg = msgCheckAgain
            )

            is ResourceUiState.Error -> ErrorTemplate(
                modifier = modifier,
                onTryAgain = onTryAgain,
                msg = msgTryAgain
            )

            ResourceUiState.Loading -> loadingView()
            is ResourceUiState.Success -> successView(resourceUiState.data)
            ResourceUiState.Idle -> Unit
        }
    }
}