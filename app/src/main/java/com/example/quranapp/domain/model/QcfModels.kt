package com.example.quranapp.domain.model

data class QcfPage(
    val pageNumber: Int,
    val lines: List<QcfLine>,
    val verses: List<QcfVerse>
)

data class QcfLine(
    val lineNumber: Int,
    val text: String, // The QCF v1 character mapping string for this line
    val surahNumber: Int? = null, // If this line is a surah header
    val isBismillah: Boolean = false // If this line is the Bismillah header
)

data class QcfVerse(
    val id: Int,
    val surahNumber: Int,
    val ayahNumber: Int,
    val codeV1: String, // The raw QCF mapping code
    val page: Int
)
