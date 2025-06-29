package com.raafi.muhasabahharian.presentation.riwayat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.raafi.muhasabahharian.data.local.entity.MuhasabahEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailRiwayatScreen(
    riwayatId: String,
    onNavigateBack: () -> Unit,
    onEditRiwayat: (Int) -> Unit,
    onDeleteRiwayat: (Int) -> Unit,
    viewModel: DetailRiwayatViewModel = hiltViewModel()
) {
    val muhasabah = viewModel.muhasabah.collectAsState().value
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    var score by remember { mutableStateOf(0f) }
    var mood by remember { mutableStateOf("") }
    var activity by remember { mutableStateOf("") }
    var obstacle by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    LaunchedEffect(riwayatId) {
        viewModel.loadReflectionById(riwayatId.toInt())
    }

    LaunchedEffect(muhasabah) {
        muhasabah?.let {
            score = it.score.toFloat() / 100
            mood = it.mood
            activity = it.activity
            obstacle = it.obstacle
            note = it.note
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Refleksi ${muhasabah?.type ?: "..."}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Text(
                            text = muhasabah?.date ?: "",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isEditing = !isEditing
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Black)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            if (isEditing && muhasabah != null) {
                FloatingActionButton(onClick = {
                    val updated = muhasabah.copy(
                        score = (score * 100).toInt(),
                        mood = mood,
                        activity = activity,
                        obstacle = obstacle,
                        note = note
                    )
                    viewModel.updateReflection(updated) {
                        isEditing = false
                    }
                }) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "save")
                }
            }
        }
    ) { padding ->
        muhasabah?.let {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(if (isEditing) Color.White else Color(0xFFF5F5F5))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    SliderQuestionView("Skor", score, isEditing) { score = it }
                }
                item {
                    EmojiPickerView("Mood", mood, isEditing) { mood = it }
                }
                item {
                    TextQuestionView("Aktivitas", activity, isEditing) { activity = it }
                }
                item {
                    TextQuestionView("Hambatan", obstacle, isEditing) { obstacle = it }
                }
                item {
                    TextQuestionView("Catatan Tambahan", note, isEditing) { note = it }
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            muhasabah?.let {
                                viewModel.deleteReflection(it.id) {
                                    onDeleteRiwayat(it.id)
                                }
                            }
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Hapus", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Batal")
                    }
                },
                title = { Text("Konfirmasi Hapus") },
                text = { Text("Apakah kamu yakin ingin menghapus refleksi ini?") }
            )
        }
    }
}

@Composable
private fun SliderQuestionView(
    label: String,
    value: Float,
    readOnly: Boolean,
    onValueChange: (Float) -> Unit = {}
) {
    Column {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            enabled = readOnly,
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF8BC34A),
                activeTrackColor = Color(0xFF8BC34A),
                inactiveTrackColor = Color(0xFFE0E0E0)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EmojiPickerView(
    label: String,
    selectedEmoji: String,
    readOnly: Boolean,
    onEmojiSelected: (String) -> Unit = {}
) {
    Column {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(listOf("😊", "😐", "😢", "😠", "😌")) { emoji ->
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(if (selectedEmoji == emoji) Color(0xFF8BC34A).copy(alpha = 0.1f) else Color.Transparent)
                        .border(
                            width = if (selectedEmoji == emoji) 2.dp else 0.dp,
                            color = Color(0xFF8BC34A),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .clickable(enabled = readOnly) { onEmojiSelected(emoji) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 28.sp)
                }
            }
        }
    }
}

@Composable
private fun TextQuestionView(
    label: String,
    value: String,
    readOnly: Boolean,
    onTextChange: (String) -> Unit = {}
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        BasicTextField(
            value = value,
            onValueChange = onTextChange,
            readOnly = !readOnly,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 80.dp)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                .padding(12.dp),
            decorationBox = { innerTextField ->
                innerTextField()
            }
        )
    }
}
