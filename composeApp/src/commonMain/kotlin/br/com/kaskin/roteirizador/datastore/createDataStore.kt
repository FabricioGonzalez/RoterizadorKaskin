package br.com.kaskin.roteirizador.datastore


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

fun createDataStore(producerPath: () -> String): DataStore<Preferences> {

    return PreferenceDataStoreFactory.createWithPath(produceFile = { producerPath().toPath() })
}

internal const val DATA_STORE_FILE_NAME = "prefs.preferences_pb"