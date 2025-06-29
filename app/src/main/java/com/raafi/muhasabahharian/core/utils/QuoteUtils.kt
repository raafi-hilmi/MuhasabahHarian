package com.raafi.muhasabahharian.core.utils


object QuoteUtils {
    val dailyQuotes = listOf(
        Triple(
            "لَا تَحْزَنْ إِنَّ اللَّهَ مَعَنَا",
            "\"Jangan bersedih, sesungguhnya Allah bersama kita.\"",
            "QS. At-Taubah: 40"
        ),
        Triple(
            "وَمَا تَوْفِيقِي إِلَّا بِاللَّهِ",
            "\"Tidak ada keberhasilan bagiku kecuali dengan pertolongan Allah.\"",
            "QS. Hud: 88"
        ),
        Triple(
            "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا",
            "\"Sesungguhnya bersama kesulitan ada kemudahan.\"",
            "QS. Al-Insyirah: 6"
        ),
        Triple(
            "إِنَّ اللَّهَ يُحِبُّ الْمُتَوَكِّلِينَ",
            "\"Sesungguhnya Allah mencintai orang-orang yang bertawakal.\"",
            "QS. Ali Imran: 159"
        ),
        Triple(
            "إِنَّ اللَّهَ غَفُورٌ رَحِيمٌ",
            "\"Sesungguhnya Allah Maha Pengampun lagi Maha Penyayang.\"",
            "QS. Al-Baqarah: 173"
        )
    )

    fun getRandomQuote(): Triple<String, String, String> {
        return dailyQuotes.random()
    }
}
