import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
// Pastikan semua import lain yang relevan sudah ada

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuhasabahScreen(
    navController: NavController,
    moodName: String,
    muhasabahViewModel: MuhasabahViewModel = viewModel()
) {
    LaunchedEffect(key1 = moodName) {
        muhasabahViewModel.loadQuestionsForMood(moodName)
    }

    val questions by muhasabahViewModel.questions.collectAsState()
    val currentIndex by muhasabahViewModel.currentQuestionIndex.collectAsState()
    val feedback by muhasabahViewModel.feedback.collectAsState()

    var showFeedbackDialog by remember { mutableStateOf(false) }

    // 1. State lokal untuk menampung input dari TextField
    var currentAnswer by remember { mutableStateOf("") }

    // 2. Efek untuk mereset jawaban saat pertanyaan berganti
    LaunchedEffect(currentIndex) {
        currentAnswer = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Membuat Column bisa di-scroll
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (questions.isNotEmpty()) {
            Text(
                text = "Pertanyaan ${currentIndex + 1} dari ${questions.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = questions[currentIndex],
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Tambahkan OutlinedTextField untuk input pengguna
            OutlinedTextField(
                value = currentAnswer,
                onValueChange = { currentAnswer = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                label = { Text("Tuliskan jawabanmu di sini...") },
                shape = MaterialTheme.shapes.large
            )

            Spacer(modifier = Modifier.height(24.dp))

            val isLastQuestion = currentIndex == questions.size - 1
            val buttonText = if (isLastQuestion) "Selesai" else "Selanjutnya"

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    // 4. Simpan jawaban sebelum melanjutkan
                    muhasabahViewModel.saveCurrentAnswer(currentAnswer)

                    if (isLastQuestion) {
                        muhasabahViewModel.submitMuhasabah() // Panggil fungsi submit
                        showFeedbackDialog = true
                    } else {
                        muhasabahViewModel.nextQuestion()
                    }
                }
            ) {
                Text(buttonText)
            }

        } else {
            CircularProgressIndicator()
        }
    }

    if (showFeedbackDialog) {
        AlertDialog(
            onDismissRequest = {
                showFeedbackDialog = false
                navController.popBackStack()
            },
            title = {
                Text(text = "Feedback")
            },
            text = {
                Text(text = feedback)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showFeedbackDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Kembali ke Beranda")
                }
            }
        )
    }
}