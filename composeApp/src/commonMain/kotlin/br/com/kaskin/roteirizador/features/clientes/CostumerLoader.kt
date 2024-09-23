package br.com.kaskin.roteirizador.features.clientes

import br.com.kaskin.roteirizador.data.ApiConnector
import br.com.kaskin.roteirizador.shared.extensions.format
import br.com.kaskin.roteirizador.shared.extensions.now
import io.ktor.http.appendPathSegments
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime

class CostumerLoader(private val connector: ApiConnector) {
    suspend fun syncCostumers(
        dataInicio: LocalDateTime = LocalDateTime.now(),
        dataFim: LocalDateTime = LocalDateTime.now()
    ): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val response = connector.get {
                    url {
                        appendPathSegments("costumers", "sync", encodeSlash = true)
                        parameters.append("dataInicio", dataInicio.format())
                        parameters.append("dataFim", dataFim.format())
                    }
                }
                if (response.status.isSuccess()) Result.success(Unit)
                else Result.failure(Exception("Vazio"))
            }
        } catch (e: Exception) {
            Result.failure(e)
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