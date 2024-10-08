package br.com.kaskin.roteirizador.features.remessas.models

import kotlinx.serialization.Serializable

@Serializable
data class CostumerListItem(
    val bairro: String,
    val cidade: String,
    val costumerCode: Int,
    val costumerName: String,
    val operacao: String,
    val orderCode: Int,
    val orderValue: Double,
    val paymentMethodCode: Int,
    val paymentName: String,
    val vendedor: String,
    /*val items:List<ProductItem>*/
)