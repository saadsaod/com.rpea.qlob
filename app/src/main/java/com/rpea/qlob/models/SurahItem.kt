package com.rpea.qlob.models

data class SurahItem(
    val id: Int,
    val name: String,
    val type: Int, // 1 for Makki, 2 for Madani
    val serverUrl: String,
    val isDownloaded: Boolean = false,
    val localPath: String? = null
)
