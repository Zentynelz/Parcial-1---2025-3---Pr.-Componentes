package com.f1pitstop.app.ui.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import com.f1pitstop.app.data.model.PitStop
import com.f1pitstop.app.ui.theme.F1PitStopTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SummaryScreen(
    onNew: () -> Unit = {},
    onList: () -> Unit = {},
    viewModel: SummaryViewModel? = null
) {
    // Si tenemos ViewModel, usamos datos reales, sino datos estáticos
    val fastestPitStop by (viewModel?.fastestPitStop?.collectAsState() ?: remember { mutableStateOf(null) })
    val averagePitStopTime by (viewModel?.averagePitStopTime?.collectAsState() ?: remember { mutableStateOf(null) })
    val totalPitStopsCount by (viewModel?.totalPitStopsCount?.collectAsState() ?: remember { mutableStateOf(0) })
    val lastPitStopsForChart by (viewModel?.lastPitStopsForChart?.collectAsState() ?: remember { mutableStateOf(emptyList<PitStop>()) })


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
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título principal con estilo F1
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = "F1 PIT STOP TRACKER",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(20.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))

        // Estadísticas principales
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatisticCard(
                title = "Pit Stop Más Rápido",
                value = fastestPitStop?.let { "%.1f s".format(it.tiempoTotal) } ?: "N/A",
                subtitle = fastestPitStop?.let { 
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it.fechaHora)) 
                } ?: ""
            )
            StatisticCard(
                title = "Promedio Pit Stop",
                value = averagePitStopTime?.let { "%.1f s".format(it) } ?: "N/A"
            )
            StatisticCard(
                title = "Total Pit Stops",
                value = totalPitStopsCount.toString()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Gráfico de barras de los últimos pit stops
        if (lastPitStopsForChart.isNotEmpty()) {
            Text(
                text = "Últimos Pit Stops (Tiempo en segundos)",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            LastPitStopsBarChart(lastPitStopsForChart)
        }

        Spacer(modifier = Modifier.height(32.dp))

            // Botones de navegación mejorados
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onNew,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "NUEVO PIT STOP",
                            fontWeight = FontWeight.SemiBold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Button(
                        onClick = onList,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "VER LISTA",
                            fontWeight = FontWeight.SemiBold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticCard(title: String, value: String, subtitle: String = "") {
    Card(
        modifier = Modifier.width(120.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Text(text = value, style = MaterialTheme.typography.headlineSmall)
            if (subtitle.isNotEmpty()) {
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun LastPitStopsBarChart(pitStops: List<PitStop>) {
    if (pitStops.isEmpty()) return
    
    val barData = pitStops.mapIndexed { index, pitStop ->
        BarData(
            point = Point(index.toFloat(), pitStop.tiempoTotal.toFloat()),
            color = if (pitStop.estado == com.f1pitstop.app.data.model.EstadoPitStop.OK) 
                Color.Green else Color.Red
        )
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .steps(barData.size - 1)
        .labelData { index -> pitStops.getOrNull(index)?.piloto ?: "" }
        .build()

    val yAxisData = AxisData.Builder()
        .steps(5)
        .labelData { index -> (index * 2).toString() + "s" }
        .build()

    val barChartData = BarChartData(
        chartData = barData,
        xAxisData = xAxisData,
        yAxisData = yAxisData
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        BarChart(modifier = Modifier.fillMaxSize(), barChartData = barChartData)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSummaryScreen() {
    F1PitStopTheme {
        SummaryScreen()
    }
}