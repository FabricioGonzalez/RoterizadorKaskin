package br.com.kaskin.roteirizador.di

import br.com.kaskin.roteirizador.datastore.DATA_STORE_FILE_NAME
import br.com.kaskin.roteirizador.datastore.createDataStore
import org.koin.dsl.module
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

actual val datastoreModule = module {
    single {
        createDataStore {
            Path(System.getenv("APPDATA"), "RoteirizadorKaskin", DATA_STORE_FILE_NAME).absolutePathString()
        }
    }
}