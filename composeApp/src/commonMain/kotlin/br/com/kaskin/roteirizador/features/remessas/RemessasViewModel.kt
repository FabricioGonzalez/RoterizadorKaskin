package br.com.kaskin.roteirizador.features.remessas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.kaskin.roteirizador.shared.snackbar.SnackbarController
import br.com.kaskin.roteirizador.shared.snackbar.SnackbarEvent
import br.com.kaskin.roteirizador.shared.uistate.ResourceUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

class RemessasViewModel(private val remessaLoader: RemessaLoader) : ViewModel() {

    private val _remessas =
        MutableStateFlow<ResourceUiState<List<CostumerListItem>>>(ResourceUiState.Idle)
    val remessas = _remessas.asStateFlow()

    val vendedores = _remessas.filter { it is ResourceUiState.Success }
        .map { (it as ResourceUiState.Success).data }
        .transform { remessa ->
        emit(remessa.distinctBy { it.vendedor }
            .map { it.vendedor })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bairros = _remessas.filter { it is ResourceUiState.Success }
        .map { (it as ResourceUiState.Success).data }
        .transform { remessa ->
        emit(remessa.distinctBy { it.bairro }
            .map { it.bairro })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cidades = _remessas.filter { it is ResourceUiState.Success }
        .map { (it as ResourceUiState.Success).data }
        .transform { remessa ->
        emit(remessa.distinctBy { it.cidade }
            .map { it.cidade })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun handleIntent(intents: RemessasViewIntents) {
        when (intents) {
            is RemessasViewIntents.loadRemessas -> {
                viewModelScope.launch {
                    _remessas.update {
                       ResourceUiState.Loading
                    }
                    _remessas.update {
                        remessaLoader.loadRemessas(
                            dataInicio = intents.dataInicio,
                            dataFim = intents.dataFim
                        ).takeIf { it.isNotEmpty() }
                            ?.let { ResourceUiState.Success(it) } ?: ResourceUiState.Error("Vazio")
                    }
                }
            }

            is RemessasViewIntents.updateRemessas -> {
                viewModelScope.launch {
                    remessaLoader.updateOrders(intents.remessas)
                        .onSuccess {
                            SnackbarController.sendEvent(
                                SnackbarEvent("Remessas Atualizadas com sucesso")
                            )
                        }.onFailure {
                            SnackbarController.sendEvent(
                                SnackbarEvent("Erro ao atualizar remessas")
                            )
                        }
                }
            }

            is RemessasViewIntents.syncRemessas -> {
                viewModelScope.launch {
                    remessaLoader.syncOrders(intents.remessas)
                        .onSuccess {
                            SnackbarController.sendEvent(
                                SnackbarEvent("Remessas sincronizadas com sucesso")
                            )
                        }.onFailure {
                            SnackbarController.sendEvent(
                                SnackbarEvent("Erro na sincronizacao das remessas")
                            )
                        }
                }
            }
        }
    }
}

sealed interface RemessasViewIntents {
    data class updateRemessas(val remessas: List<Int>) : RemessasViewIntents
    data class syncRemessas(val remessas: List<Int>) : RemessasViewIntents
    data class loadRemessas(val dataInicio: LocalDateTime, val dataFim: LocalDateTime) :
        RemessasViewIntents
}