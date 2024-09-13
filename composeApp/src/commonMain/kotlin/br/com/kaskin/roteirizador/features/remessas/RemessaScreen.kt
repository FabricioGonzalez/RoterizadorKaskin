package br.com.kaskin.roteirizador.features.remessas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
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
    val (allVendedoresSelected, allVendedoresSelectedChanged) = remember { mutableStateOf(false) }
    val (allCidadesSelected, allCidadesChanged) = remember { mutableStateOf(false) }
    val (allBairrosSelected, allBairrosChanged) = remember { mutableStateOf(false) }

    val vendedores =
        remember {
            derivedStateOf {
                remessas.distinctBy { it.vendedor }
                    .map { it.vendedor }.toMutableStateList()

            }
        }
    val selectedVendedores = remember { mutableStateListOf<String>() }

    val cidades =
        remember {
            derivedStateOf {
                remessas.distinctBy { it.cidade }
                    .map { it.cidade }.toMutableStateList()

            }
        }
    val selectedCidades = remember { mutableStateListOf<String>() }

    val bairros =
        remember {
            derivedStateOf {
                remessas.distinctBy { it.bairro }
                    .map { it.bairro }.toMutableStateList()

            }
        }
    val selectedBairros = remember { mutableStateListOf<String>() }

    val dataInicioPickerState = rememberDatePickerState()
    val dataFimPickerState = rememberDatePickerState()
    val scope = rememberCoroutineScope()

    val scaffoldNavigation = rememberSupportingPaneScaffoldNavigator()

    val pedidos = remember {
        derivedStateOf {
            remessas
                .filter { it.vendedor in selectedVendedores || (selectedVendedores.isEmpty() || selectedVendedores.all { item -> item.isBlank() }) }
                .filter { it.bairro in selectedBairros || (selectedBairros.isEmpty() || selectedBairros.all { item -> item.isBlank() }) }
                .filter { it.cidade in selectedCidades || (selectedCidades.isEmpty() || selectedCidades.all { item -> item.isBlank() }) }
        }
    }

    SupportingPaneScaffold(
        modifier = modifier.padding(8.dp).clip(MaterialTheme.shapes.small),
        value = scaffoldNavigation.scaffoldValue,
        directive = scaffoldNavigation.scaffoldDirective,
        mainPane = {
            AnimatedPane(modifier = modifier.fillMaxSize()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

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

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        contentPadding = PaddingValues(8.dp),
                        rowHeight = 64.dp,
                        columns = listOf(
                            DataColumn {
                                Checkbox(allSelected, {
                                    allSelectedChanged(!allSelected)
                                    if (allSelected) {
                                        selectedRemessas.clear()
                                    } else selectedRemessas.addAll(pedidos.value.filter { item ->
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
                        pedidos.value.forEach { remessa ->
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
                Surface(
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        24.dp
                    )
                ) {
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

                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Adaptive(160.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalItemSpacing = 8.dp
                        ) {
                            item {

                                DataTable(
                                    modifier = Modifier.width(160.dp).height(300.dp),
                                    contentPadding = PaddingValues(8.dp),
                                    rowHeight = 64.dp,
                                    columns = listOf(
                                        DataColumn {
                                            Checkbox(allVendedoresSelected, {
                                                allVendedoresSelectedChanged(!allVendedoresSelected)
                                                if (allVendedoresSelected) {
                                                    selectedVendedores.clear()
                                                } else selectedVendedores.addAll(vendedores.value.filter { item ->
                                                    item !in selectedVendedores
                                                }.map { it })
                                            })
                                        },
                                        DataColumn {
                                            Text("Nome")
                                        }
                                    )
                                ) {
                                    vendedores.value.forEach { vendedor ->
                                        row {
                                            cell {
                                                Checkbox(
                                                    selectedVendedores.any { it == vendedor },
                                                    {
                                                        if (selectedVendedores.any { it == vendedor }) {
                                                            selectedVendedores.remove(vendedor)
                                                        } else selectedVendedores.add(vendedor)

                                                    })
                                            }
                                            cell { Text(vendedor) }
                                        }
                                    }
                                }
                            }
                            item {
                                DataTable(
                                    modifier = Modifier.width(160.dp).height(300.dp),
                                    contentPadding = PaddingValues(8.dp),
                                    rowHeight = 64.dp,
                                    columns = listOf(
                                        DataColumn {
                                            Checkbox(allCidadesSelected, {
                                                allCidadesChanged(!allCidadesSelected)
                                                if (allCidadesSelected) {
                                                    selectedCidades.clear()
                                                } else selectedCidades.addAll(cidades.value.filter { item ->
                                                    item !in selectedCidades
                                                }.map { it })
                                            })
                                        },
                                        DataColumn {
                                            Text("Nome")
                                        }
                                    )
                                ) {
                                    cidades.value.forEach { cidade ->
                                        row {
                                            cell {
                                                Checkbox(
                                                    selectedCidades.any { it == cidade },
                                                    {
                                                        if (selectedCidades.any { it == cidade }) {
                                                            selectedCidades.remove(cidade)
                                                        } else selectedCidades.add(cidade)

                                                    })
                                            }
                                            cell { Text(cidade) }
                                        }
                                    }
                                }
                            }
                            item {

                                DataTable(
                                    modifier = Modifier.width(160.dp).height(300.dp),
                                    contentPadding = PaddingValues(8.dp),
                                    rowHeight = 64.dp,
                                    columns = listOf(
                                        DataColumn {
                                            Checkbox(allBairrosSelected, {
                                                allBairrosChanged(!allBairrosSelected)
                                                if (allBairrosSelected) {
                                                    selectedBairros.clear()
                                                } else selectedBairros.addAll(bairros.value.filter { item ->
                                                    item !in selectedBairros
                                                }.map { it })
                                            })
                                        },
                                        DataColumn {
                                            Text("Nome")
                                        }
                                    )
                                ) {
                                    bairros.value.forEach { bairro ->
                                        row {
                                            cell {
                                                Checkbox(
                                                    selectedBairros.any { it == bairro },
                                                    {
                                                        if (selectedBairros.any { it == bairro }) {
                                                            selectedBairros.remove(bairro)
                                                        } else selectedBairros.add(bairro)

                                                    })
                                            }
                                            cell { Text(bairro) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
