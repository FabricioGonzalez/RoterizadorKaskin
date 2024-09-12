package br.com.kaskin.roteirizador.features.entregas

import kotlinx.serialization.Serializable

@Serializable
data class Delivery(
    val date: String,
    val delivered: Int,
    val description: String,
    val id: Int,
    val pending: Int,
    val placa: String,
    val returned: Int,
    val status: String,
    val value: Int
)