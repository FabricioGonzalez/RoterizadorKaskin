package br.com.kaskin.roteirizador.features.entregas.data

import br.com.kaskin.roteirizador.data.ApiConnector
import br.com.kaskin.roteirizador.features.entregas.models.Delivery
import br.com.kaskin.roteirizador.shared.extensions.format
import br.com.kaskin.roteirizador.shared.extensions.now
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime

class EntregasLoader(private val connector: ApiConnector) {

    suspend fun loadDeliveries(
        dataInicio: LocalDateTime = LocalDateTime.now(),
        dataFim: LocalDateTime = LocalDateTime.now()
    ): List<Delivery> {
        return try {
            withContext(Dispatchers.IO) {
                val result = connector.get {
                    url {
                        appendPathSegments("deliveries", encodeSlash = true)
                        parameters.append("inicial", dataInicio.format())
                        parameters.append("final", dataFim.format())
                    }
                }
                if (result.status == HttpStatusCode.OK)
                    result.body<List<Delivery>>()
                else emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }

    }

    suspend fun syncDelivery(
        delivery: Int
    ): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val response = connector.get {
                    url {
                        appendPathSegments(
                            "deliveries",
                            "sync",
                            delivery.toString(),
                            encodeSlash = true
                        )
                    }
                }
                if (response.status.isSuccess()) Result.success(Unit)
                else Result.failure(Exception("Vazio"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}

