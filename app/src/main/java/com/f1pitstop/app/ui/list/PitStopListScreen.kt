
// app/src/main/java/com/f1pitstop/app/ui/list/PitStopListScreen.kt
package com.f1pitstop.app.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.f1pitstop.app.R
import com.f1pitstop.app.data.model.PitStop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PitStopListScreen(
    onBack: () -> Unit,
    onNew: () -> Unit,
    onEdit: (Long) -> Unit,
    vm: PitStopListViewModel = viewModel()
) {
    val items by vm.items.collectAsState()
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var deleteId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.listado_pit_stops),
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = { 
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = { 
                    TextButton(onClick = onNew) { 
                        Text(
                            text = stringResource(R.string.registrar_pit_stop),
                            fontWeight = FontWeight.SemiBold
                        ) 
                    } 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { p ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Column(Modifier.padding(p).fillMaxSize()) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        vm.onQueryChange(it.text)
                    },
                    placeholder = { Text(stringResource(R.string.buscar)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                if (items.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.pit_stop_list_empty),
                                modifier = Modifier.padding(24.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items, key = { it.id }) { item ->
                            PitStopRow(
                                item = item,
                                onEdit = { onEdit(item.id) },
                                onDelete = { deleteId = item.id }
                            )
                        }
                    }
                }
            }
        }

        if (deleteId != null) {
            val current = items.firstOrNull { it.id == deleteId }
            AlertDialog(
                onDismissRequest = { deleteId = null },
                confirmButton = {
                    TextButton(onClick = { vm.deleteById(deleteId!!); deleteId = null }) {
                        Text(
                            text = stringResource(R.string.pit_stop_delete_confirm),
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = { 
                    TextButton(onClick = { deleteId = null }) { 
                        Text(
                            text = stringResource(R.string.cancelar),
                            fontWeight = FontWeight.SemiBold
                        ) 
                    } 
                },
                title = { 
                    Text(
                        text = stringResource(R.string.pit_stop_delete_title),
                        fontWeight = FontWeight.Bold
                    ) 
                },
                text = { 
                    Text(stringResource(R.string.pit_stop_delete_message, current?.piloto ?: "")) 
                },
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                textContentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun PitStopRow(
    item: PitStop,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), 
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${item.piloto} • ${item.escuderia.displayName}", 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${item.getFechaFormateada()} • ${item.getTiempoFormateado()} • ${item.estado.displayName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEdit) { 
                Icon(
                    Icons.Default.Edit, 
                    contentDescription = "Editar",
                    tint = MaterialTheme.colorScheme.primary
                ) 
            }
            IconButton(onClick = onDelete) { 
                Icon(
                    Icons.Default.Delete, 
                    contentDescription = stringResource(R.string.pit_stop_delete_action),
                    tint = MaterialTheme.colorScheme.error
                ) 
            }
        }
    }
}
