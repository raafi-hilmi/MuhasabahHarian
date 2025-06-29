package com.raafi.muhasabahharian.presentation.statistik

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.raafi.muhasabahharian.presentation.common.components.BottomNavigationBar
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatistikScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRiwayat: () -> Unit,
    onNavigateToMuhasabah: (String) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    viewModel: StatistikViewModel = hiltViewModel()
) {
    var selectedTemplate by remember { mutableStateOf("") }
    var selectedPeriod by remember { mutableStateOf("") }
    var isTemplateDropdownExpanded by remember { mutableStateOf(false) }

    val filteredData = viewModel.getFilteredByType(selectedTemplate)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

    val dayFormatter = remember(selectedPeriod) {
        if (selectedPeriod == "Bulanan")
            DateTimeFormatter.ofPattern("d MMMM", Locale.getDefault())
        else
            DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault())
    }

    val groupedData = when (selectedPeriod) {
        "Bulanan" -> filteredData.groupBy {
            LocalDate.parse(it.date, formatter).withDayOfMonth(1)
        }
        "Mingguan" -> filteredData.groupBy {
            val date = LocalDate.parse(it.date, formatter)
            date.with(DayOfWeek.MONDAY)
        }
        else -> emptyMap()
    }

    val chartData = groupedData.map { entry ->
        entry.value.map { it.score }.average().toFloat() / 10f
    }
    val dayLabels = groupedData.keys.mapIndexed { i, d ->
        if (selectedPeriod == "Bulanan") d.month.name.lowercase()
            .replaceFirstChar { it.titlecase() }
        else "Week \n ${i + 1}"
    }

    val latestPeriod = when (selectedPeriod) {
        "Mingguan" -> LocalDate.now().with(DayOfWeek.MONDAY)
        "Bulanan" -> LocalDate.now().withDayOfMonth(1)
        else -> null
    }

    val latestData = latestPeriod?.let { groupedData[it] } ?: emptyList()

    val totalHari = latestData
        .map { LocalDate.parse(it.date, formatter) }
        .distinct()
        .count()

    val hariPalingAktif = latestData
        .groupBy { LocalDate.parse(it.date, formatter) }
        .maxByOrNull { it.value.size }
        ?.key?.format(dayFormatter) ?: "-"

    val skorRataRata =
        if (latestData.isNotEmpty()) latestData.map { it.score }.average().toFloat() / 10f else 0f
    val moodDominan =
        latestData.groupingBy { it.mood }.eachCount().maxByOrNull { it.value }?.key ?: "-"

    // Label periode untuk teks dinamis
    val periodLabel = if (selectedPeriod == "Bulanan") "bulan ini" else "minggu ini"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Statistik & Progress",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "statistik",
                onNavigateToHome = onNavigateToHome,
                onNavigateToMuhasabah = { onNavigateToMuhasabah("hati") },
                onNavigateToStatistik = {}
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                "Template Refleksi",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = isTemplateDropdownExpanded,
                onExpandedChange = { isTemplateDropdownExpanded = !isTemplateDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedTemplate,
                    onValueChange = {},
                    shape = RoundedCornerShape(size = 16.dp),
                    placeholder = { Text("Pilih template...") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4CAF50),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )

                ExposedDropdownMenu(
                    expanded = isTemplateDropdownExpanded,
                    onDismissRequest = { isTemplateDropdownExpanded = false }
                ) {
                    listOf("Produktif", "Emosional", "Islami", "Olahraga").forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                selectedTemplate = it
                                isTemplateDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Mingguan", "Bulanan").forEach { period ->
                    FilterChip(
                        onClick = { selectedPeriod = period },
                        label = { Text(period) },
                        selected = selectedPeriod == period,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedTemplate.isNotBlank() && selectedPeriod.isNotBlank()) {
                Text(
                    text = "📊 Skor Rata-rata Refleksi (0–10)",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(30.dp),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.End
                            ) {
                                // Perbaikan label sumbu Y (0.0 - 1.0)
                                for (i in 10 downTo 0 step 2) {
                                    Text("$i", fontSize = 12.sp, color = Color.Gray)
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 36.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                chartData.forEach { value ->
                                    Box(
                                        modifier = Modifier
                                            .width(8.dp)
                                            .height((value * 14).dp)
                                            .background(Color(0xFF2196F3), RoundedCornerShape(4.dp))
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 36.dp, top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            dayLabels.forEach {
                                Text(
                                    it,
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            if (selectedPeriod == "Mingguan") "Ringkasan Refleksi Minggu ini" else "Ringkasan Refleksi Bulan ini",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            "Total refleksi: ${latestData.size} | Hari: $totalHari",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )

                        Text(
                            text = if (selectedPeriod == "Bulanan")
                                "Tanggal paling aktif bulan ini: $hariPalingAktif"
                            else
                                "Hari paling aktif minggu ini: $hariPalingAktif",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )

                        Text(
                            "Skor rata-rata ${selectedTemplate.lowercase()}: ${
                                "%.1f".format(
                                    skorRataRata
                                )
                            }",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                if (selectedPeriod == "Bulanan") "Mood dominan bulan ini: "
                                else "Mood dominan minggu ini: ",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                moodDominan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Insight Otomatis",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column {
                    Text(
                        "📌 Pada $periodLabel kamu melakukan refleksi $selectedTemplate sebanyak ${latestData.size} kali dalam $totalHari hari ",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        "📈 Skor rata-rata: ${"%.1f".format(skorRataRata)}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        "Mood dominan: $moodDominan",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onNavigateToRiwayat,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Lihat Riwayat Lengkap",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}