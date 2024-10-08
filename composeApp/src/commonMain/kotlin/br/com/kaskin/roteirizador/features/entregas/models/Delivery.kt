package br.com.kaskin.roteirizador.features.entregas.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Delivery(
    val date: LocalDateTime,
    val delivered: Int,
    val description: String,
    val id: Int,
    val pending: Int,
    val placa: String,
    val returned: Int,
    val status: String,
    val value: Double
)