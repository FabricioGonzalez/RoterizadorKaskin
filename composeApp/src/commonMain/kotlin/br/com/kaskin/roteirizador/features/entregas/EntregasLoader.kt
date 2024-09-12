package br.com.kaskin.roteirizador.features.entregas

import br.com.kaskin.roteirizador.features.remessas.CostumerListItem
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

class EntregasLoader {
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

    suspend fun loadDeliveries(
        dataInicio: LocalDateTime = LocalDateTime.now(),
        dataFim: LocalDateTime = LocalDateTime.now()
    ): List<Delivery> {
        return try {
            withContext(Dispatchers.IO) {
                val result = client.get(url) {
                    url {
                        appendPathSegments("deliveries", encodeSlash = true)
                        parameters.append("dataInicio", dataInicio.format())
                        parameters.append("dataFim", dataFim.format())
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
    ) {
        try {
            withContext(Dispatchers.IO) {
                client.get(url) {
                    url {
                        appendPathSegments(
                            "deliveries",
                            "sync",
                            delivery.toString(),
                            encodeSlash = true
                        )
                    }
                }
            }
        } catch (e: Exception) {
            println(e)
        }
    }

}

