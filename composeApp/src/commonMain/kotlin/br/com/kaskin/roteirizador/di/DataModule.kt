package br.com.kaskin.roteirizador.di

import br.com.kaskin.roteirizador.data.ApiConnector
import br.com.kaskin.roteirizador.features.clientes.CostumerLoader
import br.com.kaskin.roteirizador.features.entregas.data.EntregasLoader
import br.com.kaskin.roteirizador.features.remessas.data.RemessaLoader
import org.koin.dsl.module

val dataModule = module {
    single {
        ApiConnector(get())
    }
    single {
        RemessaLoader(get())
    }
    single {
        CostumerLoader(get())
    }
    single {
        EntregasLoader(get())
    }
}