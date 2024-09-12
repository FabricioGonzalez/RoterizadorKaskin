package br.com.kaskin.roteirizador.features.remessas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Filter
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import br.com.kaskin.roteirizador.designsystem.organisms.DateTimePicker
import br.com.kaskin.roteirizador.shared.extensions.now
import br.com.kaskin.roteirizador.shared.extensions.toDateTime
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.material3.DataTable
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun RemessasScreen(modifier: Modifier = Modifier) {

    val remessaLoader = remember { RemessaLoader() }

    val remessas = remember { mutableStateListOf<CostumerListItem>() }
    val selectedRemessas = remember { mutableStateListOf<Int>() }
    val (allSelected, allSelectedChanged) = remember { mutableStateOf(false) }

    val dataInicioPickerState = rememberDatePickerState()
    val dataFimPickerState = rememberDatePickerState()
    val scope = rememberCoroutineScope()

    val scaffoldNavigation = rememberSupportingPaneScaffoldNavigator()

    SupportingPaneScaffold(
        value = scaffoldNavigation.scaffoldValue,
        directive = scaffoldNavigation.scaffoldDirective,
        mainPane = {
            AnimatedPane(modifier = modifier.fillMaxSize()) {
                Column {
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {

                        DateTimePicker(datePickerState = dataInicioPickerState)
                        DateTimePicker(datePickerState = dataFimPickerState)

                        Button({
                            scope.launch {
                                remessas.addAll(
                                    remessaLoader.loadRemessas(
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

                    Row {
                        Button({
                            scope.launch {
                                remessaLoader.syncOrders(selectedRemessas.toList())
                            }
                        }) {
                            Text("Sincronizar")
                        }
                        Button({
                            scope.launch {
                                remessaLoader.updateOrders(selectedRemessas.toList())
                            }
                        }) {
                            Text("Atualizar")
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
                                        selectedRemessas.clear()
                                    } else selectedRemessas.addAll(remessas.filter { item ->
                                        item.orderCode !in selectedRemessas
                                    }.map { it.orderCode })
                                })
                            },
                            DataColumn {
                                Text("Codigo Pedido")
                            },
                            DataColumn {
                                Text("Codigo Cliente")
                            },
                            DataColumn {
                                Text("Nome")
                            },
                            DataColumn {
                                Text("Operacao")
                            },
                            DataColumn {
                                Text("Forma Pagamento")
                            },
                            DataColumn {
                                Text("Vendedor")
                            },
                            DataColumn {
                                Text("Bairro")
                            },
                            DataColumn {
                                Text("Cidade")
                            },
                            DataColumn {
                                Text("Valor")
                            },
                        )
                    ) {
                        remessas.forEach { remessa ->
                            row {
                                cell {
                                    Checkbox(selectedRemessas.any { it == remessa.orderCode }, {
                                        if (selectedRemessas.any { it == remessa.orderCode }) {
                                            selectedRemessas.remove(remessa.orderCode)
                                        } else selectedRemessas.add(remessa.orderCode)

                                    })
                                }
                                cell { Text("${remessa.orderCode}") }
                                cell { Text("${remessa.costumerCode}") }
                                cell { Text(remessa.costumerName) }
                                cell { Text(remessa.operacao) }
                                cell { Text(remessa.paymentName) }
                                cell { Text(remessa.vendedor) }
                                cell { Text(remessa.bairro) }
                                cell { Text(remessa.cidade) }
                                cell { Text("${remessa.orderValue}") }
                            }
                        }
                    }
                }
            }
        },
        supportingPane = {
            AnimatedPane {
                Column {
                    TopAppBar(navigationIcon = {
                        IconButton({
                            scaffoldNavigation.navigateBack()
                        }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    }, title = {}, actions = {})
                }
            }
        }
    )
}
