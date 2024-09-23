package br.com.kaskin.roteirizador.features.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel

class SettingsViewModel(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {


}