package br.com.kaskin.roteirizador.features.clientes

import br.com.kaskin.roteirizador.shared.extensions.format
import br.com.kaskin.roteirizador.shared.extensions.now
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime

class CostumerLoader {
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

    suspend fun syncCostumers(
        dataInicio: LocalDateTime = LocalDateTime.now(),
        dataFim: LocalDateTime = LocalDateTime.now()
    ) {
        try {
            withContext(Dispatchers.IO) {
                client.get(url) {
                    url {
                        appendPathSegments("costumers", "sync", encodeSlash = true)
                        parameters.append("dataInicio", dataInicio.format())
                        parameters.append("dataFim", dataFim.format())
                    }
                }
            }
        } catch (_: Exception) {

        }

    }

    suspend fun cleanBase() {
        try {
            withContext(Dispatchers.IO) {
                client.get(url) {
                    url {
                        appendPathSegments("costumers", "clean", encodeSlash = true)
                    }
                }

            }
        } catch (_: Exception) {

        }

    }

    suspend fun syncCostumer(
        costumerCode: Int
    ) {
        try {
            withContext(Dispatchers.IO) {
                client.get(url) {
                    url {
                        appendPathSegments(
                            "costumers",
                            "sync",
                            costumerCode.toString(),
                            encodeSlash = true
                        )
                    }
                }
            }
        } catch (_: Exception) {

        }

    }

    suspend fun updateCostumer(
        costumerCode: Int
    ) {
        try {
            withContext(Dispatchers.IO) {
                client.get(url) {
                    url {
                        parameters.append("codigo", costumerCode.toString())
                    }
                }
            }
        } catch (e: Exception) {
            println(e)
        }
    }
}