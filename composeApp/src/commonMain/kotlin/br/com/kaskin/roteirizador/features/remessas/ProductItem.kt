package br.com.kaskin.roteirizador.features.remessas

import kotlinx.serialization.Serializable

@Serializable
data class ProductItem(
    val code: Int,
    val name: String,
    val value: Double,
    val quantity: Int,
    val unitaryValue: Double
)
