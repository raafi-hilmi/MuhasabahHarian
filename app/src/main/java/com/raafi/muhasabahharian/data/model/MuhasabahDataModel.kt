package com.raafi.muhasabahharian.data.model

data class MuhasabahTemplate(
    val id: String,
    val title: String,
    val questions: List<Question>
)

data class Question(
    val key: String,
    val label: String,
    val type: QuestionType,
    val options: List<String>? = null
)

enum class QuestionType {
    SLIDER,
    EMOJI_PICKER,
    TEXT,
    TEXT_OPTIONAL
}

data class MuhasabahAnswer(
    val templateId: String,
    val answers: Map<String, Any>,
    val date: String
)

object MuhasabahData {
    private val templates = listOf(
        MuhasabahTemplate(
            id = "produktif",
            title = "Refleksi Produktif",
            questions = listOf(
                Question(
                    key = "score",
                    label = "Seberapa produktif harimu hari ini?",
                    type = QuestionType.SLIDER
                ),
                Question(
                    key = "mood",
                    label = "Perasaan yang kamu rasakan setelah aktivitas hari ini?",
                    type = QuestionType.EMOJI_PICKER,
                    options = listOf("😊", "😐", "😢", "😠", "😌")
                ),
                Question(
                    key = "activity",
                    label = "Apa tujuan utama atau hal penting yang kamu lakukan hari ini?",
                    type = QuestionType.TEXT
                ),
                Question(
                    key = "obstacle",
                    label = "Apa hambatan atau gangguan yang kamu alami hari ini?",
                    type = QuestionType.TEXT
                ),
                Question(
                    key = "note",
                    label = "Catatan tambahan (opsional)",
                    type = QuestionType.TEXT_OPTIONAL
                )
            )
        ),
        MuhasabahTemplate(
            id = "emosional",
            title = "Refleksi Emosional",
            questions = listOf(
                Question(
                    key = "score",
                    label = "Seberapa stabil emosimu hari ini?",
                    type = QuestionType.SLIDER
                ),
                Question(
                    key = "mood",
                    label = "Emosi dominan yang kamu rasakan hari ini?",
                    type = QuestionType.EMOJI_PICKER,
                    options = listOf("😊", "😐", "😢", "😠", "😌")
                ),
                Question(
                    key = "activity",
                    label = "Hal atau kejadian apa yang paling memengaruhi emosimu hari ini?",
                    type = QuestionType.TEXT
                ),
                Question(
                    key = "obstacle",
                    label = "Apa yang membuat emosimu terganggu?",
                    type = QuestionType.TEXT
                ),
                Question(
                    key = "note",
                    label = "Catatan tambahan (opsional)",
                    type = QuestionType.TEXT_OPTIONAL
                )
            )
        ),
        MuhasabahTemplate(
            id = "islami",
            title = "Refleksi Islami",
            questions = listOf(
                Question(
                    key = "score",
                    label = "Seberapa baik hubunganmu dengan Allah hari ini?",
                    type = QuestionType.SLIDER
                ),
                Question(
                    key = "mood",
                    label = "Bagaimana kondisi hatimu setelah ibadah hari ini?",
                    type = QuestionType.EMOJI_PICKER,
                    options = listOf("😊", "😐", "😢", "😠", "😌")
                ),
                Question(
                    key = "activity",
                    label = "Apa saja amal atau ibadah yang kamu lakukan hari ini?",
                    type = QuestionType.TEXT
                ),
                Question(
                    key = "obstacle",
                    label = "Godaan atau kelalaian apa yang kamu rasakan hari ini?",
                    type = QuestionType.TEXT
                ),
                Question(
                    key = "note",
                    label = "Catatan tambahan (opsional)",
                    type = QuestionType.TEXT_OPTIONAL
                )
            )
        ),
        MuhasabahTemplate(
            id = "olahraga",
            title = "Refleksi Tubuh (Olahraga)",
            questions = listOf(
                Question(
                    key = "score",
                    label = "Seberapa puas kamu dengan kondisi tubuhmu hari ini?",
                    type = QuestionType.SLIDER
                ),
                Question(
                    key = "mood",
                    label = "Perasaanmu setelah olahraga hari ini?",
                    type = QuestionType.EMOJI_PICKER,
                    options = listOf("😊", "😐", "😢", "😠", "😌")
                ),
                Question(
                    key = "activity",
                    label = "Olahraga atau aktivitas fisik apa yang kamu lakukan hari ini?",
                    type = QuestionType.TEXT
                ),
                Question(
                    key = "obstacle",
                    label = "Apa saja tantangan atau alasan sulit untuk berolahraga?",
                    type = QuestionType.TEXT
                ),
                Question(
                    key = "note",
                    label = "Catatan tambahan (opsional)",
                    type = QuestionType.TEXT_OPTIONAL
                )
            )
        )
    )

    fun getTemplateById(id: String): MuhasabahTemplate? {
        return templates.find { it.id == id }
    }

    fun getAllTemplates(): List<MuhasabahTemplate> {
        return templates
    }
}