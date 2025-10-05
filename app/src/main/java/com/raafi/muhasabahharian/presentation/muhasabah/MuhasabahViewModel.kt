import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raafihilmi.muhasabahharian.data.Mood
import com.raafihilmi.muhasabahharian.data.MoodDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MuhasabahViewModel : ViewModel() {

    private val _questions = MutableStateFlow<List<String>>(emptyList())
    val questions: StateFlow<List<String>> = _questions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _feedback = MutableStateFlow("")
    val feedback: StateFlow<String> = _feedback.asStateFlow()

    private val _answers = MutableStateFlow<MutableList<String>>(mutableListOf())
    val answers: StateFlow<List<String>> = _answers.asStateFlow()

    fun loadQuestionsForMood(moodName: String) {
        viewModelScope.launch {
            val moodSet = MoodDataStore.moodQuestionSets.find { it.mood.name == moodName }

            if (moodSet != null) {
                _questions.value = moodSet.questions
                _feedback.value = moodSet.feedback
                _answers.value = MutableList(moodSet.questions.size) { "" }
                _currentQuestionIndex.value = 0
            } else {
                _questions.value = listOf("Data pertanyaan tidak ditemukan.")
                _feedback.value = "Terjadi kesalahan. Silakan coba lagi."
            }
        }
    }

    fun saveCurrentAnswer(answer: String) {
        val currentAnswers = _answers.value
        if (_currentQuestionIndex.value < currentAnswers.size) {
            currentAnswers[_currentQuestionIndex.value] = answer
            _answers.value = currentAnswers
        }
    }

    fun nextQuestion() {
        if (_currentQuestionIndex.value < _questions.value.size - 1) {
            _currentQuestionIndex.value++
        }
    }

    fun submitMuhasabah() {
        println("Muhasabah Selesai: ${answers.value}")
    }
}