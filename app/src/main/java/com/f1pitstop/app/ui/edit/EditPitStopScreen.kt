// app/src/main/java/com/f1pitstop/app/ui/edit/EditPitStopScreen.kt
package com.f1pitstop.app.ui.edit




import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.f1pitstop.app.R
import com.f1pitstop.app.data.model.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPitStopScreen(
    existingId: Long? = null,
    onDone: () -> Unit,
    vm: EditPitStopViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()

    var piloto by remember { mutableStateOf("") }
    var escuderia by remember { mutableStateOf(Escuderia.MERCEDES) }
    var tiempo by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf(TipoNeumatico.SOFT) }
    var numNeum by remember { mutableStateOf("4") }
    var estado by remember { mutableStateOf(EstadoPitStop.OK) }
    var motivo by remember { mutableStateOf("") }
    var mecanico by remember { mutableStateOf("") }
    
    // Estados para validación y errores
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(existingId) {
        if (existingId != null) {
            vm.load(existingId)?.let { p ->
                piloto = p.piloto
                escuderia = p.escuderia
                tiempo = p.tiempoTotal.toString()
                tipo = p.cambioNeumaticos
                numNeum = p.numeroNeumaticosCambiados.toString()
                estado = p.estado
                motivo = p.motivoFallo.orEmpty()
                mecanico = p.mecanicoPrincipal
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (existingId == null) stringResource(R.string.registrar_pit_stop) else "Editar Pit Stop",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
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
            Column(
                modifier = Modifier
                    .padding(p)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            
                // Título de la sección
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "DATOS DEL PIT STOP",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                // Mostrar mensaje de error si existe
                if (errorMessage.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = "ERROR: $errorMessage",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            
                // Sección de información del piloto
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "INFORMACIÓN DEL PILOTO",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        OutlinedTextField(
                            value = piloto, 
                            onValueChange = { piloto = it; errorMessage = "" }, 
                            label = { Text(stringResource(R.string.piloto)) }, 
                            modifier = Modifier.fillMaxWidth(),
                            isError = piloto.isBlank() && errorMessage.isNotEmpty(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        EscuderiaDropdown(escuderia) { escuderia = it }
                    }
                }

                // Sección de datos técnicos
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "DATOS TÉCNICOS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        OutlinedTextField(
                            value = tiempo, 
                            onValueChange = { tiempo = it; errorMessage = "" }, 
                            label = { Text(stringResource(R.string.tiempo_total)) }, 
                            modifier = Modifier.fillMaxWidth(),
                            isError = tiempo.toDoubleOrNull() == null && errorMessage.isNotEmpty(),
                            supportingText = { Text("Ejemplo: 2.5 segundos") },
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        TipoDropdown(tipo) { tipo = it }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = numNeum, 
                            onValueChange = { numNeum = it; errorMessage = "" }, 
                            label = { Text(stringResource(R.string.numero_neumaticos)) }, 
                            modifier = Modifier.fillMaxWidth(),
                            isError = numNeum.toIntOrNull() == null && errorMessage.isNotEmpty(),
                            supportingText = { Text("Entre 1 y 4 neumáticos") },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Sección de estado y equipo
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ESTADO Y EQUIPO",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        EstadoDropdown(estado) { 
                            estado = it
                            // Limpiar motivo si cambia a OK
                            if (it == EstadoPitStop.OK) {
                                motivo = ""
                            }
                        }
                        
                        // Solo mostrar campo de motivo si el estado es FALLIDO
                        if (estado == EstadoPitStop.FALLIDO) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = motivo, 
                                onValueChange = { motivo = it; errorMessage = "" }, 
                                label = { Text(stringResource(R.string.motivo_fallo)) }, 
                                modifier = Modifier.fillMaxWidth(),
                                isError = motivo.isBlank() && estado == EstadoPitStop.FALLIDO && errorMessage.isNotEmpty(),
                                supportingText = { Text("Requerido para pit stops fallidos") },
                                shape = RoundedCornerShape(12.dp),
                                minLines = 2
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = mecanico, 
                            onValueChange = { mecanico = it; errorMessage = "" }, 
                            label = { Text(stringResource(R.string.mecanico_principal)) }, 
                            modifier = Modifier.fillMaxWidth(),
                            isError = mecanico.isBlank() && errorMessage.isNotEmpty(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Botones de acción
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                // Validar campos antes de guardar
                                val validationErrors = mutableListOf<String>()
                                
                                if (piloto.isBlank()) {
                                    validationErrors.add("El nombre del piloto es requerido")
                                }
                                
                                val tiempoDouble = tiempo.toDoubleOrNull()
                                if (tiempoDouble == null || tiempoDouble <= 0) {
                                    validationErrors.add("El tiempo debe ser un número mayor a 0")
                                }
                                
                                val numNeumInt = numNeum.toIntOrNull()
                                if (numNeumInt == null || numNeumInt < 1 || numNeumInt > 4) {
                                    validationErrors.add("El número de neumáticos debe estar entre 1 y 4")
                                }
                                
                                if (mecanico.isBlank()) {
                                    validationErrors.add("El nombre del mecánico es requerido")
                                }
                                
                                if (estado == EstadoPitStop.FALLIDO && motivo.isBlank()) {
                                    validationErrors.add("El motivo del fallo es requerido para pit stops fallidos")
                                }
                                
                                if (validationErrors.isNotEmpty()) {
                                    errorMessage = validationErrors.joinToString("\n")
                                    return@Button
                                }
                                
                                // Si la validación pasa, guardar
                                scope.launch {
                                    try {
                                        isLoading = true
                                        val entity = PitStop(
                                            id = existingId ?: 0L,
                                            piloto = piloto.trim(),
                                            escuderia = escuderia,
                                            tiempoTotal = tiempoDouble!!,
                                            cambioNeumaticos = tipo,
                                            numeroNeumaticosCambiados = numNeumInt!!,
                                            estado = estado,
                                            motivoFallo = if (estado == EstadoPitStop.FALLIDO) motivo.trim() else null,
                                            mecanicoPrincipal = mecanico.trim()
                                        )
                                        vm.save(entity)
                                        onDone()
                                    } catch (e: Exception) {
                                        errorMessage = "Error al guardar: ${e.message}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) { 
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = null
                                    )
                                }
                                Text(
                                    text = if (isLoading) "Guardando..." else stringResource(R.string.guardar),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onDone,
                            enabled = !isLoading,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) { 
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = null
                                )
                                Text(
                                    text = stringResource(R.string.cancelar),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EscuderiaDropdown(value: Escuderia, onChange: (Escuderia) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = value.displayName, 
            onValueChange = {}, 
            readOnly = true,
            label = { Text(stringResource(R.string.escuderia)) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Escuderia.values().forEach { escuderia ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = escuderia.displayName,
                            fontWeight = if (escuderia == value) FontWeight.Bold else FontWeight.Normal
                        ) 
                    }, 
                    onClick = { onChange(escuderia); expanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TipoDropdown(value: TipoNeumatico, onChange: (TipoNeumatico) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = value.displayName, 
            onValueChange = {}, 
            readOnly = true,
            label = { Text(stringResource(R.string.cambio_neumaticos)) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            TipoNeumatico.values().forEach { tipo ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = tipo.displayName,
                            fontWeight = if (tipo == value) FontWeight.Bold else FontWeight.Normal
                        ) 
                    }, 
                    onClick = { onChange(tipo); expanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EstadoDropdown(value: EstadoPitStop, onChange: (EstadoPitStop) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = value.displayName, 
            onValueChange = {}, 
            readOnly = true,
            label = { Text(stringResource(R.string.estado)) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (value == EstadoPitStop.OK) 
                    MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            EstadoPitStop.values().forEach { estado ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = estado.displayName,
                            fontWeight = if (estado == value) FontWeight.Bold else FontWeight.Normal,
                            color = if (estado == EstadoPitStop.OK) 
                                MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                        ) 
                    }, 
                    onClick = { onChange(estado); expanded = false }
                )
            }
        }
    }
}
