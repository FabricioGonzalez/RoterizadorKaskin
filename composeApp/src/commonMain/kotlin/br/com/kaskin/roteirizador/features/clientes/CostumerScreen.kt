package br.com.kaskin.roteirizador.features.clientes

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import br.com.kaskin.roteirizador.shared.extensions.now
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun CostumerScreen(modifier: Modifier = Modifier, viewModel: ClientesViewModel = koinViewModel()) {

    val (codigoCliente, onCodigoChanged) = remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button({
                scope.launch {
                    viewModel.handleIntent(ClientsViewIntents.SyncCostumers(
                        dataInicio = LocalDateTime.now(),
                        dataFim = LocalDateTime.now(),
                    ))
                }
            }) {
                Text("Sincronizar Clientes")
            }
            Button({
                scope.launch {
                    viewModel.handleIntent(ClientsViewIntents.CleanCostumers)
                }
            }) {
                Text("Limpar Base")
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        viewModel.handleIntent(ClientsViewIntents.SyncCostumer(it))
                    }
                }
            }) {
                Text("Sincronizar Cliente")
            }
            Button({
                scope.launch {
                    codigoCliente.toIntOrNull()?.let {
                        viewModel.handleIntent(ClientsViewIntents.UpdateCostumer(it))
                    }
                }
            }) {
                Text("Atualizar Cliente")
            }
        }
    }

}
