package br.com.kaskin.roteirizador.features.clientes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.launch


@Composable
fun CostumerScreen(modifier: Modifier = Modifier) {

    val costumerLoader = remember { CostumerLoader() }

    val (codigoCliente, onCodigoChanged) = remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Column {
        Row {
            Button({
                scope.launch {
                    costumerLoader.syncCostumers()
                }
            }) {
                Text("Sincronizar Clientes")
            }
            Button({
                scope.launch {
                    costumerLoader.cleanBase()
                }
            }) {
                Text("Limpar Base")
            }
        }

        Row {
            OutlinedTextField(
                value = codigoCliente,
                onValueChange = onCodigoChanged,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
            Button({
                scope.launch {
                    codigoCliente.toIntOrNull()?.let {
                        costumerLoader.syncCostumer(it)
                    }
                }
            }) {
                Text("Sincronizar Cliente")
            }
            Button({
                scope.launch {
                    codigoCliente.toIntOrNull()?.let {
                        costumerLoader.updateCostumer(it)
                    }
                }
            }) {
                Text("Atualizar Cliente")
            }
        }
    }

}
