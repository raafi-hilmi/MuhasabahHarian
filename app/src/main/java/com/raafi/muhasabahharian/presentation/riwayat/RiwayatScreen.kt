package com.raafi.muhasabahharian.presentation.riwayat

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import java.io.File


data class RiwayatItem(
    val id: String,
    val type: String,
    val title: String,
    val date: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit = {},
    viewModel: RiwayatViewModel = hiltViewModel()
) {
    val viewModel: RiwayatViewModel = hiltViewModel()
    val riwayatList by viewModel.riwayat.collectAsState()
    val context = LocalContext.current
    val pdfPath by viewModel.exportedPdfPath.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(pdfPath) {
        pdfPath?.let {
            showDialog = true
            isLoading = false
        }
    }

    LaunchedEffect(pdfPath) {
        if (pdfPath != null) showDialog = true
    }
    var selectedFilter by remember { mutableStateOf("Semua") }
    var isFilterDropdownExpanded by remember { mutableStateOf(false) }


    val filteredList = if (selectedFilter == "Semua") {
        riwayatList
    } else {
        riwayatList.filter { it.type.equals(selectedFilter, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Riwayat",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Filter Section
            Text(
                text = "Pilih Riwayat",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = isFilterDropdownExpanded,
                onExpandedChange = { isFilterDropdownExpanded = !isFilterDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedFilter,
                    onValueChange = { },
                    readOnly = true,
                    shape = RoundedCornerShape(size = 16.dp),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown"
                        )
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
                    expanded = isFilterDropdownExpanded,
                    onDismissRequest = { isFilterDropdownExpanded = false }
                ) {
                    listOf("Semua", "Produktif", "Islami", "Emosional", "Olahraga").forEach { filter ->
                        DropdownMenuItem(
                            text = { Text(filter) },
                            onClick = {
                                selectedFilter = filter
                                isFilterDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Riwayat List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Data tidak ditemukan", color = Color.Gray)
                }
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredList) { item ->
                    if (item != null) {
                        RiwayatItemCard(
                            item = item,
                            onDetailClick = { onNavigateToDetail(item.id) }
                        )
                    } else {
                        Text("Data tidak ditemukan")
                    }
                }
            }
            if (showDialog && pdfPath != null) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("PDF berhasil disimpan") },
                    text = { Text("Apakah kamu ingin membuka file sekarang?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                openPdfFile(context, pdfPath!!)
                                showDialog = false
                            }
                        ) { Text("Ya") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Nanti saja")
                        }
                    }
                )
            }
            // Bottom Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { isLoading = true
                        viewModel.exportRiwayatToPdf(context = context)},
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "Export PDF",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Button(
                    onClick = { showDeleteConfirmationDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Hapus Semua",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        if (showDeleteConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmationDialog = false },
                title = { Text("Hapus Semua Riwayat") },
                text = { Text("Apakah Anda yakin ingin menghapus semua riwayat refleksi? Tindakan ini tidak dapat dibatalkan.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteAllRiwayat()
                            showDeleteConfirmationDialog = false
                            Toast.makeText(context, "Semua riwayat telah dihapus", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Ya, Hapus", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteConfirmationDialog = false }
                    ) {
                        Text("Batal")
                    }
                }
            )
        }

    }
}

@Composable
fun RiwayatItemCard(
    item: RiwayatItem,
    onDetailClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = item.date,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = item.description,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDetailClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text(
                        text = "Lihat Detail",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

fun openPdfFile(context: Context, filePath: String) {
    val file = File(filePath)
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(uri, "application/pdf")
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "Tidak ada aplikasi untuk membuka PDF", Toast.LENGTH_LONG).show()
    }
}
