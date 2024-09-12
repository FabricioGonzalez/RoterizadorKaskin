package br.com.kaskin.roteirizador.features.remessas

import br.com.kaskin.roteirizador.shared.extensions.format
import br.com.kaskin.roteirizador.shared.extensions.now
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RemessaLoader {
    private val url = "http://localhost:5031/api/v1"

    private val client = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 10000
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.v("HTTP Client", null, message)
                }
            }
            level = LogLevel.ALL
        }.also { Napier.base(DebugAntilog()) }

        install(ContentNegotiation) {
            json() // Example: Register JSON content transformation
            // Add more transformations as needed for other content types
        }
    }

    suspend fun loadRemessas(
        dataInicio: LocalDateTime = LocalDateTime.now(),
        dataFim: LocalDateTime = LocalDateTime.now()
    ): List<CostumerListItem> {
        return try {
            withContext(Dispatchers.IO) {
                val result = client.get(url) {
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
    ) {
        try {
            withContext(Dispatchers.IO) {
                val body = Json.encodeToString(orders)
                val response = client.post(url) {
                    url {
                        appendPathSegments("orders", "sync", "remessas", encodeSlash = true)
                    }
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }

                println(response.status)
                println(body)
            }
        } catch (e: Exception) {
            println(e)
        }
    }

    suspend fun updateOrders(
        orders: List<Int>
    ) {
        try {
            withContext(Dispatchers.IO) {
                val body = Json.encodeToString(orders)
                val response =
                    client.post(url) {
                        url {
                            appendPathSegments("orders", "update", "remessas", encodeSlash = true)
                        }
                        contentType(ContentType.Application.Json)
                        setBody(body)
                    }

                println(response.status)
                println(body)
            }
        } catch (
            e: Exception
        ) {
            println(e)
        }

    }
}