package br.com.kaskin.roteirizador.features.remessas

import br.com.kaskin.roteirizador.data.ApiConnector
import br.com.kaskin.roteirizador.shared.extensions.format
import br.com.kaskin.roteirizador.shared.extensions.now
import io.ktor.client.call.body
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.utils.HttpResponseReceiveFail
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RemessaLoader(private val connector: ApiConnector) {

    suspend fun loadRemessas(
        dataInicio: LocalDateTime = LocalDateTime.now(),
        dataFim: LocalDateTime = LocalDateTime.now()
    ): List<CostumerListItem> {
        return try {
            withContext(Dispatchers.IO) {
                val result = connector.get {
                    url {
                        appendPathSegments("orders", "prepared", encodeSlash = true)
                        parameters.append("dataInicio", dataInicio.format())
                        parameters.append("dataFim", dataFim.format())
                    }
                }
                if (result.status == HttpStatusCode.OK)
                    result.body<List<CostumerListItem>>()
                else emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }

    }

    suspend fun syncOrders(
        orders: List<Int>
    ): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val body = Json.encodeToString(orders)
                val response = connector.post {
                    url {
                        appendPathSegments("orders", "sync", "remessas", encodeSlash = true)
                    }
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
                if (response.status.isSuccess()) Result.success(Unit)
                else Result.failure(Exception("Vazio"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOrders(
        orders: List<Int>
    ): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val body = Json.encodeToString(orders)
                val response =
                    connector.post {
                        url {
                            appendPathSegments("orders", "update", "remessas", encodeSlash = true)
                        }
                        contentType(ContentType.Application.Json)
                        setBody(body)
                    }

                println(response.status)
                if (response.status.isSuccess()) Result.success(Unit)
                else Result.failure(Exception("Vazio"))

            }
        } catch (
            e: Exception
        ) {
            Result.failure(e)
        }

    }
}