package br.com.kaskin.roteirizador.features.entregas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Filter
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import br.com.kaskin.roteirizador.designsystem.organisms.DateTimePicker
import br.com.kaskin.roteirizador.features.remessas.CostumerListItem
import br.com.kaskin.roteirizador.features.remessas.RemessaLoader
import br.com.kaskin.roteirizador.shared.extensions.format
import br.com.kaskin.roteirizador.shared.extensions.now
import br.com.kaskin.roteirizador.shared.extensions.toDateTime
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.material3.DataTable
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun EntregasScreen(modifier: Modifier = Modifier) {
    val entregasLoader = remember { EntregasLoader() }

    val entregas = remember { mutableStateListOf<Delivery>() }
    val selectedEntregas = remember { mutableStateListOf<Int>() }
    val (allSelected, allSelectedChanged) = remember { mutableStateOf(false) }

    val dataInicioPickerState = rememberDatePickerState()
    val dataFimPickerState = rememberDatePickerState()
    val scope = rememberCoroutineScope()

    val scaffoldNavigation = rememberSupportingPaneScaffoldNavigator()

    SupportingPaneScaffold(
        modifier = modifier.padding(8.dp).clip(MaterialTheme.shapes.small),
        value = scaffoldNavigation.scaffoldValue,
        directive = scaffoldNavigation.scaffoldDirective,
        mainPane = {
            AnimatedPane(modifier = modifier.fillMaxSize()) {
                Surface {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            DateTimePicker(datePickerState = dataInicioPickerState)
                            DateTimePicker(datePickerState = dataFimPickerState)

                            Button({
                                scope.launch {
                                    entregas.addAll(
                                        entregasLoader.loadDeliveries(
                                            dataInicio = dataInicioPickerState.selectedDateMillis?.toDateTime()
                                                ?: LocalDateTime.now(),
                                            dataFimPickerState.selectedDateMillis?.toDateTime()
                                                ?: LocalDateTime.now()
                                        )

                                    )
                                }
                            }) {
                                Text("Buscar")
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button({
                                scope.launch {
                                    selectedEntregas.forEach { delivery ->
                                        entregasLoader.syncDelivery(delivery)
                                    }
                                }
                            }) {
                                Text("Sincronizar")
                            }
                        }
                        IconButton({
                            scaffoldNavigation.navigateTo(SupportingPaneScaffoldRole.Supporting)
                        }) {
                            Icon(Icons.Rounded.Filter, null)
                        }
                        DataTable(
                            columns = listOf(
                                DataColumn {
                                    Checkbox(allSelected, {
                                        allSelectedChanged(!allSelected)
                                        if (allSelected) {
                                            selectedEntregas.clear()
                                        } else selectedEntregas.addAll(entregas.filter { item ->
                                            item.id !in selectedEntregas
                                        }.map { it.id })
                                    })
                                },
                                DataColumn {
                                    Text("Codigo Roteirização")
                                },
                                DataColumn {
                                    Text("Veiculo")
                                },
                                DataColumn {
                                    Text("Nome")
                                },
                                DataColumn {
                                    Text("Status")
                                },
                                DataColumn {
                                    Text("Data Saída")
                                },
                                DataColumn {
                                    Text("Pendente")
                                },
                                DataColumn {
                                    Text("Entregue")
                                },
                                DataColumn {
                                    Text("Devolvido")
                                },
                            )
                        ) {
                            entregas.forEach { entrega ->
                                row {
                                    cell {
                                        Checkbox(selectedEntregas.any { it == entrega.id }, {
                                            if (selectedEntregas.any { it == entrega.id }) {
                                                selectedEntregas.remove(entrega.id)
                                            } else selectedEntregas.add(entrega.id)

                                        })
                                    }
                                    cell { Text("${entrega.id}") }
                                    cell { Text(entrega.placa) }
                                    cell { Text(entrega.description) }
                                    cell { Text(entrega.status) }
                                    cell { Text(entrega.date.format()) }
                                    cell { Text("${entrega.pending}") }
                                    cell { Text("${entrega.delivered}") }
                                    cell { Text("${entrega.returned}") }
                                }
                            }
                        }
                    }
                }
            }
        },
        supportingPane = {
            AnimatedPane {
                Surface(color = MaterialTheme.colorScheme.surfaceColorAtElevation(24.dp)) {
                    Column {
                        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                24.dp
                            )
                        ), navigationIcon = {
                            IconButton({
                                scaffoldNavigation.navigateBack()
                            }) {
                                Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                            }
                        }, title = {}, actions = {})
                    }
                }
            }
        }
    )
}