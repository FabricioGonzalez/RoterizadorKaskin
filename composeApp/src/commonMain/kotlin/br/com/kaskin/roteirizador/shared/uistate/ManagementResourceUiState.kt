package br.com.kaskin.roteirizador.shared.uistate

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import roterizador_kaskin.composeapp.generated.resources.Res
import roterizador_kaskin.composeapp.generated.resources.empty_template_message
import roterizador_kaskin.composeapp.generated.resources.error_template_message

@Composable
fun <T> ManagementResourceUiState(
    modifier: Modifier = Modifier,
    resourceUiState: ResourceUiState<T>,
    successView: @Composable (data: T) -> Unit,
    loadingView: @Composable () -> Unit = { Loading(modifier) },
    onTryAgain: () -> Unit = {},
    msgTryAgain: String = stringResource(Res.string.error_template_message),
    onCheckAgain: () -> Unit = {},
    msgCheckAgain: String = stringResource(Res.string.empty_template_message),
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