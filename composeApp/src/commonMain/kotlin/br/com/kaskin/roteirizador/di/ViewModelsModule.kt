package br.com.kaskin.roteirizador.di

import br.com.kaskin.roteirizador.features.clientes.ClientesViewModel
import br.com.kaskin.roteirizador.features.entregas.EntregasViewModel
import br.com.kaskin.roteirizador.features.remessas.RemessasViewModel
import br.com.kaskin.roteirizador.features.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        RemessasViewModel(get())
    }

    viewModel {
        ClientesViewModel(get())
    }
    viewModel {
        EntregasViewModel(get())
    }
    viewModel {
        SettingsViewModel(get())
    }
}