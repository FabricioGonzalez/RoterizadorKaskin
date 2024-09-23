package br.com.kaskin.roteirizador.features.entregas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.kaskin.roteirizador.shared.snackbar.SnackbarController
import br.com.kaskin.roteirizador.shared.snackbar.SnackbarEvent
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

class EntregasViewModel(
    private val entregasLoader: EntregasLoader
) : ViewModel() {
    private val _entregas = MutableStateFlow<List<Delivery>>(emptyList())
    val entregas = _entregas.asStateFlow()


    fun handleIntent(intents: EntregasViewIntents) {
        when (intents) {
            EntregasViewIntents.cleanCostumers -> TODO()
            is EntregasViewIntents.loadDeliveries -> {
                viewModelScope.launch {
                    _entregas.update {
                        entregasLoader.loadDeliveries(
                            dataInicio = intents.dataInicio,
                            dataFim = intents.dataFim
                        )
                    }
                }
            }

            is EntregasViewIntents.syncCostumer -> TODO()
            is EntregasViewIntents.syncDeliveries -> {
                viewModelScope.launch {
                    intents.deliveries.map {
                        async {
                            entregasLoader.syncDelivery(it)
                        }
                    }.awaitAll()
                        .all {
                            it.isFailure
                        }.let {
                            if (it) {
                                SnackbarController.sendEvent(
                                    SnackbarEvent("Falha ao sincronizar cargas")
                                )
                            } else
                                SnackbarController.sendEvent(
                                    SnackbarEvent("Cargas sincronizadas com sucesso")
                                )
                        }
                }
            }
        }
    }
}

sealed interface EntregasViewIntents {
    data class syncCostumer(val code: Int) : EntregasViewIntents
    data class syncDeliveries(val deliveries: List<Int>) : EntregasViewIntents
    data class loadDeliveries(val dataInicio: LocalDateTime, val dataFim: LocalDateTime) :
        EntregasViewIntents

    data object cleanCostumers : EntregasViewIntents
}