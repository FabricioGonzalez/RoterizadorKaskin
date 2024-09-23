package br.com.kaskin.roteirizador.designsystem.organisms

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarViewDay
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import br.com.kaskin.roteirizador.shared.extensions.formatView
import br.com.kaskin.roteirizador.shared.extensions.toDateTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    modifier: Modifier = Modifier,
    datePickerState: DatePickerState = rememberDatePickerState(),
    label: @Composable (() -> Unit)? = null
) {
    val (show, onShowChanged) = remember { mutableStateOf(false) }

    if (show) DatePickerDialog(onDismissRequest = {
        onShowChanged(false)
    }, confirmButton = {
        TextButton({
            onShowChanged(false)
        }) {
            Text("Confirmar")
        }
    }) {
        DatePicker(datePickerState)
    }
    OutlinedTextField(
        modifier = modifier,
        value = datePickerState.selectedDateMillis?.toDateTime()?.formatView() ?: "",
        onValueChange = {},
        label = label,
        trailingIcon = {
            IconButton({
                onShowChanged(true)
            }) {
                Icon(Icons.Rounded.CalendarViewDay, null)
            }
        })
}
