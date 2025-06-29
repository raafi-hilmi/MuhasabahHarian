package com.raafi.muhasabahharian.presentation.muhasabah

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.raafi.muhasabahharian.data.model.*
import com.raafi.muhasabahharian.presentation.common.components.BottomNavigationBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuhasabahScreen(
    type: String,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit = {}
) {
    val viewModel: MuhasabahViewModel = hiltViewModel()
    val template = remember { MuhasabahData.getTemplateById(type) }
    var answers by remember { mutableStateOf(mutableMapOf<String, Any>()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    if (template == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Red),
            contentAlignment = Alignment.Center,
        ) {
            Text("Template tidak ditemukan")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = template.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
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

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(template.questions) { question ->
                    QuestionItem(
                        question = question,
                        answer = answers[question.key],
                        onAnswerChange = { newAnswer ->
                            answers = answers.toMutableMap().apply {
                                put(question.key, newAnswer)
                            }
                        }
                    )
                }

                // Submit Button
                item {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .fillMaxWidth(),
                        snackbar = { snackbarData ->
                            Snackbar(
                                snackbarData = snackbarData,
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                dismissActionContentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val isValid = template.questions.all { question ->
                                val answer = answers[question.key]
                                when (question.type) {
                                    QuestionType.TEXT_OPTIONAL -> true
                                    QuestionType.SLIDER -> answer is Float && answer in 0f..1f
                                    QuestionType.EMOJI_PICKER -> answer is String && answer.isNotBlank()
                                    QuestionType.TEXT -> answer is String && answer.isNotBlank()
                                }
                            }

                            if (isValid) {
                                viewModel.saveReflection(
                                    type = type,
                                    answers = answers,
                                    onSuccess = onNavigateToHome
                                )
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Selain catatan, harap diisi", withDismissAction = true)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8BC34A)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Simpan",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionItem(
    question: Question,
    answer: Any?,
    onAnswerChange: (Any) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        when (question.type) {
            QuestionType.SLIDER -> {
                SliderQuestion(
                    question = question,
                    value = answer as? Float ?: 0.5f,
                    onValueChange = onAnswerChange
                )
            }
            QuestionType.EMOJI_PICKER -> {
                EmojiPickerQuestion(
                    question = question,
                    selectedEmoji = answer as? String,
                    onEmojiSelected = onAnswerChange
                )
            }
            QuestionType.TEXT, QuestionType.TEXT_OPTIONAL -> {
                TextQuestion(
                    question = question,
                    text = answer as? String ?: "",
                    onTextChange = onAnswerChange
                )
            }
        }
    }
}

@Composable
private fun SliderQuestion(
    question: Question,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Text(
            text = question.label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Slider(
            value = value,
            onValueChange = onValueChange,
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
private fun EmojiPickerQuestion(
    question: Question,
    selectedEmoji: String?,
    onEmojiSelected: (String) -> Unit
) {
    Column {
        Text(
            text = question.label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(question.options ?: emptyList()) { emoji ->
                EmojiButton(
                    emoji = emoji,
                    isSelected = selectedEmoji == emoji,
                    onClick = { onEmojiSelected(emoji) }
                )
            }
        }
    }
}

@Composable
private fun EmojiButton(
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                if (isSelected) Color(0xFF8BC34A).copy(alpha = 0.1f)
                else Color.Transparent
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = Color(0xFF8BC34A),
                shape = RoundedCornerShape(28.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 28.sp
        )
    }
}

@Composable
private fun TextQuestion(
    question: Question,
    text: String,
    onTextChange: (String) -> Unit
) {
    Column {
        Text(
            text = question.label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = if (question.type == QuestionType.TEXT_OPTIONAL) 120.dp else 80.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            decorationBox = { innerTextField ->
                if (text.isEmpty()) {
                    Text(
                        text = if (question.type == QuestionType.TEXT_OPTIONAL) "Tulis catatan tambahan..." else "Tulis jawabanmu di sini...",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                innerTextField()
            }
        )
    }
}