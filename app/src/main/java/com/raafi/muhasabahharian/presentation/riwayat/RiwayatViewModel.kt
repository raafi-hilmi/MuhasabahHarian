package com.raafi.muhasabahharian.presentation.riwayat

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raafi.muhasabahharian.data.local.entity.MuhasabahEntity
import com.raafi.muhasabahharian.data.repository.MuhasabahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.*


@HiltViewModel
class RiwayatViewModel @Inject constructor(
    private val repository: MuhasabahRepository
) : ViewModel() {
    private val _exportedPdfPath = MutableStateFlow<String?>(null)
    val exportedPdfPath: StateFlow<String?> = _exportedPdfPath
    private val _riwayat = MutableStateFlow<List<RiwayatItem>>(emptyList())
    val riwayat: StateFlow<List<RiwayatItem>> = _riwayat.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllReflections()
                .collect { list ->
                    _riwayat.value = list.map { it.toRiwayatItem() }
                }
        }
    }

    private fun MuhasabahEntity.toRiwayatItem(): RiwayatItem {
        val formattedDate = try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate = sdf.parse(this.date)
            SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")).format(parsedDate!!)
        } catch (e: Exception) {
            this.date
        }

        return RiwayatItem(
            id = id.toString(),
            type = type,
            title = "Refleksi ${type.replaceFirstChar { it.uppercase() }}",
            date = formattedDate,
            description = buildString {
                append("Aktivitas: $activity\n")
                append("Hambatan: $obstacle\n")
                if (note.isNotBlank()) append("Catatan: $note")
            }.trim()
        )
    }

    fun deleteAllRiwayat() {
        viewModelScope.launch {
            repository.deleteAllReflections()
        }
    }

    fun exportRiwayatToPdf(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val reflections = repository.getAllReflections().first()
                if (reflections.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Tidak ada data", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val pdfDocument = PdfDocument()
                val paint = Paint().apply {
                    textSize = 12f
                    color = Color.BLACK
                }

                var pageNumber = 1
                var y = 50
                val margin = 40
                val pageHeight = 842 // A4 height
                val lineHeight = 20

                fun createNewPage(): Pair<PdfDocument.Page, Canvas> {
                    val pageInfo = PdfDocument.PageInfo.Builder(595, pageHeight, pageNumber).create()
                    val newPage = pdfDocument.startPage(pageInfo)
                    val newCanvas = newPage.canvas
                    pageNumber++
                    y = margin // Reset Y position
                    return newPage to newCanvas
                }

                var (currentPage, canvas) = createNewPage()

                reflections.forEachIndexed { index, item ->
                    val formattedDate = try {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val parsedDate = sdf.parse(item.date)
                        SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")).format(parsedDate!!)
                    } catch (e: Exception) {
                        item.date
                    }

                    val lines = listOf(
                        "[$index] Tanggal: $formattedDate",
                        "Tipe: ${item.type.replaceFirstChar { it.uppercase() }}",
                        "Mood: ${item.mood} | Skor: ${item.score}",
                        "Aktivitas: ${item.activity}",
                        "Hambatan: ${item.obstacle}",
                        "Catatan: ${item.note}",
                        "----------------------------------------"
                    )

                    for (line in lines) {
                        if (y > pageHeight - margin) {
                            pdfDocument.finishPage(currentPage)
                            val (newPage, newCanvas) = createNewPage()
                            currentPage = newPage
                            canvas = newCanvas
                        }

                        canvas.drawText(line, margin.toFloat(), y.toFloat(), paint)
                        y += lineHeight
                    }
                }

                pdfDocument.finishPage(currentPage)

                val filename = "riwayat_muhasabah_${System.currentTimeMillis()}.pdf"
                val file = File(context.getExternalFilesDir(null), filename)

                FileOutputStream(file).use { fos ->
                    pdfDocument.writeTo(fos)
                }

                pdfDocument.close()

                _exportedPdfPath.value = file.absolutePath

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Export gagal: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
