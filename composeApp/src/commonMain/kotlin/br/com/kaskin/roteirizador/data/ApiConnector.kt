package br.com.kaskin.roteirizador.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import br.com.kaskin.roteirizador.shared.ApiConstants
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.prepareGet
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readRemaining
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class ApiConnector(store: DataStore<Preferences>) {
    private var url = store.data.map { it[stringPreferencesKey("api_url")] }
    private val client = HttpClient() {
        install(SSE) {
            showCommentEvents()
            showRetryEvents()
        }
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

    suspend <T> fun getAsync(
        block: HttpRequestBuilder.() -> Unit,
        onRecieved: (T) -> Unit
    ) {
        client.prepareGet(url.firstOrNull() ?: ApiConstants.ApiUrl)
            .execute { httpResponse ->
                val channel: ByteReadChannel = httpResponse.body()
                while (!channel.isClosedForRead) {

                    channel.readUTF8Line()?.let()
                    onRecieved()
                }

            }
    }

    suspend fun get(block: HttpRequestBuilder.() -> Unit): HttpResponse {
        return client.get(url.firstOrNull() ?: ApiConstants.ApiUrl, block)
    }

    suspend fun post(block: HttpRequestBuilder.() -> Unit): HttpResponse {
        return client.post(url.firstOrNull() ?: ApiConstants.ApiUrl, block)
    }

    suspend fun put(block: HttpRequestBuilder.() -> Unit): HttpResponse {
        return client.put(url.firstOrNull() ?: ApiConstants.ApiUrl, block)
    }

    suspend fun patch(block: HttpRequestBuilder.() -> Unit): HttpResponse {
        return client.patch(url.firstOrNull() ?: ApiConstants.ApiUrl, block)
    }

    suspend fun delete(block: HttpRequestBuilder.() -> Unit): HttpResponse {
        return client.delete(url.firstOrNull() ?: ApiConstants.ApiUrl, block)
    }
}