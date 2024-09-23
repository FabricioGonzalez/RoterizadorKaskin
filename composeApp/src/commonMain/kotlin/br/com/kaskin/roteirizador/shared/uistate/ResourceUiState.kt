package br.com.kaskin.roteirizador.shared.uistate


sealed interface ResourceUiState<out T> {
    data class Success<T>(val data: T) : ResourceUiState<T>
    data class Error(val message: String? = null) : ResourceUiState<Nothing>
    data object Loading : ResourceUiState<Nothing>
    data object Empty : ResourceUiState<Nothing>
    data object Idle : ResourceUiState<Nothing>
}