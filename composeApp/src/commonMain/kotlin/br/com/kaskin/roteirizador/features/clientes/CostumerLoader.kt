package br.com.kaskin.roteirizador.features.clientes

import br.com.kaskin.roteirizador.data.ApiConnector
import br.com.kaskin.roteirizador.shared.extensions.format
import br.com.kaskin.roteirizador.shared.extensions.now
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.timeout
import io.ktor.http.appendPathSegments
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class CostumerLoader(private val connector: ApiConnector) {
    suspend fun syncCostumers(
        dataInicio: LocalDateTime = LocalDateTime.now(),
        dataFim: LocalDateTime = LocalDateTime.now()
    ): Flow<CostumerApiResponse?> = flow {
        try {
            withContext(Dispatchers.IO) {
                connector.getAsync(block = {
                    url {
                        appendPathSegments("costumers", "sync", encodeSlash = true)
                        parameters.append("dataInicio", dataInicio.format())
                        parameters.append("dataFim", dataFim.format())
                    }
                    timeout {
                        requestTimeoutMillis = Long.MAX_VALUE
                    }
                },
                    onRecieved = { recieved ->
                        try {
                            emit(
                                Json.decodeFromString<CostumerApiResponse>(recieved)
                            )
                        } catch (e: Exception) {
                            Napier.e("Erro",e)
                            null
                        }


                    })
            }
        } catch (e: Exception) {
            throw e
        }

    }

    suspend fun cleanBase(): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val response = connector.get {
                    url {
                        appendPathSegments("costumers", "clean", encodeSlash = true)
                    }
                }
                if (response.status.isSuccess()) Result.success(Unit)
                else Result.failure(Exception("Vazio"))

            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    suspend fun syncCostumer(
        costumerCode: Int
    ): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val response = connector.get {
                    url {
                        appendPathSegments(
                            "costumers",
                            "sync",
                            costumerCode.toString(),
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

    suspend fun updateCostumer(
        costumerCode: Int
    ): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val response = connector.get {
                    url {
                        parameters.append("codigo", costumerCode.toString())
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

@Serializable
data class CostumerApiResponse(
    val classificacao: String,
    val codigo: String,
    val coordenada: Coordenada,
    val diasAtendimento: List<String>,
    val emails: List<String>,
    val endereco: Endereco,
    val fimAtendimento: Int,
    val inicioAtendimento: Int,
    val nome: String,
    val telefones: List<String>
)

@Serializable
data class Coordenada(
    val latitude: Int,
    val longitude: Int
)

@Serializable
data class Endereco(
    val bairro: String,
    val cep: String,
    val complemento: String,
    val logradouro: String,
    val municipio: String,
    val numero: Int,
    val uf: String
)