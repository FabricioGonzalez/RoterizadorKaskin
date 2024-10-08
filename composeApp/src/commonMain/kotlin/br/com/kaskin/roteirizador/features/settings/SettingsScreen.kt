package br.com.kaskin.roteirizador.features.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import br.com.kaskin.roteirizador.data.ApiConnector
import br.com.kaskin.roteirizador.shared.ApiConstants
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.getKoin

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    preferences: DataStore<Preferences> = getKoin().get<DataStore<Preferences>>(),
    apiConnector: ApiConnector = getKoin().get<ApiConnector>()
) {
    val scope = rememberCoroutineScope()

    Column(modifier = modifier.padding(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val apiUrl by preferences.data.map {
                    it[stringPreferencesKey("api_url")]
                        ?: ApiConstants.ApiUrl

            }.collectAsState(ApiConstants.ApiUrl)

            Text("Api")
            OutlinedTextField(value = apiUrl, onValueChange = { value ->
                scope.launch {
                    preferences.edit {
                        it[stringPreferencesKey("api_url")] = value
                    }
                }
            }, trailingIcon = {
                IconButton({
                        /*apiConnector.changeUrl(apiUrl)*/
                }) {
                    Icon(Icons.Rounded.Save, null)
                }
            })
        }
    }
}