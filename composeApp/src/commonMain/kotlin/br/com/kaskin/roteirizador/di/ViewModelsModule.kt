package br.com.kaskin.roteirizador.di

import br.com.kaskin.roteirizador.features.clientes.ClientesViewModel
import br.com.kaskin.roteirizador.features.entregas.details.EntregaDetailViewModel
import br.com.kaskin.roteirizador.features.entregas.lists.EntregasViewModel
import br.com.kaskin.roteirizador.features.remessas.contratos.ContratosScreenViewModel
import br.com.kaskin.roteirizador.features.remessas.remessas.RemessasViewModel
import br.com.kaskin.roteirizador.features.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        RemessasViewModel(get())
    }

    viewModel {
        ContratosScreenViewModel()
    }
    viewModel {
        ClientesViewModel(get())
    }
    viewModel {
        EntregasViewModel(get())
    }
    viewModel {
        EntregaDetailViewModel(get())
    }
    viewModel {
        SettingsViewModel(get())
    }
}