package br.com.kaskin.roteirizador.features.clientes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.kaskin.roteirizador.shared.snackbar.SnackbarController
import br.com.kaskin.roteirizador.shared.snackbar.SnackbarEvent
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

class ClientesViewModel(private val costumerLoader: CostumerLoader) : ViewModel() {

    fun handleIntent(intent: ClientsViewIntents) {
        when (intent) {
            is ClientsViewIntents.SyncCostumer -> {
                viewModelScope.launch {
                    costumerLoader.syncCostumer(intent.code)
                        .onSuccess {
                            SnackbarController.sendEvent(
                                SnackbarEvent("Cliente Sincronizado")
                            )
                        }.onFailure {
                            SnackbarController.sendEvent(
                                SnackbarEvent("Falha ao sincronizar cliente")
                            )
                        }
                }
            }

            is ClientsViewIntents.SyncCostumers -> {
                viewModelScope.launch {
                    costumerLoader.syncCostumers(
                        dataInicio = intent.dataInicio,
                        dataFim = intent.dataFim
                    ).onSuccess {
                        SnackbarController.sendEvent(
                            SnackbarEvent("Clientes Sincronizados")
                        )
                    }.onFailure {
                        SnackbarController.sendEvent(
                            SnackbarEvent("Falha ao sincronizar clientes")
                        )
                    }
                }
            }

            is ClientsViewIntents.UpdateCostumer -> {
                viewModelScope.launch {
                    costumerLoader.updateCostumer(intent.code)
                        .onSuccess {
                            SnackbarController.sendEvent(
                                SnackbarEvent("Cliente Atualizado")
                            )
                        }.onFailure {
                            SnackbarController.sendEvent(
                                SnackbarEvent("Falha ao atualizar Cliente")
                            )
                        }
                }
            }

            ClientsViewIntents.CleanCostumers -> {
                viewModelScope.launch {
                    costumerLoader.cleanBase()
                        .onSuccess {
                            SnackbarController.sendEvent(
                                SnackbarEvent("Base Limpa")
                            )
                        }.onFailure {
                            SnackbarController.sendEvent(
                                SnackbarEvent("Falha ao limpar base")
                            )
                        }
                }
            }
        }
    }
}


sealed interface ClientsViewIntents {
    data class SyncCostumer(val code: Int) : ClientsViewIntents
    data class UpdateCostumer(val code: Int) : ClientsViewIntents
    data class SyncCostumers(val dataInicio: LocalDateTime, val dataFim: LocalDateTime) :
        ClientsViewIntents

    data object CleanCostumers : ClientsViewIntents
}

sealed interface ClientsViewEffects {
    data class EndedSuccessfullyEffect(val result: String) : ClientsViewEffects
    data class ErrorOccured(val result: String) : ClientsViewEffects
}